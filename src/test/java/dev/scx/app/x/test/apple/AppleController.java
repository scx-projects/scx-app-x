package dev.scx.app.x.test.apple;

import dev.scx.app.x.test.base.BaseEntityService;
import dev.scx.web.annotation.Body;
import dev.scx.web.annotation.PathCapture;
import dev.scx.web.annotation.Route;
import dev.scx.web.annotation.Routes;

import static dev.scx.http.method.HttpMethod.*;

@Routes("/api/apple")
public class AppleController {

    protected final BaseEntityService<Apple> service;

    // 测试泛型注入
    public AppleController(BaseEntityService<Apple> service) {
        this.service = service;
    }

    // 查询全部 Apple
    @Route(value = "", methods = GET)
    public Object list() {
        return service.find();
    }

    // 根据 ID 查询单条 Apple
    @Route(value = "/:id", methods = GET)
    public Object get(@PathCapture Long id) {
        return service.get(id);
    }

    // 新增 Apple
    @Route(value = "", methods = POST)
    public Object create(@Body Apple apple) {
        return service.add(apple);
    }

    // 更新 Apple
    @Route(value = "/:id", methods = PUT)
    public Object update(@PathCapture Long id, @Body Apple apple) {
        apple.id = id;
        return service.update(apple);
    }

    // 删除 Apple
    @Route(value = "/:id", methods = DELETE)
    public Object delete(@PathCapture Long id) {
        return service.delete(id);
    }

}
