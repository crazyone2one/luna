package cn.master.luna.entity;

import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Created by 11's papa on 2025/7/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "组织")
@Table(value = "tb_mine_info", dataSource = "ds2")
public class MineInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String mineName;
    private String mineCode;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;
}
