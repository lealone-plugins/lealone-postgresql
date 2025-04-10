/*
 * Copyright Lealone Database Group.
 * Licensed under the Server Side Public License, v 1.
 * Initial Developer: zhh
 */
package com.lealone.plugins.postgresql.server.io;

import com.lealone.net.NetBuffer;

public class NetBufferInput {

    private NetBuffer buffer;

    public void reset(NetBuffer buffer) {
        this.buffer = buffer;
    }

    public int read() {
        return buffer.getUnsignedByte();
    }

    public byte readByte() {
        int ch = read();
        return (byte) ch;
    }

    public short readShort() {
        int ch1 = read();
        int ch2 = read();
        return (short) ((ch1 << 8) + (ch2 << 0));
    }

    public int readInt() {
        int ch1 = read();
        int ch2 = read();
        int ch3 = read();
        int ch4 = read();
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
    }

    public void readFully(byte b[]) {
        for (int i = 0, len = b.length; i < len; i++)
            b[i] = readByte();
    }
}
