# JAVA操作数据库

## 代码

```java
package com.wondersgroup.etrain;

import com.mysql.jdbc.Driver;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestLable {

    public static void main(String[] args) throws SQLException {


        Connection connection = null;
        ResultSet resultSet = null;
        Statement statement = null;
        try {
            DriverManager.registerDriver(new Driver());
//            String url = "jdbc:mysql://172.27.1.70:3306/etrain?useUnicode=true&amp;characterEncoding=UTF-8";
            String url = "jdbc:mysql://localhost:3306/etrain?useUnicode=true&amp;characterEncoding=UTF-8";
//            String username = "etrain";
//            String password = "GHxIeAkI$1Ovyw6w";
            String username = "root";
            String password = "root123";
            connection = DriverManager.getConnection(url, username, password);
//CREATE DATABASE IF NOT EXISTS "+dataBaseName+" default charset utf8 COLLATE utf8_general_ci;
            String createDatabasesSql = "CREATE DATABASE IF NOT EXISTS create_datebase_test default charset utf8 COLLATE utf8_general_ci";

            //建数据库
            PreparedStatement cdps = connection.prepareStatement(createDatabasesSql);
            cdps.executeUpdate();

            cdps.close();
            connection.close();

            //建表
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/create_datebase_test?useUnicode=true&amp;characterEncoding=UTF-8", username, password);
            statement = connection.createStatement();
            String createTableSql = "CREATE TABLE IF NOT EXISTS `create_table_test` ( " +
                    " id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID', " +
                    " created_time datetime NOT NULL COMMENT '创建时间', " +
                    " updated_time datetime NOT NULL COMMENT '更新时间', " +
                    " author varchar(255) NOT NULL DEFAULT '' COMMENT '作者', " +
                    " PRIMARY KEY (`id`) " +
                    ") ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COMMENT='创建测试表'";
            statement.executeUpdate(createTableSql);

            //插入数据
//            long now = Instant.now().toEpochMilli();
            String format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
            String replace = UUID.randomUUID().toString().replace("-", "");
            String insertLineInfoSql = "INSERT INTO create_table_test(created_time,updated_time,author) VALUES('" + format + "','" + format + "','" + replace + "')";
//            PreparedStatement ilisps = connection.prepareStatement(insertLineInfoSql);
//            ilisps.executeUpdate();
            statement.executeUpdate(insertLineInfoSql);

            //查询数据
            String sql = "select id from create_table_test";
            resultSet = statement.executeQuery(sql);

//            preparedStatement = connection.prepareStatement(sql);
//            resultSet = preparedStatement.executeQuery();

            List list = new ArrayList<String>();
            while (resultSet.next()){
                String id = resultSet.getString("id");
                list.add(id);
            }
            System.out.println(list.toString());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            resultSet.close();
            connection.close();
            statement.close();
        }

//        int i = 0;
//        outer: // 此处不允许存在执行语句
//        for(; true ;) { // 无限循环
//            System.out.println("inner_after");
//            inner: // 此处不允许存在执行语句
//            for(; i < 10; i++) {
//                System.out.println("i = " + i);
//                if(i == 2) {
//                    System.out.println("continue");
//                    continue;
//                }
//                if(i == 3) {
//                    System.out.println("break");
//                    i++; // 否则 i 永远无法获得自增
//                    // 获得自增
//                    break;
//                }
//                if(i == 7) {
//                    System.out.println("continue outer");
//                    i++;  // 否则 i 永远无法获得自增
//                    // 获得自增
//                    continue outer;
//                }
//                if(i == 8) {
//                    System.out.println("break outer");
//                    break outer;
//                }
//                for(int k = 0; k < 5; k++) {
//                    if(k == 3) {
//                        System.out.println("continue inner");
//                        continue inner;
//                    }
//                }
//            }
//        }
        // 在此处无法 break 或 continue 标签
    }
}

```

