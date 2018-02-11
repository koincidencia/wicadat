package kazy.koincidencia.wicadat;

import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by kazy on 2018. 02. 10..
 */

public class WirelessDataUpdater implements Runnable {
    private String TAG = "WirelessDataUpdater";
    private String data;
    private TextView rpmValue;
    private TextView voltageValue;
    private TextView tempValue;

    public WirelessDataUpdater(String data, TextView[] textViews) {
        this.data = data;
        rpmValue = textViews[0];
        voltageValue = textViews[1];
        tempValue = textViews[2];
    }

    @Override
    public void run() {
        try {
            if (data != null) {
                String[] strs = data.split(",");
                rpmValue.setText(strs[0]);
                voltageValue.setText(strs[1]);
                tempValue.setText(strs[2]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
