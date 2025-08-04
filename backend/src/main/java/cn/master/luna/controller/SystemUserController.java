package cn.master.luna.controller;

import cn.master.luna.constants.Created;
import cn.master.luna.constants.OperationLogType;
import cn.master.luna.constants.Updated;
import cn.master.luna.entity.SystemUser;
import cn.master.luna.entity.UserTemplate;
import cn.master.luna.entity.dto.*;
import cn.master.luna.entity.request.AddUserRequest;
import cn.master.luna.entity.request.MemberRequest;
import cn.master.luna.handler.annotation.Log;
import cn.master.luna.service.SystemUserRoleService;
import cn.master.luna.service.SystemUserService;
import cn.master.luna.service.log.UserLogService;
import cn.master.luna.util.SessionUtils;
import com.alibaba.excel.EasyExcelFactory;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
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

    @PostMapping("/delete")
    @Operation(summary = "系统设置-系统-用户-删除用户")
    @Log(type = OperationLogType.DELETE, expression = "#msClass.deleteLog(#request)", msClass = UserLogService.class)
    public TableBatchProcessResponse remove(@Validated @RequestBody TableBatchProcessDTO request) {
        return systemUserService.deleteUser(request, SessionUtils.getCurrentUserId(), SessionUtils.getUserName());
    }

    /**
     * 根据主键更新用户。
     *
     * @param request 用户
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PostMapping("update")
    @Operation(description = "系统设置-系统-用户-修改用户")
    public AddUserRequest update(@Validated({Updated.class}) @RequestBody AddUserRequest request) {
        return systemUserService.updateUser(request, SessionUtils.getUserName());
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

    @PostMapping("/reset/password")
    @Operation(summary = "系统设置-系统-用户-重置用户密码")
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.resetPasswordLog(#request)", msClass = UserLogService.class)
    public TableBatchProcessResponse resetPassword(@Validated @RequestBody TableBatchProcessDTO request) {
        return systemUserService.resetPassword(request, SessionUtils.getUserName());
    }

    @GetMapping("/templates/download")
    @Operation(summary = "系统设置-系统-用户-用户导入模板")
    public void download(HttpServletResponse response) throws IOException {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("user_import_" + System.currentTimeMillis(), StandardCharsets.UTF_8);
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcelFactory.write(response.getOutputStream(), UserTemplate.class).sheet("模板").doWrite((Collection<?>) null);
    }

    @PostMapping(value = "/import", consumes = {"multipart/form-data"})
    @Operation(summary = "系统设置-系统-用户-导入用户")
    public UserImportResponse importUser(@RequestPart(value = "file", required = false) MultipartFile excelFile) {
        return systemUserService.importByExcel(excelFile, "LOCAL", SessionUtils.getUserName());
    }
}
