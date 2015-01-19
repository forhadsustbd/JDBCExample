package com.tigerit.jdbc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Created by forhad on 12/22/14.
 */
public class MySqlKeyValue {

    private Connection connection;

    private String credentialPath;
    private Properties properties = new Properties();

    private String userName;
    private String passWord;
    private String urlMysqlDB;

    private String tableName;

    public MySqlKeyValue(String tableName, String credentialPath) {
        this.credentialPath = credentialPath;
        credentialLoad();
        connectionEstablished();
        this.tableName = tableName;
        tableCreate();
    }

    private void connectionEstablished() {
        this.userName = properties.getProperty("mysql.username");
        this.passWord = properties.getProperty("mysql.password");
        this.urlMysqlDB = properties.getProperty("mysql.url");

        try {
            this.connection = DriverManager.getConnection(urlMysqlDB, userName, passWord);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void credentialLoad() {
        InputStream input  = null;
        try {
            input = new FileInputStream(credentialPath);
            // load a properties file
            properties.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tableCreate() {
        String sql;

        sql="CREATE TABLE IF NOT EXISTS "+tableName+" (\n" +
                "  DB_KEY VARCHAR(25) PRIMARY KEY NOT NULL,\n" +
                "  DB_VALUE VARCHAR(25) NOT NULL \n" +
                ") ENGINE=InnoDB;";
        execute(sql);
    }

    public boolean execute(String sqlStatement) {
        Statement statement = null;
        boolean val = false;
        try {
            statement = connection.createStatement();
            val = statement.execute(sqlStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return val;
        }
    }

    private boolean find(String key) {
        String findSql = "SELECT * from "+tableName+" where DB_KEY=?;";

        boolean val = false;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(findSql);
            preparedStatement.setString(1,key);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) val = true;
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(statement != null) {
                    statement.close();
                }
                if(resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return val;
    }

    public boolean put(String key, String value) {
        boolean chk = find(key);
        //boolean chk = false;
        String insertSql = "INSERT INTO "+tableName + " (DB_KEY,DB_VALUE) VALUES (?,?);";
        String updateSql = "UPDATE "+tableName+" set DB_KEY=?, DB_VALUE=? WHERE DB_KEY=?;";

        try {
            if(chk == false) {
                //New Key
                PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
                preparedStatement.setString(1,key);
                preparedStatement.setString(2,value);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return true;
            } else {
                //Update Key
                PreparedStatement preparedStatement = connection.prepareStatement(updateSql);
                preparedStatement.setString(1,key);
                preparedStatement.setString(2,value);
                preparedStatement.setString(3,key);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void put(int key, int value) {
        String insertSql = "INSERT INTO "+tableName + " (DB_KEY,DB_VALUE) VALUES (?,?);";

        try {
            //New Key
            PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
            preparedStatement.setInt(1,key);
            preparedStatement.setInt(2,value);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        String getSql = "SELECT DB_VALUE from "+tableName+" WHERE DB_KEY=?;";
        String value = null;
        ResultSet resultSet = null;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getSql);
            preparedStatement.setString(1,key);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                value = resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    public void dropTable() {
        String sql = "DROP TABLE IF EXISTS "+this.tableName+";";
        execute(sql);
    }

}
