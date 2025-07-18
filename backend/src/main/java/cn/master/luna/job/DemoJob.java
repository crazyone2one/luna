package cn.master.luna.job;

import cn.master.luna.entity.SystemProject;
import cn.master.luna.handler.schedule.BaseScheduleJob;
import com.mybatisflex.core.query.QueryChain;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

/**
 * @author Created by 11's papa on 2025/7/8
 */
@Slf4j
@DisallowConcurrentExecution
public class DemoJob extends BaseScheduleJob {
    public static JobKey getJobKey(String resourceId) {
        return JobKey.jobKey(resourceId, DemoJob.class.getName());
    }

    public static TriggerKey getTriggerKey(String resourceId) {
        return new TriggerKey(resourceId, DemoJob.class.getName());
    }

    @Override
    protected void businessExecute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String resourceId = jobDataMap.getString("resourceId");
        String projectId = jobDataMap.getString("projectId");
        String userId = jobDataMap.getString("userId");
        log.info("resourceId:{}, userId:{}", resourceId, userId);
        SystemProject systemProject = QueryChain.of(SystemProject.class).where(SystemProject::getId).eq(projectId).one();
        log.info("systemProject:{}", systemProject);
    }
}
