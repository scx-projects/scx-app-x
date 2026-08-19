package dev.scx.app.x.config.code_source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/// ScxCodeSourceHelper
///
/// @author scx567888
final class ScxCodeSourceHelper {

    /// 读取 jar 包中的所有 class
    public static List<Class<?>> loadClassesFromJar(Path jarFilePath, ClassLoader classLoader) throws IOException, ClassNotFoundException {
        try (var jarFile = new JarFile(jarFilePath.toFile())) {
            // 先获取所有 候选的 JarEntry
            var jarEntries = jarFile.stream()
                .filter(jarEntry -> !jarEntry.isDirectory())
                .filter(jarEntry -> jarEntry.getName().endsWith(".class"))
                .toList();

            // 转换成 classes
            var classes = new ArrayList<Class<?>>();
            for (var jarEntry : jarEntries) {
                var clazz = loadClassFromJar(jarEntry, classLoader);
                classes.add(clazz);
            }
            return classes;
        }
    }

    /// 根据文件获取 class 列表
    public static List<Class<?>> loadClassesFromPath(Path classRootPath, ClassLoader classLoader) throws IOException, ClassNotFoundException {
        try (var pathStream = Files.walk(classRootPath)) {
            // 先获取所有 候选的 Path
            var paths = pathStream
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".class"))
                .toList();

            // 转换成 classes
            var classes = new ArrayList<Class<?>>();
            for (var path : paths) {
                var clazz = loadClassFromPath(classRootPath.relativize(path), classLoader);
                classes.add(clazz);
            }
            return classes;
        }
    }

    /// 从 JarEntry 加载 class
    private static Class<?> loadClassFromJar(JarEntry jarEntry, ClassLoader classLoader) throws ClassNotFoundException {
        // 1, 获取原始名称
        var jarEntryName = jarEntry.getName();
        // 2, 将 / 转成 .
        var tempName = jarEntryName.replace('/', '.');
        // 3, 移除尾部 .class , 这里是可以保证 path 最后一定是 .class 所以在此处可以放心移除
        var className = tempName.substring(0, tempName.length() - ".class".length());
        // 4, 加载 class
        return loadClass0(className, classLoader);
    }

    /// 从 Path 加载 class
    private static Class<?> loadClassFromPath(Path path, ClassLoader classLoader) throws ClassNotFoundException {
        // 1, 遍历 path 拼接每一段.
        var sj = new StringJoiner(".");
        for (var p : path) {
            sj.add(p.toString());
        }
        // 2, 获取名称
        var tempName = sj.toString();
        // 3, 移除尾部 .class , 这里是可以保证 path 最后一定是 .class 所以在此处可以放心移除
        var className = tempName.substring(0, tempName.length() - ".class".length());
        // 4, 加载 class
        return loadClass0(className, classLoader);
    }

    private static Class<?> loadClass0(String className, ClassLoader classLoader) throws ClassNotFoundException {
        // 这里我们直接 loadClass, 但不要触发初始化
        return classLoader.loadClass(className);
    }

}
