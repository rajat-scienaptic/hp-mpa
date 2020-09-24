package com.mpa.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "mexicoQPPEntityManagerFactory", transactionManagerRef = "mexicoQPPTransactionManager", basePackages = {
        "com.mpa.repository.qpp" })
public class MexicoQPPDataSourceConfiguration {

    @Bean(name = "mexicoQPPDataSource")
    @ConfigurationProperties(prefix = "spring.mexico-qpp.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "mexicoQPPEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mexicoQPPEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                           @Qualifier("mexicoQPPDataSource") DataSource dataSource) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        return builder.dataSource(dataSource).properties(properties)
                .packages("com.mpa.model.qpp").persistenceUnit("mexico-qpp").build();
    }

    @Bean(name = "mexicoQPPTransactionManager")
    public PlatformTransactionManager mexicoQPPTransactionManager(
            @Qualifier("mexicoQPPEntityManagerFactory") EntityManagerFactory mexicoQPPEntityManagerFactory) {
        return new JpaTransactionManager(mexicoQPPEntityManagerFactory);
    }
}