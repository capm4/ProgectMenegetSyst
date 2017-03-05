package dao.jdbc;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnectionPool {

    private static BasicDataSource dataSource = new BasicDataSource();
    private static final Logger LOGGER = LoggerFactory.getLogger(DBConnectionPool.class);
    private static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/javaee_hw2?useSSL=false&serverTimezone=UTC";
    private static final String USER = "user";
    private static final String PASSWORD = "qwe123";


    static {
        LOGGER.info("Loading JDBC driver: com.mysql.cj.jdbc.Driver");
        dataSource.setDriverClassName(DRIVER_CLASS_NAME);
        LOGGER.info("Driver loaded successfully");
        dataSource.setUrl(DB_URL);
        dataSource.setUsername(USER);
        dataSource.setPassword(PASSWORD);
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}

