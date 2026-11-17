package dev.scx.app.x.scheduling;

import java.lang.annotation.*;

/// 调度器注解
///
/// 目前仅支持 cron 形式.
///
/// @author scx567888
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ScheduledList.class)
public @interface Scheduled {

    String cron();

}
