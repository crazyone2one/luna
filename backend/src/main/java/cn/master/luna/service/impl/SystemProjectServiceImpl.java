package cn.master.luna.service.impl;

import cn.master.luna.constants.InternalUserRole;
import cn.master.luna.entity.SystemOrganization;
import cn.master.luna.entity.SystemProject;
import cn.master.luna.entity.SystemUser;
import cn.master.luna.entity.UserRoleRelation;
import cn.master.luna.entity.dto.ProjectDTO;
import cn.master.luna.entity.request.OrganizationProjectRequest;
import cn.master.luna.exception.CustomException;
import cn.master.luna.mapper.SystemProjectMapper;
import cn.master.luna.service.SystemProjectService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.luna.entity.table.SystemOrganizationTableDef.SYSTEM_ORGANIZATION;
import static cn.master.luna.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;
import static cn.master.luna.entity.table.SystemUserRoleTableDef.SYSTEM_USER_ROLE;
import static cn.master.luna.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.luna.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;

/**
 * 项目 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SystemProjectServiceImpl extends ServiceImpl<SystemProjectMapper, SystemProject> implements SystemProjectService {

    @Override
    public Page<ProjectDTO> getProjectPage(OrganizationProjectRequest request) {
        Page<ProjectDTO> page = queryChain().select(SYSTEM_PROJECT.ALL_COLUMNS)
                .select(SYSTEM_ORGANIZATION.NAME.as("organization_name"))
                .from(SYSTEM_PROJECT)
                .innerJoin(SYSTEM_ORGANIZATION).on(SYSTEM_PROJECT.ORGANIZATION_ID.eq(SYSTEM_ORGANIZATION.ID))
                .where(SYSTEM_PROJECT.ORGANIZATION_ID.eq(request.getOrganizationId())
                        .and(SYSTEM_PROJECT.NAME.like(request.getKeyword())
                                .or(SYSTEM_PROJECT.NUM.like(request.getKeyword()))))
                .orderBy(SYSTEM_PROJECT.CREATE_TIME.desc())
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), ProjectDTO.class);
        buildUserInfo(page);
        return page;
    }

    @Override
    public ProjectDTO add(SystemProject request, String createUser) {
        ProjectDTO projectDTO = new ProjectDTO();
        return projectDTO;
    }

    @Override
    public List<SystemProject> getUserProject(String organizationId, String userId) {
        checkOrg(organizationId);
        SystemUser user = QueryChain.of(SystemUser.class).where(SystemUser::getId).eq(userId).one();
        String projectId;
        if (user != null && StringUtils.isNotBlank(user.getLastProjectId())) {
            projectId = user.getLastProjectId();
        } else {
            projectId = null;
        }
        //判断用户是否是系统管理员
        List<SystemProject> allProject;
        boolean exists = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.eq(userId)
                .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.ADMIN.name()))).exists();
        if (exists) {
            allProject = queryChain().where(SYSTEM_PROJECT.ORGANIZATION_ID.eq(organizationId)
                    .and(SYSTEM_PROJECT.ENABLE.eq(true))
            ).orderBy(SYSTEM_PROJECT.NAME.desc()).list();
        } else {
            allProject = queryChain().select(QueryMethods.distinct(SYSTEM_PROJECT.ALL_COLUMNS))
                    .from(SYSTEM_USER_ROLE)
                    .join(USER_ROLE_RELATION).on(SYSTEM_USER_ROLE.ID.eq(USER_ROLE_RELATION.ROLE_ID))
                    .join(SYSTEM_PROJECT).on(SYSTEM_PROJECT.ID.eq(USER_ROLE_RELATION.SOURCE_ID))
                    .join(SYSTEM_USER).on(SYSTEM_USER.ID.eq(USER_ROLE_RELATION.USER_ID))
                    .where(USER_ROLE_RELATION.USER_ID.eq(userId)
                            .and(SYSTEM_USER_ROLE.TYPE.eq("PROJECT"))
                            .and(SYSTEM_PROJECT.ORGANIZATION_ID.eq(organizationId)).and(SYSTEM_PROJECT.ENABLE.eq(true)))
                    .orderBy(SYSTEM_PROJECT.NAME.desc())
                    .list();
        }
        List<SystemProject> temp = allProject;
        return allProject.stream()
                .filter(project -> StringUtils.equals(project.getId(), projectId))
                .findFirst()
                .map(project -> {
                    temp.remove(project);
                    temp.addFirst(project);
                    return temp;
                })
                .orElse(allProject);
    }

    private void checkOrg(String organizationId) {
        QueryChain.of(SystemOrganization.class).where(SYSTEM_ORGANIZATION.ID.eq(organizationId))
                .oneOpt().orElseThrow(() -> new CustomException("<组织不存在>"));
    }

    private void buildUserInfo(Page<ProjectDTO> page) {
        List<ProjectDTO> projectList = page.getRecords();
        // 获取项目id
        List<String> projectIds = projectList.stream().map(ProjectDTO::getId).toList();
        List<ProjectDTO> projectDTOList = getProjectExtendDTOList(projectIds);
        Map<String, ProjectDTO> projectMap = projectDTOList.stream().collect(Collectors.toMap(ProjectDTO::getId, projectDTO -> projectDTO));
        projectList.forEach(projectDTO -> {
            projectDTO.setMemberCount(projectMap.get(projectDTO.getId()).getMemberCount());
        });
        page.setRecords(projectList);
    }

    private List<ProjectDTO> getProjectExtendDTOList(List<String> projectIds) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select(USER_ROLE_RELATION.SOURCE_ID, SYSTEM_USER.ID)
                .from(USER_ROLE_RELATION)
                .leftJoin(SYSTEM_USER).on(SYSTEM_USER.ID.eq(USER_ROLE_RELATION.USER_ID))
                .where(USER_ROLE_RELATION.SOURCE_ID.in(projectIds));
        return queryChain().select(SYSTEM_PROJECT.ID)
                .select("count(distinct temp.id) as memberCount")
                .from(SYSTEM_PROJECT.as("p"))
                .leftJoin(queryWrapper).as("temp").on("p.id = temp.source_id")
                .groupBy(SYSTEM_PROJECT.ID).listAs(ProjectDTO.class);
    }
}
