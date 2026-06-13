package dev.scx.app.x.test.ws;

import dev.scx.app.x.test.website.WebSiteController;
import dev.scx.scheduling.ScxScheduling;
import dev.scx.web.annotation.Route;
import dev.scx.web.annotation.Routes;
import dev.scx.websocket.event.ScxEventWebSocket;
import dev.scx.websocket.x.ScxServerWebSocketHandshakeRequest;

import java.time.LocalDateTime;

import static dev.scx.web.annotation.Route.RouteKind.WEBSOCKET_UPGRADE;
import static java.lang.System.Logger.Level.DEBUG;

@Routes("/now")
public class WsHandler {

    private static final System.Logger logger = System.getLogger(WebSiteController.class.getName());

    @Route(kind = WEBSOCKET_UPGRADE)
    public void onHandshakeRequest(ScxServerWebSocketHandshakeRequest request) throws Exception {
        var context = ScxEventWebSocket.of(request.upgrade());
        logger.log(DEBUG, "连接了");
        var scheduleContext = ScxScheduling.setInterval(() -> {
            logger.log(DEBUG, "发送消息");
            context.send(LocalDateTime.now().toString());
        }, 500);
        context.onText(c -> {
            logger.log(DEBUG, "收到消息 : " + c + " ");
        });
        context.onClose(c -> {
            logger.log(DEBUG, "Close " + c.code() + " " + c.reason());
            scheduleContext.cancel();
        });
        context.onError(error -> {
            logger.log(DEBUG, "Error " + error);
            scheduleContext.cancel();
        });
        context.start();
    }

}
