package com.example.viktordluhos.hmir_demogui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class LightSensorActivity extends Menu{

    private SensorManager sManager;
    Sensor lightSensor;
    public TextView light_tv;
    public PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_sensor);
        light_tv = (TextView) findViewById(R.id.textViewLight_layout);
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sManager.getDefaultSensor(Sensor.TYPE_LIGHT);


        pieChart = (PieChart) findViewById(R.id.pieChart);



        light_tv.setText("wait for it");
    }
    @Override
    protected void onResume() {
        super.onResume();
        sManager.registerListener((SensorEventListener) this, lightSensor, SensorManager.SENSOR_DELAY_UI);
    }
    @Override
    protected void onPause() {
        super.onPause();

        sManager.unregisterListener((SensorEventListener) this);
    }
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        //Do nothing.
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        float luminicity;
        float max_luminicity;

        if (event.sensor.getType() == Sensor.TYPE_LIGHT){
            light_tv.setText(Html.fromHtml("<b>Light (lx) | Hardware</b><br/>" +
                    "Value: " + String.format("%.0f",event.values[0]) + "<br/>"));

            luminicity = event.values[0];
            if (luminicity > 100){
                max_luminicity = 200;
            }
            else{
                max_luminicity = 100;
            }
            ArrayList<Entry> entries = new ArrayList<Entry>();
            if ((max_luminicity-luminicity) < 0){
                entries.add(new Entry(0  , 0));
            }else{
                entries.add(new Entry((max_luminicity-luminicity)  , 0));
            }
            entries.add(new Entry(luminicity , 1));

            PieDataSet dataSet = new PieDataSet(entries , "");
            ArrayList<String> labels = new ArrayList<String>();

            labels.add("Light");
            labels.add("Dark");

            PieData data = new PieData(labels, dataSet);
            pieChart.setData(data);
            pieChart.setDrawSliceText(false);
            pieChart.getLegend().setEnabled(false);
            pieChart.setDescription("");
            if (luminicity < 15)
                dataSet.setColors(new int[] {Color.TRANSPARENT , Color.RED});
            else
                dataSet.setColors(new int[] {Color.TRANSPARENT , Color.GREEN});

            pieChart.setTouchEnabled(false);
            pieChart.setCenterText(Integer.toString(Math.round(luminicity)) + " lx");
            pieChart.setCenterTextSize(20);
            pieChart.setHoleColor(Color.WHITE);
            pieChart.removeAllViews();

            dataSet.setDrawValues(false);
        }
    }



    public void lightSensor (){


    }
}
