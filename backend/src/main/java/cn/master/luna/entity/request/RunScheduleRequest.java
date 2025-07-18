package cn.master.luna.entity.request;

import lombok.Data;

import java.util.Map;

/**
 * @author Created by 11's papa on 2025/7/16
 */
@Data
public class RunScheduleRequest {
    private String scheduleId;
    private String sensorType;
    private Map<String, String> params;
}
