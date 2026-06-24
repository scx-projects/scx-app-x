package dev.scx.app.x.sql;

import com.zaxxer.hikari.HikariDataSource;
import dev.scx.ansi.Ansi;
import dev.scx.app.ScxApp;
import dev.scx.app.ScxAppModule;
import dev.scx.app.ScxAppModuleDefinition;
import dev.scx.app.environment.ScxEnvironment;
import dev.scx.jdbc.spy.ScxJdbcSpy;
import dev.scx.jdbc.spy.listener.logging.LoggingDataSourceListener;
import dev.scx.jdbc.spy.listener.logging.PreparedStatementLogStyle;
import dev.scx.sql.JDBCConnectionInfo;
import dev.scx.sql.SQLClient;
import dev.scx.sql.TypeSQLResolver;

import java.util.Arrays;

import static dev.scx.jdbc.spy.listener.logging.PreparedStatementLogStyle.RENDERED_SQL;
import static dev.scx.jdbc.spy.listener.logging.PreparedStatementLogStyle.SQL_AND_PARAMETERS;

/// ScxAppSQLModule
///
/// @author scx567888
/// @version 0.0.1
public final class ScxAppSQLModule implements ScxAppModule {

    private SQLClient sqlClient;

    // 内部字段 打印 Info 用
    private JDBCConnectionInfo jdbcConnectionInfo;
    private boolean spyEnabled;

    private static PreparedStatementLogStyle toSpyLogStyle(String spyStyle) {
        var s = spyStyle.trim().toUpperCase();
        return switch (s) {
            case "RENDERED_SQL", "RENDERED-SQL", "RENDERED", "R" -> RENDERED_SQL;
            case "SQL_AND_PARAMETERS", "SQL-AND-PARAMETERS", "PARAMETERS", "PARAMETER", "P" -> SQL_AND_PARAMETERS;
            default -> throw new IllegalArgumentException("不支持的 Spy Style : " + spyStyle);
        };
    }

    @Override
    public ScxAppModuleDefinition init(ScxEnvironment environment) {
        var dataSourceUrl = environment.get("scx.sql.url", String.class);
        var dataSourceUsername = environment.get("scx.sql.username", String.class);
        var dataSourcePassword = environment.get("scx.sql.password", String.class);
        var dataSourceParameters = environment.get("scx.sql.parameters", String[].class, new String[0]);
        this.spyEnabled = environment.get("scx.sql.spy.enabled", boolean.class, false);
        var spyStyle = environment.get("scx.sql.spy.style", String.class, "RENDERED_SQL");

        this.jdbcConnectionInfo = new JDBCConnectionInfo(
            dataSourceUrl,
            dataSourceUsername,
            dataSourcePassword,
            dataSourceParameters
        );

        // 这里额外添加一个 处理 json 的 handler
        var typeSQLResolver = TypeSQLResolver.builder()
            .registerDefaultHandlers()
            .registerHandlerFactory(new ObjectSQLHandlerFactory())
            .build();

        this.sqlClient = SQLClient.of(
            jdbcConnectionInfo,
            typeSQLResolver,
            d -> {
                var dataSource = new HikariDataSource();
                dataSource.setDataSource(d);
                return dataSource;
            },
            d -> spyEnabled ?
                ScxJdbcSpy.spy(d, new LoggingDataSourceListener(toSpyLogStyle(spyStyle))) :
                d
        );

        // 把 sqlClient 注入到 容器中
        return ScxAppModuleDefinition.of()
            .componentInstance(this.sqlClient);
    }

    @Override
    public void start(ScxApp scxApp) throws Exception {
        Ansi.ansi()
            .green("URL           -->     " + this.jdbcConnectionInfo.url()).ln()
            .green("Username      -->     " + this.jdbcConnectionInfo.username()).ln()
            .green("Parameters    -->     " + Arrays.toString(this.jdbcConnectionInfo.parameters())).ln()
            .green("Spy           -->     " + this.spyEnabled).println();
    }

    /// 暴露 sqlClient 允许外部访问
    public SQLClient sqlClient() {
        return this.sqlClient;
    }

}
