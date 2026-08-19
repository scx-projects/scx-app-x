package dev.scx.app.x.config.code_source;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static dev.scx.app.x.config.code_source.ScxCodeSourceHelper.loadClassesFromJar;
import static dev.scx.app.x.config.code_source.ScxCodeSourceHelper.loadClassesFromPath;

/// ScxCodeSourceImpl
///
/// @author scx567888
final class ScxCodeSourceImpl implements ScxCodeSource {

    private final Class<?> clazz;
    private final URI uri;
    private final Path path;
    private final boolean isJar;
    private final Path baseDirectory;

    public ScxCodeSourceImpl(Class<?> clazz) {
        this.clazz = clazz;
        this.uri = URI.create(clazz.getProtectionDomain().getCodeSource().getLocation().toString());
        this.path = Path.of(this.uri).toAbsolutePath().normalize();
        this.isJar = Files.isRegularFile(this.path) && this.path.toString().toLowerCase().endsWith(".jar");
        this.baseDirectory = this.isJar ? this.path.getParent() : this.path;
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public Path path() {
        return path;
    }

    @Override
    public boolean isJar() {
        return isJar;
    }

    @Override
    public Path baseDirectory() {
        return baseDirectory;
    }

    @Override
    public List<Class<?>> loadClasses() throws IOException, ClassNotFoundException {
        // 判断当前是否处于 jar 包中 并使用不同的 方式加载
        return isJar ? loadClassesFromJar(path, clazz.getClassLoader()) : loadClassesFromPath(path, clazz.getClassLoader());
    }

}
