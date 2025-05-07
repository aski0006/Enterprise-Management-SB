package com.asaki0019.enterprisemanagementsb.core.datasource;

import com.asaki0019.enterprisemanagementsb.core.sysLogger.SysLogger;
import com.asaki0019.enterprisemanagementsb.core.utils.MessageConstructor;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;


@Component
public class ShowPoolConfig {
    private final DataSource dataSource;
    private final SysLogger sysLogger;

    @Autowired
    public ShowPoolConfig(DataSource dataSource, SysLogger sysLogger) {
        this.dataSource = dataSource;
        this.sysLogger = sysLogger;
    }

    @PostConstruct
    public void showPoolConfig() {
        if (dataSource instanceof HikariDataSource hds) {
            String message = MessageConstructor.constructMessage(
                    "HikariCP Pool Configuration",
                    "Pool Name", hds.getPoolName(),
                    "JDBC URL", hds.getJdbcUrl(),
                    "Maximum Pool Size", hds.getMaximumPoolSize(),
                    "Minimum Idle", hds.getMinimumIdle(),
                    "Connection Timeout", hds.getConnectionTimeout() + "ms",
                    "Idle Timeout", hds.getIdleTimeout() + "ms",
                    "Max Lifetime", hds.getMaxLifetime() + "ms",
                    "Validation Timeout", hds.getValidationTimeout() + "ms",
                    "Leak Detection Threshold", hds.getLeakDetectionThreshold() + "ms",
                    "Initialization Fail Timeout", hds.getInitializationFailTimeout() + "ms"
            );
            sysLogger.info(message, false);

        }
    }
}