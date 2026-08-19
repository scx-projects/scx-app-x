package dev.scx.app.x.http;

import dev.scx.app.ScxApp;
import dev.scx.app.ScxAppModule;
import dev.scx.app.ScxAppModuleDefinition;
import dev.scx.app.x.config.ScxAppConfigModule;
import dev.scx.app.x.config.environment.type.ConfiguredPath;
import dev.scx.app.x.config.environment.type.ConfiguredSize;
import dev.scx.http.ScxHttpServer;
import dev.scx.http.routing.Router;
import dev.scx.http.x.HttpServer;
import dev.scx.http.x.HttpServerOptions;
import dev.scx.http.x.error_handler.DefaultHttpServerErrorHandler;
import dev.scx.tcp.tls.TLS;
import dev.scx.websocket.x.WebSocketUpgradeRequestFactory;

import java.io.IOException;

import static dev.scx.app.x.http.ScxAppHttpModuleHelper.printRouteInfo;
import static dev.scx.app.x.http.ScxAppHttpModuleHelper.printServerAddresses;

/// ScxAppHttpModule
///
/// @author scx567888
public final class ScxAppHttpModule implements ScxAppModule {

    private HttpServerOptions httpServerOptions;
    private ScxHttpServer httpServer;
    private Router router;

    public ScxAppHttpModule() {
        this.router = Router.of();
    }

    @Override
    public ScxAppModuleDefinition define() {
        return ScxAppModuleDefinition.of()
            .require(ScxAppConfigModule.class)
            .startAfter(ScxAppConfigModule.class);
    }

    @Override
    public void start(ScxApp scxApp) throws IOException {
        var configModule = scxApp.getModule(ScxAppConfigModule.class);

        var environment = configModule.environment();

        var maxPayloadSize = environment.get("scx.http.max-payload-size", ConfiguredSize.class, "16MB");
        var useDevelopmentErrorPage = environment.get("scx.http.use-development-error-page", Boolean.class, false);

        var sslEnabled = environment.get("scx.http.ssl.enabled", Boolean.class, false);
        var sslPath = environment.get("scx.http.ssl.path", ConfiguredPath.class);
        var sslPassword = environment.get("scx.http.ssl.password", String.class);

        this.httpServerOptions = new HttpServerOptions();

        this.httpServerOptions.maxPayloadSize(maxPayloadSize.size());

        if (sslEnabled) {
            if (sslPath == null) {
                throw new IllegalArgumentException("scx.http.ssl.path 不能为空");
            }
            if (sslPassword == null) {
                throw new IllegalArgumentException("scx.http.ssl.password 不能为空");
            }
            var tls = TLS.of(sslPath.path(), sslPassword);
            this.httpServerOptions.tls(tls);
        }

        // 添加一个 websocket 处理器
        this.httpServerOptions.addUpgradeRequestFactory(new WebSocketUpgradeRequestFactory());

        this.httpServer = new HttpServer(this.httpServerOptions)
            .onRequest(this.router)
            .onError(new DefaultHttpServerErrorHandler(useDevelopmentErrorPage));

        var port = environment.get("scx.http.port", Integer.class, 8888);

        // 打印路由信息.
        printRouteInfo(this.router);

        this.httpServer.start(port);

        // 打印服务器地址
        printServerAddresses(this.httpServer.localAddress(), this.httpServerOptions.tls() != null);
    }

    @Override
    public void stop(ScxApp scxApp) {
        if (this.httpServer != null) {
            this.httpServer.stop();
        }
    }

    /// 暴露 httpServer 允许外部访问
    public ScxHttpServer httpServer() {
        if (httpServer == null) {
            throw new IllegalStateException(name() + " has not started");
        }
        return httpServer;
    }

    /// 暴露 router 允许外部访问
    public Router router() {
        return router;
    }

}
