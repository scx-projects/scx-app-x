package dev.scx.app.x.di;

import dev.scx.ansi.Ansi;
import dev.scx.app.ScxApp;
import dev.scx.app.ScxAppModule;
import dev.scx.app.ScxAppModuleDefinition;
import dev.scx.app.x.config.ScxAppConfigModule;
import dev.scx.di.ComponentContainer;
import dev.scx.di.ComponentContainerBuilder;
import dev.scx.di.dependency_resolver.InjectAnnotationDependencyResolver;
import dev.scx.di.dependency_resolver.ValueAnnotationDependencyResolver;

/// ScxAppDIModule
///
/// @author scx567888
public final class ScxAppDIModule implements ScxAppModule {

    private ComponentContainerBuilder componentContainerBuilder;
    private ComponentContainer componentContainer;

    public ScxAppDIModule() {
        this.componentContainerBuilder = ComponentContainer.builder()
            .addDependencyResolver(new InjectAnnotationDependencyResolver());
        this.componentContainer = null;
    }

    @Override
    public ScxAppModuleDefinition define() throws Exception {
        return ScxAppModuleDefinition.of()
            .startAfter(ScxAppConfigModule.class);
    }

    @Override
    public void start(ScxApp scxApp) throws Exception {
        var configModule = scxApp.getModule(ScxAppConfigModule.class);

        // 如果 Config 模块存在, 添加 @Value 依赖解析器, 同时把 ScxEnvironment 作为实例注入到组件容器中
        if (configModule != null) {
            var environment = configModule.environment();
            // 添加为值解析器
            componentContainerBuilder.addDependencyResolver(new ValueAnnotationDependencyResolver(environment::get));
            // 注册 ScxEnvironment
            componentContainerBuilder.registerComponent(environment.getClass().getName(), environment);
        }

        // 1, 构建 DI 容器.
        this.componentContainer = componentContainerBuilder.build();

        // 2, 验证 DI 容器
        this.componentContainer.verifyComponents();

        // 3, 打印信息
        Ansi.ansi()
            .brightYellow("已加载 " + componentContainer.componentDefinitions().size() + " 个 Component !!!")
            .println();

        // 4, 全部 成功之后销毁 builder
        this.componentContainerBuilder = null;
    }

    public ComponentContainerBuilder componentContainerBuilder() {
        if (componentContainerBuilder == null) {
            throw new IllegalStateException(name() + " has already started");
        }
        return componentContainerBuilder;
    }

    public ComponentContainer componentContainer() {
        if (componentContainer == null) {
            throw new IllegalStateException(name() + " has not started");
        }
        return componentContainer;
    }

}
