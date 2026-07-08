package dev.scx.app.x.component;

import dev.scx.ansi.Ansi;
import dev.scx.app.ScxApp;
import dev.scx.app.ScxAppModule;
import dev.scx.app.ScxAppModuleDefinition;
import dev.scx.app.environment.ScxEnvironment;

/// ScxAppComponentModule
///
/// @author scx567888
public final class ScxAppComponentModule implements ScxAppModule {

    @Override
    public ScxAppModuleDefinition init(ScxEnvironment environment) {
        return ScxAppModuleDefinition.of()
            .componentSelector(c -> c.getAnnotation(Component.class) != null);
    }

    @Override
    public void start(ScxApp scxApp) throws Exception {
        Ansi.ansi()
            .brightYellow("已加载 " + scxApp.componentContainer().componentDefinitions().size() + " 个 Component !!!")
            .println();
    }

}
