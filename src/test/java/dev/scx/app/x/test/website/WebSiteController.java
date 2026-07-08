package dev.scx.app.x.test.website;

import dev.scx.app.x.test.car.CarService;
import dev.scx.app.x.test.person.Person;
import dev.scx.app.x.test.person.PersonService;
import dev.scx.di.annotation.Inject;
import dev.scx.digest.ScxDigest;
import dev.scx.http.media.multi_part.MultiPartPart;
import dev.scx.http.method.HttpMethod;
import dev.scx.http.routing.RoutingContext;
import dev.scx.random.ScxRandom;
import dev.scx.web.ScxWeb;
import dev.scx.web.annotation.Part;
import dev.scx.web.annotation.QueryParam;
import dev.scx.web.annotation.Route;
import dev.scx.web.annotation.Routes;
import dev.scx.web.result.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static dev.scx.io.ScxIO.byteOutputToOutputStream;
import static dev.scx.random.ScxRandom.NUMBER_AND_LETTER;
import static java.lang.System.Logger.Level.DEBUG;

/// 简单测试
///
/// @author scx567888
@Routes
public class WebSiteController {

    private static final System.Logger logger = System.getLogger(WebSiteController.class.getName());

    final CarService carService;

    @Inject
    public CarService carService1;

    @Inject
    public PersonService personService;

    public WebSiteController(CarService carService) {
        this.carService = carService;
    }

    @Route(value = "/test0", methods = HttpMethod.POST)
    public Object test0(@QueryParam String name,
                        @QueryParam Integer age,
                        @Part MultiPartPart content,
                        @Part MultiPartPart content1) {
        var request = ScxWeb.routingContext().request();
        var remoteAddressStr = request.remotePeer().address().toString();
        logger.log(DEBUG, "客户端 IP :" + remoteAddressStr);
        return Map.of("now", LocalDateTime.now(),
            "name", name,
            "age", age,
            "content", content.asString(),
            "content1", content1.asString());
    }

    @Route(value = "/test-transaction", methods = HttpMethod.GET)
    public Html testTransaction(RoutingContext ctx) throws Exception {
        var p1 = personService.add(new Person().setMoney(100));
        var p2 = personService.add(new Person().setMoney(200));
        var sb = new StringBuilder();
        sb.append("转账开始前: ").append("p1(").append(p1.money).append(") p2(").append(p2.money).append(")</br>");
        try {
            //模拟 转账
            personService.sqlClient().autoTransaction(() -> {

                //给 p1 扣钱
                p1.money = p1.money - 50;
                var p11 = personService.update(p1);

                //给 p2 加钱
                p2.money = p2.money + 50;
                var p21 = personService.update(p2);

                sb.append("转账中: ").append("p1(")
                    .append(p11.money)
                    .append(") p2(")
                    .append(p21.money).append(")</br>");

                throw new RuntimeException("模拟发生异常 !!!");
            });
        } catch (Exception e) {
            var p11 = personService.get(p1.id);
            var p21 = personService.get(p2.id);
            sb.append("出错了 回滚后: ").append("p1(").append(p11.money).append(") p2(").append(p21.money).append(")</br>");
        }
        return Html.of(sb.toString());
    }

    /// 多个路由
    @Route(methods = HttpMethod.GET, priority = 5)
    public void any(RoutingContext c) throws Throwable {
        logger.log(DEBUG, "永远匹配的路由" + c.request().path());
        c.data().put("name", "小明");
        c.next();
    }

    /// 测试!!!
    @Route(value = "/download", methods = HttpMethod.GET)
    public Binary download() {
        var s = new StringBuilder();
        for (int i = 0; i < 9999; i = i + 1) {
            s.append("这是文字 ").append(i).append(", ");
        }
        return Binary.download(s.toString().getBytes(StandardCharsets.UTF_8), "测试中 + - ~!文 a😊😂 🤣 ghj ❤😍😒👌.txt");
    }

    /// 测试!!!
    @Route(value = "/raw", methods = HttpMethod.GET)
    public String raw() {
        var s = new StringBuilder();
        for (int i = 0; i < 9999; i = i + 1) {
            s.append("这是文字 ").append(i).append(", ");
        }
        return s.toString();
    }

    /// 测试!!!
    @Route(value = "/md5", methods = HttpMethod.GET)
    public String md5() {
        return ScxDigest.md5Hex("123");
    }

    /// 测试!!!
    @Route(value = "/get-random-code", methods = HttpMethod.GET)
    public String getRandomCode() {
        return ScxRandom.randomString(9999, NUMBER_AND_LETTER);
    }

    /// 测试 json
    @Route(value = "/big-json", methods = HttpMethod.GET)
    public WebResult bigJson() {
        var users = carService1.find();
        return Json.of(users);
    }

    /// 测试 xml
    @Route(value = "/big-xml", methods = HttpMethod.GET)
    public WebResult bigXml() {
        var users = carService1.find();
        return Xml.of(users);
    }

    /// 测试 ZIP
    @Route(value = "/zip", methods = HttpMethod.GET)
    public Binary zip() throws Exception {
        // 大型文件请使用这种方法下载
        return Binary.download(byteOutput -> {
            try (var zos = new ZipOutputStream(byteOutputToOutputStream(byteOutput))) {
                zos.setLevel(0);

                zos.putNextEntry(new ZipEntry("第一个目录/第二个目录/第二个目录中的文件.txt"));
                zos.write("文件内容".getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();

                zos.putNextEntry(new ZipEntry("第一个目录/这是一系列空目录/这是一系列空目录/这是一系列空目录/这是一系列空目录/这是一系列空目录"));
                zos.closeEntry();

                zos.putNextEntry(new ZipEntry("第一个目录/这不是一系列空目录/这不是一系列空目录/这不是一系列空目录/这不是一系列空目录/这不是一系列空目录"));
                zos.closeEntry();

                zos.putNextEntry(new ZipEntry("第一个目录/这不是一系列空目录/这不是一系列空目录/这不是一系列空目录/这不是一系列空目录/这不是一系列空目录/一个文本文件.txt"));
                zos.write("一些内容,一些内容,一些内容,一些内容 下😊😂🤣❤😍😒👌😘".getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();

            }
        }, "测试压缩包.zip");

    }

}
