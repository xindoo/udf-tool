package me.xindoo.udftool.client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;


public class Client {
    public static void test() throws IOException {
        Scanner scanner = new Scanner(System.in);
        Socket socket = null;
        try {
            socket = new Socket("47.75.177.135", 9999);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream inputStream = new FileInputStream("/Users/xindoo/Downloads/windows95.zip");

        OutputStream outputStream = socket.getOutputStream();
        byte b[] = new byte[1024];
        int cnt = 1;
        long all = inputStream.available();
        System.out.println(all);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        outputStream.write("test.zip\n".getBytes());
        outputStream.flush();
        while (inputStream.read(b) > 0) {
            cnt++;
            if (cnt%100 == 0) {

                System.out.println(" " +(1.0- (inputStream.available()*1.0)/all) * 100 + "%");
            }
            outputStream.write(b);
            outputStream.flush();
        }
        inputStream.close();
        outputStream.close();
        socket.close();
        System.out.println("finished");
    }
    public static void main(String[] args) {
        try {
            test();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
