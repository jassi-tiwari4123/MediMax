package com.ohms.utility;

import com.ohms.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection — provides JDBC connections to the MySQL database.
 *
 * INTERVIEW POINTS:
 *   - Singleton: only one DBConnection instance exists.
 *   - Static factory method: getConnection() abstracts driver loading.
 *   - Exception wrapping: SQLException → DatabaseException keeps the
 *     DAO layer decoupled from JDBC implementation details.
 *   - closeQuietly() helpers prevent resource leaks in finally blocks.
 *
 * NOTE: For production, replace with a connection pool (HikariCP/C3P0).
 *       This implementation is intentionally simple for learning purposes.
 */
public final class DBConnection {

    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);

    // Load JDBC driver once when class is initialized
    static {
        try {
            Class.forName(AppConfig.getDbDriver());
            logger.info("JDBC driver loaded: {}", AppConfig.getDbDriver());
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(
                "MySQL JDBC driver not found: " + e.getMessage());
        }
    }

    // Private constructor — utility class, no instantiation
    private DBConnection() {}

    // ── Public API ───────────────────────────────────────────────

    /**
     * Returns a new JDBC Connection from DriverManager.
     *
     * @return open Connection
     * @throws DatabaseException if connection cannot be established
     */
    public static Connection getConnection() throws DatabaseException {
        try {
            Connection conn = DriverManager.getConnection(
                AppConfig.getDbUrl(),
                AppConfig.getDbUsername(),
                AppConfig.getDbPassword()
            );
            conn.setAutoCommit(true);  // explicit transactions override this
            return conn;
        } catch (SQLException e) {
            logger.error("Failed to obtain DB connection: {}", e.getMessage());
            throw new DatabaseException("Unable to connect to the database.", e);
        }
    }

    // ── Resource cleanup helpers ─────────────────────────────────

    /**
     * Closes a Connection, suppressing any SQLException.
     * Always call in finally or try-with-resources.
     */
    public static void closeQuietly(java.sql.Connection conn) {
        if (conn != null) {
            try { conn.close(); }
            catch (SQLException e) {
                logger.warn("Error closing connection: {}", e.getMessage());
            }
        }
    }

    public static void closeQuietly(java.sql.Statement stmt) {
        if (stmt != null) {
            try { stmt.close(); }
            catch (SQLException e) {
                logger.warn("Error closing statement: {}", e.getMessage());
            }
        }
    }

    public static void closeQuietly(java.sql.ResultSet rs) {
        if (rs != null) {
            try { rs.close(); }
            catch (SQLException e) {
                logger.warn("Error closing result set: {}", e.getMessage());
            }
        }
    }

    /**
     * Rolls back a connection silently — used in catch blocks.
     */
    public static void rollbackQuietly(java.sql.Connection conn) {
        if (conn != null) {
            try { conn.rollback(); }
            catch (SQLException e) {
                logger.warn("Error rolling back transaction: {}", e.getMessage());
            }
        }
    }
}
