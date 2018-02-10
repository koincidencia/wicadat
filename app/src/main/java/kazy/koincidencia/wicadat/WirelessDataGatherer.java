package kazy.koincidencia.wicadat;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by kazy on 2018. 02. 10..
 */

public class WirelessDataGatherer implements Runnable {
    private static String TAG = "WirelessDataGatherer";
    private String severIp =   "192.168.1.51";
    private int serverPort = 65432;
    private Socket connectionSocket;
    private Handler uitaskHandler;
    private boolean exit = false;
    private TextView[] textViews;

    public  WirelessDataGatherer(Handler handler, TextView[] textViews) {
        this.uitaskHandler = handler;
        this.textViews = textViews;
    }

    @Override
    public void run() {
        while (!exit) {
            try {
                // Create a new instance of Socket
                connectionSocket = new Socket();
                // Connect to server
                connect();
                // Receive data
                BufferedReader reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                connectionSocket.setSoTimeout(10000);
                while(!exit) {
                    String unparsedData = reader.readLine();
                    Log.d(TAG, unparsedData);
                    Runnable showData = new WirelessDataUpdater(unparsedData, textViews);
                    uitaskHandler.post(showData);
                }
            } catch (Exception e) {
                Writer writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                String s = writer.toString();
                Log.e(TAG, s);
                Log.d(TAG, "Disconnected.");
            }
        }
    }

    private void connect() throws Exception{
        Log.d(TAG, "Connecting...");
        InetAddress serverAddr = InetAddress.getByName(severIp);
        connectionSocket.connect(new InetSocketAddress(serverAddr, serverPort), 5000);
        Log.d(TAG, "Connected! ");
    }

}
