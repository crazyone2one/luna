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
import cn.master.luna.entity.OperationLog;
import cn.master.luna.service.OperationLogService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 操作日志 控制层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-14
 */
@RestController
@Tag(name = "操作日志接口")
@RequestMapping("/operationLog")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 添加操作日志。
     *
     * @param operationLog 操作日志
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description="保存操作日志")
    public boolean save(@RequestBody @Parameter(description="操作日志")OperationLog operationLog) {
        return operationLogService.save(operationLog);
    }

    /**
     * 根据主键删除操作日志。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键操作日志")
    public boolean remove(@PathVariable @Parameter(description="操作日志主键") String id) {
        return operationLogService.removeById(id);
    }

    /**
     * 根据主键更新操作日志。
     *
     * @param operationLog 操作日志
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新操作日志")
    public boolean update(@RequestBody @Parameter(description="操作日志主键") OperationLog operationLog) {
        return operationLogService.updateById(operationLog);
    }

    /**
     * 查询所有操作日志。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有操作日志")
    public List<OperationLog> list() {
        return operationLogService.list();
    }

    /**
     * 根据操作日志主键获取详细信息。
     *
     * @param id 操作日志主键
     * @return 操作日志详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取操作日志")
    public OperationLog getInfo(@PathVariable @Parameter(description="操作日志主键") String id) {
        return operationLogService.getById(id);
    }

    /**
     * 分页查询操作日志。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询操作日志")
    public Page<OperationLog> page(@Parameter(description="分页信息") Page<OperationLog> page) {
        return operationLogService.page(page);
    }

}
