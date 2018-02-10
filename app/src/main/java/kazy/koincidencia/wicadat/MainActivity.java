package kazy.koincidencia.wicadat;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "TCPClient";
    private String severIp =   "192.168.1.51";
    private int serverPort = 65432;
    private Socket connectionSocket;
    TextView rpmValue;
    TextView voltageValue;
    TextView tempValue;
    boolean exit = false ;
    Handler uithreadHandler = new Handler();
    String unparsedData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rpmValue = (TextView) findViewById(R.id.rpmValue);
        voltageValue = (TextView) findViewById(R.id.voltageValue);
        tempValue = (TextView) findViewById(R.id.tempValue);

        rpmValue.setText("0");
        voltageValue.setText("12.1");
        tempValue.setText("23");

        new Thread(getDataTask).start();
    }

    Runnable getDataTask = new Runnable() {
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
                    while(!exit) {
                        unparsedData = reader.readLine();
                        if (unparsedData == null) {
                            throw new Exception("Invalid data");
                        }
                        Log.d(TAG, "getDataTask" + unparsedData);
                        uithreadHandler.post(updateUITask);
                    }

                } catch (Exception e) {
                    // Catch the exception that socket.connect might throw
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }
    };

    protected void connect() throws Exception{
        Log.d(TAG, "C: Connecting...");
        InetAddress serverAddr = InetAddress.getByName(severIp);
        connectionSocket.connect(new InetSocketAddress(serverAddr, serverPort), 5000);
        Log.d(TAG, "Connected! ");
    }

    Runnable updateUITask = new Runnable() {
        @Override
        public void run() {
            try {
                if (unparsedData != null) {
                    String[] strs = unparsedData.split(",");
                    rpmValue.setText(strs[0]);
                    voltageValue.setText(strs[1]);
                    tempValue.setText(strs[2]);
                    Log.d(TAG, "updateUITask" + unparsedData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
