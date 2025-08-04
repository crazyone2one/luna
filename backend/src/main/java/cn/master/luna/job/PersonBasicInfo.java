package cn.master.luna.job;

import cn.master.luna.constants.ApplicationNumScope;
import cn.master.luna.constants.PersonJobType;
import cn.master.luna.constants.PersonWorkType;
import cn.master.luna.entity.SystemProject;
import cn.master.luna.handler.schedule.BaseScheduleJob;
import cn.master.luna.util.*;
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

import static cn.master.luna.util.ChineseInfoGenerator.generatePersonInfo;

/**
 * @author Created by 11's papa on 2025/7/28
 */
public class PersonBasicInfo extends BaseScheduleJob {
    public static JobKey getJobKey(String resourceId) {
        return JobKey.jobKey(resourceId, PersonBasicInfo.class.getName());
    }

    public static TriggerKey getTriggerKey(String resourceId) {
        return new TriggerKey(resourceId, PersonBasicInfo.class.getName());
    }

    private final Random random = new Random();

    @Override
    protected void businessExecute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String projectId = jobDataMap.getString("projectId");
        SystemProject systemProject = QueryChain.of(SystemProject.class).where(SystemProject::getId).eq(projectId).one();
        LocalDateTime now = LocalDateTime.now();
        String fileName = systemProject.getNum() + "_NRYXX_" + DateUtils.localDateTime2String(now) + ".txt";
        String header = systemProject.getNum() + ";" + systemProject.getName() + ";" + DateUtils.localDateTime2StringStyle2(now) + "^";
        StringBuilder content = new StringBuilder();
        List<Row> deptList = getDeptList();
        Row dept = deptList.get(random.nextInt(deptList.size()));
        generatorContent(content, systemProject, projectId, dept);
        if (!content.isEmpty()) {
            content.append("]]]");
            FileUtils.genFile("/app/ftp/rydw/" + fileName, header + content, "[NRYXX]人员基本信息");
        }
    }

    private void generatorContent(StringBuilder content, SystemProject systemProject, String projectId, Row dept) {
        ChineseInfoGenerator.PersonInfo personInfo = generatePersonInfo();
        String personCode = systemProject.getNum() + NumGenerator.nextNum(projectId, ApplicationNumScope.NJXRY);
        content.append(personCode).append(";").append(personInfo.getName()).append(";").append(personInfo.getIdCard()).append(";");
        content.append(StringUtils.getRandomEnum(PersonWorkType.class).getCode()).append(";");
        content.append(StringUtils.getRandomEnum(PersonJobType.class).getCode()).append(";");
        content.append(dept.getString("name")).append(";");
        content.append(personInfo.getBirthDate()).append(";");
        content.append(personInfo.getEducation()).append(";");
        content.append("0;0;");
        content.append(personInfo.getPhone()).append(";");
        content.append(";;金属非金属地下矿山安全作业;金属非金属地下矿山井下电气作业;2022-03-05;2026-03-04^");
    }

    private List<Row> getDeptList() {
        try {
            DataSourceKey.use("ds2");
            Map<String, Object> map = new HashMap<>();
            map.put("is_delete", "0");
            map.put("parent_id", "4");
            return Db.selectListByMap("system_dept", map);
        } finally {
            DataSourceKey.clear();
        }
    }
}
