package cn.master.luna.config;

import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.keygen.KeyGenerators;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import org.springframework.context.annotation.Configuration;

/**
 * @author Created by 11's papa on 2025/7/1
 */
@Configuration
public class MyBatisFlexConfiguration implements MyBatisFlexCustomizer {
    @Override
    public void customize(FlexGlobalConfig flexGlobalConfig) {
        FlexGlobalConfig.KeyConfig keyConfig = new FlexGlobalConfig.KeyConfig();
        keyConfig.setKeyType(KeyType.Generator);
        keyConfig.setValue(KeyGenerators.flexId);
        FlexGlobalConfig.getDefaultConfig().setKeyConfig(keyConfig);
        flexGlobalConfig.setDbType(DbType.MYSQL);

    }
}
