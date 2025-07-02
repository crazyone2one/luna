package cn.master.luna;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.master.luna.mapper")
public class LunaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LunaApplication.class, args);
    }

}
