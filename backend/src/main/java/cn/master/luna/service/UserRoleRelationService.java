package cn.master.luna.service;

import cn.master.luna.entity.SystemUser;
import cn.master.luna.entity.UserRoleRelation;
import cn.master.luna.entity.dto.UserRoleRelationUserDTO;
import cn.master.luna.entity.request.GlobalUserRoleRelationQueryRequest;
import cn.master.luna.entity.request.GlobalUserRoleRelationUpdateRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 用户组关系 服务层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-02
 */
public interface UserRoleRelationService extends IService<UserRoleRelation> {

    Page<UserRoleRelationUserDTO> listUser(GlobalUserRoleRelationQueryRequest request);

    void delete(String id);

    void add(GlobalUserRoleRelationUpdateRequest request);

    void updateUserSystemGlobalRole(@Valid SystemUser user, @Valid @NotEmpty String operator, @Valid @NotEmpty List<String> roleList);
}
