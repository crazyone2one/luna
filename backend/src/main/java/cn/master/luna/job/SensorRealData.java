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
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Created by 11's papa on 2025/7/16
 */
public class SensorRealData extends BaseScheduleJob {
    private final Random random = new Random();
    private static final  String SENSOR_CODE = "sensor_code";
    private static final String SENSOR_TYPE = "sensor_type";
    private static final String SENSOR_LOCATION = "sensor_location";
    private static final String SENSOR_VALUE_UNIT = "sensor_value_unit";

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
        if (DateUtils.isInDailyRange(11, 0, 11, 5, now)) {
            Row row = sensorList.get(random.nextInt(sensorList.size()));
            LocalTime nowTime = now.toLocalTime();
            LocalTime endTime = LocalTime.of(11, 5);
            if (nowTime.equals(endTime)) {
                errorInfoFinish(row, content, now);
            } else {
                errorInfo(row, content, now);
            }
            fileName = systemProject.getNum() + "_NCDYCBJ_" + DateUtils.localDateTime2String(now) + ".txt";
        } else {
            sensorList.forEach(sensor -> {
                content.append(sensor.get(SENSOR_CODE)).append(";")
                        .append(sensor.get(SENSOR_TYPE)).append(";")
                        .append(sensor.get(SENSOR_LOCATION)).append(";");
                content.append(StringUtils.doubleTypeString(10, 50)).append(";")
                        .append(sensor.get(SENSOR_VALUE_UNIT)).append(";")
                        .append("0").append(";");
                content.append(DateUtils.localDateTime2StringStyle2(now));
                content.append("^");
            });
        }
        if (!content.isEmpty()) {
            content.append("]]]");
            FileUtils.genFile("/app/ftp/aqjk/" + fileName, header + content, "[NCDSS]测点实时数据");
        }
    }

    private void errorInfo(Row sensor, StringBuilder content, LocalDateTime localDateTime) {
        content.append(sensor.get(SENSOR_CODE)).append(";")
                .append(sensor.get(SENSOR_TYPE)).append(";")
                .append(sensor.get(SENSOR_LOCATION)).append(";");
        content.append(sensor.get(SENSOR_VALUE_UNIT)).append(";");
        content.append("001;");
        // 开始时间、结束时间
        content.append(DateUtils.localDateTime2StringStyle2(localDateTime)).append(";");
        content.append(";");
        content.append("80;");
        content.append(DateUtils.localDateTime2StringStyle2(localDateTime)).append(";");
        content.append(";").append(";");
        content.append(";;;;;;");
        content.append(DateUtils.localDateTime2StringStyle2(localDateTime)).append("^");
    }

    private void errorInfoFinish(Row sensor, StringBuilder content, LocalDateTime localDateTime) {
        content.append(sensor.get(SENSOR_CODE)).append(";")
                .append(sensor.get(SENSOR_TYPE)).append(";")
                .append(sensor.get(SENSOR_LOCATION)).append(";");
        content.append(sensor.get(SENSOR_VALUE_UNIT)).append(";");
        content.append("001;");
        // 开始时间、结束时间
        content.append(DateUtils.localDateTime2StringStyle2(localDateTime.minusSeconds(20))).append(";");
        content.append(DateUtils.localDateTime2StringStyle2(localDateTime)).append(";");
        content.append("80;").append(DateUtils.localDateTime2StringStyle2(localDateTime)).append(";");
        content.append(";").append(";");
        content.append(";");//
        content.append("异常原因：xxxxx;处理措施：xxxxxx;");
        content.append(DateUtils.localDateTime2StringStyle2(localDateTime)).append(";异常数据录入人;");
        content.append(DateUtils.localDateTime2StringStyle2(localDateTime)).append("^");
    }

    private List<Row> getRow(String resourceId) {
        try {
            DataSourceKey.use("ds2");
            Map<String, Object> map = new HashMap<>();
            map.put("is_delete", "0");
            List<Row> stationinfo = Db.selectListByMap("sf_aqjk_sensor", map);
            return stationinfo.stream().filter(row -> row.get(SENSOR_CODE).toString().startsWith(resourceId)).toList();
        } finally {
            DataSourceKey.clear();
        }
    }
}
