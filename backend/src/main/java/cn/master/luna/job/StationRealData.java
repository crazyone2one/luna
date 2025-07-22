package cn.master.luna.job;

import cn.master.luna.entity.SystemProject;
import cn.master.luna.handler.schedule.BaseScheduleJob;
import cn.master.luna.util.DateUtils;
import cn.master.luna.util.FileUtils;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 每1分钟上传一次监测监控分站实时数据
 * @author Created by 11's papa on 2025/7/22
 */
public class StationRealData extends BaseScheduleJob {
    public static JobKey getJobKey(String resourceId) {
        return JobKey.jobKey(resourceId, StationRealData.class.getName());
    }

    public static TriggerKey getTriggerKey(String resourceId) {
        return new TriggerKey(resourceId, StationRealData.class.getName());
    }

    @Override
    protected void businessExecute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String projectId = jobDataMap.getString("projectId");
        SystemProject systemProject = QueryChain.of(SystemProject.class).where(SystemProject::getId).eq(projectId).one();
        LocalDateTime now = LocalDateTime.now();
        String fileName = systemProject.getNum() + "_NFZSS_" + DateUtils.localDateTime2String(now) + ".txt";
        String header = systemProject.getNum() + ";" + systemProject.getName() + ";" + DateUtils.localDateTime2StringStyle2(now) + "^";
        StringBuilder content = new StringBuilder();
        List<Row> stationList = getStationList(systemProject.getNum());
        if (!stationList.isEmpty()) {
            for (Row row : stationList) {
                content.append(row.get("station_code", String.class)).append(";");
                content.append("0;").append("0;");
                content.append(DateUtils.localDateTime2StringStyle2(now)).append("^");
            }
            if (!content.isEmpty()) {
                content.append("]]]");
                FileUtils.genFile("/app/ftp/aqjk/" + fileName, header + content, "[NFZSS]分站实时信息");
            }
        }

    }

    private List<Row> getStationList(String resourceId) {
        try {
            DataSourceKey.use("ds2");
            Map<String, Object> map = new HashMap<>();
            map.put("is_delete", "0");
            return Db.selectListByMap("sf_aqjk_stationinfo", map);
        } finally {
            DataSourceKey.clear();
        }
    }
}
