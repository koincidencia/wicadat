package kazy.koincidencia.wicadat;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView rpmValue;
    TextView voltageValue;
    TextView tempValue;
    Handler uiThreadHandler = new Handler();
    Thread wirelessDataGathererThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rpmValue = (TextView) findViewById(R.id.rpmValue);
        voltageValue = (TextView) findViewById(R.id.voltageValue);
        tempValue = (TextView) findViewById(R.id.tempValue);

        rpmValue.setText("0");
        voltageValue.setText("0");
        tempValue.setText("0");

        TextView[] textViews = {rpmValue, voltageValue, tempValue};
        wirelessDataGathererThread = new Thread(new WirelessDataGatherer(uiThreadHandler, textViews));
        wirelessDataGathererThread.start();
    }
}
