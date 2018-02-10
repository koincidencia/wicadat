package kazy.koincidencia.wicadat;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private String TAG = "MainActivity";
    private TextView rpmValue;
    private TextView voltageValue;
    private TextView tempValue;
    private TextView axValue;
    private TextView ayValue;
    private TextView azValue;
    private Button calibrateButton;

    private Handler uiThreadHandler = new Handler();
    private Thread wirelessDataGathererThread;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private boolean calibrationMode = false;
    private int calibrationCntr = 0;
    final private int CALIBRATION_END_COUNT = 10;
    private float[] offsetVector = {0, 0, 0};
    Semaphore sensorMutex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorMutex = new Semaphore(1);

        rpmValue = (TextView) findViewById(R.id.rpmValue);
        voltageValue = (TextView) findViewById(R.id.voltageValue);
        tempValue = (TextView) findViewById(R.id.tempValue);
        axValue = (TextView) findViewById(R.id.axValue);
        ayValue = (TextView) findViewById(R.id.ayValue);
        azValue = (TextView) findViewById(R.id.azValue);
        calibrateButton = (Button) findViewById(R.id.calibButton);

        calibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(calibrate).start();
            }
        });

        TextView[] textViews = {rpmValue, voltageValue, tempValue};
        wirelessDataGathererThread = new Thread(new WirelessDataGatherer(uiThreadHandler, textViews));
        wirelessDataGathererThread.start();

        sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            sensorMutex.acquire();
            if (event.sensor.equals(accelerometer)) {
                if (calibrationMode) {
                    offsetVector[0] += event.values[0];
                    offsetVector[1] += event.values[1];
                    offsetVector[2] += event.values[2];
                    calibrationCntr++;
                } else {
                    float[] accelVector = {0, 0, 0};
                    accelVector[0] = event.values[0] - offsetVector[0];
                    accelVector[1] = event.values[1] - offsetVector[1];
                    accelVector[2] = event.values[2] - offsetVector[2];
                    axValue.setText(String.valueOf(accelVector[0]));
                    ayValue.setText(String.valueOf(accelVector[1]));
                    azValue.setText(String.valueOf(accelVector[2]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sensorMutex.release();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private Runnable calibrate = new Runnable() {
        @Override
        public void run() {
            try {
                sensorMutex.acquire();
                Log.d(TAG, "Starting calibration thread");
                calibrationCntr = 0;
                offsetVector[0] = 0;
                offsetVector[1] = 0;
                offsetVector[2] = 0;
                calibrationMode = true;
                sensorMutex.release();
                while(calibrationCntr < CALIBRATION_END_COUNT) {
                    Thread.sleep(10);
                }
                sensorMutex.acquire();
                calibrationMode = false;
                offsetVector[0] = offsetVector[0] / calibrationCntr;
                offsetVector[1] = offsetVector[1] / calibrationCntr;
                offsetVector[2] = offsetVector[2] / calibrationCntr;
                Log.d(TAG, "Calibration done");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                sensorMutex.release();
            }
        }
    };
}
