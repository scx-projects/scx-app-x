package dev.scx.app.x.scheduling;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// [Scheduled] 的重复注解容器.
///
/// @author scx567888
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ScheduledList {

    Scheduled[] value();

}
