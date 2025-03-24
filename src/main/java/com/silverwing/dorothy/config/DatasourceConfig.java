package com.silverwing.dorothy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
@Profile("test")
public class DatasourceConfig {

    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .addScript("classpath:data_init.sql")
                .build();
    }
}
