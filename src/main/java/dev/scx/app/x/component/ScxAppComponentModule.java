package dev.scx.app.x.component;

import dev.scx.app.ScxApp;
import dev.scx.app.ScxAppModule;
import dev.scx.app.ScxAppModuleDefinition;
import dev.scx.app.x.di.ScxAppDIModule;

/// ScxAppComponentModule
///
/// @author scx567888
public final class ScxAppComponentModule implements ScxAppModule {

    @Override
    public ScxAppModuleDefinition define() {
        return ScxAppModuleDefinition.of()
            .require(ScxAppDIModule.class)
            .startBefore(ScxAppDIModule.class);
    }

    @Override
    public void start(ScxApp scxApp) throws Exception {
        var diModule = scxApp.getModule(ScxAppDIModule.class);

        var componentContainerBuilder = diModule.componentContainerBuilder();

        for (var c : scxApp.candidates()) {
            if (c.getAnnotation(Component.class) != null) {
                componentContainerBuilder.registerComponentType(c.getName(), c);
            }
        }
    }

}
