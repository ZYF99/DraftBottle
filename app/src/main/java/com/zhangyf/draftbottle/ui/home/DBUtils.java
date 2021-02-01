package com.zhangyf.draftbottle.ui.home;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Objects;

/**
 * 数据库工具类：连接数据库用、获取数据库数据用
 * 相关操作数据库的方法均可写在该类
 */
public class DBUtils {

    private static String driver = "com.mysql.jdbc.Driver";// MySql驱动

//    private static String url = "jdbc:mysql://localhost:3306/map_designer_test_db";

    private static String user = "test1";// 用户名

    private static String password = "test1test1";// 密码

    private static Connection connectionInstance = null;

    private static Connection getConn(String dbName) {
        if (connectionInstance == null) {
            try {
                Class.forName(driver);// 动态加载类
                // 尝试建立到给定数据库URL的连接 jdbc:mysql://" + ip + ":3306/
                connectionInstance = DriverManager.getConnection("jdbc:mysql://cdb-phyixcea.bj.tencentcdb.com:10015/" + dbName, user, password);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connectionInstance;
    }

    //查询版本
    public static String getVersion() {

        String tag = "版本";
        HashMap<String, Object> map = new HashMap<>();
        // 根据数据库名称，建立连接
        Connection connection = getConn("test1");

        try {
            // mysql简单的查询语句。这里是根据list表的‘题号’字段来查询某条记录
            String sql = "select * from token where `学号` = ?";
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    // 设置上面的sql语句中的？的值为name
                    ps.setString(1, tag);
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    if (rs != null) {
                        int count = rs.getMetaData().getColumnCount();

                        while (rs.next()) {
                            // 注意：下标是从1开始的
                            for (int i = 1; i <= count; i++) {
                                String field = rs.getMetaData().getColumnName(i);
                                map.put(field, rs.getString(field));
                            }
                        }
                        //connection.close();
                        ps.close();
                        String versionTmp = map.get("次数").toString();
                        Log.e("DBUtils", "查询版本：" + versionTmp);
                        return versionTmp;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DBUtils", "异常：" + e.getMessage());
            return null;
        }

    }

    //查询答案
    public static HashMap<String, Object> getQuestionAnswer(String name) {

        HashMap<String, Object> map = new HashMap<>();
        // 根据数据库名称，建立连接
        Connection connection = getConn("test1");

        try {
            // mysql简单的查询语句。这里是根据list表的‘题号’字段来查询某条记录
            String sql = "select * from zhanghao where `题号` = ?";
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    // 设置上面的sql语句中的？的值为name
                    ps.setString(1, name);
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    if (rs != null) {
                        int count = rs.getMetaData().getColumnCount();
                        Log.e("DBUtils", "列总数：" + count);
                        while (rs.next()) {
                            // 注意：下标是从1开始的
                            for (int i = 1; i <= count; i++) {
                                String field = rs.getMetaData().getColumnName(i);
                                map.put(field, rs.getString(field));
                            }
                        }
                        //connection.close();
                        ps.close();
                        return map;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DBUtils", "异常：" + e.getMessage());
            return null;
        }

    }

    //查询次数
    public static String getCount() {

        String tag = "标签";
        HashMap<String, Object> map = new HashMap<>();
        // 根据数据库名称，建立连接
        Connection connection = getConn("test1");

        try {
            // mysql简单的查询语句。这里是根据list表的‘题号’字段来查询某条记录
            String sql = "select * from token where `学号` = ?";
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    // 设置上面的sql语句中的？的值为name
                    ps.setString(1, tag);
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    if (rs != null) {
                        int count = rs.getMetaData().getColumnCount();

                        while (rs.next()) {
                            // 注意：下标是从1开始的
                            for (int i = 1; i <= count; i++) {
                                String field = rs.getMetaData().getColumnName(i);
                                map.put(field, rs.getString(field));
                            }
                        }
                        //connection.close();
                        ps.close();
                        String countTmp = map.get("次数").toString();
                        Log.e("DBUtils", "查询次数：" + countTmp);
                        return countTmp;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DBUtils", "异常：" + e.getMessage());
            return null;
        }

    }

}
