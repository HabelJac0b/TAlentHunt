package com.talenthunt.db;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class DatabaseManager {
    private static String jdbcUrl;
    private static String username;
    private static String password;
    private static boolean isH2Fallback = false;

    static {
        initDatabase();
    }

    public static void initDatabase() {
        Properties props = new Properties();
        File propFile = new File("db.properties");
        if (propFile.exists()) {
            try (FileInputStream fis = new FileInputStream(propFile)) {
                props.load(fis);
            } catch (Exception e) {
                System.out.println("[DB] Could not load db.properties, using defaults.");
            }
        }

        String host = props.getProperty("db.host", "localhost");
        String port = props.getProperty("db.port", "3306");
        String dbName = props.getProperty("db.name", "talent_hunt_db");
        String user = props.getProperty("db.user", "root");
        String pass = props.getProperty("db.password", "root");
        boolean fallbackEnabled = Boolean.parseBoolean(props.getProperty("db.fallback.enabled", "true"));

        // Try primary MySQL connection
        String mysqlUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(mysqlUrl, user, pass);
            conn.close();
            jdbcUrl = mysqlUrl;
            username = user;
            password = pass;
            isH2Fallback = false;
            System.out.println("[DB] Successfully connected to MySQL server at " + host + ":" + port + "/" + dbName);
        } catch (Throwable mysqlEx) {
            System.err.println("[DB ERROR] Failed to connect to MySQL (" + host + ":" + port + "): " + mysqlEx.getMessage());
            if (!fallbackEnabled) {
                throw new RuntimeException("MySQL connection failed and fallback is disabled. Check credentials in db.properties: " + mysqlEx.getMessage(), mysqlEx);
            }
            System.out.println("[DB] Initializing Embedded Database Engine in MySQL Compatibility Mode...");
            
            try {
                Class.forName("org.h2.Driver");
                jdbcUrl = "jdbc:h2:./talent_hunt_h2db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;AUTO_SERVER=TRUE";
                username = "sa";
                password = "";
                isH2Fallback = true;
                System.out.println("[DB] Embedded MySQL-compatible engine initialized successfully.");
            } catch (ClassNotFoundException h2Ex) {
                System.err.println("[DB] Error: No database driver found. Please check lib directory.");
                h2Ex.printStackTrace();
            }
        }

        // Initialize Schema & Seed Data if tables don't exist
        executeScriptsIfNeeded();
    }

    public static Connection getConnection() throws SQLException {
        if (jdbcUrl == null) {
            throw new SQLException("Database not initialized.");
        }
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public static boolean isH2Fallback() {
        return isH2Fallback;
    }

    private static void executeScriptsIfNeeded() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            boolean dataLoaded = false;
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    dataLoaded = true;
                }
            } catch (Exception e) {
                dataLoaded = false;
            }

            if (!dataLoaded) {
                System.out.println("[DB] Executing schema DDL script...");
                executeSqlScript(conn, "sql/schema.sql");
                System.out.println("[DB] Executing seed DML script...");
                executeSqlScript(conn, "sql/seed.sql");
                System.out.println("[DB] Database schema and initial seed loaded successfully.");
            } else {
                System.out.println("[DB] Existing database tables found ready for queries.");
            }

            // Ensure Habel Jacob and Admin accounts exist
            try (PreparedStatement checkPs = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE email = ?")) {
                checkPs.setString(1, "habel@webcraft.com");
                try (ResultSet rsCheck = checkPs.executeQuery()) {
                    if (!rsCheck.next() || rsCheck.getInt(1) == 0) {
                        try (PreparedStatement insertPs = conn.prepareStatement(
                            "INSERT INTO users (name, email, password, role, avatar_url, bio) VALUES (?, ?, ?, ?, ?, ?)")) {
                            insertPs.setString(1, "Habel Jacob (26MCAB25)");
                            insertPs.setString(2, "habel@webcraft.com");
                            insertPs.setString(3, "user123");
                            insertPs.setString(4, "CUSTOMER");
                            insertPs.setString(5, "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80");
                            insertPs.setString(6, "Lead Presenter on HTML5 & JavaScript (ID: 26MCAB25).");
                            insertPs.executeUpdate();
                        }
                    }
                }
                checkPs.setString(1, "admin@webcraft.com");
                try (ResultSet rsCheck = checkPs.executeQuery()) {
                    if (!rsCheck.next() || rsCheck.getInt(1) == 0) {
                        try (PreparedStatement insertPs = conn.prepareStatement(
                            "INSERT INTO users (name, email, password, role, avatar_url, bio) VALUES (?, ?, ?, ?, ?, ?)")) {
                            insertPs.setString(1, "Admin Supervisor");
                            insertPs.setString(2, "admin@webcraft.com");
                            insertPs.setString(3, "admin123");
                            insertPs.setString(4, "ADMIN");
                            insertPs.setString(5, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=300&q=80");
                            insertPs.setString(6, "Course Supervisor & WebCraft Administrator.");
                            insertPs.executeUpdate();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[DB] Warning during script execution: " + e.getMessage());
        }
    }

    private static void executeSqlScript(Connection conn, String scriptPath) throws Exception {
        File file = new File(scriptPath);
        if (!file.exists()) {
            System.out.println("[DB] Script file not found: " + scriptPath);
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            Statement stmt = conn.createStatement();
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("--") || trimmed.startsWith("//") || trimmed.toUpperCase().startsWith("USE ") || trimmed.toUpperCase().startsWith("CREATE DATABASE ")) {
                    continue;
                }
                sb.append(line).append("\n");
                if (trimmed.endsWith(";")) {
                    String sql = sb.toString().trim();
                    if (sql.endsWith(";")) {
                        sql = sql.substring(0, sql.length() - 1);
                    }
                    if (!sql.isEmpty()) {
                        try {
                            stmt.execute(sql);
                        } catch (SQLException e) {
                            System.err.println("[DB Script Warning] " + e.getMessage());
                        }
                    }
                    sb.setLength(0);
                }
            }
            stmt.close();
        }
    }
}
