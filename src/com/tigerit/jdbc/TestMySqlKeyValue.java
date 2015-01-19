package com.tigerit.jdbc;

/**
 * @author Forhad Ahmed
 */
public class TestMySqlKeyValue {

    public static void main(String[] args) {

        MySqlKeyValue mySqlKeyValue = new MySqlKeyValue("testing","props/credential.properties");

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            mySqlKeyValue.put(i+"",i+"");
        }
        long endTime = System.currentTimeMillis();

        double time = (endTime - startTime)/1000.0;

        System.out.println("time = " + time);

    }
}
