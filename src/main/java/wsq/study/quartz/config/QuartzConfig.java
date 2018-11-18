package wsq.study.quartz.config;

import java.io.IOException;
import java.util.Properties;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import wsq.study.quartz.QuartzJobFactory;


/**
 * 配置任务调度中心 [QRTZ_JOB_DETAILS], [QRTZ_TRIGGERS] and [QRTZ_CRON_TRIGGERS]
 *
 * 应用启动时自动加载job
 *
 * @author lance
 */
@Configuration
public class QuartzConfig {

    @Autowired
    private QuartzJobFactory quartzJobFactory;

    @Value("${jdbc_url}")
    private String jdbcUrl;

    @Value("${jdbc_user}")
    private String jdbcUser;

    @Value("${jdbc_password}")
    private String jdbcpwd;

    @Value("${auto_startup}")
    private String autoStartup;

    public QuartzConfig() {
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        // 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        // 延时启动(秒)
        schedulerFactoryBean.setStartupDelay(10);
        // 设置quartz的配置文件
        schedulerFactoryBean.setQuartzProperties(quartzProperties());
        // 设置自定义Job Factory，用于Spring管理Job bean
        schedulerFactoryBean.setJobFactory(quartzJobFactory);

        schedulerFactoryBean.setAutoStartup(Boolean.valueOf(autoStartup));
        return schedulerFactoryBean;
    }

    @Bean
    public Scheduler scheduler() throws IOException {
        return schedulerFactoryBean().getScheduler();
    }

    /**
     * 设置quartz属性
     *
     * @throws IOException 2016年10月8日下午2:39:05
     */
    @Bean
    public Properties quartzProperties() throws IOException {
        Properties prop = new Properties();
        prop.put("quartz.scheduler.instanceName", "ServerScheduler");
        prop.put("org.quartz.scheduler.instanceId", "AUTO");
        // prop.put("org.quartz.scheduler.instanceId", "NON_CLUSTERED");

        prop.put("org.quartz.scheduler.skipUpdateCheck", "true");
        prop.put("org.quartz.scheduler.jobFactory.class", "org.quartz.simpl.SimpleJobFactory");
        prop.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        prop.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        prop.put("org.quartz.jobStore.dataSource", "quartzDataSource");
        prop.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        prop.put("org.quartz.jobStore.isClustered", "true");
        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        // 多线程
        prop.put("org.quartz.threadPool.threadCount", "3");

        prop.put("org.quartz.dataSource.quartzDataSource.driver", "com.mysql.jdbc.Driver");
        prop.put("org.quartz.dataSource.quartzDataSource.URL", jdbcUrl);
        prop.put("org.quartz.dataSource.quartzDataSource.user", jdbcUser);
        prop.put("org.quartz.dataSource.quartzDataSource.password", jdbcpwd);
        prop.put("org.quartz.dataSource.quartzDataSource.maxConnections", "10");
        return prop;
    }
}