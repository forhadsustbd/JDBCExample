package com.tigerit.jdbc;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
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
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(urlMysqlDB, userName, passWord);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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

        sql = "DROP TABLE IF EXISTS "+tableNameDB+" ;";
        execute(sql);

        sql="CREATE TABLE IF NOT EXISTS "+tableNameDB+" (\n" +
                "  DB_KEY VARCHAR(60) PRIMARY KEY NOT NULL ,\n" +
                "  DATA_BLOB BLOB NOT NULL,\n" +
                "  DATA_ARRAY BINARY(200) NOT NULL\n" +
                ") ENGINE=InnoDB;";
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

        String insertSql = "INSERT INTO "+tableName + " (DB_KEY,DATA_BLOB,DATA_ARRAY) VALUES (?,?,?);";
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(insertSql);

            for (int i = 0; i < packetList.size(); i++) {
                String key = packetList.get(i).getKey();
                byte[] bigArr = packetList.get(i).getBigArr();
                byte[] shortArr = packetList.get(i).getShortArr();

                preparedStatement.setString(1, key);
                preparedStatement.setBinaryStream(2, new ByteArrayInputStream(bigArr),bigArr.length);
                preparedStatement.setBytes(3,shortArr);
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

    public Packet getPacket(int serial) {
        String selectSql = "SELECT * from "+tableName + " WHERE DB_KEY=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
            preparedStatement.setString(1,serial+"");
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                String key = resultSet.getString(1);
                Blob blob = resultSet.getBlob(2);
                byte[] bigArr = blob.getBytes(1L,(int)blob.length());
                byte[] shortArr = resultSet.getBytes(3);
                Packet packet = new Packet(key,bigArr,shortArr);
                return packet;
            } else {
                System.out.println("serial = " + serial+" not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {

        MySqlBatchTest mySqlBatchTest = new MySqlBatchTest("test","props/credential.properties");

        Random random = new Random();
        int serial = 1;
        int batchSize = 100;
        int test = 1000;
        int dataSize = 20;

        byte[] bigArr = new byte[dataSize];
        random.nextBytes(bigArr);
        byte[] shortArr = new byte[200];
        random.nextBytes(shortArr);

        long pStartTime = System.currentTimeMillis();

        for (int i = 0; i < test;i++) {
            ArrayList<Packet> packets = new ArrayList<Packet>();
            for (int j = 0; j < batchSize; j++) {
                String key = serial+"";

                packets.add(new Packet(key,bigArr,shortArr));
                serial++;
            }
            long startTime = System.currentTimeMillis();
            mySqlBatchTest.putBatch(packets);
            long endTime = System.currentTimeMillis();
            double time = (endTime-startTime)/1000.0;
            System.out.println("time = " + time);
            System.out.println(i);
        }
        long pEndTime = System.currentTimeMillis();
        double time = (pEndTime - pStartTime)/1000.0;
        System.out.println("Total time = " + time);
        System.out.println("Totatl Insert : "+(serial-1));
        for (int i = 1; i < serial; i++) {
            if(i%100 == 0) System.out.println("i = " + i);
            Packet packet = mySqlBatchTest.getPacket(i);
            String key = packet.getKey();
            byte[] tempBig = packet.getBigArr();
            byte[] tempShort = packet.getShortArr();
            if(!key.equals(i+"")) {
                System.out.println("Key Not Matched");
                break;
            }
            if(!Arrays.equals(bigArr,tempBig)) {
                System.out.println("Error Big Arr");
                break;
            }

            if(!Arrays.equals(shortArr,tempShort)) {
                System.out.println("Error Short Arr");
                break;
            }
        }

    }
}
