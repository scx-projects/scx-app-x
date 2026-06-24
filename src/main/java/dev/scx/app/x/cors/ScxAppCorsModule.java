package dev.scx.app.x.cors;

import dev.scx.app.ScxApp;
import dev.scx.app.ScxAppModule;
import dev.scx.app.ScxAppModuleDefinition;
import dev.scx.app.environment.ScxEnvironment;
import dev.scx.app.x.http.ScxAppHttpModule;
import dev.scx.http.headers.HttpHeaderName;
import dev.scx.http.routing.Route;
import dev.scx.http.routing.method_matcher.MethodMatcher;
import dev.scx.http.routing.path_matcher.PathMatcher;
import dev.scx.http.routing.x.cors.CorsHandler;
import dev.scx.http.routing.x.cors.allow_headers.AllowHeaders;
import dev.scx.http.routing.x.cors.allow_methods.AllowMethods;
import dev.scx.http.routing.x.cors.allow_origin.AllowOrigin;
import dev.scx.http.routing.x.cors.expose_headers.ExposeHeaders;

import static dev.scx.http.headers.HttpHeaderName.CONTENT_DISPOSITION;

/// ScxAppCorsModule
///
/// @author scx567888
/// @version 0.0.1
public final class ScxAppCorsModule implements ScxAppModule {

    private static final HttpHeaderName[] DEFAULT_EXPOSED_HEADERS = new HttpHeaderName[]{CONTENT_DISPOSITION};

    /// Cors handler
    private CorsHandler corsHandler;

    @Override
    public ScxAppModuleDefinition init(ScxEnvironment environment) {
        var allowedOrigin = environment.get("scx.cors.allowed-origin", String.class, "*");
        var allowCredentials = environment.get("scx.cors.allow-credentials", Boolean.class, false);

        // 设置 cors handler
        this.corsHandler = CorsHandler.of()
            // 这里我们不把 * 看作 Wildcard 而是 允许所有 Host 的特殊表达式, 这里使用 Reflect 这样才能和 allowCredentials=true 正确配合
            .allowOrigin(allowedOrigin.equals("*") ? AllowOrigin.ofReflect() : AllowOrigin.of(allowedOrigin))
            .allowHeaders(AllowHeaders.ofReflect())
            .allowMethods(AllowMethods.ofReflect())
            .exposeHeaders(ExposeHeaders.of(DEFAULT_EXPOSED_HEADERS))
            .allowCredentials(allowCredentials);

        return ScxAppModuleDefinition.of()
            .require(ScxAppHttpModule.class)
            .startBefore(ScxAppHttpModule.class);
    }

    @Override
    public void start(ScxApp scxApp) {
        var httpModule = scxApp.getComponent(ScxAppHttpModule.class);

        var router = httpModule.router();

        // 注册路由
        var corsHandlerRoute = Route.of(PathMatcher.any(), MethodMatcher.any(), corsHandler);

        // 使用较靠前的优先级
        router.route(-10000, corsHandlerRoute);
    }

    /// 暴露 corsHandler 允许外部访问.
    public CorsHandler corsHandler() {
        return corsHandler;
    }

}
