package cn.master.luna.service.impl;

import cn.master.luna.constants.UserRoleScope;
import cn.master.luna.entity.*;
import cn.master.luna.entity.dto.*;
import cn.master.luna.entity.request.AddUserRequest;
import cn.master.luna.entity.request.MemberRequest;
import cn.master.luna.exception.CustomException;
import cn.master.luna.handler.listener.UserImportEventListener;
import cn.master.luna.mapper.SystemUserMapper;
import cn.master.luna.mapper.UserRoleRelationMapper;
import cn.master.luna.service.SystemUserService;
import cn.master.luna.service.UserRoleRelationService;
import cn.master.luna.util.SessionUtils;
import cn.master.luna.util.Translator;
import com.alibaba.excel.EasyExcelFactory;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.master.luna.entity.table.SystemUserRoleTableDef.SYSTEM_USER_ROLE;
import static cn.master.luna.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.luna.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;

/**
 * 用户 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRelationMapper userRoleRelationMapper;
    private final UserRoleRelationService userRoleRelationService;

    @Override
    public Page<UserTableResponse> getUserPage(BasePageRequest request) {
        Page<UserTableResponse> page = queryChain()
                .where(SYSTEM_USER.ID.eq(request.getKeyword())
                        .or(SYSTEM_USER.NAME.eq(request.getKeyword()))
                        .or(SYSTEM_USER.EMAIL.eq(request.getKeyword()))
                        .or(SYSTEM_USER.PHONE.eq(request.getKeyword())))
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), UserTableResponse.class);
        if (!page.getRecords().isEmpty()) {
            List<String> userIds = page.getRecords().stream().map(SystemUser::getId).toList();
            Map<String, UserTableResponse> roleAndOrganizationMap = selectGlobalUserRoleAndOrganization(userIds);
            page.getRecords().forEach(user -> {
                UserTableResponse roleOrgModel = roleAndOrganizationMap.get(user.getId());
                if (Objects.nonNull(roleOrgModel)) {
                    user.setOrganizationList(roleOrgModel.getOrganizationList());
                    user.setUserRoleList(roleOrgModel.getUserRoleList());
                }
            });
        }
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemUser addUser(AddUserRequest request) {
        checkRoleIsGlobalAndHaveMember(request.getUserRoleIdList(), true);
        validateUserInfo(request.getEmail());
        SystemUser user = new SystemUser();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setEnable(true);
        user.setSource("LOCAL");
        user.setCreateUser(SessionUtils.getCurrentUserId());
        user.setUpdateUser(SessionUtils.getCurrentUserId());
        user.setPassword(passwordEncoder.encode(request.getEmail()));
        mapper.insert(user);
        if (!request.getUserRoleIdList().isEmpty()) {
            request.getUserRoleIdList().forEach(userRoleId -> {
                UserRoleRelation relation = new UserRoleRelation();
                relation.setRoleId(userRoleId);
                relation.setUserId(user.getId());
                relation.setSourceId(UserRoleScope.SYSTEM);
                relation.setOrganizationId(UserRoleScope.SYSTEM);
                relation.setCreateUser(SessionUtils.getCurrentUserId());
                userRoleRelationMapper.insert(relation);
            });
        }
        return user;
    }

    @Override
    public Page<UserExtendDTO> getMemberList(MemberRequest request) {
        return queryChain()
                .select(QueryMethods.distinct(SYSTEM_USER.ALL_COLUMNS))
                .select("count(urr.id) > 0 as memberFlag")
                .from(SYSTEM_USER).as("u")
                .leftJoin(USER_ROLE_RELATION).as("urr").on(USER_ROLE_RELATION.USER_ID.eq(SYSTEM_USER.ID)
                        .and(USER_ROLE_RELATION.SOURCE_ID.eq(request.getSourceId())))
                .where(SYSTEM_USER.NAME.like(request.getKeyword()).or(SYSTEM_USER.EMAIL.like(request.getKeyword())))
                .groupBy(SYSTEM_USER.ID)
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), UserExtendDTO.class);
    }

    @Override
    public List<String> getBatchUserIds(TableBatchProcessDTO request) {
        if (request.isSelectAll()) {
            List<String> userIdList = queryChain().select(SYSTEM_USER.ID).from(SYSTEM_USER)
                    .where(SYSTEM_USER.ID.eq(request.getCondition().getKeyword())
                            .or(SYSTEM_USER.NAME.like(request.getCondition().getKeyword())
                                    .or(SYSTEM_USER.EMAIL.like(request.getCondition().getKeyword()))
                                    .or(SYSTEM_USER.PHONE.like(request.getCondition().getKeyword()))))
                    .listAs(String.class);
            if (CollectionUtils.isNotEmpty(request.getExcludeIds())) {
                userIdList.removeAll(request.getExcludeIds());
            }
            return userIdList;
        } else {
            return request.getSelectIds();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TableBatchProcessResponse resetPassword(TableBatchProcessDTO request, String userName) {
        request.setSelectIds(getBatchUserIds(request));
        checkUserInDb(request.getSelectIds());
        List<SystemUser> users = mapper.selectListByIds(request.getSelectIds());
        users.forEach(user -> {
            SystemUser newUser = new SystemUser();
            newUser.setId(user.getId());
            if (user.getName().equals("admin")) {
                newUser.setPassword(passwordEncoder.encode("Password@"));
            } else {
                newUser.setPassword(passwordEncoder.encode(user.getEmail()));
            }
            newUser.setUpdateUser(userName);
            mapper.update(newUser);
        });
        TableBatchProcessResponse response = new TableBatchProcessResponse();
        response.setTotalCount(request.getSelectIds().size());
        response.setSuccessCount(request.getSelectIds().size());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TableBatchProcessResponse deleteUser(TableBatchProcessDTO request, String operatorId, String operatorName) {
        List<String> userIdList = getBatchUserIds(request);
        checkUserInDb(userIdList);
        //检查是否含有Admin
        checkProcessUserAndThrowException(userIdList, operatorId, operatorName, Translator.get("user.not.delete"));
        TableBatchProcessResponse response = new TableBatchProcessResponse();
        response.setTotalCount(userIdList.size());
        response.setSuccessCount(mapper.deleteBatchByIds(userIdList));
        //删除用户角色关系
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where(USER_ROLE_RELATION.USER_ID.in(userIdList));
        userRoleRelationMapper.deleteByQuery(queryWrapper);
        return response;
    }

    @Override
    public AddUserRequest updateUser(AddUserRequest request, String operator) {
        checkRoleIsGlobalAndHaveMember(request.getUserRoleIdList(), true);
        checkUserEmail(request.getId(), request.getEmail());
        SystemUser user = new SystemUser();
        BeanUtils.copyProperties(request, user);
        user.setUpdateUser(operator);
        mapper.update(user);
        userRoleRelationService.updateUserSystemGlobalRole(user, user.getUpdateUser(), request.getUserRoleIdList());
        return request;
    }

    @Override
    public UserImportResponse importByExcel(MultipartFile excelFile, String source, String operator) {
        UserImportResponse importResponse = new UserImportResponse();
        ExcelParseDTO<UserExcelRowDTO> excelParseDTO = new ExcelParseDTO<>();
        try {
            excelParseDTO = getUserExcelParseDTO(excelFile);
        } catch (Exception e) {
            log.error("import user  error", e);
        }
        if (CollectionUtils.isNotEmpty(excelParseDTO.getDataList())) {
            saveUserByExcelData(excelParseDTO.getDataList(), source, operator);
        }
        importResponse.generateResponse(excelParseDTO);
        return importResponse;
    }

    private void saveUserByExcelData(@Valid @NotEmpty List<UserExcelRowDTO> dataList,
                                     @Valid @NotEmpty String source,
                                     @Valid @NotBlank String operator) {
        dataList.forEach(dto -> {
            SystemUser user = new SystemUser();
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setPhone(dto.getPhone());
            user.setEnable(true);
            user.setSource(source);
            user.setCreateUser(operator);
            user.setUpdateUser(operator);
            user.setPassword(passwordEncoder.encode(dto.getEmail()));
            mapper.insert(user);
            UserRoleRelation relation = new UserRoleRelation();
            relation.setRoleId("member");
            relation.setUserId(user.getId());
            relation.setSourceId(UserRoleScope.SYSTEM);
            relation.setOrganizationId(UserRoleScope.SYSTEM);
            relation.setCreateUser(SessionUtils.getCurrentUserId());
            userRoleRelationMapper.insert(relation);
        });
    }

    private ExcelParseDTO<UserExcelRowDTO> getUserExcelParseDTO(MultipartFile excelFile) throws IOException {
        UserImportEventListener userImportEventListener = new UserImportEventListener();
        EasyExcelFactory.read(excelFile.getInputStream(), UserTemplate.class, userImportEventListener).sheet().doRead();
        return validateExcelUserInfo(userImportEventListener.getExcelParseDTO());
    }

    private ExcelParseDTO<UserExcelRowDTO> validateExcelUserInfo(@Valid @NotNull ExcelParseDTO<UserExcelRowDTO> excelParseDTO) {
        List<UserExcelRowDTO> prepareSaveList = excelParseDTO.getDataList();
        if (CollectionUtils.isNotEmpty(prepareSaveList)) {
            Map<String, String> userInDbMap = queryChain()
                    .where(SYSTEM_USER.EMAIL.in(prepareSaveList.stream().map(UserExcelRowDTO::getEmail).collect(Collectors.toList())))
                    .list().stream().collect(Collectors.toMap(SystemUser::getEmail, SystemUser::getId));
            for (UserExcelRowDTO dto : prepareSaveList) {
                if (userInDbMap.containsKey(dto.getEmail())) {
                    dto.setErrorMessage(Translator.get("user.email.import.in_system") + ": " + dto.getEmail());
                    excelParseDTO.addErrorRowData(dto.getDataIndex(), dto);
                }
            }
            excelParseDTO.getDataList().removeAll(excelParseDTO.getErrRowData().values());
        }
        return excelParseDTO;
    }

    private void checkUserEmail(String id, String email) {
        if (queryChain().where(SYSTEM_USER.EMAIL.eq(email).and(SYSTEM_USER.ID.ne(id))).exists()) {
            throw new CustomException(Translator.get("user_email_already_exists"));
        }
    }

    private void checkProcessUserAndThrowException(List<String> userIdList, String operatorId, String operatorName, String exceptionMessage) {
        for (String userId : userIdList) {
            //当前用户或admin不能被操作
            if (Strings.CS.equals(userId, operatorId)) {
                throw new CustomException(exceptionMessage + ":" + operatorName);
            } else if (Strings.CS.equals(userId, "71026562839000138")) {
                throw new CustomException(exceptionMessage + ": 71026562839000138");
            }
        }
    }

    private void checkUserInDb(@Valid List<String> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            throw new CustomException(Translator.get("user.not.exist"));
        }
        List<SystemUser> users = mapper.selectListByIds(userIdList);
        if (userIdList.size() != users.size()) {
            throw new CustomException(Translator.get("user.not.exist"));
        }
    }

    private void validateUserInfo(String email) {
        if (queryChain().where(SystemUser::getEmail).eq(email).exists()) {
            throw new CustomException("<邮箱重复>email");
        }
    }

    private void checkRoleIsGlobalAndHaveMember(@Valid @NotEmpty List<String> roleIdList, boolean isSystem) {
        long count = QueryChain.of(SystemUserRole.class).where(SYSTEM_USER_ROLE.ID.in(roleIdList)
                        .and(SYSTEM_USER_ROLE.TYPE.eq("SYSTEM").when(isSystem))
                        .and(SYSTEM_USER_ROLE.SCOPE_ID.eq("GLOBAL")))
                .count();
        if (count != roleIdList.size()) {
            throw new CustomException("<角色不是全局系统角色>");
        }
    }

    private Map<String, UserTableResponse> selectGlobalUserRoleAndOrganization(@Valid @NotEmpty List<String> userIds) {
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class).where(UserRoleRelation::getUserId).in(userIds).list();
        List<String> userRoleIdList = userRoleRelations.stream().map(UserRoleRelation::getRoleId).distinct().toList();
        List<String> sourceIdList = userRoleRelations.stream().map(UserRoleRelation::getSourceId).distinct().toList();
        Map<String, SystemUserRole> userRoleMap = new HashMap<>();
        Map<String, SystemOrganization> organizationMap = new HashMap<>();
        if (!userRoleIdList.isEmpty()) {
            userRoleMap = QueryChain.of(SystemUserRole.class)
                    .where(SystemUserRole::getId).in(userRoleIdList)
                    .and(SystemUserRole::getScopeId).eq("global")
                    .list().stream()
                    .collect(Collectors.toMap(SystemUserRole::getId, item -> item));
        }
        if (!sourceIdList.isEmpty()) {
            organizationMap = QueryChain.of(SystemOrganization.class)
                    .where(SystemOrganization::getId).in(sourceIdList)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(SystemOrganization::getId, item -> item));
        }
        Map<String, UserTableResponse> returnMap = new HashMap<>();
        for (UserRoleRelation userRoleRelation : userRoleRelations) {
            UserTableResponse userInfo = returnMap.get(userRoleRelation.getUserId());
            if (userInfo == null) {
                userInfo = new UserTableResponse();
                userInfo.setId(userRoleRelation.getUserId());
                returnMap.put(userRoleRelation.getUserId(), userInfo);
            }
            SystemUserRole userRole = userRoleMap.get(userRoleRelation.getRoleId());
            if (userRole != null && StringUtils.equalsIgnoreCase(userRole.getType(), "system")) {
                userInfo.setUserRole(userRole);
            }
            SystemOrganization organization = organizationMap.get(userRoleRelation.getSourceId());
            if (organization != null && !userInfo.getOrganizationList().contains(organization)) {
                userInfo.setOrganization(organization);
            }
        }
        return returnMap;
    }
}
