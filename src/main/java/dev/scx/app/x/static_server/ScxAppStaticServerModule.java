package dev.scx.app.x.static_server;

import dev.scx.app.ScxApp;
import dev.scx.app.ScxAppModule;
import dev.scx.app.ScxAppModuleDefinition;
import dev.scx.app.environment.ScxEnvironment;
import dev.scx.app.x.http.ScxAppHttpModule;
import dev.scx.reflect.TypeReference;

import static dev.scx.app.x.static_server.ScxAppStaticServerModuleHelper.printStaticServersInfo;

/// ScxAppStaticServerModule
///
/// @author scx567888
public final class ScxAppStaticServerModule implements ScxAppModule {

    @Override
    public ScxAppModuleDefinition init(ScxEnvironment environment) {
        return ScxAppModuleDefinition.of()
            .require(ScxAppHttpModule.class)
            .startBefore(ScxAppHttpModule.class);
    }

    @Override
    public void start(ScxApp scxApp) throws Exception {
        var httpModule = scxApp.getComponent(ScxAppHttpModule.class);

        var router = httpModule.router();

        var environment = scxApp.environment();

        var staticServers = environment.get("scx.static-servers", new TypeReference<StaticServer[]>() {}, new StaticServer[0]);

        for (var staticServer : staticServers) {
            var route = staticServer.toRoute();
            // 用一个较靠后的优先级
            router.route(999999, route);
        }

        // 打印静态服务器信息
        printStaticServersInfo(staticServers);

    }

}
