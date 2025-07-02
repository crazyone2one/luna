package cn.master.luna.controller;

import cn.master.luna.entity.SystemUser;
import cn.master.luna.service.SystemUserService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/systemUser")
public class SystemUserController {

    @Autowired
    private SystemUserService systemUserService;

    /**
     * 添加用户。
     *
     * @param systemUser 用户
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description="保存用户")
    public boolean save(@RequestBody @Parameter(description="用户")SystemUser systemUser) {
        return systemUserService.save(systemUser);
    }

    /**
     * 根据主键删除用户。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键用户")
    public boolean remove(@PathVariable @Parameter(description="用户主键") String id) {
        return systemUserService.removeById(id);
    }

    /**
     * 根据主键更新用户。
     *
     * @param systemUser 用户
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新用户")
    public boolean update(@RequestBody @Parameter(description="用户主键") SystemUser systemUser) {
        return systemUserService.updateById(systemUser);
    }

    /**
     * 查询所有用户。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有用户")
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
    @Operation(description="根据主键获取用户")
    public SystemUser getInfo(@PathVariable @Parameter(description="用户主键") String id) {
        return systemUserService.getById(id);
    }

    /**
     * 分页查询用户。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询用户")
    public Page<SystemUser> page(@Parameter(description="分页信息") Page<SystemUser> page) {
        return systemUserService.page(page);
    }

}
