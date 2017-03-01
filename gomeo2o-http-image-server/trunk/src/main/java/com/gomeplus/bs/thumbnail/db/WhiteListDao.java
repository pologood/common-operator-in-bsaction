package com.gomeplus.bs.thumbnail.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.gomeplus.bs.thumbnail.config.Config;
import com.mysql.jdbc.Driver;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by daisyli on 16/4/27.
 */
@Slf4j
public class WhiteListDao {
    private static Driver driver = null;
    private static Connection conn = null;
    private static Statement stmt = null;
    private static Properties props = null;

    private final static WhiteListDao instance = new WhiteListDao();

    private WhiteListDao() {}

    public static WhiteListDao getInstance() {
        return instance;
    }

    static {
    	props = new Properties();
        props.put("user", Config.DB_USERNAME);
        props.put("password", Config.DB_PASSWORD);
        
        boolean result = connect();
        int i = 0;
        while (i < 5) {
        	if (!result)
        		connect();
        	i++;
        }

    }
    
    public static boolean connect() {
    	try {
    		driver = new Driver();
            conn = driver.connect(Config.DB_URL, props);
            stmt = conn.createStatement();
            return true;
        } catch (SQLException e) {
            log.error("DB connenct failed " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取白名单列表
     * @return
     * @throws SQLException
     */
    public Set<String> getWhiteSet() throws SQLException {
        String sql = "select * from white_list";
        ResultSet rs = stmt.executeQuery(sql);
        Set<String> whiteSet = new HashSet<>();
        while (rs.next()) {
            String dimension = rs.getString("dimension");
            if (dimension != null && !dimension.equals("")) {
                whiteSet.add(dimension);
            }
        }

        rs.close();
        return whiteSet;

    }

    public void beforeCloseServer() throws Exception {
        stmt.close();
        conn.close();
    }
}
