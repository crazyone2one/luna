package cn.master.luna.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * @author Created by 11's papa on 2025/7/18
 */
@Configuration
public class MessageSourceConfig {
    @Value("${spring.messages.default-locale}")
    private String defaultLocale;
    @Value("${spring.messages.basename}")
    private String[] basements;

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        Locale defaultLocale = Locale.forLanguageTag(this.defaultLocale);
        // 设置消息资源文件的基名
        messageSource.setBasenames(basements);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name()); // 设置编码
        messageSource.setDefaultLocale(defaultLocale); // 设置默认语言
        return messageSource;
    }
}
