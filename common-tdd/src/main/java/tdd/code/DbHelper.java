package tdd.code;

import java.sql.*;
import java.util.List;

import static tdd.code.DataBaseSetting.*;

/**
 * @author: Pantheon
 * @date: 2019/9/19 20:14
 * @comment:
 */
public class DbHelper {


    private Connection connect ;


    public void init() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        connect =  connection;
    }




    public List<String> showDatabases(){

    }



    public List<String> showTables(){

    }


    public List<String>

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        DbHelper dbHelper = new DbHelper();
        dbHelper.init();
        Connection connection = dbHelper.getConnection();
        PreparedStatement statement = connection.prepareStatement("show databases");
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
    }

}
