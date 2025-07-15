package cn.master.luna.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;

/**
 * @author Created by 11's papa on 2025/7/15
 */
@Slf4j
public class FileUtils {
    /***
     * @param filePath 文件存放位置
     * @param content 文件内容
     * @param type 文件类型
     */
    public static void genFile(String filePath, String content, String type) {
        FileWriter fw = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(filePath);
            fw.write(content);
            log.info("{} created successfully", type);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            try {
                assert fw != null;
                fw.close();
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

}
