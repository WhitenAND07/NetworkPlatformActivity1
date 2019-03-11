package com.example.testaccelerometre;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class TestAccelerometreActivity extends Activity implements SensorEventListener {
    private SensorManager accelerometerSensorManager;
    private Sensor accelerometerSensor;
    private SensorManager lightSensorManager;
    private Sensor lightSensor;
    private boolean color = false;
    private TextView view1, view2, view3;
    private ScrollView scrollView3;
    private long lastUpdate;
    private float downLevel, upLevel, lastLightValue;



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        view1 = findViewById(R.id.textView1);
        view2 = findViewById(R.id.textView2);
        view3 = findViewById(R.id.textView3);
        scrollView3 = findViewById(R.id.scrollView3);

        view1.setBackgroundColor(Color.GREEN);
        view3.setBackgroundColor(Color.YELLOW);
        scrollView3.setBackgroundColor(Color.YELLOW);

        accelerometerSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (accelerometerSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometerSensor =
                    accelerometerSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            // register this class as a listener for the accelerometer sensor

            String capabilities =
                    "Resolution: " + accelerometerSensor.getResolution() + "\n" +
                            "Power: " + accelerometerSensor.getPower() + "\n" +
                            "Maximum Range: " + accelerometerSensor.getMaximumRange();
            view2.setText(String.format("%s\n%s", getString(R.string.shake), capabilities));
        } else {
            view2.setText(R.string.accelerometerNotFound);
        }
        lastUpdate = System.currentTimeMillis();

        lightSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (lightSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            lightSensor = lightSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

            float maxRang = lightSensor.getMaximumRange();
            downLevel = maxRang / 3 ;
            upLevel = 2 * (maxRang / 3);
            view3.setText(String.format("%s - %s \n", getString(R.string.lightSensorUp), maxRang));
        } else {
            view2.setText(R.string.lightSensorNotFound);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        accelerometerSensorManager.registerListener(this, accelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        lightSensorManager.registerListener(this, lightSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        manageSensors(event);
    }

    private void manageSensors(SensorEvent event) {
        if (event.sensor == accelerometerSensor) getAccelerometer(event);
        else if (event.sensor == lightSensor) getLight(event);
    }

    private void getLight(SensorEvent event) {
        float values[] = event.values;
        float x = values[0];
        long actualTime = System.currentTimeMillis();
        if ((Math.abs(lastLightValue - x)) >= 5000.0) {
            if (actualTime - lastUpdate < 2000) {
                return;
            }

            lastUpdate = actualTime;
            lastLightValue = x;
            if (x < downLevel) {
                view3.setText(view3.getText().toString() + getText(R.string.newValue) + x +"\n" + "LOW Intensity" + "\n");
            } else if (x > upLevel) {
                view3.setText(view3.getText().toString() + getText(R.string.newValue) + x +"\n" + "HIGH Intensity" + "\n");
            } else {
                view3.setText(view3.getText().toString() + getText(R.string.newValue) + x +"\n" + "MEDIUM Intensity" + "\n");
            }
        }
    }

    private void getAccelerometer(SensorEvent event) {
        float values[] = event.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();
        if (accelationSquareRoot >= 2)
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;

            Toast.makeText(this, R.string.shuffed, Toast.LENGTH_SHORT).show();
            if (color) {
                view1.setBackgroundColor(Color.GREEN);

            } else {
                view1.setBackgroundColor(Color.RED);
            }
            color = !color;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        accelerometerSensorManager.unregisterListener(this);
        lightSensorManager.unregisterListener(this);
    }
    @Override
    protected void onStop() {
        // unregister listener
        super.onStop();
        accelerometerSensorManager.unregisterListener(this);
        lightSensorManager.unregisterListener(this);
    }
}
