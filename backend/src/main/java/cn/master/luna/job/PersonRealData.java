package cn.master.luna.job;

import cn.master.luna.entity.SystemProject;
import cn.master.luna.handler.schedule.BaseScheduleJob;
import cn.master.luna.util.DateUtils;
import cn.master.luna.util.FileUtils;
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
 * @author Created by 11's papa on 2025/7/25
 */
public class PersonRealData extends BaseScheduleJob {
    public static JobKey getJobKey(String resourceId) {
        return JobKey.jobKey(resourceId, PersonRealData.class.getName());
    }

    public static TriggerKey getTriggerKey(String resourceId) {
        return new TriggerKey(resourceId, PersonRealData.class.getName());
    }

    private final Random random = new Random();

    @Override
    protected void businessExecute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String projectId = jobDataMap.getString("projectId");
        StringBuilder content = new StringBuilder();
        String areaType = "20";
        SystemProject systemProject = QueryChain.of(SystemProject.class).where(SystemProject::getId).eq(projectId).one();
        LocalDateTime now = LocalDateTime.now();
        String fileName = systemProject.getNum() + "_NRYSS_" + DateUtils.localDateTime2String(now) + ".txt";
        String header = systemProject.getNum() + ";" + systemProject.getName() + ";" + DateUtils.localDateTime2StringStyle2(now) + "^";
        List<Row> rowList = getSubstationByAreaType(areaType);
        List<Row> personList = getPersonList();
        if (CollectionUtils.isNotEmpty(rowList)) {
            Row row = rowList.get(random.nextInt(rowList.size()));
            personList.forEach(person -> {
                content.append(person.getString("person_code")).append(";")
                        .append(person.getString("person_name")).append(";")
                        .append("1;").append(DateUtils.localDateTime2StringStyle2(now.minusHours(5))).append(";;")
                        .append(areaType).append(";").append(DateUtils.localDateTime2StringStyle2(now)).append(";")
                        .append(row.getString("station_code")).append(";")
                        .append(DateUtils.localDateTime2StringStyle2(now))
                        .append(";1;1;200;1;19626981.17;3933930.97;930.97;1;1;0;;^");
            });
        }

        if (!content.isEmpty()) {
            content.append("]]]");
            FileUtils.genFile("/app/ftp/rydw/" + fileName, header + content, "[NRYSS]人员实时数据");
        }
    }

    private List<Row> getSubstationByAreaType(String areaType) {
        try {
            DataSourceKey.use("ds2");
            Map<String, Object> map = new HashMap<>();
            map.put("area_type", areaType);
            map.put("is_delete", "0");
            return Db.selectListByMap("sf_jxzy_substation", map);
        } finally {
            DataSourceKey.clear();
        }
    }

    private List<Row> getPersonList() {
        try {
            DataSourceKey.use("ds2");
            Map<String, Object> map = new HashMap<>();
            map.put("is_delete", "0");
            return Db.selectListByMap("sf_jxzy_person_new", map);
        } finally {
            DataSourceKey.clear();
        }
    }
}
