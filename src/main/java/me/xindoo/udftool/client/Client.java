package me.xindoo.udftool.client;

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
import java.net.Socket;

import static me.xindoo.udftool.common.UDFConstant.*;

public class Client {
    private static Options options;
    static  {
        options = new Options();
        Option upload = OptionBuilder.withArgName( "filename")
                .withLongOpt("put")
                .hasArg()
                .withDescription(  "Upload file from local to service")
                .create( "u");
        Option download = OptionBuilder.withArgName( "filename")
                .withLongOpt("get")
                .hasArg()
                .withDescription(  "Download file from l service to local")
                .create( "d");

        options.addOption( "h", "help", false,"help");
        options.addOption(upload);
        options.addOption(download);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "udf", options );
    }

    public static void upLoad(String fileName) throws IOException {
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", 9999);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject json = new JSONObject();
        json.put(TYPE, UPLOAD);
        json.put(FILE_NAME, "QQ_V6.5.1.dmg");
        InputStream fileInputStream = new FileInputStream(fileName);

        OutputStream socketOutputStream = socket.getOutputStream();
        byte b[] = new byte[UDFConstant.BUFFER_SIZE];
        //Send upload signal
        socketOutputStream.write(json.toString().getBytes());
        socketOutputStream.flush();

        //Send data
        int len = 0;
        while ((len = fileInputStream.read(b)) > 0) {
            socketOutputStream.write(b, 0, len);
            socketOutputStream.flush();
        }
        fileInputStream.close();
        socketOutputStream.close();
        socket.close();
        System.out.println("finished");
    }

    public static void downLoad(String fileName) throws IOException {
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", 9999);
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
        OutputStream fileOutputStream = new FileOutputStream(PATH_PREFIX+"downLoad/"+fileName);
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

        args = new String[]{"--get", "/Users/xindoo/Downloads/QQ_V6.5.1.dmg"};
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args);
            if (line.hasOption("u")) {
                String filname = line.getOptionValue("u");
                try {
                    upLoad(filname);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(line.hasOption("get")) {
                String filname = line.getOptionValue("d");
                try {
                    downLoad("QQ_V6.5.1.dmg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        catch( ParseException exp ) {
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
        }
    }
}
