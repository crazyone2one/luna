package cn.master.luna.controller;

import cn.master.luna.constants.ScheduleResourceType;
import cn.master.luna.constants.ScheduleType;
import cn.master.luna.entity.SystemSchedule;
import cn.master.luna.job.DemoJob;
import cn.master.luna.service.SystemScheduleService;
import cn.master.luna.util.IDGenerator;
import cn.master.luna.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public void save(@RequestBody @Parameter(description = "定时任务") SystemSchedule systemSchedule) {
        Boolean enable = true;
        String typeValue = "0 0/1 * * * ?";
        String projectId = "100001100001";
        SystemSchedule schedule = systemScheduleService.getScheduleByResource(projectId, DemoJob.class.getName());
        Optional<SystemSchedule> optional = Optional.ofNullable(schedule);
        optional.ifPresentOrElse(s -> {
            s.setValue(typeValue);
            systemScheduleService.updateById(s);
            systemScheduleService.addOrUpdateCronJob(s, DemoJob.getJobKey(projectId), DemoJob.getTriggerKey(projectId), DemoJob.class);
        }, () -> {
            SystemSchedule build = SystemSchedule.builder()
                    .name("demo schedule job")
                    .key(IDGenerator.nextStr())
                    .resourceId(projectId)
                    .projectId(projectId)
                    .enable(enable)
                    .type(ScheduleType.CRON.name())
                    .value(typeValue)
                    .job(DemoJob.class.getName())
                    .resourceType(ScheduleResourceType.DEMO.name())
                    .createUser(SessionUtils.getUserName())
                    .build();
            systemScheduleService.addSchedule(build);
            systemScheduleService.addOrUpdateCronJob(build, DemoJob.getJobKey(projectId), DemoJob.getTriggerKey(projectId), DemoJob.class);
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
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description = "分页查询定时任务")
    public Page<SystemSchedule> page(@Parameter(description = "分页信息") Page<SystemSchedule> page) {
        return systemScheduleService.page(page);
    }

}
