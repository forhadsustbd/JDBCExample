package com.tigerit.jdbc;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by forhad on 12/22/14.
 */
public class TestMysqlJDBC {

    public static void main(String[] args) {

        String tableName = "UserInfo";
        String credentialProperties = "props/credential.properties";
        MySqlKeyValue mySqlKeyValue = new MySqlKeyValue(tableName,credentialProperties);

        for (int i = 1; i <= 100; i++) {
            if(i%10 == 0) System.out.println(i);
            mySqlKeyValue.put(i+"",i+"");
        }

        for (int i = 1; i <= 100; i++) {
            System.out.println(mySqlKeyValue.get(i+""));
        }
    }
}
