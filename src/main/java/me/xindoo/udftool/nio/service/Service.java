package me.xindoo.udftool.nio.service;

import com.alibaba.fastjson.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static me.xindoo.udftool.common.UDFConstant.*;

public class Service implements Runnable {
    private SocketChannel threadsc;
    public Service(SocketChannel sc )  {
        threadsc = sc;
    }
    public void run() {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        byte[] b = new byte[BUFFER_SIZE];
        try {
            int len = threadsc.read(buffer);
            buffer.flip();
            buffer.get(b);
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String jsonStr = new String(b);
        JSONObject json = JSONObject.parseObject(jsonStr);
        System.out.print(json);
        String type = json.getString(TYPE);
        if (UPLOAD.equals(type)) {
            upLoad(json.getString(FILE_NAME));
        } else if(DOWNLOAD.equals(type)) {
            downLoad(json.getString(FILE_NAME));
        } else {
            System.out.println("Impossible to be here");
        }
    }
    private void downLoad(String filename) {
    }
    private void upLoad(String filename) {
        try {
            RandomAccessFile aFile = null;
            aFile = new RandomAccessFile("/Users/xindoo/Downloads/"+filename, "rw");
            FileChannel fc = aFile.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            int len = 0;
            while ((len = threadsc.read(buffer)) != -1) {
                buffer.flip();
                fc.write(buffer);
                buffer.compact();
            }
            fc.close();
            threadsc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startService() throws IOException {
        ServerSocketChannel ssc = null;
        ExecutorService threadPool = new ThreadPoolExecutor( 10, 100,
                1L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        try {
            ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(PORT));
            boolean isStop = false;
            while (true && !isStop) {
                SocketChannel sc = ssc.accept();
                threadPool.submit(new Service(sc));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ssc.close();
    }

    public static void main(String[] args) {
        try {
            startService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
