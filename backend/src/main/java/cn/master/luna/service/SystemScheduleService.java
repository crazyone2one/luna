package cn.master.luna.service;

import cn.master.luna.entity.SystemSchedule;
import cn.master.luna.entity.request.SchedulePageRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

/**
 * 定时任务 服务层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
public interface SystemScheduleService extends IService<SystemSchedule> {
    void addSchedule(SystemSchedule schedule);

    SystemSchedule getScheduleByResource(String resourceId, String job);

    void addOrUpdateCronJob(SystemSchedule request, JobKey jobKey, TriggerKey triggerKey, Class clazz);

    int deleteByResourceId(String scenarioId, JobKey jobKey, TriggerKey triggerKey);

    Page<SystemSchedule> getSchedulePage(SchedulePageRequest request);

    void enable(String id);
}
