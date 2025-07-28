package cn.master.luna.job;

import cn.master.luna.constants.AreaType;
import cn.master.luna.entity.SystemProject;
import cn.master.luna.handler.schedule.BaseScheduleJob;
import cn.master.luna.util.DateUtils;
import cn.master.luna.util.FileUtils;
import cn.master.luna.util.JacksonUtils;
import cn.master.luna.util.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mybatisflex.core.query.QueryChain;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 接入系统区域基本信息数据有变化，则立即上传变化量数据。接入系统数据无变化时，至少每天上传一次感知基础数据
 *
 * @author Created by 11's papa on 2025/7/22
 */
public class PersonAreaBasicInfo extends BaseScheduleJob {
    private final static List<String> ignoreList = new ArrayList<>(Arrays.asList("10", "11", "12", "13", "14", "15", "16"));

    public static JobKey getJobKey(String resourceId) {
        return JobKey.jobKey(resourceId, PersonAreaBasicInfo.class.getName());
    }

    public static TriggerKey getTriggerKey(String resourceId) {
        return new TriggerKey(resourceId, PersonAreaBasicInfo.class.getName());
    }

    @Override
    protected void businessExecute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String projectId = jobDataMap.getString("projectId");
        Object config = jobDataMap.getWrappedMap().get("config");
        String areaType = "20";
        Map<String, String> jsonObject = JacksonUtils.toJSONObject(config, new TypeReference<>() {});
        if (Objects.nonNull(jsonObject)) {
            areaType = jsonObject.get("areaType");
        }
        SystemProject systemProject = QueryChain.of(SystemProject.class).where(SystemProject::getId).eq(projectId).one();
        LocalDateTime now = LocalDateTime.now();
        String fileName = systemProject.getNum() + "_NRYQY_" + DateUtils.localDateTime2String(now) + ".txt";
        String header = systemProject.getNum() + ";" + systemProject.getName() + ";" + DateUtils.localDateTime2StringStyle2(now) + "^";
        StringBuilder content = new StringBuilder();

        switch (areaType) {
            case "11":
                genBodyContent(content, AreaType.A11, systemProject.getNum());
                break;
            case "12":
                genBodyContent(content, AreaType.A12, systemProject.getNum());
                break;
            case "13":
                genBodyContent(content, AreaType.A13, systemProject.getNum());
                break;
            case "17":
                genBodyContent(content, AreaType.A17, systemProject.getNum());
                break;
            case "18":
                genBodyContent(content, AreaType.A18, systemProject.getNum());
                break;
            case "19":
                genBodyContent(content, AreaType.A19, systemProject.getNum());
                break;
            case "20":
                genBodyContent(content, AreaType.A20, systemProject.getNum());
                break;
            default:
                content.append(genBodyContent(content, AreaType.A21, systemProject.getNum()))
                        .append(genBodyContent(content, AreaType.A22, systemProject.getNum()))
                        .append(genBodyContent(content, AreaType.A23, systemProject.getNum()));
                break;
        }
        if (!content.isEmpty()) {
            content.append("]]]");
            FileUtils.genFile("/app/ftp/rydw/" + fileName, header + content, "[NRYQY]人员区域基本信息");
        }
    }

    private StringBuilder genBodyContent(StringBuilder content, AreaType areaType, String projectNum) {
        String randomString = StringUtils.generateRandomString(4);
        content.append(areaType.getValue()).append(";");
        content.append(projectNum).append(randomString).append(";");
        content.append("15;29;");
        if (!ignoreList.contains(areaType.getValue())) {
            content.append(randomString);
        }
        content.append(areaType.getDesc()).append("^");
        return content;
    }
}
