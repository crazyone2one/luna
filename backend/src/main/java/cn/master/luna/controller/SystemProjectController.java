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
import cn.master.luna.entity.SystemProject;
import cn.master.luna.service.SystemProjectService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 项目 控制层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
@RestController
@Tag(name = "项目接口")
@RequestMapping("/systemProject")
public class SystemProjectController {

    @Autowired
    private SystemProjectService systemProjectService;

    /**
     * 添加项目。
     *
     * @param systemProject 项目
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description="保存项目")
    public boolean save(@RequestBody @Parameter(description="项目")SystemProject systemProject) {
        return systemProjectService.save(systemProject);
    }

    /**
     * 根据主键删除项目。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键项目")
    public boolean remove(@PathVariable @Parameter(description="项目主键") String id) {
        return systemProjectService.removeById(id);
    }

    /**
     * 根据主键更新项目。
     *
     * @param systemProject 项目
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新项目")
    public boolean update(@RequestBody @Parameter(description="项目主键") SystemProject systemProject) {
        return systemProjectService.updateById(systemProject);
    }

    /**
     * 查询所有项目。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有项目")
    public List<SystemProject> list() {
        return systemProjectService.list();
    }

    /**
     * 根据项目主键获取详细信息。
     *
     * @param id 项目主键
     * @return 项目详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取项目")
    public SystemProject getInfo(@PathVariable @Parameter(description="项目主键") String id) {
        return systemProjectService.getById(id);
    }

    /**
     * 分页查询项目。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询项目")
    public Page<SystemProject> page(@Parameter(description="分页信息") Page<SystemProject> page) {
        return systemProjectService.page(page);
    }

}
