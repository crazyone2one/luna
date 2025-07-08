package cn.master.luna.service.impl;

import cn.master.luna.constants.ApplicationNumScope;
import cn.master.luna.entity.SystemSchedule;
import cn.master.luna.exception.CustomException;
import cn.master.luna.handler.schedule.ScheduleManager;
import cn.master.luna.mapper.SystemScheduleMapper;
import cn.master.luna.service.SystemScheduleService;
import cn.master.luna.util.NumGenerator;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时任务 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
@Service
@RequiredArgsConstructor
public class SystemScheduleServiceImpl extends ServiceImpl<SystemScheduleMapper, SystemSchedule> implements SystemScheduleService {
    private final ScheduleManager scheduleManager;

    @Override
    public void addSchedule(SystemSchedule schedule) {
        schedule.setNum(getNextNum(schedule.getProjectId()));
        mapper.insert(schedule);
    }

    private Long getNextNum(String projectId) {
        return NumGenerator.nextNum(projectId, ApplicationNumScope.TASK);
    }

    @Override
    public SystemSchedule getScheduleByResource(String resourceId, String job) {
        List<SystemSchedule> schedules = queryChain()
                .where(SystemSchedule::getResourceId).eq(resourceId).and(SystemSchedule::getJob).eq(job)
                .list();
        if (schedules.isEmpty()) {
            return null;
        }
        return schedules.getFirst();
    }

    @Override
    public void addOrUpdateCronJob(SystemSchedule request, JobKey jobKey, TriggerKey triggerKey, Class clazz) {
        Boolean enable = request.getEnable();
        String cronExpression = request.getValue();
        if (Boolean.TRUE.equals(enable) && StringUtils.isNotBlank(cronExpression)) {
            try {
                scheduleManager.addOrUpdateCronJob(jobKey, triggerKey, clazz, cronExpression, scheduleManager.getDefaultJobDataMap(request, cronExpression, request.getCreateUser()));
            } catch (SchedulerException e) {
                throw new CustomException("定时任务开启异常: " + e.getMessage());
            }
        } else {
            try {
                scheduleManager.removeJob(jobKey, triggerKey);
            } catch (Exception e) {
                throw new CustomException("定时任务关闭异常: " + e.getMessage());
            }
        }
    }

    @Override
    public int deleteByResourceId(String scenarioId, JobKey jobKey, TriggerKey triggerKey) {
        QueryChain<SystemSchedule> queryChain = queryChain().where(SystemSchedule::getResourceId).eq(scenarioId);
        scheduleManager.removeJob(jobKey, triggerKey);
        return mapper.deleteByQuery(queryChain);
    }

    private void removeJob(String key, String job) {
        scheduleManager.removeJob(new JobKey(key, job), new TriggerKey(key, job));
    }
}
