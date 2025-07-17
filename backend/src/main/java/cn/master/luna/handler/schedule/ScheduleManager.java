package cn.master.luna.handler.schedule;

import cn.master.luna.entity.SystemSchedule;
import cn.master.luna.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

/**
 * 定时任务管理类
 *
 * @author Created by 11's papa on 2025/7/8
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleManager {
    private final Scheduler scheduler;

    public void addCronJob(JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> jobClass, String cron, JobDataMap jobDataMap) {
        try {
            log.info("addCronJob: {},{}", triggerKey.getName(), triggerKey.getGroup());
            JobBuilder jobBuilder = JobBuilder.newJob(jobClass).withIdentity(jobKey);
            if (jobDataMap != null) {
                jobBuilder.usingJobData(jobDataMap);
            }
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            triggerBuilder.withIdentity(triggerKey);
            triggerBuilder.startNow();
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
            CronTrigger trigger = (CronTrigger) triggerBuilder.build();
            scheduler.scheduleJob(jobBuilder.build(), trigger);
        } catch (SchedulerException e) {
            throw new CustomException("定时任务配置异常: " + e.getMessage());
        }
    }

    public void addCronJob(JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> jobClass, String cron) {
        addCronJob(jobKey, triggerKey, jobClass, cron, null);
    }

    public void modifyCronJobTime(TriggerKey triggerKey, String cron) {
        log.info("modifyCronJobTime: {},{}", triggerKey.getName(), triggerKey.getGroup());
        try {
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }

            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(cron)) {
                /* 方式一 ：调用 rescheduleJob 开始 */
                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();// 触发器
                triggerBuilder.withIdentity(triggerKey);// 触发器名,触发器组
                triggerBuilder.startNow(); // 立即执行
                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron)); // 触发器时间设定
                trigger = (CronTrigger) triggerBuilder.build(); // 创建Trigger对象
                scheduler.rescheduleJob(triggerKey, trigger); // 修改一个任务的触发时间
            }
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public void removeJob(JobKey jobKey, TriggerKey triggerKey) {
        try {
            log.info("RemoveJob: {},{}", jobKey.getName(), jobKey.getGroup());
            scheduler.pauseTrigger(triggerKey);
            scheduler.unscheduleJob(triggerKey);
            scheduler.deleteJob(jobKey);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CustomException(e);
        }
    }

    public void pauseJob(JobKey jobKey) {
        try {
            log.info("pauseJob: {},{}", jobKey.getName(), jobKey.getGroup());
            // 暂停任务
            scheduler.pauseJob(jobKey);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CustomException(e);
        }
    }

    public void startJobs(JobKey jobKey) {
        try {
            log.info("startJob: {},{}", jobKey.getName(), jobKey.getGroup());
            scheduler.triggerJob(jobKey);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CustomException(e);
        }
    }

    public void shutdownJobs(Scheduler schedule) {
        try {
            if (!schedule.isShutdown()) {
                schedule.shutdown();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CustomException(e);
        }
    }

    public void addOrUpdateCronJob(JobKey jobKey, TriggerKey triggerKey, Class jobClass, String cron, JobDataMap jobDataMap)
            throws SchedulerException {
        log.info("AddOrUpdateCronJob: {},{}", jobKey.getName(), triggerKey.getGroup());

        if (scheduler.checkExists(triggerKey)) {
            modifyCronJobTime(triggerKey, cron);
        } else {
            addCronJob(jobKey, triggerKey, jobClass, cron, jobDataMap);
        }
    }

    public void addOrUpdateCronJob(JobKey jobKey, TriggerKey triggerKey, Class jobClass, String cron) throws SchedulerException {
        addOrUpdateCronJob(jobKey, triggerKey, jobClass, cron, null);
    }

    public JobDataMap getDefaultJobDataMap(SystemSchedule schedule, String expression, String userId) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("resourceId", schedule.getResourceId());
        jobDataMap.put("expression", expression);
        jobDataMap.put("userId", userId);
        jobDataMap.put("config", schedule.getConfig());
        jobDataMap.put("projectId", schedule.getProjectId());
        return jobDataMap;
    }
}
