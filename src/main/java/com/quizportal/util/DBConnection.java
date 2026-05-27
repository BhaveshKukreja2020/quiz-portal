package com.quizportal.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC connection factory.
 *
 * <p>Returns a new Connection per call; callers MUST close it
 * (use try-with-resources).  For production workloads, replace
 * with a DataSource / connection pool (HikariCP etc.).
 *
 * <p>Configuration: edit the three constants below, or pass
 * the values via Tomcat context.xml / environment variables.
 */
public final class DBConnection {

    // ── Change these three values to match your environment ──────────────
    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "quiz_portal";
    private static final String USER     = "root";       // your DB username
    private static final String PASSWORD = "root";   // your DB password
    // ─────────────────────────────────────────────────────────────────────

    /**
     * JDBC URL parameters explained:
     *  useSSL=false           — skip SSL cert validation in dev
     *  serverTimezone=UTC     — avoid timezone ambiguity with MySQL 8
     *  allowPublicKeyRetrieval=true — needed by Connector/J 8 on first connect
     *  characterEncoding=UTF-8     — ensure UTF-8 for all string I/O
     *  autoReconnect=true          — reconnect if connection drops (basic guard)
     */
    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE +
            "?useSSL=false" +
            "&serverTimezone=UTC" +
            "&allowPublicKeyRetrieval=true" +
            "&characterEncoding=UTF-8" +
            "&autoReconnect=true";

    static {
        // Connector/J 8.x auto-registers via SPI (no Class.forName needed).
        // Kept here as a safeguard for older containers / class-loader setups.
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {
            // Silently ignore — driver is available via ServiceLoader
        }
    }

    /** Returns a fresh JDBC Connection. Caller is responsible for closing it. */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private DBConnection() { /* no instances */ }
}
