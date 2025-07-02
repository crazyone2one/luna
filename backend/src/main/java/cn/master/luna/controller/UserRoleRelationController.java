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
import cn.master.luna.entity.UserRoleRelation;
import cn.master.luna.service.UserRoleRelationService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 用户组关系 控制层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-02
 */
@RestController
@Tag(name = "用户组关系接口")
@RequestMapping("/userRoleRelation")
public class UserRoleRelationController {

    @Autowired
    private UserRoleRelationService userRoleRelationService;

    /**
     * 添加用户组关系。
     *
     * @param userRoleRelation 用户组关系
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description="保存用户组关系")
    public boolean save(@RequestBody @Parameter(description="用户组关系")UserRoleRelation userRoleRelation) {
        return userRoleRelationService.save(userRoleRelation);
    }

    /**
     * 根据主键删除用户组关系。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键用户组关系")
    public boolean remove(@PathVariable @Parameter(description="用户组关系主键") String id) {
        return userRoleRelationService.removeById(id);
    }

    /**
     * 根据主键更新用户组关系。
     *
     * @param userRoleRelation 用户组关系
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新用户组关系")
    public boolean update(@RequestBody @Parameter(description="用户组关系主键") UserRoleRelation userRoleRelation) {
        return userRoleRelationService.updateById(userRoleRelation);
    }

    /**
     * 查询所有用户组关系。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有用户组关系")
    public List<UserRoleRelation> list() {
        return userRoleRelationService.list();
    }

    /**
     * 根据用户组关系主键获取详细信息。
     *
     * @param id 用户组关系主键
     * @return 用户组关系详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取用户组关系")
    public UserRoleRelation getInfo(@PathVariable @Parameter(description="用户组关系主键") String id) {
        return userRoleRelationService.getById(id);
    }

    /**
     * 分页查询用户组关系。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询用户组关系")
    public Page<UserRoleRelation> page(@Parameter(description="分页信息") Page<UserRoleRelation> page) {
        return userRoleRelationService.page(page);
    }

}
