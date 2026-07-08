package dev.scx.app.x.fix_table;

import dev.scx.ansi.Ansi;
import dev.scx.app.ScxApp;
import dev.scx.app.ScxAppModule;
import dev.scx.app.ScxAppModuleDefinition;
import dev.scx.app.environment.ScxEnvironment;
import dev.scx.app.x.http.ScxAppHttpModule;
import dev.scx.app.x.sql.ScxAppSQLModule;

import static dev.scx.app.x.fix_table.ScxAppFixTableModuleHelper.*;

/// ScxAppFixTableModule
///
/// @author scx567888
public final class ScxAppFixTableModule implements ScxAppModule {

    static System.Logger logger = System.getLogger(ScxAppFixTableModule.class.getName());

    @Override
    public ScxAppModuleDefinition init(ScxEnvironment environment) {
        return ScxAppModuleDefinition.of()
            .require(ScxAppSQLModule.class)
            .startAfter(ScxAppSQLModule.class)
            .startBefore(ScxAppHttpModule.class);
    }

    @Override
    public void start(ScxApp scxApp) {
        var sqlModule = scxApp.getComponent(ScxAppSQLModule.class);
        var sqlClient = sqlModule.sqlClient();

        var fixTable = scxApp.environment().get("scx.fix-table.enabled", boolean.class, false);

        if (!fixTable) {
            return;
        }

        if (!checkDataSource(sqlClient)) {
            Ansi.ansi().brightCyan("数据源连接失败!!! 已跳过修复表!!!").println();
            return;
        }

        // 获取所有 table 类
        var tableClassList = getTableClassList(scxApp);

        if (checkNeedFixTable(tableClassList, sqlClient)) {
            fixTable(tableClassList, sqlClient);
        } else {
            Ansi.ansi().brightCyan("没有表需要修复...").println();
        }
    }

}
