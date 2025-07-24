package cn.master.luna.service;

import cn.master.luna.entity.SystemUser;
import cn.master.luna.entity.dto.BasePageRequest;
import cn.master.luna.entity.dto.UserExtendDTO;
import cn.master.luna.entity.dto.UserTableResponse;
import cn.master.luna.entity.request.AddUserRequest;
import cn.master.luna.entity.request.MemberRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

/**
 * 用户 服务层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-01
 */
public interface SystemUserService extends IService<SystemUser> {

    Page<UserTableResponse> getUserPage(BasePageRequest request);

    SystemUser addUser(AddUserRequest request);

    Page<UserExtendDTO> getMemberList(MemberRequest request);
}
