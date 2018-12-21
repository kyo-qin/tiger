package org.tiger.guava.common.datasource;

import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
public class DatasourceConfig implements TransactionManagementConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(DatasourceConfig.class);

    @Bean(destroyMethod = "close", initMethod = "init", name = "dataSource")
    @Primary
    public DruidDataSource dataSource() {
        DruidDataSource dds = null;
        try {
            dds = new DruidDataSource();
            dds.setUrl("jdbc:mysql://192.168.251.51:3306/egame_netpay?characterEncoding=utf8");
            dds.setUsername("root");
            dds.setPassword("test$11");
            dds.setInitialSize(1);
            dds.setMinIdle(1);
            dds.setMaxActive(200);
            dds.setMaxWait(60000);
            dds.setTimeBetweenEvictionRunsMillis(60000);
            dds.setMinEvictableIdleTimeMillis(300000);
            dds.setValidationQuery("SELECT 'x'");
            dds.setTestWhileIdle(true);
            dds.setTestOnBorrow(false);
            dds.setTestOnReturn(false);
            dds.setPoolPreparedStatements(true);
            dds.setMaxPoolPreparedStatementPerConnectionSize(20);
            dds.setFilters("stat");
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return dds;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(@Autowired DataSource dataSource) {
        SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();
        ssfb.setDataSource(dataSource);
        ssfb.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
        ssfb.setMapperLocations(new ClassPathResource[] { new ClassPathResource("mapper/ConfigMapper.xml") });
        return ssfb;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("org.tiger.guava.commona");
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        return mapperScannerConfigurer;
    }

    @Bean(name = "txManager")
    public DataSourceTransactionManager transactionManager(@Autowired DataSource dataSource) {
        DataSourceTransactionManager tx = new DataSourceTransactionManager();
        tx.setDataSource(dataSource);
        return tx;
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return txManager;
    }

    @Resource(name = "txManager")
    private PlatformTransactionManager txManager;
}
