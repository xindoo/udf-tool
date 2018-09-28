package me.xindoo.udftool.io.service;

import com.alibaba.fastjson.JSONObject;

import java.io.FileInputStream;
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

import static me.xindoo.udftool.common.UDFConstant.*;

public class Service implements Runnable{
    private InputStream inputStream;
    private OutputStream outputStream;
    private byte[] b = new byte[BUFFER_SIZE];
    private Socket socket;
    public Service(Socket socket) {
        this.socket = socket;
    }

    private void downLoad(String fileName) {
        try {
            inputStream = new FileInputStream(PATH_PREFIX + fileName);
            outputStream = socket.getOutputStream();
            int len = 0;
            while ((len = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, len);
                outputStream.flush();
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void upLoad(String fileName) {
        try {
            outputStream = new FileOutputStream(PATH_PREFIX+fileName);
            int len = 0;
            while ((len = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, len);
                outputStream.flush();
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            inputStream = socket.getInputStream();
            int len = inputStream.read(b);
            String jsonStr = new String(b, 0, len);
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
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("***********finished*************");
    }

    public static void startService() throws IOException {
        ServerSocket serverSocket = null;
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ExecutorService threadPool = new ThreadPoolExecutor( 10, 100,
                1L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), threadFactory);
        try {
            serverSocket = new ServerSocket(PORT);
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
