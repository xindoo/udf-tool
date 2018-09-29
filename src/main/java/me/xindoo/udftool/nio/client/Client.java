package me.xindoo.udftool.nio.client;


import com.alibaba.fastjson.JSONObject;
import me.xindoo.udftool.common.UDFConstant;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import static me.xindoo.udftool.common.UDFConstant.*;

public class Client {
    private static Options options;

    static {
        options = new Options();
        Option upload = OptionBuilder.withArgName("filename")
                .withLongOpt("put")
                .hasArg()
                .withDescription("Upload file from local to service")
                .create("u");
        Option download = OptionBuilder.withArgName("filename")
                .withLongOpt("get")
                .hasArg()
                .withDescription("Download file from l service to local")
                .create("d");

        options.addOption("h", "help", false, "help");
        options.addOption(upload);
        options.addOption(download);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("udf", options);
    }

    public static void upLoad(String fileName) throws IOException {
        SocketChannel sc = null;
        FileChannel fc = null;
        try {
            sc = SocketChannel.open();
            RandomAccessFile aFile = new RandomAccessFile(fileName, "r");
            JSONObject json = new JSONObject();
            json.put(TYPE, UPLOAD);
            json.put(FILE_NAME, "a.log");
            fc = aFile.getChannel();

//            sc.configureBlocking(false);
            sc.connect(new InetSocketAddress("127.0.0.1", PORT));
//            if (sc.finishConnect()) {
//
//            }

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            int len = 0;
            sc.write(buffer.put(json.toJSONString().getBytes()));
            buffer.clear();
            while ((len = fc.read(buffer)) != -1) {
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
        System.out.println("finished");
    }

    public static void downLoad(String fileName) throws IOException {
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject json = new JSONObject();
        json.put(TYPE, DOWNLOAD);
        json.put(FILE_NAME, "QQ_V6.5.1.dmg");
        OutputStream socketOutputStream = socket.getOutputStream();
        byte b[] = new byte[UDFConstant.BUFFER_SIZE];
        //Send download signal
        socketOutputStream.write(json.toString().getBytes());
        socketOutputStream.flush();

        //Write binary stream to local disk
        InputStream socketInputStream = socket.getInputStream();
        OutputStream fileOutputStream = new FileOutputStream(PATH_PREFIX + "downLoad/" + fileName);
        int l = 0;
        while ((l = socketInputStream.read(b)) > 0) {
            fileOutputStream.write(b, 0, l);
            fileOutputStream.flush();
        }
        fileOutputStream.close();
        socketInputStream.close();
        socketOutputStream.close();
        socket.close();
        System.out.println("finished");
    }

    public static void main(String[] args) {
        args = new String[]{"-u", "/Users/xindoo/Downloads/a.log"};
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("u")) {
                String filname = line.getOptionValue("u");
                try {
                    upLoad(filname);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (line.hasOption("get")) {
                String filname = line.getOptionValue("d");
                try {
                    downLoad("QQ_V6.5.1.dmg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
    }

    public static void main1(String[] args) {
        SocketChannel sc = null;
        FileChannel fc = null;
        try {
            sc = SocketChannel.open();
            RandomAccessFile aFile = new RandomAccessFile("/Users/xindoo/Downloads/a.log", "r");
            fc = aFile.getChannel();

            sc.configureBlocking(false);
            sc.connect(new InetSocketAddress("127.0.0.1", PORT));
            if (sc.finishConnect()) {

            }
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            int len = 0;
            while ((len = fc.read(buffer)) != -1) {
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


