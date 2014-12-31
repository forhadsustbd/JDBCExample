package com.tigerit.jdbc;

/**
 * Created by forhad on 12/29/14.
 */
public class Packet {

    private String key;
    private byte[] bigArr;
    private byte[] shortArr;

    public Packet(String key, byte[] bigArr, byte[] shortArr) {
        this.key = key;
        this.bigArr = bigArr;
        this.shortArr = shortArr;
    }

    public String getKey() {
        return key;
    }

    public byte[] getBigArr() {
        return bigArr;
    }

    public byte[] getShortArr() {
        return shortArr;
    }
}
