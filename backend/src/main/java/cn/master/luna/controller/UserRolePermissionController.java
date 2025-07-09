package cn.master.luna.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.luna.entity.UserRolePermission;
import cn.master.luna.service.UserRolePermissionService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 用户组权限 控制层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
@RestController
@Tag(name = "用户组权限接口")
@RequestMapping("/userRolePermission")
public class UserRolePermissionController {

    @Autowired
    private UserRolePermissionService userRolePermissionService;

    /**
     * 添加用户组权限。
     *
     * @param userRolePermission 用户组权限
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description="保存用户组权限")
    public boolean save(@RequestBody @Parameter(description="用户组权限")UserRolePermission userRolePermission) {
        return userRolePermissionService.save(userRolePermission);
    }

    /**
     * 根据主键删除用户组权限。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键用户组权限")
    public boolean remove(@PathVariable @Parameter(description="用户组权限主键") String id) {
        return userRolePermissionService.removeById(id);
    }

    /**
     * 根据主键更新用户组权限。
     *
     * @param userRolePermission 用户组权限
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新用户组权限")
    public boolean update(@RequestBody @Parameter(description="用户组权限主键") UserRolePermission userRolePermission) {
        return userRolePermissionService.updateById(userRolePermission);
    }

    /**
     * 查询所有用户组权限。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有用户组权限")
    public List<UserRolePermission> list() {
        return userRolePermissionService.list();
    }

    /**
     * 根据用户组权限主键获取详细信息。
     *
     * @param id 用户组权限主键
     * @return 用户组权限详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取用户组权限")
    public UserRolePermission getInfo(@PathVariable @Parameter(description="用户组权限主键") String id) {
        return userRolePermissionService.getById(id);
    }

    /**
     * 分页查询用户组权限。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询用户组权限")
    public Page<UserRolePermission> page(@Parameter(description="分页信息") Page<UserRolePermission> page) {
        return userRolePermissionService.page(page);
    }

}
