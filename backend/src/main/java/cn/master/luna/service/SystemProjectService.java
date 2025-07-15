package cn.master.luna.service;

import cn.master.luna.entity.SystemProject;
import cn.master.luna.entity.dto.ProjectDTO;
import cn.master.luna.entity.dto.UserDTO;
import cn.master.luna.entity.dto.UserExtendDTO;
import cn.master.luna.entity.request.OrganizationProjectRequest;
import cn.master.luna.entity.request.ProjectSwitchRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 项目 服务层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
public interface SystemProjectService extends IService<SystemProject> {

    Page<ProjectDTO> getProjectPage(OrganizationProjectRequest request);

    ProjectDTO add(SystemProject request, String createUser);

    List<SystemProject> getUserProject(String organizationId, String userId);

    ProjectDTO updateProject(SystemProject systemProject, String updateUser);

    List<UserExtendDTO> getUserAdminList(String organizationId, String keyword);

    UserDTO switchProject(ProjectSwitchRequest request, String currentUserId);
}
