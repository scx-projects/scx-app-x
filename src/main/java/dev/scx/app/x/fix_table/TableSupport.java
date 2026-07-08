package dev.scx.app.x.fix_table;

import dev.scx.sql.SQLClient;
import dev.scx.sql.SQLRunner;
import dev.scx.sql.dialect.Dialect;
import dev.scx.sql.schema.Column;
import dev.scx.sql.schema.Index;
import dev.scx.sql.schema.Table;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static dev.scx.sql.SQL.sql;
import static dev.scx.sql.metadata.DatabaseMetadataReader.loadCurrentTable;

/// 表相关便捷支持类
///
/// 基于现有 SQL、Dialect、metadata 与 schema 原语,
/// 提供面向单表的高层便利能力,
/// 用于简化表结构读取、比对、DDL 生成与修复等常见操作.
///
/// 该类属于 support 层:
/// - 不定义核心抽象
/// - 不承担底层原语职责
/// - 仅用于帮助用户更方便地组合和使用现有能力
///
/// @author scx567888
final class TableSupport {

    /// 查找新表中存在、旧表中不存在的列与索引
    public static TableDiff diffTable(Table oldTable, Table newTable) {
        var needAddColumns = new ArrayList<Column>();
        var needDropColumns = new ArrayList<Column>();

        for (var oldColumn : oldTable.columns()) {
            var newColumn = newTable.getColumn(oldColumn.name());
            if (newColumn == null) {
                needDropColumns.add(oldColumn);
            }
        }

        for (var newColumn : newTable.columns()) {
            var oldColumn = oldTable.getColumn(newColumn.name());
            if (oldColumn == null) {
                needAddColumns.add(newColumn);
            }
        }

        // 保守策略: 索引只按 columnName 判断 "是否存在"
        var oldIndexColumnNames = Arrays.stream(oldTable.indexes())
            .map(Index::columnName)
            .collect(Collectors.toSet());

        var newIndexColumnNames = Arrays.stream(newTable.indexes())
            .map(Index::columnName)
            .collect(Collectors.toSet());

        var needAddIndexes = new ArrayList<Index>();
        var needDropIndexes = new ArrayList<Index>();

        for (var oldIndex : oldTable.indexes()) {
            if (!newIndexColumnNames.contains(oldIndex.columnName())) {
                needDropIndexes.add(oldIndex);
            }
        }

        for (var newIndex : newTable.indexes()) {
            if (!oldIndexColumnNames.contains(newIndex.columnName())) {
                needAddIndexes.add(newIndex);
            }
        }

        return new TableDiff(
            needAddColumns.toArray(Column[]::new),
            needDropColumns.toArray(Column[]::new),
            needAddIndexes.toArray(Index[]::new),
            needDropIndexes.toArray(Index[]::new)
        );
    }

    /// 获取迁移 SQL（当前只保守添加不存在的列和索引）
    public static List<String> getFixTableSQLs(Table oldTable, Table newTable, Dialect dialect) {
        var tableDiff = diffTable(oldTable, newTable);
        var list = new ArrayList<String>();

        // 默认我们只保守 add 没有的列.
        for (var column : tableDiff.needAddColumns()) {
            list.addAll(dialect.getAddColumnDDLs(newTable, column));
        }
        // 默认我们只保守 add 没有的索引.
        for (var index : tableDiff.needAddIndexes()) {
            list.addAll(dialect.getAddIndexDDLs(newTable, index));
        }
        return list;
    }

    /// 和当前数据库中同名表进行比对并修复
    /// (若表不存在则创建, 若存在则只添加不存在的列和索引)
    public static void fixTable(Table table, SQLClient sqlClient) throws SQLException {
        try (var con = sqlClient.dataSource().getConnection()) {
            // 查找同名表
            var tableMetaData = loadCurrentTable(con, table.name(), sqlClient.dialect());

            // 没有这个表 创建表
            if (tableMetaData == null) {
                var createTableDDLs = sqlClient.dialect().getCreateTableDDLs(table);
                for (var createTableDDL : createTableDDLs) {
                    SQLRunner.execute(con, sql(createTableDDL));
                }
                return;
            }

            // 有表 获取迁移 SQL
            var migrateSQLs = getFixTableSQLs(tableMetaData, table, sqlClient.dialect());

            for (var migrateSQL : migrateSQLs) {
                SQLRunner.execute(con, sql(migrateSQL));
            }
        }
    }

    /// 检查是否需要修复表
    ///
    /// @param table table
    /// @return true 需要 false 不需要
    public static boolean checkNeedFixTable(Table table, SQLClient sqlClient) throws SQLException {
        try (var con = sqlClient.dataSource().getConnection()) {
            // 查找同名表
            var tableMetaData = loadCurrentTable(con, table.name(), sqlClient.dialect());

            // 没有这个表 创建表
            if (tableMetaData == null) {
                return true;
            }

            // 有表 获取迁移 SQL
            var tableDiff = diffTable(tableMetaData, table);

            return tableDiff.needAddColumns().length > 0 || tableDiff.needAddIndexes().length > 0;
        }
    }

    /// 表比较结果
    public record TableDiff(
        Column[] needAddColumns,
        Column[] needDropColumns,
        Index[] needAddIndexes,
        Index[] needDropIndexes
    ) {}

}
