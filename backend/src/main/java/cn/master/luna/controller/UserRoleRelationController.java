package cn.master.luna.controller;

import cn.master.luna.constants.Created;
import cn.master.luna.entity.UserRoleRelation;
import cn.master.luna.entity.dto.UserRoleRelationUserDTO;
import cn.master.luna.entity.request.GlobalUserRoleRelationQueryRequest;
import cn.master.luna.entity.request.GlobalUserRoleRelationUpdateRequest;
import cn.master.luna.service.UserRoleRelationService;
import cn.master.luna.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户组关系 控制层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-02
 */
@RestController
@Tag(name = "用户组关系接口")
@RequiredArgsConstructor
@RequestMapping("/user/role/relation")
public class UserRoleRelationController {

    private final UserRoleRelationService userRoleRelationService;

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
    @GetMapping("/global/delete/{id}")
    @Operation(description="系统设置-系统-用户组-用户关联关系-删除全局用户组和用户的关联关系")
    public void remove(@PathVariable @Parameter(description="用户组关系主键") String id) {
        userRoleRelationService.delete(id);
    }

    @PostMapping("/global/add")
    @Operation(description = "系统设置-系统-用户组-用户关联关系-创建全局用户组和用户的关联关系")
    public void add(@Validated({Created.class}) @RequestBody GlobalUserRoleRelationUpdateRequest request) {
        request.setCreateUser(SessionUtils.getUserName());
        userRoleRelationService.add(request);
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
    @PostMapping("/global/list")
    @Operation(description="系统设置-系统-用户组-用户关联关系-获取全局用户组对应的用户列表")
    public Page<UserRoleRelationUserDTO> list(@Validated @RequestBody GlobalUserRoleRelationQueryRequest request) {
        return userRoleRelationService.listUser(request);
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
