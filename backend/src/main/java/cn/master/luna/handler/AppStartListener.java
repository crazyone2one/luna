package cn.master.luna.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * @author Created by 11's papa on 2025/7/8
 */
@Slf4j
public class AppStartListener implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        log.info("================= 应用启动 =================");
    }
}
