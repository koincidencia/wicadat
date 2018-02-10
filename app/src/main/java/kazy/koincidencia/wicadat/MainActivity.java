package kazy.koincidencia.wicadat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView rpmValue;
    TextView voltageValue;
    TextView tempValue;

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
    }
}
