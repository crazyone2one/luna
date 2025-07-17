package cn.master.luna.service.impl;

import cn.master.luna.constants.ApplicationNumScope;
import cn.master.luna.constants.HttpMethodConstants;
import cn.master.luna.constants.OperationLogType;
import cn.master.luna.entity.SystemProject;
import cn.master.luna.entity.SystemSchedule;
import cn.master.luna.entity.dto.LogDTO;
import cn.master.luna.entity.dto.LogDTOBuilder;
import cn.master.luna.entity.dto.OptionDTO;
import cn.master.luna.entity.dto.ProjectDTO;
import cn.master.luna.entity.request.RunScheduleRequest;
import cn.master.luna.entity.request.SchedulePageRequest;
import cn.master.luna.entity.request.ScheduleRequest;
import cn.master.luna.exception.CustomException;
import cn.master.luna.handler.schedule.ScheduleManager;
import cn.master.luna.mapper.SystemScheduleMapper;
import cn.master.luna.service.OperationLogService;
import cn.master.luna.service.SystemScheduleService;
import cn.master.luna.util.NumGenerator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.luna.entity.table.SystemOrganizationTableDef.SYSTEM_ORGANIZATION;
import static cn.master.luna.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;
import static cn.master.luna.entity.table.SystemScheduleTableDef.SYSTEM_SCHEDULE;

/**
 * 定时任务 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemScheduleServiceImpl extends ServiceImpl<SystemScheduleMapper, SystemSchedule> implements SystemScheduleService {
    private final ScheduleManager scheduleManager;
    private final OperationLogService operationLogService;

    @Override
    public void addSchedule(SystemSchedule schedule) {
        schedule.setNum(getNextNum(schedule.getProjectId()));
        mapper.insert(schedule);
    }

    private Long getNextNum(String projectId) {
        return NumGenerator.nextNum(projectId, ApplicationNumScope.TASK);
    }

    @Override
    public SystemSchedule getScheduleByResource(String resourceId, String job) {
        List<SystemSchedule> schedules = queryChain()
                .where(SystemSchedule::getResourceId).eq(resourceId).and(SystemSchedule::getJob).eq(job)
                .list();
        if (schedules.isEmpty()) {
            return null;
        }
        return schedules.getFirst();
    }

    @Override
    public void addOrUpdateCronJob(SystemSchedule request, JobKey jobKey, TriggerKey triggerKey, Class clazz) {
        Boolean enable = request.getEnable();
        String cronExpression = request.getValue();
        if (Boolean.TRUE.equals(enable) && StringUtils.isNotBlank(cronExpression)) {
            try {
                scheduleManager.addOrUpdateCronJob(jobKey, triggerKey, clazz, cronExpression, scheduleManager.getDefaultJobDataMap(request, cronExpression, request.getCreateUser()));
            } catch (SchedulerException e) {
                throw new CustomException("定时任务开启异常: " + e.getMessage());
            }
        } else {
            try {
                scheduleManager.removeJob(jobKey, triggerKey);
            } catch (Exception e) {
                throw new CustomException("定时任务关闭异常: " + e.getMessage());
            }
        }
    }

    @Override
    public int deleteByResourceId(String scenarioId, JobKey jobKey, TriggerKey triggerKey) {
        QueryChain<SystemSchedule> queryChain = queryChain().where(SystemSchedule::getResourceId).eq(scenarioId);
        scheduleManager.removeJob(jobKey, triggerKey);
        return mapper.deleteByQuery(queryChain);
    }

    @Override
    public Page<SystemSchedule> getSchedulePage(SchedulePageRequest request) {
        List<OptionDTO> projectList = QueryChain.of(SystemProject.class)
                .select(SYSTEM_PROJECT.ID, SYSTEM_PROJECT.NAME)
                .where(SYSTEM_PROJECT.ORGANIZATION_ID.eq(request.getOrgId()))
                .orderBy(SYSTEM_PROJECT.CREATE_TIME.desc())
                .listAs(OptionDTO.class);
        List<String> projectIds = projectList.stream().map(OptionDTO::getId).toList();
        Page<SystemSchedule> page = queryChain()
                .select(SYSTEM_SCHEDULE.ALL_COLUMNS)
                .select(" QRTZ_TRIGGERS.NEXT_FIRE_TIME AS next_time")
                .select(" QRTZ_TRIGGERS.PREV_FIRE_TIME AS last_time")
                .select(SYSTEM_PROJECT.ORGANIZATION_ID)
                .from(SYSTEM_SCHEDULE).as("task")
                .leftJoin(SYSTEM_PROJECT).on(SYSTEM_SCHEDULE.PROJECT_ID.eq(SYSTEM_PROJECT.ID))
                .leftJoin("QRTZ_TRIGGERS").on("task.key = QRTZ_TRIGGERS.TRIGGER_NAME")
                .where(SYSTEM_SCHEDULE.PROJECT_ID.in(projectIds)
                        .and(SYSTEM_SCHEDULE.NAME.like(request.getKeyword())
                                .or(SYSTEM_SCHEDULE.NUM.like(request.getKeyword()))))
                .page(new Page<>(request.getPage(), request.getPageSize()));
        processTaskCenterSchedule(page, projectIds);
        return page;
    }

    private void processTaskCenterSchedule(Page<SystemSchedule> page, List<String> projectIds) {
//        List<SystemSchedule> list = page.getRecords();
//        if (!CollectionUtils.isEmpty(list)) {
//            if (projectIds.isEmpty()) {
//                projectIds = list.stream().map(SystemSchedule::getProjectId).toList();
//            }
//            List<OptionDTO> orgListByProjectList = QueryChain.of(SystemProject.class)
//                    .select(SYSTEM_PROJECT.ID, SYSTEM_ORGANIZATION.NAME)
//                    .from(SYSTEM_PROJECT).innerJoin(SYSTEM_ORGANIZATION).on(SYSTEM_PROJECT.ORGANIZATION_ID.eq(SYSTEM_ORGANIZATION.ID))
//                    .where(SYSTEM_PROJECT.ID.in(projectIds))
//                    .listAs(OptionDTO.class);
//            Map<String, String> orgMap = orgListByProjectList.stream().collect(Collectors.toMap(OptionDTO::getId, OptionDTO::getName));
//            Set<String> userSet = list.stream()
//                    .flatMap(item -> Stream.of(item.getCreateUser()))
//                    .collect(Collectors.toSet());
//        }
    }

    @Override
    public void enable(String id) {
        SystemSchedule schedule = mapper.selectOneById(id);
        if (schedule == null) {
            throw new CustomException("<UNK>");
        }
        schedule.setEnable(!schedule.getEnable());
        mapper.update(schedule);
        try {
            addOrUpdateCronJob(schedule, new JobKey(schedule.getKey(), schedule.getJob()),
                    new TriggerKey(schedule.getKey(), schedule.getJob()), Class.forName(schedule.getJob()));
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateCron(ScheduleRequest request, String userName, String path, String module) {
        SystemSchedule schedule = checkScheduleExit(request.getId());
        schedule.setValue(request.getCron());
        mapper.update(schedule);
        try {
            addOrUpdateCronJob(schedule, new JobKey(schedule.getKey(), schedule.getJob()),
                    new TriggerKey(schedule.getKey(), schedule.getJob()), Class.forName(schedule.getJob()));
            saveLog(List.of(schedule), userName, path, HttpMethodConstants.GET.name(), module, OperationLogType.UPDATE.name());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private SystemSchedule checkScheduleExit(String id) {
        SystemSchedule schedule = mapper.selectOneById(id);
        if (schedule == null) {
            throw new CustomException("<定时任务不存在>");
        }
        return schedule;
    }

    private void saveLog(List<SystemSchedule> scheduleList, String userName, String path, String method, String module, String type) {
        if (scheduleList.isEmpty()) {
            return;
        }
        List<String> projectIds = scheduleList.stream().map(SystemSchedule::getProjectId).distinct().toList();
        List<ProjectDTO> orgList = getOrgListByProjectIds(projectIds);
        //生成map key:项目id value:组织id
        Map<String, String> orgMap = orgList.stream().collect(Collectors.toMap(ProjectDTO::getId, ProjectDTO::getOrganizationId));
        List<LogDTO> logs = new ArrayList<>();
        scheduleList.forEach(s -> {
            LogDTO dto = LogDTOBuilder.builder()
                    .projectId(s.getProjectId())
                    .organizationId(orgMap.get(s.getProjectId()))
                    .type(type)
                    .module(module)
                    .method(method)
                    .path(path)
                    .sourceId(s.getResourceId())
                    .content(s.getName())
                    .createUser(userName)
                    .build().getLogDTO();
            logs.add(dto);
        });
        operationLogService.batchAdd(logs);
    }

    private List<ProjectDTO> getOrgListByProjectIds(List<String> projectIds) {
        return QueryChain.of(SystemProject.class)
                .select(SYSTEM_PROJECT.ID, SYSTEM_ORGANIZATION.ID.as("organizationId"))
                .from(SYSTEM_PROJECT).innerJoin(SYSTEM_ORGANIZATION).on(SYSTEM_ORGANIZATION.ID.eq(SYSTEM_PROJECT.ORGANIZATION_ID))
                .where(SYSTEM_PROJECT.ID.in(projectIds))
                .listAs(ProjectDTO.class);
    }

    @Override
    public int editSchedule(SystemSchedule schedule) {
        return mapper.update(schedule);
    }

    @Override
    public void runScheduleTask(RunScheduleRequest request, String userName, String path, String module) {
        SystemSchedule schedule = checkScheduleExit(request.getScheduleId());
        removeJob(schedule.getKey(), schedule.getJob());
        Map<String,String> object = new HashMap<>();
        object.put("sensorType", request.getSensorType());
        schedule.setConfig(object);
        JobKey jobKey = new JobKey(schedule.getKey(), schedule.getJob());
        TriggerKey triggerKey = new TriggerKey(schedule.getKey(), schedule.getJob());
        try {
            Class<?> targetClass = Class.forName(schedule.getJob());
            addOrUpdateCronJob(schedule, jobKey, triggerKey, targetClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        scheduleManager.startJobs(jobKey);
        saveLog(List.of(schedule), userName, path, HttpMethodConstants.GET.name(), module, OperationLogType.EXECUTE.name());
    }

    private void removeJob(String key, String job) {
        scheduleManager.removeJob(new JobKey(key, job), new TriggerKey(key, job));
    }
}
