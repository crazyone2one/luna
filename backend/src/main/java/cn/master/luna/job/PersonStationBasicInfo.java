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
import org.apache.commons.collections4.CollectionUtils;
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
 * 人员基站文件
 *
 * @author Created by 11's papa on 2025/7/25
 */
public class PersonStationBasicInfo extends BaseScheduleJob {
    public static JobKey getJobKey(String resourceId) {
        return JobKey.jobKey(resourceId, PersonStationBasicInfo.class.getName());
    }

    public static TriggerKey getTriggerKey(String resourceId) {
        return new TriggerKey(resourceId, PersonStationBasicInfo.class.getName());
    }

    private final Random random = new Random();

    @Override
    protected void businessExecute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String projectId = jobDataMap.getString("projectId");
//        Object config = jobDataMap.getWrappedMap().get("config");
//        AreaType areaType = StringUtils.getRandomEnum(AreaType.class);
        StringBuilder content = new StringBuilder();
        String areaType = "20";
        List<Row> personAreaList = getPersonAreaList(areaType);
        if (CollectionUtils.isNotEmpty(personAreaList)) {
            Row row = personAreaList.get(random.nextInt(personAreaList.size()));
            String areaCode = row.getString("area_code");
            String randomString = StringUtils.generateRandomString(6);
            content.append(areaCode).append(randomString).append(";")
                    .append(areaType)
                    .append(";19626981.17;3933930.97;930.97;")
                    .append(randomString).append("掘进工作面^");
        }
        SystemProject systemProject = QueryChain.of(SystemProject.class).where(SystemProject::getId).eq(projectId).one();
        LocalDateTime now = LocalDateTime.now();
        String fileName = systemProject.getNum() + "_NRYJZ_" + DateUtils.localDateTime2String(now) + ".txt";
        String header = systemProject.getNum() + ";" + systemProject.getName() + ";300;KJXXX;XXX公司;XXX编号;" + DateUtils.localDateTime2StringStyle2(now) + "^";
        if (!content.isEmpty()) {
            content.append("]]]");
            FileUtils.genFile("/app/ftp/rydw/" + fileName, header + content, "[NRYJZ]人员基站信息");
        }
    }

    private List<Row> getPersonAreaList(String areaType) {
        try {
            DataSourceKey.use("ds2");
            Map<String, Object> map = new HashMap<>();
            map.put("area_type", areaType);
            map.put("is_delete", "0");
            return Db.selectListByMap("sf_jxzy_area", map);
        } finally {
            DataSourceKey.clear();
        }
    }
}
