package com.tigerit.jdbc;

import java.util.Random;

/**
 * @author Forhad Ahmed
 */
public class TestMySqlKeyValue {

    public static void main(String[] args) {

        MySqlKeyValue mySqlKeyValue = new MySqlKeyValue("testing","props/credential.properties");

        long prevTime = System.currentTimeMillis();
        int N = 5000000;
        int M = 1000;
        Random random = new Random();
        for (int i = 1; i <= N; i++) {
            StringBuilder str = new StringBuilder("");
            for (int j = 0; j < M; j++) {
                char ch = (char) (Math.abs(random.nextInt())%26+'a');
                str.append(ch);
            }
            //System.out.println("str = " + str);
            if(i%10000 == 0) {
                long curTime = System.currentTimeMillis();
                double time = (curTime - prevTime) / 1000.0;
                System.out.println("time = " + time);
                prevTime = curTime;
            }
            mySqlKeyValue.put(i+"",str.toString());
        }

        /*for (int i = 1; i <= N; i++) {
            if (i % 1000 == 0) {
                long curTime = System.currentTimeMillis();
                double time = (curTime - prevTime) / 1000.0;
                System.out.println("time = " + time);
                prevTime = curTime;
            }
            String tempStr = mySqlKeyValue.get(i + "");
            if(tempStr.length() != M) break;
        }*/
    }
}
