package dev.scx.app.x.auto_scan;

import dev.scx.app.ScxAppModule;
import dev.scx.app.ScxAppModuleDefinition;
import dev.scx.app.environment.ScxEnvironment;

import java.io.IOException;
import java.util.List;

import static dev.scx.app.x.auto_scan.ScxAutoScanModuleHelper.findCandidatesByBaseClass;

/// ScxAutoScanModule
///
/// 默认会扫描当前模块类所在包及其子包下的所有类,
/// 并将这些类加入 ScxApp candidates.
///
/// @author scx567888
public abstract class ScxAutoScanModule implements ScxAppModule {

    /// 配置 AutoScan 扫描到的候选类
    ///
    /// 这里传入的是可变 List.
    /// 子类可以原地 remove / removeIf / add,
    /// 也可以返回一个新的 List.
    protected List<Class<?>> configureCandidates(List<Class<?>> candidates) {
        return candidates;
    }

    @Override
    public ScxAppModuleDefinition init(ScxEnvironment environment) throws IOException, ClassNotFoundException {
        var candidates = findCandidatesByBaseClass(this.getClass());

        // 允许用户配置最终的 candidates
        candidates = configureCandidates(candidates);

        // 注入
        return ScxAppModuleDefinition.of()
            .candidate(candidates.toArray(Class<?>[]::new));
    }

}
