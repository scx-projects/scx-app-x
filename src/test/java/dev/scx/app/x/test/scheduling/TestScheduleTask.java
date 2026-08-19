package dev.scx.app.x.test.scheduling;

import dev.scx.app.x.component.Component;
import dev.scx.app.x.scheduling.Scheduled;

import java.lang.System.Logger;

import static java.lang.System.Logger.Level.WARNING;

@Component
public class TestScheduleTask {

    private static final Logger logger = System.getLogger(TestScheduleTask.class.getName());

    @Scheduled(cron = "*/1 * * * * ?")
    public void oneSecondTasks() {
        logger.log(WARNING, "这是 通过注解的 定时任务打印的 !!!");
    }

}
