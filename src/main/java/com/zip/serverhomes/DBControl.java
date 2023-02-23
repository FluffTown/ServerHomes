package com.zip.serverhomes;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBControl {
    public Connection connection;
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    public DBControl() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mariadb://127.0.0.1/Server","root","password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void insert(String TABLE, String[] args) {
        try {
            Statement stmt = connection.createStatement();
            String values = "";
            for (int i = 0; i < args.length; i++) {
                if (isNumeric(args[i])) {
                    values += "" + args[i] + "";
                } else {
                    values += "\"" + args[i] + "\"";
                }
                if (i < args.length-1) {
                    values += ",";
                }
            }
            String sql = "INSERT INTO " + TABLE + " VALUES (" + values + ");";
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void delete(String TABLE, String label, String key, String label1, String key1) {
        try {
            Statement stmt = connection.createStatement();
            String sql = "DELETE FROM "+TABLE+" WHERE "+label+"="+key+" AND "+label1+"="+key1;
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void delete(String TABLE, String label, String key) {
        try {
            Statement stmt = connection.createStatement();
            String sql = "DELETE FROM "+TABLE+" WHERE "+label+"="+key;
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<String> select(String TABLE, String label, String key, String key1) {
        try {
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM "+TABLE+" WHERE "+label+"="+key;

            ResultSet rs = stmt.executeQuery(sql);

            List<String> out = new ArrayList();
            while(rs.next()) {
                out.add(rs.getString(key1));
            }
            return out;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public ResultSet selectRaw(String TABLE, String label, String key, String label1, String key1) {
        try {
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM "+TABLE+" WHERE "+label+"="+key+" AND "+label1+"="+key1;

            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}