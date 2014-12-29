package com.tigerit.jdbc;

/**
 * Created by forhad on 12/29/14.
 */
public class Packet {

    private String key;
    private String value;

    public Packet(String key,String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
