package cn.master.luna.controller;

import cn.master.luna.constants.Created;
import cn.master.luna.entity.SystemUser;
import cn.master.luna.entity.dto.BasePageRequest;
import cn.master.luna.entity.dto.UserExtendDTO;
import cn.master.luna.entity.dto.UserSelectOption;
import cn.master.luna.entity.dto.UserTableResponse;
import cn.master.luna.entity.request.AddUserRequest;
import cn.master.luna.entity.request.MemberRequest;
import cn.master.luna.service.SystemUserRoleService;
import cn.master.luna.service.SystemUserService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户 控制层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-01
 */
@RestController
@Tag(name = "用户接口")
@RequiredArgsConstructor
@RequestMapping("/system/user")
public class SystemUserController {

    private final SystemUserService systemUserService;
    private final SystemUserRoleService systemUserRoleService;

    /**
     * 添加用户。
     *
     * @param request 用户
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description = "保存用户")
    public SystemUser save(@Validated({Created.class}) @RequestBody @Parameter(description = "用户") AddUserRequest request) {
        return systemUserService.addUser(request);
    }

    /**
     * 根据主键删除用户。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description = "根据主键用户")
    public boolean remove(@PathVariable @Parameter(description = "用户主键") String id) {
        return systemUserService.removeById(id);
    }

    /**
     * 根据主键更新用户。
     *
     * @param systemUser 用户
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description = "根据主键更新用户")
    public boolean update(@RequestBody @Parameter(description = "用户主键") SystemUser systemUser) {
        return systemUserService.updateById(systemUser);
    }

    /**
     * 查询所有用户。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description = "查询所有用户")
    public List<SystemUser> list() {
        return systemUserService.list();
    }

    /**
     * 根据用户主键获取详细信息。
     *
     * @param id 用户主键
     * @return 用户详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取用户")
    public SystemUser getInfo(@PathVariable @Parameter(description = "用户主键") String id) {
        return systemUserService.getById(id);
    }

    /**
     * 分页查询用户。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("/page")
    @Operation(description = "分页查询用户")
    public Page<UserTableResponse> page(@Validated @RequestBody BasePageRequest request) {
        return systemUserService.getUserPage(request);
    }

    @GetMapping("/get/global/system/role")
    @Operation(summary = "系统设置-系统-用户-查找系统级用户组")
    public List<UserSelectOption> getGlobalSystemRole() {
        return systemUserRoleService.getGlobalSystemRoleList();
    }

    @PostMapping("/organization/member-list")
    @Operation(summary = "系统设置-系统-组织与项目-获取添加成员列表")
    public Page<UserExtendDTO> getMemberOptionList(@Validated @RequestBody MemberRequest request) {
        return systemUserService.getMemberList(request);
    }
}
