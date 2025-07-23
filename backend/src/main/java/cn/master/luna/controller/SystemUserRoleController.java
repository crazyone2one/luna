package cn.master.luna.controller;

import cn.master.luna.constants.Created;
import cn.master.luna.entity.SystemUser;
import cn.master.luna.entity.SystemUserRole;
import cn.master.luna.entity.dto.PermissionDefinitionItem;
import cn.master.luna.entity.dto.UserExtendDTO;
import cn.master.luna.entity.request.*;
import cn.master.luna.service.SystemUserRoleService;
import cn.master.luna.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户组 控制层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-02
 */
@RestController
@Tag(name = "用户组接口")
@RequestMapping("/user/role")
@RequiredArgsConstructor
public class SystemUserRoleController {

    private final SystemUserRoleService systemUserRoleService;

    /**
     * 添加用户组。
     *
     * @param request 用户组
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("/organization/save")
    @Operation(description = "系统设置-组织-用户组-添加用户组")
    public SystemUserRole save(@Validated({Created.class}) @RequestBody @Parameter(description = "用户组") OrganizationUserRoleEditRequest request) {
        SystemUserRole userRole = new SystemUserRole();
        userRole.setCreateUser(SessionUtils.getUserName());
        BeanUtils.copyProperties(request, userRole);
        return systemUserRoleService.add(userRole);
    }

    @PostMapping("/global/save")
    @Operation(description = "系统设置-系统-用户组-添加自定义全局用户组")
    public SystemUserRole saveGlobalUserRole(@Validated({Created.class}) @RequestBody @Parameter(description = "用户组") UserRoleUpdateRequest request) {
        SystemUserRole userRole = new SystemUserRole();
        userRole.setCreateUser(SessionUtils.getUserName());
        BeanUtils.copyProperties(request, userRole);
        return systemUserRoleService.saveGlobalUserRole(userRole);
    }

    @PostMapping("/project/save")
    @Operation(description = "项目管理-项目与权限-用户组-添加用户组")
    public SystemUserRole saveProjectUserRole(@Validated({Created.class}) @RequestBody @Parameter(description = "用户组") ProjectUserRoleEditRequest request) {
        SystemUserRole userRole = new SystemUserRole();
        userRole.setCreateUser(SessionUtils.getUserName());
        BeanUtils.copyProperties(request, userRole);
        return systemUserRoleService.saveProjectUserRole(userRole);
    }

    /**
     * 根据主键删除用户组。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description = "根据主键用户组")
    public boolean remove(@PathVariable @Parameter(description = "用户组主键") String id) {
        return systemUserRoleService.removeById(id);
    }

    /**
     * 根据主键更新用户组。
     *
     * @param request 用户组
     * @return {@code SystemUserRole} 更新成功，{@code false} 更新失败
     */
    @PostMapping("/organization/update")
    @Operation(description = "系统设置-组织-用户组-修改用户组")
    public SystemUserRole update(@RequestBody @Parameter(description = "用户组主键") OrganizationUserRoleEditRequest request) {
        SystemUserRole userRole = new SystemUserRole();
        BeanUtils.copyProperties(request, userRole);
        return systemUserRoleService.update(userRole);
    }

    @PostMapping("/global/update")
    @Operation(description = "系统设置-系统-用户组-更新自定义全局用户组")
    public SystemUserRole updateGlobalUserRole(@RequestBody @Parameter(description = "用户组主键") UserRoleUpdateRequest request) {
        SystemUserRole userRole = new SystemUserRole();
        BeanUtils.copyProperties(request, userRole);
        return systemUserRoleService.updateGlobalUserRole(userRole);
    }
    @PostMapping("/project/update")
    @Operation(description = "项目管理-项目与权限-用户组-修改用户组")
    public SystemUserRole updateProjectUserRole(@RequestBody @Parameter(description = "用户组主键") ProjectUserRoleEditRequest request) {
        SystemUserRole userRole = new SystemUserRole();
        BeanUtils.copyProperties(request, userRole);
        return systemUserRoleService.updateProjectUserRole(userRole);
    }

    /**
     * 系统设置-系统-用户组-获取全局用户组列表。
     *
     * @return 所有数据
     */
    @GetMapping("/global/list")
    @Operation(description = "系统设置-系统-用户组-获取全局用户组列表")
    public List<SystemUserRole> list() {
        return systemUserRoleService.getGlobalList();
    }

    @GetMapping("/organization/list/{organizationId}")
    @Operation(description = "系统设置-组织-用户组-获取用户组列表")
    public List<SystemUserRole> list(@PathVariable String organizationId) {
        return systemUserRoleService.getOrgList(organizationId);
    }

    /**
     * 根据用户组主键获取详细信息。
     *
     * @param id 用户组主键
     * @return 用户组详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取用户组")
    public SystemUserRole getInfo(@PathVariable @Parameter(description = "用户组主键") String id) {
        return systemUserRoleService.getById(id);
    }

    /**
     * 分页查询用户组。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description = "分页查询用户组")
    public Page<SystemUserRole> page(@Parameter(description = "分页信息") Page<SystemUserRole> page) {
        return systemUserRoleService.page(page);
    }

    @GetMapping("/global/permission/setting/{id}")
    @Operation(summary = "系统设置-系统-用户组-获取全局用户组对应的权限配置")
    public List<PermissionDefinitionItem> getPermissionSetting(@PathVariable String id) {
        return systemUserRoleService.getPermissionSetting(id);
    }

    @PostMapping("/global/permission/update")
    @Operation(summary = "系统设置-系统-用户组-编辑全局用户组对应的权限配置")
    public void updateGlobalPermissionSetting(@Validated @RequestBody PermissionSettingUpdateRequest request) {
        systemUserRoleService.updateGlobalPermissionSetting(request);
    }

    @GetMapping("/organization/permission/setting/{id}")
    @Operation(summary = "系统设置-组织-用户组-获取用户组对应的权限配置")
    public List<PermissionDefinitionItem> getOrgPermissionSetting(@PathVariable String id) {
        return systemUserRoleService.getOrgPermissionSetting(id);
    }

    @PostMapping("/organization/permission/update")
    @Operation(summary = "系统设置-组织-用户组-修改用户组对应的权限配置")
    public void updateOrgPermissionSetting(@Validated @RequestBody PermissionSettingUpdateRequest request) {
        systemUserRoleService.updateOrgPermissionSetting(request);
    }

    @PostMapping("/organization/list-member")
    @Operation(summary = "系统设置-组织-用户组-获取成员列表")
    public Page<SystemUser> listMember(@Validated @RequestBody OrganizationUserRoleMemberRequest request) {
        return systemUserRoleService.listMember(request);
    }

    @PostMapping("/organization/add-member")
    @Operation(summary = "系统设置-组织-用户组-添加用户组成员")
    public void addMember(@Validated @RequestBody OrganizationUserRoleMemberEditRequest request) {
        systemUserRoleService.addMember(request);
    }

    @PostMapping("/organization/remove-member")
    @Operation(summary = "系统设置-组织-用户组-删除用户组成员")
    public void listMember(@Validated @RequestBody OrganizationUserRoleMemberEditRequest request) {
        systemUserRoleService.removeMember(request);
    }

    @GetMapping("/project/permission/setting/{id}")
    @Operation(summary = "项目管理-项目与权限-用户组-获取用户组对应的权限配置")
    public List<PermissionDefinitionItem> getProjectPermissionSetting(@PathVariable String id) {
        return systemUserRoleService.getProjectPermissionSetting(id);
    }

    @PostMapping("/project/permission/update")
    @Operation(summary = "项目管理-项目与权限-用户组-修改用户组对应的权限配置")
    public void updateProjectPermissionSetting(@Validated @RequestBody PermissionSettingUpdateRequest request) {
        systemUserRoleService.updateProjectPermissionSetting(request);
    }

    @GetMapping("/organization/get-member/option/{organizationId}/{roleId}")
    @Operation(summary = "系统设置-组织-用户组-获取成员下拉选项")
    public List<UserExtendDTO> getMember(@PathVariable String organizationId,
                                         @PathVariable String roleId,
                                         @Schema(description = "查询关键字，根据邮箱和用户名查询")
                                         @RequestParam(required = false) String keyword) {
        return systemUserRoleService.getMember(organizationId, roleId, keyword);
    }
}
