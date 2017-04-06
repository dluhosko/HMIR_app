package com.example.viktordluhos.hmir_demogui;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class acc_activity extends Menu {
    private SensorManager sManager;
    Sensor accelerometer;
    public TextView acc_tv;


    GraphView graph;
    DataPoint[] values;
    BarGraphSeries<DataPoint> series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc);
        acc_tv = (TextView) findViewById(R.id.textview_acc);
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        acc_tv.setText("wait for it");

        graph = (GraphView) findViewById(R.id.graph);
        values = new DataPoint[3];


        //
    }

    @Override
    protected void onResume() {
        super.onResume();
        sManager.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_UI);
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
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            acc_tv.setText(Html.fromHtml("<b>Accelerometer including gravity (m/s<sup><small>2</small></sup>) | Hardware</b><br/>" +
                    "X-axis: " + String.format("%.2f", event.values[0]) + "<br/>" +
                    "Y-axis: " + String.format("%.2f", event.values[1]) + "<br/>" +
                    "Z-axis: " + String.format("%.2f", event.values[2]) + "<br/>"));
            graph.removeAllSeries();
            for (int i = 0; i < 3; i++) {
                DataPoint v = new DataPoint(i + 1, event.values[i]);
                values[i] = v;
            }
            series = new BarGraphSeries<>(values);
            series.setSpacing(50);
            graph.addSeries(series);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(4);
            graph.getViewport().setMinY(-10);
            graph.getViewport().setMaxY(10);
            series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    if (data.getX() == 1)
                    {
                        return Color.RED;
                    }
                    else if (data.getX() == 2) {
                        return Color.GREEN;
                    }
                    else
                        return Color.BLUE;
                }
            });
            //series.setColor(Color.RED);

        }
    }
}
