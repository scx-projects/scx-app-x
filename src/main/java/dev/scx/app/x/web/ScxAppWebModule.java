package dev.scx.app.x.web;

import dev.scx.app.ScxApp;
import dev.scx.app.ScxAppModule;
import dev.scx.app.ScxAppModuleDefinition;
import dev.scx.app.x.di.ScxAppDIModule;
import dev.scx.app.x.http.ScxAppHttpModule;
import dev.scx.web.ScxWeb;
import dev.scx.web.annotation.Routes;

import java.util.ArrayList;

/// ScxAppWebModule
///
/// @author scx567888
public final class ScxAppWebModule implements ScxAppModule {

    private ScxWeb scxWeb;

    public ScxAppWebModule() {
        this.scxWeb = new ScxWeb();
    }

    @Override
    public ScxAppModuleDefinition define() {
        return ScxAppModuleDefinition.of()
            .require(ScxAppHttpModule.class)
            .require(ScxAppDIModule.class)
            .startBefore(ScxAppHttpModule.class)
            .startAfter(ScxAppDIModule.class);
    }

    @Override
    public void start(ScxApp scxApp) {
        var httpModule = scxApp.getModule(ScxAppHttpModule.class);
        var diModule = scxApp.getModule(ScxAppDIModule.class);

        var componentContainer = diModule.componentContainer();

        var router = httpModule.router();

        var webRoutes = new ArrayList<>();

        // 过滤所有候选类中的 webRoute
        for (var candidate : scxApp.candidates()) {
            if (candidate.getAnnotation(Routes.class) != null) {
                var component = componentContainer.getComponent(candidate);
                webRoutes.add(component);
            }
        }

        // 创建 路由
        var routes = scxWeb.routes(webRoutes.toArray());
        for (var route : routes) {
            router.route(route.priority(), route);
        }
    }

    /// 暴露 scxWeb 允许外部访问
    public ScxWeb scxWeb() {
        return scxWeb;
    }

}
