package dev.scx.app.x.auto_scan;

import dev.scx.app.code_source.ScxCodeSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/// ScxAutoScanModuleHelper
///
/// @author scx567888
/// @version 0.0.1
public final class ScxAutoScanModuleHelper {

    /// 根据 baseClass 的包路径 查找 所有候选 class
    /// 这里返回的 是可变的 list
    public static List<Class<?>> findCandidatesByBaseClass(Class<?> baseClass) throws IOException, ClassNotFoundException {
        var classes = ScxCodeSource.of(baseClass).loadClasses();
        var basePackageName = baseClass.getPackageName();
        var p = basePackageName + ".";
        return classes.stream()
            .filter(c -> c.getPackageName().equals(basePackageName) || c.getPackageName().startsWith(p))
            .collect(Collectors.toCollection(ArrayList::new));
    }

}
