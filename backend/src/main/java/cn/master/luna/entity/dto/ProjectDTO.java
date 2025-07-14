package cn.master.luna.entity.dto;

import cn.master.luna.entity.SystemProject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Created by 11's papa on 2025/7/11
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectDTO extends SystemProject implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description =  "项目成员数量", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long memberCount;
    @Schema(description = "所属组织", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String organizationName;
    @Schema(description = "创建人是否是管理员", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean projectCreateUserIsAdmin;
    @Schema(description =  "模块设置", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> moduleIds;
}
