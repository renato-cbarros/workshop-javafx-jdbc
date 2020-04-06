package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.util.Properties;

public class DB {
	
	public static Connection conn = null;
	
	public static Connection getConnection() {
		if (conn == null) {
			try {
				Properties prop = loadProperties("db.properties");
				String url = prop.getProperty("dburl");
				conn = DriverManager.getConnection(url, prop);
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}

		}
		return conn;
	}
	
	public static void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	
	
	private static Properties loadProperties (String path) {
		
		try (FileInputStream fs = new FileInputStream(path)) {
			Properties prop = new Properties();
			prop.load(fs);
			return prop;
		} catch (IOException e) {
			throw new DbException(e.getMessage());
		}
	}
	
	public static void closeStatement(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}

	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}

}
