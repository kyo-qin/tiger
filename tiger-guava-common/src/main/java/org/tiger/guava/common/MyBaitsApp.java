package org.tiger.guava.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.tiger.guava.common.datasource.DatasourceConfig;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@Import(value = { DatasourceConfig.class })
public class MyBaitsApp {

    private static final Logger logger = LoggerFactory.getLogger(MyBaitsApp.class);

    public static void main(String[] args) {
        SpringApplication.run(MyBaitsApp.class, args);
    }

}
