/*
 * Copyright Lealone Database Group.
 * Licensed under the Server Side Public License, v 1.
 * Initial Developer: zhh
 */
package com.lealone.plugins.postgresql.server.io;

import com.lealone.db.session.Session;
import com.lealone.net.NetBuffer;
import com.lealone.net.TransferOutputStream.GlobalWritableChannel;
import com.lealone.net.WritableChannel;

public class NetBufferOutput {

    private final GlobalWritableChannel channel;
    private final NetBuffer buffer;

    public NetBufferOutput(WritableChannel writableChannel, NetBuffer outBuffer) {
        channel = new GlobalWritableChannel(writableChannel, outBuffer);
        buffer = channel.getGlobalBuffer();
    }

    public void writeShort(int v) {
        write((v >>> 8) & 0xFF);
        write((v >>> 0) & 0xFF);
    }

    public void writeInt(int v) {
        write((v >>> 24) & 0xFF);
        write((v >>> 16) & 0xFF);
        write((v >>> 8) & 0xFF);
        write((v >>> 0) & 0xFF);
    }

    public void write(int b) {
        buffer.appendByte((byte) b);
    }

    public void write(byte b[]) {
        write(b, 0, b.length);
    }

    public void write(byte b[], int off, int len) {
        buffer.appendBytes(b, off, len);
    }

    public void setInt(int pos, int v) {
        buffer.setByte(pos, (byte) ((v >>> 24) & 0xFF));
        buffer.setByte(pos + 1, (byte) ((v >>> 16) & 0xFF));
        buffer.setByte(pos + 2, (byte) ((v >>> 8) & 0xFF));
        buffer.setByte(pos + 3, (byte) (v & 0xFF));
    }

    public int length() {
        return buffer.length();
    }

    public void flush() {
        channel.flush();
    }

    public void startMessage() {
        channel.startWrite(Session.STATUS_OK);
    }
}
