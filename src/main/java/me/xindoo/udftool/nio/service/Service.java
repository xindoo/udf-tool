package me.xindoo.udftool.nio.service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static me.xindoo.udftool.common.UDFConstant.*;

public class Service {
    public static void main(String[] args) {
        SocketChannel sc = null;
        ServerSocketChannel ssc = null;
        FileChannel fc = null;
        try {
            RandomAccessFile aFile = new RandomAccessFile("/Users/xindoo/Downloads/b.log", "rw");
            fc = aFile.getChannel();
            ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(PORT));
            sc = ssc.accept();
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            int len = 0;
            while ((len = sc.read(buffer)) != -1) {
                buffer.flip();
                fc.write(buffer);
                buffer.compact();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fc.close();
            sc.close();
            ssc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
