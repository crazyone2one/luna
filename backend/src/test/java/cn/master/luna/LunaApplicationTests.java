package cn.master.luna;

import cn.master.luna.entity.SystemUser;
import cn.master.luna.handler.UserSourceEnum;
import cn.master.luna.mapper.SystemUserMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class LunaApplicationTests {
    @Resource
    PasswordEncoder passwordEncoder;
    @Resource
    SystemUserMapper systemUserMapper;
    @Test
    void contextLoads() {
        SystemUser.SystemUserBuilder builder = SystemUser.builder()
                .name("admin").password(passwordEncoder.encode("123456"))
                .email("admin@luna.com").source(UserSourceEnum.LOCAL.getDesc())
                .createUser("admin").updateUser("admin").enable(true);
        systemUserMapper.insert(builder.build());
    }

}
