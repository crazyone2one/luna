package cn.master.luna.controller;

import cn.master.luna.constants.OperationLogType;
import cn.master.luna.entity.SystemProject;
import cn.master.luna.entity.dto.ProjectDTO;
import cn.master.luna.entity.request.ProjectSwitchRequest;
import cn.master.luna.handler.ResultHandler;
import cn.master.luna.handler.annotation.Log;
import cn.master.luna.service.SystemProjectService;
import cn.master.luna.service.log.OrganizationProjectLogService;
import cn.master.luna.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目 控制层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
@Tag(name = "项目接口")
@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class SystemProjectController {

    private final SystemProjectService systemProjectService;

    /**
     * 添加项目。
     *
     * @param request 项目
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description = "保存项目")
    public ProjectDTO save(@RequestBody @Parameter(description = "项目") SystemProject request) {
        return systemProjectService.add(request, SessionUtils.getCurrentUserId());
    }

    /**
     * 根据主键删除项目。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description = "根据主键项目")
    public boolean remove(@PathVariable @Parameter(description = "项目主键") String id) {
        return systemProjectService.removeById(id);
    }

    /**
     * 根据主键更新项目。
     *
     * @param systemProject 项目
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PostMapping("/update")
    @Operation(description = "根据主键更新项目")
    public ProjectDTO update(@RequestBody @Parameter(description = "项目主键") SystemProject systemProject) {
        return systemProjectService.updateProject(systemProject, SessionUtils.getCurrentUserId());
    }

    /**
     * 查询所有项目。
     *
     * @return 所有数据
     */
    @GetMapping("/list/options/{organizationId}")
    @Operation(description = "查询所有项目")
    public List<SystemProject> list(@PathVariable String organizationId) {
        return systemProjectService.getUserProject(organizationId, SessionUtils.getCurrentUserId());
    }

    /**
     * 根据项目主键获取详细信息。
     *
     * @param id 项目主键
     * @return 项目详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取项目")
    public SystemProject getInfo(@PathVariable @Parameter(description = "项目主键") String id) {
        return systemProjectService.getById(id);
    }

    @PostMapping("/switch")
    @Operation(summary = "切换项目")
    public ResultHandler switchProject(@RequestBody ProjectSwitchRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("userInfo", systemProjectService.switchProject(request, SessionUtils.getCurrentUserId()));
        return ResultHandler.success(result);
    }

    @GetMapping("/organization/enable/{id}")
    @Operation(summary = "系统设置-组织-项目-启用")
    @Parameter(name = "id", description = "项目ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.updateLog(#id)", msClass = OrganizationProjectLogService.class)
    public void enable(@PathVariable String id) {
        systemProjectService.enable(id, SessionUtils.getUserName());
    }

    @GetMapping("/organization/disable/{id}")
    @Operation(summary = "系统设置-组织-项目-禁用")
    @Parameter(name = "id", description = "项目ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.updateLog(#id)", msClass = OrganizationProjectLogService.class)
    public void disable(@PathVariable String id) {
        systemProjectService.disable(id, SessionUtils.getUserName());
    }
}
