package dev.scx.app.x.scheduling;

import dev.scx.ansi.Ansi;
import dev.scx.app.ScxApp;
import dev.scx.app.ScxAppModule;
import dev.scx.function.Function1Void;
import dev.scx.reflect.ClassInfo;
import dev.scx.scheduling.ScheduleHandle;
import dev.scx.scheduling.ScxScheduling;
import dev.scx.scheduling.TaskContext;

import java.util.ArrayList;
import java.util.List;

import static dev.scx.reflect.AccessModifier.PUBLIC;

/// ScxAppSchedulingModule
///
/// @author scx567888
/// @version 0.0.1
public final class ScxAppSchedulingModule implements ScxAppModule {

    private final List<ScheduleHandle> scheduleHandleList;

    public ScxAppSchedulingModule() {
        this.scheduleHandleList = new ArrayList<>();
    }

    @Override
    public void start(ScxApp scxApp) throws Exception {
        var componentContainer = scxApp.componentContainer();
        var componentDefinitions = componentContainer.componentDefinitions().values();

        root:
        for (var componentDefinition : componentDefinitions) {
            var component = componentContainer.getComponent(componentDefinition.componentName());
            var typeInfo = componentDefinition.componentType();

            // 只处理 classInfo
            if (!(typeInfo instanceof ClassInfo classInfo)) {
                continue root;
            }

            methods:
            for (var method : classInfo.methods()) {
                // 收集注解
                var scheduleds = method.findAnnotations(Scheduled.class);

                // 没注解跳过
                if (scheduleds.length == 0) {
                    continue methods;
                }

                // 只处理 public 方法
                if (method.accessModifier() != PUBLIC) {
                    throw new IllegalArgumentException("被 Scheduled 注解标识的方法 必须是 public : " + method.declaringClass().name() + "#" + method.signature());
                }

                if (method.isStatic()) {
                    throw new IllegalArgumentException("被 Scheduled 注解标识的方法 不能是 static : " + method.declaringClass().name() + "#" + method.signature());
                }

                if (method.parameters().length != 0) {
                    throw new IllegalArgumentException("被 Scheduled 注解标识的方法不可以有参数 : " + method.declaringClass().name() + "#" + method.signature());
                }

                Function1Void<TaskContext, ?> task = c -> method.invoke(component);

                for (var scheduled : scheduleds) {
                    var handle = ScxScheduling.cron()
                        .cronExpression(scheduled.cron())
                        .start(task);
                    this.scheduleHandleList.add(handle);
                }

            }
        }

        Ansi.ansi()
            .brightBlue("已注册 " + scheduleHandleList.size() + " 个 Scheduled Task !!!")
            .println();
    }

    @Override
    public void stop(ScxApp scxApp) {
        for (var handle : scheduleHandleList) {
            handle.cancel();
        }
        scheduleHandleList.clear();
    }

}
