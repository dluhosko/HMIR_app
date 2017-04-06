package com.example.viktordluhos.hmir_demogui;

import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import static android.R.attr.orientation;


public class Menu extends AppCompatActivity implements SensorEventListener{
//AppCompatActivity
    public SensorManager sManager;
    public Sensor accelerometer;
    public  Sensor gyroscope;
    public  Sensor orientation;
    public  Sensor light;
    public  Sensor proximity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);


        accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        orientation = sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        light = sManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximity = sManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }
    public void  goToGyroActivity(View view)
    {
        Intent intent = new Intent (this, GyroActivity.class);
        startActivity(intent);
    }
    public void goToAccelerometerActivity(View view)
    {
        Intent intent = new Intent (this, acc_activity.class);
        startActivity(intent);
    }
    public void goToMagnetActivity(View view)
    {
        Intent intent = new Intent (this, MagnetActivity.class);
        startActivity(intent);
    }
    public void goToLightSensorActivity(View view)
    {
        Intent intent = new Intent (this, LightSensorActivity.class);
        startActivity(intent);
    }
    public void goToCameraActivity(View view)
    {
        Intent intent = new Intent (this, CameraActivity.class);
        startActivity(intent);
    }
    public void goToReadSensorsActivity(View view)
    {
        Intent intent = new Intent (this, ReadSensorsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
