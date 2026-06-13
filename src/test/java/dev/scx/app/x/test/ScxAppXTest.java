package dev.scx.app.x.test;

import dev.scx.app.ScxApp;
import dev.scx.app.x.auto_scan.ScxAutoScanModule;
import dev.scx.app.x.component.ScxAppComponentModule;
import dev.scx.app.x.cors.ScxAppCorsModule;
import dev.scx.app.x.fix_table.ScxAppFixTableModule;
import dev.scx.app.x.http.ScxAppHttpModule;
import dev.scx.app.x.logging.ScxAppLoggingModule;
import dev.scx.app.x.scheduling.ScxAppSchedulingModule;
import dev.scx.app.x.sql.ScxAppSQLModule;
import dev.scx.app.x.static_server.ScxAppStaticServerModule;
import dev.scx.app.x.test.base.BaseEntityService;
import dev.scx.app.x.test.car.Car;
import dev.scx.app.x.test.car.CarColor;
import dev.scx.app.x.test.car.CarOwner;
import dev.scx.app.x.test.car.CarService;
import dev.scx.app.x.test.like.Like;
import dev.scx.app.x.test.like.LikeService;
import dev.scx.app.x.test.like.Order;
import dev.scx.app.x.test.person.Person;
import dev.scx.app.x.test.person.PersonService;
import dev.scx.app.x.web.ScxAppWebModule;
import dev.scx.http.media.multi_part.MultiPart;
import dev.scx.http.uri.ScxURI;
import dev.scx.http.x.HttpClient;
import dev.scx.random.ScxRandom;
import dev.scx.sql.SQLClient;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static dev.scx.data.field_policy.FieldPolicyBuilder.*;
import static dev.scx.data.query.BuildControl.USE_EXPRESSION;
import static dev.scx.data.query.QueryBuilder.*;
import static dev.scx.http.method.HttpMethod.POST;
import static dev.scx.random.ScxRandom.NUMBER_AND_LETTER;
import static dev.scx.serialize.ScxSerialize.toJson;
import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.ERROR;

public class ScxAppXTest extends ScxAutoScanModule {

    private static final System.Logger logger = System.getLogger(ScxAppXTest.class.getName());

    private static ScxApp SCX_APP;

    public static void main(String[] args) throws Exception {
        beforeTest();
        test1();
        test2();
        test3();
        test4();
    }

    @BeforeTest
    public static void beforeTest() throws Exception {
        // 模拟外部参数
        var args = new String[]{"--scx.http.port=8888", "--scx.fix-table.enabled=true"};

        SCX_APP = ScxApp.builder()
            .module(new ScxAppLoggingModule())
            .module(new ScxAppComponentModule())
            .module(new ScxAppHttpModule())
            .module(new ScxAppStaticServerModule())
            .module(new ScxAppWebModule())
            .module(new ScxAppSchedulingModule())
            .module(new ScxAppCorsModule())
            .module(new ScxAppSQLModule())
            .module(new ScxAppFixTableModule())
            .module(new ScxAppXTest())
            .mainClass(ScxAppXTest.class)
            .args(args)
            .run();

    }

    @Test
    public static void test1() throws IOException {
        var logger = System.getLogger(ScxAppXTest.class.getName());
        var httpClient = new HttpClient();
        // 测试 httpClient
        for (int i = 0; i < 10; i = i + 1) {
            var s = "http://127.0.0.1:8888/test0";
            var stringHttpResponse = httpClient.request()
                .method(POST)
                .uri(
                    ScxURI.of(s)
                        .addQuery("name", "小明😊123?!@%^&**()_特-殊 字=符")
                        .addQuery("age", 18)
                )
                .send(
                    MultiPart.of()
                        .add("content", "内容内容内容内容内容".getBytes(StandardCharsets.UTF_8))
                        .add("content1", "内容2内容2内容2内容2😂😂😂!!!".getBytes(StandardCharsets.UTF_8))
                );
            logger.log(DEBUG, "测试请求[{0}] : {1}", i, stringHttpResponse.asString());
        }
    }

    @Test
    public static void test2() {
        // 测试使用关键字 作为表名和列名
        var likeService = SCX_APP.getComponent(LikeService.class);
        var z = new Like();
        z.order = new Order();
        z.order.where = "123";
        var a = likeService.add(z);
        var b = likeService.update(a);
        var c = likeService.find(eq("JSON_EXTRACT(`order`, '$.where')", "123", USE_EXPRESSION));
        var d = likeService.delete(b.id);
        logger.log(DEBUG, toJson(c));
    }


    @Test
    public static void test3() {
        var carService = SCX_APP.getComponent(CarService.class);
        var carService1 = new BaseEntityService<>(Car.class, SCX_APP.getComponent(SQLClient.class));
        // 纯表达式插入
        var car = carService.add(assignField("name", "RAND()"));
        try {
            if (carService1.count() < 1500) {
                logger.log(DEBUG, "开始: 方式1 (批量) 插入");
                // 插入数据 方式1
                var s1 = System.nanoTime();
                var l = new ArrayList<Car>();
                for (int i = 0; i < 99; i = i + 1) {
                    var c = new Car();
                    c.name = ScxRandom.randomString(10, NUMBER_AND_LETTER) + "🤣";
                    c.color = CarColor.values()[ScxRandom.randomInt(4)];
                    c.owner = new CarOwner("Jack", i, new String[]{"123456789", "666666666"});
                    c.tags = new String[]{"fast", "beautiful", "small", "big"};
                    l.add(c);
                }
                carService.add(l);
                logger.log(DEBUG, "完成: 方式1 (批量) 插入 99条数据时间 :" + (System.nanoTime() - s1) / 1000_000);

                logger.log(DEBUG, "开始: 方式2 (循环单次) 插入");
                //插入数据 方式2
                var s2 = System.nanoTime();
                for (int i = 0; i < 99; i = i + 1) {
                    var c = new Car();
                    c.name = ScxRandom.randomString(10, NUMBER_AND_LETTER) + "😢";
                    c.color = CarColor.values()[ScxRandom.randomInt(4)];
                    c.owner = new CarOwner("David", i, new String[]{"987654321"});
                    carService1.add(c);
                }
                logger.log(DEBUG, "方式2 (循环单次) 插入 99条数据时间 :" + (System.nanoTime() - s2) / 1000_000);
            }

            logger.log(DEBUG, "将 id 大于 200 的 name 设置为空 !!!");
            // 方式1
            var c = new Car();
            c.name = null;
            carService.update(c, include("name").ignoreNull(false), gt("id", 200));

            // 方式2
            carService.update(ignoreNull("name", false), gt("id", 200));

            // 方式3
            carService.update(assignField("name", "NULL"), gt("id", 200));

            logger.log(DEBUG, "查询所有数据条数 !!! : " + carService.find().size());
            logger.log(DEBUG, "查询所有 id 大于 200 条数 !!! : " + carService.find(gt("id", 200)).size());
            logger.log(DEBUG, "查询所有 name 为空 条数 !!! : " + carService.find(eq("name", null)).size());
            logger.log(DEBUG, "查询所有 车主为 Jack 的条数 !!! : " + carService.find(eq("JSON_EXTRACT(owner,'$.name')", "Jack", USE_EXPRESSION)).size());
            logger.log(DEBUG, "查询所有 车主年龄大于 18 的条数 !!! : " + carService.find(gt("JSON_EXTRACT(owner,'$.age')", 18, USE_EXPRESSION)).size());
            logger.log(DEBUG, "查询所有 拥有 fast 和 big 标签的条数 !!! : " + carService.find(whereClause("JSON_CONTAINS(tags, ?)", "[\"fast\",\"big\"]")).size());
            logger.log(DEBUG, "查询所有 汽车 中 车主 的 电话号 中 包含 666666666 的条数 !!! : " + carService.find(whereClause("JSON_CONTAINS(owner,?,'$.phoneNumber')", "[\"666666666\"]")).size());

            logger.log(DEBUG, "------------------------- 测试事务 --------------------------------");
            // 测试事务
            // 插入数据 方式2
            logger.log(DEBUG, "事务开始前数据库中 数据条数 : " + carService.count());

            carService.sqlClient().autoTransaction(() -> {
                logger.log(DEBUG, "现在插入 1 数据条数");
                var bb = new Car();
                bb.name = "唯一ID";
                bb.color = CarColor.values()[ScxRandom.randomInt(4)];
                carService.add(bb);
                logger.log(DEBUG, "现在数据库中数据条数 : " + carService.count());
                logger.log(DEBUG, "现在在插入 1 错误数据");
                carService.add(bb);
            });
        } catch (Exception e) {
            logger.log(ERROR, "出错了 后滚后数据库中数据条数 : " + carService.count());
        }
        // 测试虚拟字段
        carService.update(assignField("name", "REVERSE(name)"), whereClause("1 = 1"));
        var list = carService.find(assignField("reverseName", "REVERSE(name)"));
        logger.log(DEBUG, list.get(0).reverseName);

    }

    @Test
    public static void test4() {
        var personService = SCX_APP.getComponent(PersonService.class);
        var carService = SCX_APP.getComponent(CarService.class);
        if (personService.count() < 200) {
            List<Car> list = carService.find();
            var ps = new ArrayList<Person>();
            for (int i = 0; i < list.size(); i = i + 1) {
                var p = new Person();
                p.carID = list.get(i).id;
                p.age = i;
                ps.add(p);
            }
            personService.add(ps);
        }
        // 根据所有 person 表中年龄小于 100 的 carID 查询 car 表中的数据
        var cars = carService.find(
            in(
                "id",
                personService.buildListSQL(lt("age", 100), include("carID"))
            )
        );
        var logger = System.getLogger(ScxAppXTest.class.getName());
        logger.log(DEBUG, "根据所有 person 表中年龄小于 100 的 carID 查询 car 表中的数据 总条数 {0}", cars.size());
        // 根据所有 person 表中年龄小于 100 的 carID 查询 car 表中的数据
        var cars1 = carService.find(
            in(
                "id",
                personService.buildListSQL(lt("age", 100), include("carID"))
            )
        );
        logger.log(DEBUG, "第二种方式 (whereSQL) : 根据所有 person 表中年龄小于 100 的 carID 查询 car 表中的数据 总条数 {0}", cars1.size());
    }

}
