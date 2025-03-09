package com.backend.security.config;

import javax.sql.DataSource;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.configuration.JobRunrConfiguration.JobRunrConfigurationResult;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.server.JobActivator;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.sql.common.SqlStorageProviderFactory;
import org.jobrunr.storage.sql.mariadb.MariaDbStorageProvider;
import org.jobrunr.storage.sql.sqlserver.SQLServerStorageProvider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class JobRunrConfig {

    @Bean
    public JobScheduler initJobRunr(DataSource dataSource, JobActivator jobActivator) {
        return JobRunr.configure()
                .useJobActivator(jobActivator)
                .useStorageProvider(SqlStorageProviderFactory
                          .using(dataSource))
                .useBackgroundJobServer()
                .useDashboard()
                .initialize().getJobScheduler();
    }
   @Bean
    public StorageProvider storageProvider(DataSource dataSource) {
        return new MariaDbStorageProvider(dataSource);
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/cloud");
        dataSource.setUsername("local");
        dataSource.setPassword("root");
        return dataSource;
    }

    


    // @Bean
    // public StorageProvider storageProvider() {
    //     // Create HikariDataSource
	// 	HikariConfig config = new HikariConfig();
    //     config.setJdbcUrl("jdbc:mysql://localhost:3306/jobrunr?useSSL=false&serverTimezone=UTC");
    //     config.setUsername("local");
    //     config.setPassword("root");
        
    //     HikariDataSource dataSource = new HikariDataSource(config);
        
    //     // Use the SqlStorageProvider with the created dataSource
    //     return new SQLServerStorageProvider(dataSource);
    // }

    // @Bean
    // public JobRunrConfigurationResult jobRunr(StorageProvider storageProvider) {
    //     // Configure JobRunr to use the defined storage provider
    //     return JobRunr.configure()
    //             .useStorageProvider(storageProvider)
    //             .useBackgroundJobServer()
    //             .initialize();
    // }
}
