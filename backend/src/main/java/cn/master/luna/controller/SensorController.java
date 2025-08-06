package cn.master.luna.controller;

import cn.master.luna.entity.dto.OptionDTO;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Created by 11's papa on 2025/7/28
 */
@Tag(name = "项目接口")
@RestController
@RequestMapping("/sensor")
@RequiredArgsConstructor
public class SensorController {
    @GetMapping("/get-sensor-option")
    @Operation(summary = "项目管理-成员-获取用户组下拉选项")
    public List<OptionDTO> getRoleOption() {
        List<OptionDTO> result = new ArrayList<>();
        try {
            DataSourceKey.use("ds2");
            Map<String, Object> map = new HashMap<>();
            map.put("is_delete", "0");
            List<Row> sensors = Db.selectListByMap("sf_aqjk_sensor", map);
            sensors.forEach(s -> {
                OptionDTO optionDTO = new OptionDTO();
                optionDTO.setId(s.getString("sensor_code"));
                optionDTO.setName(s.getString("sensor_location"));
                result.add(optionDTO);
            });
        } finally {
            DataSourceKey.clear();
        }
        return result;
    }
}
