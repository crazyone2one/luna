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
import java.util.*;

/**
 * 超员报警
 *
 * @author Created by 11's papa on 2025/8/5
 */
public class PersonOverCrowd extends BaseScheduleJob {
    public static JobKey getJobKey(String resourceId) {
        return JobKey.jobKey(resourceId, PersonOverCrowd.class.getName());
    }

    public static TriggerKey getTriggerKey(String resourceId) {
        return new TriggerKey(resourceId, PersonOverCrowd.class.getName());
    }

    private final Random random = new Random();

    @Override
    protected void businessExecute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String projectId = jobDataMap.getString("projectId");
        SystemProject systemProject = QueryChain.of(SystemProject.class).where(SystemProject::getId).eq(projectId).one();
        LocalDateTime now = LocalDateTime.now();
        String fileName = systemProject.getNum() + "_NRYCY_" + DateUtils.localDateTime2String(now) + ".txt";
        String header = systemProject.getNum() + ";" + systemProject.getName() + ";" + DateUtils.localDateTime2StringStyle2(now) + "^";
        StringBuilder content = new StringBuilder();
        // 17;采矿工作面;2022-04-18 12:28:30;2022-04-18 12:29:30;14012102003400007&14012102003400005^
        content.append("1;1;2;");
        List<Row> areaList = getAreaList();
        Row area = areaList.get(random.nextInt(areaList.size()));
        content.append(area.getString("area_code")).append(";");
        content.append(area.getString("area_location_description")).append(";");
        content.append(DateUtils.localDateTime2StringStyle2(now.minusMinutes(3))).append(";");
        content.append(DateUtils.localDateTime2StringStyle2(now)).append(";");
        content.append(getPersonCode()).append("^");
        if (!content.isEmpty()) {
            content.append("]]]");
            FileUtils.genFile("/app/ftp/rydw/" + fileName, header + content, "[NRYCY]超员报警数据");
        }
    }

    private List<Row> getAreaList() {
        try {
            DataSourceKey.use("ds2");
            Map<String, Object> map = new HashMap<>();
            map.put("is_delete", "0");
            return Db.selectListByMap("sf_jxzy_area", map);
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

    private String getPersonCode() {
        StringBuilder personCode = new StringBuilder();
        List<Row> personList = getPersonList();
        Collections.shuffle(personList, random);
        personList.stream().limit(2).forEach(row -> personCode.append(row.getString("person_code")).append("&"));
        return personCode.deleteCharAt(personCode.lastIndexOf("&")).toString();
    }
}
