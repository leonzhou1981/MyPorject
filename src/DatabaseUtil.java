import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DatabaseUtil {

    public static Connection getDBConnection() {
        Properties properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream("conf/database.properties");
            properties.load(fis);
            fis.close();
            String drivers = properties.getProperty("jdbc.drivers");
            if (drivers != null) {
                System.setProperty("jdbc.drivers", drivers);
            }
            String url = properties.getProperty("jdbc.url");
            String username = properties.getProperty("jdbc.username");
            String password = properties.getProperty("jdbc.password");

            return DriverManager.getConnection(url, username, password);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public static void closeDBConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int executeUpdate(Connection conn, String sql, List args) {
        int effect = 0;
        if (conn != null) {
            if (sql != null) {
                PreparedStatement statement = null;
                try {
                    statement = conn.prepareStatement(sql);
                    for (int i = 0; args != null && i < args.size(); i++) {
                        statement.setObject(i + 1, args.get(i));
                    }
                    effect = statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();

                } finally {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return effect;
    }

    public static List executeQuery(String sql, List args) {
        Connection conn = getDBConnection();
        List lstResult = executeQuery(conn, sql, args);
        closeDBConnection(conn);
        return lstResult;
    }

    public static List executeQuery(Connection conn, String sql, List args) {
        List lstResult = null;
        if (conn != null) {
            if (sql != null) {
                PreparedStatement statement = null;
                try {
                    statement = conn.prepareStatement(sql);
                    for (int i = 0; args != null && i < args.size(); i++) {
                        statement.setObject(i + 1, args.get(i));
                    }
                    ResultSet rs = statement.executeQuery();
                    lstResult = convertResultSetToListMap(rs);
                } catch (SQLException e) {
                    e.printStackTrace();

                } finally {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return lstResult;
    }

    private static List<Map> convertResultSetToListMap(ResultSet rs) throws SQLException {
        if (rs != null) {
            List lstReturn = new ArrayList();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (rs.next()) {
                Map mTemp = new HashMap();
                for (int i = 0; i < columnCount; i++) {
                    mTemp.put(rsmd.getColumnName(i + 1), rs.getObject(i + 1));
                }
                lstReturn.add(mTemp);
            }
            return lstReturn;
        } else {
            return null;
        }
    }

}
