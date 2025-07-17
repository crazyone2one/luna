package cn.master.luna.controller;

import cn.master.luna.constants.OperationLogModule;
import cn.master.luna.constants.ScheduleResourceType;
import cn.master.luna.constants.ScheduleType;
import cn.master.luna.entity.SystemSchedule;
import cn.master.luna.entity.request.RunScheduleRequest;
import cn.master.luna.entity.request.SchedulePageRequest;
import cn.master.luna.entity.request.ScheduleRequest;
import cn.master.luna.service.SystemScheduleService;
import cn.master.luna.util.IDGenerator;
import cn.master.luna.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * 定时任务 控制层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
@RestController
@Tag(name = "定时任务接口")
@RequiredArgsConstructor
@RequestMapping("/system-schedule")
public class SystemScheduleController {

    private final SystemScheduleService systemScheduleService;

    /**
     * 添加定时任务。
     *
     * @param systemSchedule 定时任务
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description = "保存定时任务")
    public void save(@RequestBody @Parameter(description = "定时任务") SystemSchedule systemSchedule) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> targetClass = Class.forName(systemSchedule.getJob());
        Method getJobKey = targetClass.getMethod("getJobKey", String.class);
        Method getTriggerKey = targetClass.getMethod("getTriggerKey", String.class);
        Object instance = targetClass.getDeclaredConstructor().newInstance();
        SystemSchedule schedule = systemScheduleService.getScheduleByResource(systemSchedule.getResourceId(), targetClass.getName());
        Optional<SystemSchedule> optional = Optional.ofNullable(schedule);
        optional.ifPresentOrElse(s -> {
            s.setValue(systemSchedule.getValue());
            systemScheduleService.updateById(s);
            try {
                systemScheduleService.addOrUpdateCronJob(s,
                        (JobKey) getJobKey.invoke(instance, systemSchedule.getResourceId()),
                        (TriggerKey) getTriggerKey.invoke(instance, systemSchedule.getResourceId()),
                        targetClass);
            } catch (IllegalAccessException | RuntimeException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }, () -> {
            SystemSchedule build = SystemSchedule.builder()
                    .name(systemSchedule.getName())
                    .key(IDGenerator.nextStr())
                    .resourceId(systemSchedule.getResourceId())
                    .projectId(systemSchedule.getResourceId())
                    .enable(systemSchedule.getEnable())
                    .type(ScheduleType.CRON.name())
                    .value(systemSchedule.getValue())
                    .job(systemSchedule.getJob())
                    .resourceType(StringUtils.isBlank(systemSchedule.getResourceType()) ? ScheduleResourceType.LUNA.name() : systemSchedule.getResourceType())
                    .createUser(SessionUtils.getUserName())
                    .build();
            systemScheduleService.addSchedule(build);
            try {
                systemScheduleService.addOrUpdateCronJob(build,
                        (JobKey) getJobKey.invoke(instance, build.getKey()),
                        (TriggerKey) getTriggerKey.invoke(instance, build.getKey()),
                        targetClass);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 根据主键删除定时任务。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description = "根据主键定时任务")
    public boolean remove(@PathVariable @Parameter(description = "定时任务主键") String id) {
        return systemScheduleService.removeById(id);
    }

    /**
     * 根据主键更新定时任务。
     *
     * @param systemSchedule 定时任务
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description = "根据主键更新定时任务")
    public boolean update(@RequestBody @Parameter(description = "定时任务主键") SystemSchedule systemSchedule) {
        return systemScheduleService.updateById(systemSchedule);
    }

    /**
     * 查询所有定时任务。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description = "查询所有定时任务")
    public List<SystemSchedule> list() {
        return systemScheduleService.list();
    }

    /**
     * 根据定时任务主键获取详细信息。
     *
     * @param id 定时任务主键
     * @return 定时任务详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取定时任务")
    public SystemSchedule getInfo(@PathVariable @Parameter(description = "定时任务主键") String id) {
        return systemScheduleService.getById(id);
    }

    /**
     * 分页查询定时任务。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("/organization/schedule/page")
    @Operation(description = "分页查询定时任务")
    public Page<SystemSchedule> page(@Validated @RequestBody SchedulePageRequest request) {
        return systemScheduleService.getSchedulePage(request);
    }

    @GetMapping("/schedule/switch/{id}")
    @Operation(summary = "后台任务开启关闭")
    public void enable(@PathVariable String id) {
        systemScheduleService.enable(id);
    }

    @PostMapping("/organization/task-center/schedule/update-cron")
    @Operation(summary = "组织-任务中心-后台任务更新cron表达式")
    public void updateValue(@Validated @RequestBody ScheduleRequest request) {
        systemScheduleService.updateCron(request, SessionUtils.getUserName(), "/organization/task-center/schedule/update-cron", OperationLogModule.SETTING_ORGANIZATION_TASK_CENTER);
    }

    @PostMapping("/organization/task-center/schedule/run")
    @Operation(summary = "组织-任务中心-后台任务更新cron表达式")
    public void runScheduleTask(@Validated @RequestBody RunScheduleRequest request) {
        systemScheduleService.runScheduleTask(request, SessionUtils.getUserName(), "/organization/task-center/schedule/run/", OperationLogModule.SETTING_ORGANIZATION_TASK_CENTER);
    }
}
