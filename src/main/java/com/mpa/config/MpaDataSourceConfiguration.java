package com.mpa.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
@EnableJpaRepositories(entityManagerFactoryRef = "mpaEntityManagerFactory", transactionManagerRef = "mpaTransactionManager", basePackages = {
        "com.mpa.repository.mpa" })
public class MpaDataSourceConfiguration {
    @Primary
    @Bean(name = "mpaDataSource")
    @ConfigurationProperties(prefix = "spring.mpa.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "mpaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mpaEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                       @Qualifier("mpaDataSource") DataSource dataSource) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        return builder.dataSource(dataSource).properties(properties)
                .packages("com.mpa.model.mpa").persistenceUnit("mpa").build();
    }

    @Primary
    @Bean(name = "mpaTransactionManager")
    public PlatformTransactionManager mpaTransactionManager(
            @Qualifier("mpaEntityManagerFactory") EntityManagerFactory mpaEntityManagerFactory) {
        return new JpaTransactionManager(mpaEntityManagerFactory);
    }
}
