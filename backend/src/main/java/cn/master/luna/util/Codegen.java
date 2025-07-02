package cn.master.luna.util;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.EntityConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.time.LocalDate;

/**
 * @author Created by 11's papa on 2025/7/1
 */
public class Codegen {
    public static void main(String[] args) {
        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/luna?useInformationSchema=true&characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("Passw0rd");

        //创建配置内容，两种风格都可以。
        GlobalConfig globalConfig = createGlobalConfigUseStyle1("user_role_relation");

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //生成代码
        generator.generate();
    }

    public static GlobalConfig createGlobalConfigUseStyle1(String tableName) {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        //设置根包
        globalConfig.getPackageConfig().setBasePackage("cn.master.luna");
        globalConfig.setSourceDir(System.getProperty("user.dir") + "/backend/src/main/java");


        //设置表前缀和只生成哪些表
        globalConfig.setTablePrefix("tb_");
        globalConfig.setGenerateTable(tableName);

        //设置生成 entity 并启用 Lombok
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);
        //设置项目的JDK版本，项目的JDK为14及以上时建议设置该项，小于14则可以不设置
        globalConfig.setEntityJdkVersion(21);
        globalConfig.getEntityConfig().setWithSwagger(true).setSwaggerVersion(EntityConfig.SwaggerVersion.DOC);
        globalConfig.enableController();
        //设置生成 mapper
        globalConfig.setMapperGenerateEnable(true);
        globalConfig.setServiceGenerateEnable(true);
        globalConfig.setServiceImplGenerateEnable(true);
        //可以单独配置某个列
        ColumnConfig columnConfig = new ColumnConfig();
        columnConfig.setColumnName("create_time");
        columnConfig.setOnInsertValue("now()");
        globalConfig.setColumnConfig(tableName, columnConfig);
//        ColumnConfig columnConfig2 = new ColumnConfig();
//        columnConfig2.setColumnName("update_time");
//        columnConfig2.setOnInsertValue("now()");
//        columnConfig2.setOnUpdateValue("now()");
//        globalConfig.setColumnConfig(tableName, columnConfig2);
        globalConfig.getJavadocConfig().setAuthor("11's papa").setSince("1.0.0 " + LocalDate.now());
        return globalConfig;
    }
}
