package kazy.koincidencia.wicadat;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private String TAG = "MainActivity";
    private TextView rpmValue;
    private TextView voltageValue;
    private TextView tempValue;
    private TextView altitudeValue;
    private TextView latitudeValue;
    private TextView longitudeValue;

    private Handler uiThreadHandler = new Handler();
    private Thread wirelessDataGathererThread;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rpmValue = (TextView) findViewById(R.id.rpmValue);
        voltageValue = (TextView) findViewById(R.id.voltageValue);
        tempValue = (TextView) findViewById(R.id.tempValue);
        altitudeValue = (TextView) findViewById(R.id.altitudeValue);
        latitudeValue = (TextView) findViewById(R.id.latitudeValue);
        longitudeValue = (TextView) findViewById(R.id.longitudeValue);

        TextView[] textViews = {rpmValue, voltageValue, tempValue};
        wirelessDataGathererThread = new Thread(new WirelessDataGatherer(uiThreadHandler, textViews));
        wirelessDataGathererThread.start();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        altitudeValue.setText(String.format(Locale.getDefault(), "%.1fm", location.getAltitude()));
        longitudeValue.setText(String.format(Locale.getDefault(), "%.5f°", location.getLongitude()));
        latitudeValue.setText(String.format(Locale.getDefault(), "%.5f°", location.getLatitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled");
    }
}
