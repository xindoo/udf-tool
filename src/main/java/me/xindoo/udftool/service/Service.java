package me.xindoo.udftool.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Service implements Runnable{
    private Socket socket;
    public Service(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = socket.getInputStream();

            byte[] b = new byte[4096];

            inputStream.read(b);
            int i = 0;
            for (i = 0; i < 4096; i++) {
                if (b[i] == (byte)'\n'){
                    break;
                }
            }
            String filename = new String(b, 0, i);
            System.out.print(filename);
            outputStream = new FileOutputStream(filename);

            int l = 0;
            while ((l = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, l);
                outputStream.flush();
            }
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(" upload finished!");
    }

    public static void startService() throws IOException {
        ServerSocket serverSocket = null;
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ExecutorService threadPool = new ThreadPoolExecutor( 10, 100,
                1L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), threadFactory);
        try {
            serverSocket = new ServerSocket(9999);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Socket socket = null;
        boolean isStop = true;
        while (true && isStop) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            threadPool.submit(threadFactory.newThread(new Service(socket)));
        }
        serverSocket.close();
    }

    public static void main(String[] args) {
        try {
            startService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
