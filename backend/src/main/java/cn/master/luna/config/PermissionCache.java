package cn.master.luna.config;

import cn.master.luna.entity.dto.PermissionDefinitionItem;
import lombok.Data;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/7/18
 */
@Data
public class PermissionCache {
    private List<PermissionDefinitionItem> permissionDefinition;
}
