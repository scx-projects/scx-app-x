package dev.scx.app.x.config.code_source;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;

/// 表示一个 Class 所属的 JVM CodeSource.
///
/// ScxCodeSource 是 SCX 对 JVM CodeSource 的封装.
/// 它表示某个 Class 所在的代码来源, 通常是开发环境中的 classes 目录,
/// 或发布环境中的 jar 文件.
///
/// 注意:
/// 这里的 Source 不是 Java 源码文件, 而是 JVM 视角下的代码来源.
/// 当前抽象默认 CodeSource 可以映射到本地文件系统 Path.
///
/// @author scx567888
public interface ScxCodeSource {

    static ScxCodeSource of(Class<?> clazz) {
        return new ScxCodeSourceImpl(clazz);
    }

    /// 返回 JVM CodeSource 的原始 URI.
    ///
    /// 这是从 Class 的 ProtectionDomain / CodeSource 推导出的原始位置.
    ///
    /// 在开发环境中通常指向 classes 目录.
    /// 在发布环境中通常指向 jar 文件.
    URI uri();

    /// 返回 uri() 对应的本地文件系统 Path.
    ///
    /// 这是 SCX 将 CodeSource URI 解释成本地 Path 后的结果,
    /// 主要用于文件系统操作, 例如判断目录、判断 jar、扫描 class、计算 baseDirectory.
    Path path();

    /// 判断 path() 是否是一个 jar 文件.
    ///
    /// 注意: 当前实现只是简单使用文件后缀判断, 并不保证该文件一定是合法 jar.
    boolean isJar();

    /// 返回 CodeSource 对应的外部文件基准目录.
    ///
    /// 当 CodeSource 是目录时, 该目录本身就是基准目录.
    /// 当 CodeSource 是 jar 文件时, jar 文件本身只是代码载体, 基准目录是它所在的父目录.
    Path baseDirectory();

    /// 加载当前 CodeSource 中的所有 Class.
    List<Class<?>> loadClasses() throws IOException, ClassNotFoundException;

}
