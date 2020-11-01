package com.zhouyun.album.api.configuration;





import com.zhouyun.core.dataSource.DataSourceProperties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 读取yml配置的信息
 */
@ConfigurationProperties(
        prefix = "database.mysql.admin"
)
public class SpringBootDataSourceProperties extends DataSourceProperties {

}
