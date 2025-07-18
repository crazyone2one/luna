package cn.master.luna.job;

import cn.master.luna.constants.ApplicationNumScope;
import cn.master.luna.entity.SystemProject;
import cn.master.luna.handler.schedule.BaseScheduleJob;
import cn.master.luna.util.DateUtils;
import cn.master.luna.util.FileUtils;
import cn.master.luna.util.NumGenerator;
import cn.master.luna.util.StringUtils;
import com.mybatisflex.core.query.QueryChain;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

import java.time.LocalDateTime;

/**
 * 分站基本信息 文件生成
 *
 * @author Created by 11's papa on 2025/7/15
 */
public class StationBasicInfo extends BaseScheduleJob {
    public static JobKey getJobKey(String resourceId) {
        return JobKey.jobKey(resourceId, StationBasicInfo.class.getName());
    }

    public static TriggerKey getTriggerKey(String resourceId) {
        return new TriggerKey(resourceId, StationBasicInfo.class.getName());
    }

    @Override
    protected void businessExecute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String projectId = jobDataMap.getString("projectId");
        SystemProject systemProject = QueryChain.of(SystemProject.class).where(SystemProject::getId).eq(projectId).one();
        LocalDateTime now = LocalDateTime.now();
        String fileName = systemProject.getNum() + "_NFZDY_" + DateUtils.localDateTime2String(now) + ".txt";
        String header = systemProject.getNum() + ";" + systemProject.getName() + ";" + DateUtils.localDateTime2StringStyle2(now) + "^";
        StringBuilder content = new StringBuilder();
//        String content = "10000000800001;东回风井口;;;^10000000800010;850中段;;;^10000000800011;870中段;;;^10000000800013;二号回风井;;;^10000000800016;三采区790中段东主运;;;^10000000800017;三采区790中段西主运;;;^10000000800018;三采区770中段东主运8-1穿口;;;^10000000800019;三采区770中段西主运13-1穿口;;;^10000000800020;三采区750入口;;;^10000000800021;三采区730中段入口;;;^10000000800022;三采区710中段入口;;;^10000000800023;三采区690中段避险硐室;;;^10000000800024;一采区790中段口;;;^10000000800025;一采区770中段口;;;^10000000800026;一采区750中段;;;^10000000800027;一采区730中段;;;^10000000800028;一采区710中段;;;^10000000800029;一采区690中段;;;^10000000800031;三采区 670口;;;^10000000800032;三采区650口;;;^10000000800033;一采区610;;;^10000000800034;三区590马头门;;;^]]]";
        for (int i = 0; i < 5; i++) {
            String tmp = buildStationInfo(projectId, systemProject.getNum());
            content.append(tmp);
        }
        if (!content.isEmpty()) {
            content.append("]]]");
            FileUtils.genFile("/app/ftp/aqjk/" + fileName, header + content, "[NFZDY]分站基本信息");
        }
    }

    private String buildStationInfo(String projectId, String systemNum) {
        String codeStuff = String.valueOf(NumGenerator.nextNum(projectId, ApplicationNumScope.NFZDY));
        return systemNum + codeStuff + ";" + StringUtils.generateRandomString(5) + "中段;;;^";
    }
}
