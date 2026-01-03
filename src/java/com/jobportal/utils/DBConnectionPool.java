package com.jobportal.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database connection pool manager using HikariCP.
 * This replaces the old singleton connection pattern with proper connection pooling.
 */
public class DBConnectionPool {
    private static final Logger logger = LoggerFactory.getLogger(DBConnectionPool.class);
    private static HikariDataSource dataSource;

    static {
        try {
            initializeDataSource();
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private static void initializeDataSource() {
        HikariConfig config = new HikariConfig();
        
        // Basic configuration
        config.setJdbcUrl(ConfigManager.getProperty("db.url"));
        config.setUsername(ConfigManager.getProperty("db.username"));
        config.setPassword(ConfigManager.getProperty("db.password"));
        config.setDriverClassName(ConfigManager.getProperty("db.driver"));

        // Pool configuration
        config.setMaximumPoolSize(ConfigManager.getIntProperty("db.pool.maximumPoolSize", 10));
        config.setMinimumIdle(ConfigManager.getIntProperty("db.pool.minimumIdle", 5));
        config.setConnectionTimeout(ConfigManager.getIntProperty("db.pool.connectionTimeout", 30000));
        config.setIdleTimeout(ConfigManager.getIntProperty("db.pool.idleTimeout", 600000));
        config.setMaxLifetime(ConfigManager.getIntProperty("db.pool.maxLifetime", 1800000));

        // Performance optimization
        config.addDataSourceProperty("cachePrepStmts", 
            ConfigManager.getProperty("db.pool.cachePrepStmts", "true"));
        config.addDataSourceProperty("prepStmtCacheSize", 
            ConfigManager.getProperty("db.pool.prepStmtCacheSize", "250"));
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 
            ConfigManager.getProperty("db.pool.prepStmtCacheSqlLimit", "2048"));

        // Pool name for monitoring
        config.setPoolName("JobPortalHikariCP");

        // Connection test query
        config.setConnectionTestQuery("SELECT 1");

        dataSource = new HikariDataSource(config);
        logger.info("HikariCP connection pool initialized successfully");
    }

    /**
     * Gets a database connection from the pool.
     *
     * @return a database connection
     * @throws SQLException if unable to get connection
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            logger.error("DataSource is null or closed");
            throw new SQLException("Database connection pool is not available");
        }
        return dataSource.getConnection();
    }

    /**
     * Closes the connection pool.
     * Should be called during application shutdown.
     */
    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("HikariCP connection pool closed");
        }
    }

    /**
     * Gets pool statistics for monitoring.
     *
     * @return pool statistics as string
     */
    public static String getPoolStats() {
        if (dataSource != null) {
            return String.format("Active: %d, Idle: %d, Total: %d, Waiting: %d",
                dataSource.getHikariPoolMXBean().getActiveConnections(),
                dataSource.getHikariPoolMXBean().getIdleConnections(),
                dataSource.getHikariPoolMXBean().getTotalConnections(),
                dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
        }
        return "Pool not initialized";
    }
}
