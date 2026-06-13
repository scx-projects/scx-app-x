package dev.scx.app.x.fix_table;

import dev.scx.ansi.Ansi;
import dev.scx.app.ScxApp;
import dev.scx.data.sql.annotation.Table;
import dev.scx.data.sql.schema_mapping.AnnotationConfigTable;
import dev.scx.sql.SQLClient;

import java.util.List;

import static dev.scx.app.x.fix_table.ScxAppFixTableModule.logger;
import static java.lang.System.Logger.Level.ERROR;

final class ScxAppFixTableModuleHelper {

    /// 检查数据源是否可用
    public static boolean checkDataSource(SQLClient sqlClient) {
        try (var connection = sqlClient.dataSource().getConnection()) {
            var dm = connection.getMetaData();
            Ansi.ansi().brightGreen("数据源连接成功 : 类型 [" + dm.getDatabaseProductName() + "]  版本 [" + dm.getDatabaseProductVersion() + "]").println();
            return true;
        } catch (Exception e) {
            Ansi.ansi().brightRed("数据源连接失败 !!!").println();
            return false;
        }
    }

    /// 获取所有 Table class
    public static List<Class<?>> getTableClassList(ScxApp scxApp) {
        return scxApp.candidates().stream()
            .filter(c -> c.getAnnotation(Table.class) != null)// 所有标记了 Table 注解的 类
            .toList();
    }

    /// 检查是否有任何 标注 Table 注解的类需要修复表
    ///
    /// @return 是否有
    public static boolean checkNeedFixTable(List<Class<?>> tableClassList, SQLClient sqlClient) {
        Ansi.ansi().brightGreen("检查数据表结构中...").println();
        for (var tableClass : tableClassList) {
            // 根据 class 获取 tableInfo
            var annotationConfigTable = new AnnotationConfigTable<>(tableClass);
            try {
                // 有任何需要修复的直接 返回 true
                if (TableSupport.checkNeedFixTable(annotationConfigTable, sqlClient)) {
                    return true;
                }
            } catch (Exception e) {
                logger.log(ERROR, "检查表失败 !!!", e);
            }
        }
        return false;
    }

    public static void fixTable(List<Class<?>> tableClassList, SQLClient sqlClient) {
        Ansi.ansi().brightMagenta("修复数据表结构中...").println();
        // 修复成功的表
        var fixSuccess = 0;
        // 修复失败的表
        var fixFail = 0;
        // 不需要修复的表
        var noNeedToFix = 0;

        for (var tableClass : tableClassList) {
            // 根据 class 获取 annotationConfigTable
            var annotationConfigTable = new AnnotationConfigTable<>(tableClass);
            try {
                if (TableSupport.checkNeedFixTable(annotationConfigTable, sqlClient)) {
                    TableSupport.fixTable(annotationConfigTable, sqlClient);
                    fixSuccess = fixSuccess + 1;
                } else {
                    noNeedToFix = noNeedToFix + 1;
                }
            } catch (Exception e) {
                logger.log(ERROR, "修复表失败 !!!", e);
                fixFail = fixFail + 1;
            }
        }

        if (fixSuccess != 0) {
            Ansi.ansi().brightGreen("修复成功 " + fixSuccess + " 张表...").println();
        }
        if (fixFail != 0) {
            Ansi.ansi().brightYellow("修复失败 " + fixFail + " 张表...").println();
        }
        if (fixSuccess + fixFail == 0) {
            Ansi.ansi().brightGreen("没有表需要修复...").println();
        }

    }

}
