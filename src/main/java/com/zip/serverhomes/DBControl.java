package com.zip.serverhomes;

import java.sql.*;

public class DBControl {
    Connection connection;
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
            select(TABLE, label1, key1);
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
    public void select(String TABLE, String label, String key) {
        try {
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM "+TABLE+" WHERE "+label+"="+key;

            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()) {
                System.out.println("out uuid: " + rs.getString("label"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}