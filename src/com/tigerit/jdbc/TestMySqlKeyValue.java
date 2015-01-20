package com.tigerit.jdbc;

/**
 * @author Forhad Ahmed
 */
public class TestMySqlKeyValue {

    public static void main(String[] args) {

        MySqlKeyValue mySqlKeyValue = new MySqlKeyValue("testing","props/credential.properties");

        long prevTime = System.currentTimeMillis();
        int N = 100000000;
        for (int i = 1; i <= N; i++) {
            if(i%10000 == 0) {
                long curTime = System.currentTimeMillis();
                double time = (curTime - prevTime) / 1000.0;
                System.out.println("time = " + time);
                prevTime = curTime;
            }
            mySqlKeyValue.put(i+"",i+"");
        }
    }
}
