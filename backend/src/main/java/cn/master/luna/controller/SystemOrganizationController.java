package cn.master.luna.controller;

import cn.master.luna.entity.SystemOrganization;
import cn.master.luna.entity.dto.OptionDTO;
import cn.master.luna.entity.dto.ProjectDTO;
import cn.master.luna.entity.dto.UserExtendDTO;
import cn.master.luna.entity.request.OrganizationProjectRequest;
import cn.master.luna.service.SystemOrganizationService;
import cn.master.luna.service.SystemProjectService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 组织 控制层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
@RestController
@Tag(name = "组织接口")
@RequiredArgsConstructor
@RequestMapping("/organization")
public class SystemOrganizationController {

    private final SystemOrganizationService systemOrganizationService;
    private final SystemProjectService systemProjectService;

    /**
     * 添加组织。
     *
     * @param systemOrganization 组织
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description = "保存组织")
    public boolean save(@RequestBody @Parameter(description = "组织") SystemOrganization systemOrganization) {
        return systemOrganizationService.save(systemOrganization);
    }

    /**
     * 根据主键删除组织。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description = "根据主键组织")
    public boolean remove(@PathVariable @Parameter(description = "组织主键") String id) {
        return systemOrganizationService.removeById(id);
    }

    /**
     * 根据主键更新组织。
     *
     * @param systemOrganization 组织
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description = "根据主键更新组织")
    public boolean update(@RequestBody @Parameter(description = "组织主键") SystemOrganization systemOrganization) {
        return systemOrganizationService.updateById(systemOrganization);
    }

    /**
     * 查询所有组织。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description = "查询所有组织")
    public List<SystemOrganization> list() {
        return systemOrganizationService.list();
    }

    /**
     * 根据组织主键获取详细信息。
     *
     * @param id 组织主键
     * @return 组织详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取组织")
    public SystemOrganization getInfo(@PathVariable @Parameter(description = "组织主键") String id) {
        return systemOrganizationService.getById(id);
    }

    /**
     * 分页查询组织。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description = "分页查询组织")
    public Page<SystemOrganization> page(@Parameter(description = "分页信息") Page<SystemOrganization> page) {
        return systemOrganizationService.page(page);
    }

    /**
     * 分页查询项目。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("/project/page")
    @Operation(description = "分页查询项目")
    public Page<ProjectDTO> page(@Validated @RequestBody OrganizationProjectRequest request) {
        return systemProjectService.getProjectPage(request);
    }

    @PostMapping("/option/all")
    @Operation(summary = "系统设置-系统-组织与项目-组织-获取系统所有组织下拉选项")
    public List<OptionDTO> listAll() {
        return systemOrganizationService.listAll();
    }

    @GetMapping("/project/user-admin-list/{organizationId}")
    @Operation(summary = "系统设置-组织-项目-获取项目管理员下拉选项")
    public List<UserExtendDTO> getUserAdminList(@PathVariable String organizationId, @Schema(description = "查询关键字，根据邮箱和用户名查询")
    @RequestParam(value = "keyword", required = false) String keyword) {
        return systemProjectService.getUserAdminList(organizationId, keyword);
    }
}
