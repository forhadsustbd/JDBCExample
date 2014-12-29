package com.tigerit.jdbc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

/**
 * Created by forhad on 12/29/14.
 */
public class MySqlBatchTest {

    private Connection connection;

    private String credentialPath;
    private Properties properties = new Properties();

    private String userName;
    private String passWord;
    private String urlMysqlDB;

    private String tableName;

    public MySqlBatchTest(String tableName, String credentialPath) {
        this.credentialPath = credentialPath;
        credentialLoad();
        connectionEstablished();
        this.tableName = tableName;
        tableCreate(this.tableName);
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

    private void tableCreate(String tableNameDB) {
        String sql;

        sql="CREATE TABLE IF NOT EXISTS "+tableNameDB+" (\n" +
                "  DB_KEY VARCHAR(60) PRIMARY KEY NOT NULL ,\n" +
                "  DB_VALUE VARCHAR(25000) NOT NULL\n" +
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

    public boolean putBatch(ArrayList<Packet> packetList) {

        String insertSql = "INSERT INTO "+tableName + " (DB_KEY,DB_VALUE) VALUES (?,?);";
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(insertSql);

            for (int i = 0; i < packetList.size(); i++) {
                String key = packetList.get(i).getKey();
                String value = packetList.get(i).getValue();

                preparedStatement.setString(1,key);
                preparedStatement.setString(2,value);
                preparedStatement.addBatch();
            }
            //New Key
            int[] affectedResultSet = preparedStatement.executeBatch();
            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public static void main(String[] args) {

        MySqlBatchTest mySqlBatchTest = new MySqlBatchTest("batchTable","props/credential.properties");
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        int valueSize = 20000;

        for (int i = 0; i < valueSize; i++) {
            stringBuilder.append('a');
        }
        System.out.println(stringBuilder.toString().getBytes().length);
        int serial = 1;
        int batchSize = 1000;
        for (int i = 0; i < 100;i++) {
            ArrayList<Packet> packets = new ArrayList<Packet>();
            for (int j = 0; j < batchSize; j++) {
                String key = serial+"";
                String value = stringBuilder.toString();
                packets.add(new Packet(key,value));
                serial++;
            }
            long startTime = System.currentTimeMillis();
            mySqlBatchTest.putBatch(packets);
            long endTime = System.currentTimeMillis();
            double time = (endTime-startTime)/1000.0;
            System.out.println("time = " + time);
            System.out.println(i);
        }

    }
}
