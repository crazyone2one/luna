package cn.master.luna.util;

import cn.master.luna.exception.CustomException;
import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Created by 11's papa on 2025/8/1
 */
@Slf4j
public class EasyExcelExporter {
    private Class clazz;

    public EasyExcelExporter(Class clazz) {
        this.clazz = clazz;
    }

    public void exportByCustomWriteHandler(HttpServletResponse response, List<Object> data, String fileName, String sheetName) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String tmpName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + tmpName + ".xlsx");
            EasyExcel.write(response.getOutputStream())
                    .sheet(sheetName)
                    .doWrite(data);
        } catch (Exception  e) {
            log.error(e.getMessage(), e);
            throw new CustomException(e.getMessage());
        }
    }
}
