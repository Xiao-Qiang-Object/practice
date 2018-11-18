 package wsq.study.quartz.config;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import wsq.study.quartz.scheduler.QuartzScheduler;


 /**
 * 注册[pring-boot]启动完成事件监听，用于启动job任务
 * 
 * @author xiaojp001
 * @date 2018/05/08
 */
@Configuration
public class SchedulerListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    public QuartzScheduler quartzScheduler;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            quartzScheduler.scheduleJobs();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
