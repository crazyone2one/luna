package cn.master.luna.job;

import cn.master.luna.entity.SystemProject;
import cn.master.luna.handler.schedule.BaseScheduleJob;
import cn.master.luna.util.DateUtils;
import cn.master.luna.util.FileUtils;
import cn.master.luna.util.StringUtils;
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
import java.util.Random;

/**
 * @author Created by 11's papa on 2025/7/16
 */
public class SensorRealData extends BaseScheduleJob {
    private final Random random = new Random();

    public static JobKey getJobKey(String resourceId) {
        return JobKey.jobKey(resourceId, SensorRealData.class.getName());
    }

    public static TriggerKey getTriggerKey(String resourceId) {
        return new TriggerKey(resourceId, SensorRealData.class.getName());
    }

    @Override
    protected void businessExecute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String projectId = jobDataMap.getString("projectId");
        SystemProject systemProject = QueryChain.of(SystemProject.class).where(SystemProject::getId).eq(projectId).one();
        LocalDateTime now = LocalDateTime.now();
        String fileName = systemProject.getNum() + "_NCDSS_" + DateUtils.localDateTime2String(now) + ".txt";
        String header = systemProject.getNum() + ";" + systemProject.getName() + ";" + DateUtils.localDateTime2StringStyle2(now) + "^";
        List<Row> sensorList = getRow(systemProject.getNum());
        StringBuilder content = new StringBuilder();
        sensorList.forEach(sensor -> {
            content.append(sensor.get("sensor_code")).append(";")
                    .append(sensor.get("sensor_type")).append(";")
                    .append(sensor.get("sensor_location")).append(";");
            content.append(StringUtils.doubleTypeString(10, 50)).append(";")
                    .append(sensor.get("sensor_value_unit")).append(";")
                    .append("0").append(";");
            content.append(DateUtils.localDateTime2StringStyle2(now));
            content.append("^");
        });
        if (!content.isEmpty()) {
            content.append("]]]");
            FileUtils.genFile("/app/ftp/aqjk/" + fileName, header + content, "[NCDSS]测点实时数据");
        }
    }

    private List<Row> getRow(String resourceId) {
        try {
            DataSourceKey.use("ds2");
            Map<String, Object> map = new HashMap<>();
            map.put("is_delete", "0");
            List<Row> stationinfo = Db.selectListByMap("sf_aqjk_sensor", map);
            return stationinfo.stream().filter(row -> row.get("sensor_code").toString().startsWith(resourceId)).toList();
        } finally {
            DataSourceKey.clear();
        }
    }
}
