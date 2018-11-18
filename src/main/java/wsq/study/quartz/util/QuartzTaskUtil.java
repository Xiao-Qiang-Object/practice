package wsq.study.quartz.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wsq.study.quartz.dto.QuartzTaskInfoDTO;
import wsq.study.quartz.exception.ServiceException;

/**
 * quartz工具类
 *
 * @author weisq
 * @date 2018/11/15
 */
@Component
public class QuartzTaskUtil {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private Scheduler scheduler;

    /**
     * 所有任务列表
     *
     * @return
     */
    public List<QuartzTaskInfoDTO> list() {
        List<QuartzTaskInfoDTO> list = new ArrayList<>();

        try {
            for (String groupJob : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(groupJob))) {
                    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                    for (Trigger trigger : triggers) {
                        Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                        JobDetail jobDetail = scheduler.getJobDetail(jobKey);

                        QuartzTaskInfoDTO info = new QuartzTaskInfoDTO();
                        if (trigger instanceof CronTrigger) {
                            CronTrigger cronTrigger = (CronTrigger)trigger;
                            info.setCronExpression(cronTrigger.getCronExpression());
                            info.setJobDescription(cronTrigger.getDescription());
                            info.setPrevFireTime(cronTrigger.getPreviousFireTime());
                            info.setNextFireTime(cronTrigger.getNextFireTime());
                            info.setCreateTime(cronTrigger.getDescription());
                        }
                        info.setJobDescription(jobDetail.getDescription());
                        info.setJobName(jobKey.getName());
                        info.setJobGroup(jobKey.getGroup());
                        info.setJobDescription(jobDetail.getDescription());
                        info.setJobStatus(triggerState.name());
                        info.setJobClass(jobDetail.getJobClass().getName());
                        list.add(info);
                    }
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 保存定时任务
     *
     * @param info 任务对象
     */
    @SuppressWarnings("unchecked")
    public void addJob(QuartzTaskInfoDTO info) {
        String jobName = info.getJobName(), jobClass = info.getJobClass(), jobGroup = info.getJobGroup(),
            cronExpression = info.getCronExpression(), jobDescription = info.getJobDescription(),
            createTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        try {
            if (checkExists(jobName, jobGroup)) {
                // logger.info("===> AddJob fail, job already exist, jobGroup:{}, jobName:{}", jobGroup, jobName);
                logger.info("===> AddJob fail, job already exist, jobGroup:{}, jobName:{}, cover:{}", jobGroup,
                    jobName);
                throw new ServiceException(String.format("Job已经存在, jobName:{%s},jobGroup:{%s}", jobName, jobGroup));
            }

            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);

            CronScheduleBuilder schedBuilder =
                CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withDescription(createTime)
                .withSchedule(schedBuilder).build();

            Class<? extends Job> clazz = (Class<? extends Job>)Class.forName(jobClass);
            JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(jobKey).withDescription(jobDescription).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException | ClassNotFoundException e) {
            logger.error("===> AddJob fail, 类名不存在或执行表达式错误, jobGroup:{}, jobName:{}, exception:{}" + jobGroup + jobName,
                ExceptionUtils.getStackTrace(e));
            throw new ServiceException("类名不存在或执行表达式错误");
        }
    }

    /**
     * 修改定时任务
     *
     * @param info 2016年10月9日下午2:20:07
     */
    public void edit(QuartzTaskInfoDTO info) {
        String jobName = info.getJobName(), jobGroup = info.getJobGroup(), cronExpression = info.getCronExpression(),
            jobDescription = info.getJobDescription(),
            createTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        try {
            if (!checkExists(jobName, jobGroup)) {
                throw new ServiceException(String.format("Job不存在, jobName:{%s},jobGroup:{%s}", jobName, jobGroup));
            }
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
            JobKey jobKey = new JobKey(jobName, jobGroup);
            CronScheduleBuilder cronScheduleBuilder =
                CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
            CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withDescription(createTime)
                .withSchedule(cronScheduleBuilder).build();

            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            jobDetail = jobDetail.getJobBuilder().withDescription(jobDescription).build();
            HashSet<Trigger> triggerSet = new HashSet<>();
            triggerSet.add(cronTrigger);

            scheduler.scheduleJob(jobDetail, triggerSet, true);
        } catch (SchedulerException e) {
            throw new ServiceException("类名不存在或执行表达式错误");
        }
    }

    /**
     * 删除定时任务
     *
     * @param jobName
     * @param jobGroup 2016年10月9日下午1:51:12
     */
    public void delete(String jobName, String jobGroup) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        try {
            if (checkExists(jobName, jobGroup)) {
                scheduler.pauseTrigger(triggerKey);
                scheduler.unscheduleJob(triggerKey);
                // logger.info("===> delete, triggerKey:{}", triggerKey);
                logger.info("===> delete, triggerKey:{}" + triggerKey);
            }
        } catch (SchedulerException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 暂停定时任务
     *
     * @param jobName
     * @param jobGroup 2016年10月10日上午9:40:19
     */
    public void pause(String jobName, String jobGroup) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        try {
            if (checkExists(jobName, jobGroup)) {
                scheduler.pauseTrigger(triggerKey);
                // logger.info("===> Pause success, triggerKey:{}", triggerKey);
                logger.info("===> Pause success, triggerKey:{}" + triggerKey);
            }
        } catch (SchedulerException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 重新开始任务
     *
     * @param jobName
     * @param jobGroup 2016年10月10日上午9:40:58
     */
    public void resume(String jobName, String jobGroup) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);

        try {
            if (checkExists(jobName, jobGroup)) {
                scheduler.resumeTrigger(triggerKey);
                // logger.info("===> Resume success, triggerKey:{}", triggerKey);
                logger.info("===> Resume success, triggerKey:{}" + triggerKey);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证是否存在
     *
     * @param jobName
     * @param jobGroup
     * @throws SchedulerException 2016年10月8日下午5:30:43
     */
    public boolean checkExists(String jobName, String jobGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        return scheduler.checkExists(triggerKey);
    }
}
