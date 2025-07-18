package cn.master.luna.job;

import cn.master.luna.constants.SensorType;
import cn.master.luna.entity.SystemProject;
import cn.master.luna.handler.schedule.BaseScheduleJob;
import cn.master.luna.util.DateUtils;
import cn.master.luna.util.FileUtils;
import cn.master.luna.util.JacksonUtils;
import cn.master.luna.util.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 测点基本信息文件
 *
 * @author Created by 11's papa on 2025/7/16
 */
public class SensorBasicInfo extends BaseScheduleJob {
    private final Random random = new Random();

    public static JobKey getJobKey(String resourceId) {
        return JobKey.jobKey(resourceId, SensorBasicInfo.class.getName());
    }

    public static TriggerKey getTriggerKey(String resourceId) {
        return new TriggerKey(resourceId, SensorBasicInfo.class.getName());
    }

    @Override
    protected void businessExecute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String projectId = jobDataMap.getString("projectId");
        Object config = jobDataMap.getWrappedMap().get("config");
        String sensorType = Objects.requireNonNull(JacksonUtils.toJSONObject(config, new TypeReference<Map<String, String>>() {
                }))
                .get("sensorType");
        SystemProject systemProject = QueryChain.of(SystemProject.class).where(SystemProject::getId).eq(projectId).one();
        LocalDateTime now = LocalDateTime.now();
        String fileName = systemProject.getNum() + "_NCDDY_" + DateUtils.localDateTime2String(now) + ".txt";
        String header = systemProject.getNum() + ";" + systemProject.getName() + ";NKJ***;金属非金属地下矿山监测监控系统;XXXX公司;2026-12-30;" + DateUtils.localDateTime2StringStyle2(now) + "^";
        // 分站信息
        List<Row> rows = getRows();
        Row row = rows.get(random.nextInt(rows.size()));
        StringBuilder content = new StringBuilder();
        switch (sensorType) {
            case "0001":
                extracted(content, systemProject, row, now, SensorType.D0001);
                break;
            case "0002":
                // todo风速
                break;
            case "0003":
                // todo环境温度
                break;
            case "0004":
                // 测点编码
                extracted(content, systemProject, row, now, SensorType.D0004);
                break;
            default:
                break;
        }

        if (!content.isEmpty()) {
            content.append("]]]");
            FileUtils.genFile("/app/ftp/aqjk/" + fileName, header + content, "[NCDDY]测点基本信息");
        }
    }

    private void extracted(StringBuilder content, SystemProject systemProject, Row row, LocalDateTime now, SensorType sensorType) {
        // 测点编码
        content.append(sensorCode(systemProject, sensorType));
        content.append("01;");
        content.append(row.get("station_code"));
        content.append(";")
                .append(sensorType.getValue()).append(";")
                .append(sensorType.getType()).append(";")
                .append(sensorType.getUnit()).append(";50;20;40;35;;;")
                .append(StringUtils.generateRandomString(5))
                .append(sensorType.getDesc()).append(";;;;;");
        content.append(DateUtils.localDateTime2StringStyle2(now));
        content.append("^");
    }

    private String sensorCode(SystemProject systemProject, SensorType sensorType) {
        return systemProject.getNum() + "01" + sensorType.getType() +
                sensorType.getValue() +
                StringUtils.generateRandomString(4) + ";";
    }

    /***
     * 获取分站数据
     * @return List<Row>
     */
    private List<Row> getRows() {
        DataSourceKey.use("ds2");
        Map<String, Object> map = new HashMap<>();
        map.put("is_delete", "0");
        return Db.selectListByMap("sf_aqjk_stationinfo", map);
    }
}
