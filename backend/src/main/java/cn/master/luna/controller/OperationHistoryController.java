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
import cn.master.luna.entity.OperationHistory;
import cn.master.luna.service.OperationHistoryService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 变更记录 控制层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-14
 */
@RestController
@Tag(name = "变更记录接口")
@RequestMapping("/operationHistory")
public class OperationHistoryController {

    @Autowired
    private OperationHistoryService operationHistoryService;

    /**
     * 添加变更记录。
     *
     * @param operationHistory 变更记录
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description="保存变更记录")
    public boolean save(@RequestBody @Parameter(description="变更记录")OperationHistory operationHistory) {
        return operationHistoryService.save(operationHistory);
    }

    /**
     * 根据主键删除变更记录。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键变更记录")
    public boolean remove(@PathVariable @Parameter(description="变更记录主键") String id) {
        return operationHistoryService.removeById(id);
    }

    /**
     * 根据主键更新变更记录。
     *
     * @param operationHistory 变更记录
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新变更记录")
    public boolean update(@RequestBody @Parameter(description="变更记录主键") OperationHistory operationHistory) {
        return operationHistoryService.updateById(operationHistory);
    }

    /**
     * 查询所有变更记录。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有变更记录")
    public List<OperationHistory> list() {
        return operationHistoryService.list();
    }

    /**
     * 根据变更记录主键获取详细信息。
     *
     * @param id 变更记录主键
     * @return 变更记录详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取变更记录")
    public OperationHistory getInfo(@PathVariable @Parameter(description="变更记录主键") String id) {
        return operationHistoryService.getById(id);
    }

    /**
     * 分页查询变更记录。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询变更记录")
    public Page<OperationHistory> page(@Parameter(description="分页信息") Page<OperationHistory> page) {
        return operationHistoryService.page(page);
    }

}
