package wsq.study.quartz.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理定时任务
 *
 * @author weisq
 * @date 2018/11/15
 */
public class QuartzTaskInfoDTO implements Serializable {
    private static final long serialVersionUID = -8054692082716173379L;
    private int id = 0;

    /** 任务名称 */
    private String jobName;

    /** 任务分组 */
    private String jobGroup = "DATAHUB_JOBGROUP";

    /** 任务描述 */
    private String jobDescription;

    /** 任务状态 */
    private String jobStatus;

    /** 任务表达式 */
    private String cronExpression;

    private String createTime;

    /** 任务依赖类路径 */
    private String jobClass;

    /**
     * 上次执行时间
     */
    private Date prevFireTime;

    /**
     * 下次执行时间
     */
    private Date nextFireTime;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public Date getPrevFireTime() {
        return prevFireTime;
    }

    public void setPrevFireTime(Date prevFireTime) {
        this.prevFireTime = prevFireTime;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }
}