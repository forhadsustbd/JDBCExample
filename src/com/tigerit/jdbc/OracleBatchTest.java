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
public class OracleBatchTest {

    private Connection connection;

    private String credentialPath;
    private Properties properties = new Properties();

    private String userName;
    private String passWord;
    private String urlOracleDB;

    private String tableName;

    public OracleBatchTest(String tableName, String credentialPath) {
        this.credentialPath = credentialPath;
        credentialLoad();
        connectionEstablished();
        this.tableName = tableName;
//        tableCreate(this.tableName);
    }

    private void connectionEstablished() {
        this.userName = properties.getProperty("oracle.username");
        this.passWord = properties.getProperty("oracle.password");
        this.urlOracleDB = properties.getProperty("oracle.url");

        try {
            this.connection = DriverManager.getConnection(urlOracleDB, userName, passWord);
            System.out.println("Connection established");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            connection.close();
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

        sql="CREATE TABLE "+tableNameDB+" (\n" +
                "  DB_KEY VARCHAR2(60) PRIMARY KEY NOT NULL ,\n" +
                "  DB_VALUE VARCHAR2(200) NOT NULL\n" +
                ")";
        execute(sql);
    }

    public boolean execute(String sqlStatement) {
        System.out.println("sqlStatement = " + sqlStatement);
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

        String insertSql = "INSERT INTO "+tableName + " (serial, serial_version) VALUES (?,?)";
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(insertSql);

            for (int i = 0; i < packetList.size(); i++) {
                int key = packetList.get(i).getKey();
                int version = packetList.get(i).getValue();

                //String value = packetList.get(i).getValue();

                preparedStatement.setInt(1,key);
                preparedStatement.setInt(2, version);
                //preparedStatement.setBytes();setBinaryStream();
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

    public void getCount(int low,int high) {
        String getSelectSql = "SELECT * FROM "+tableName+" WHERE SERIAL >= ? AND SERIAL <= ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getSelectSql);
            preparedStatement.setInt(1,low);
            preparedStatement.setInt(2,high);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                int serial = resultSet.getInt(1);
                int version = resultSet.getInt(2);
                //System.out.println("serial: "+serial+" version = " + version);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        OracleBatchTest oracleBatchTest = new OracleBatchTest("version_table","props/credential.properties");

        /*int serial = 1;
        int batchSize = 1000000;
        int M = 200;
        for (int i = 0; i < M;i++) {
            ArrayList<Packet> packets = new ArrayList<Packet>();
            for (int j = 0; j < batchSize; j++) {
                packets.add(new Packet(serial,serial));
                serial++;
            }
            long startTime = System.currentTimeMillis();
            oracleBatchTest.putBatch(packets);
            long endTime = System.currentTimeMillis();
            double time = (endTime - startTime)/1000.0;
            System.out.println(i+" >  time: " + time);
            System.out.println("i = " + i);
        }*/



        int start = 1;
        for(int i=0;i<100;i++) {
            int end = start+100000;

            long startTime = System.currentTimeMillis();
            oracleBatchTest.getCount(start,end);
            long endTime = System.currentTimeMillis();
            double time = (endTime - startTime)/1000.0;
            System.out.println(i+" >  time: " + time);

            start = end + 1;
        }

        oracleBatchTest.shutdown();

    }
}
