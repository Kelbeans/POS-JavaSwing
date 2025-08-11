package config;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DatabaseConfig {
        private static final Properties props = new Properties();

        static {
            try {
                props.load(new FileInputStream("resources/config.properties"));
            } catch (Exception e) {
                throw new RuntimeException("Failed to load config.properties", e);
            }
        }

        public static Connection getConnection() throws SQLException {
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String password = props.getProperty("db.password");
            return DriverManager.getConnection(url, user, password);
        }
}
