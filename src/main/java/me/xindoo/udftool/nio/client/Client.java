package me.xindoo.udftool.nio.client;


import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import static me.xindoo.udftool.common.UDFConstant.*;

public class Client {
    public static void main(String[] args) {
        SocketChannel sc = null;
        FileChannel fc = null;
        try {
            sc = SocketChannel.open();
            RandomAccessFile aFile = new RandomAccessFile("/Users/xindoo/Downloads/a.log", "r");
            fc = aFile.getChannel();

            sc.configureBlocking(false);
            sc.connect(new InetSocketAddress("127.0.0.1",PORT));
            if (sc.finishConnect()) {

            }
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            int len = 0;
            while ((len=fc.read(buffer)) != -1) {
                System.out.println(len);
                buffer.flip();
                sc.write(buffer);
                buffer.compact();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fc.close();
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


