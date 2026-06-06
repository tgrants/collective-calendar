package com.collectivecalendar.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class FlywayMigrationConfig implements BeanFactoryPostProcessor {

	@Bean(initMethod = "migrate")
	public Flyway flyway(DataSource dataSource) {
		return Flyway.configure()
			.dataSource(dataSource)
			.locations("classpath:db/migration")
			.baselineOnMigrate(true)
			.load();
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		String[] entityManagerFactoryNames = beanFactory.getBeanNamesForType(EntityManagerFactory.class, true, false);
		for (String name : entityManagerFactoryNames) {
			beanFactory.getBeanDefinition(name).setDependsOn("flyway");
		}
	}
}
