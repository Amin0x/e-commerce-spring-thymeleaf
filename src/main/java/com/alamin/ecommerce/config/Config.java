package com.alamin.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
//import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import javax.sql.DataSource;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration(proxyBeanMethods = false)
//@EnableJdbcHttpSession 
public class Config {

}
