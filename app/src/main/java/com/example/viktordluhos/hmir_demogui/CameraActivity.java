package com.example.viktordluhos.hmir_demogui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import java.util.ArrayList;


public class CameraActivity extends Activity implements SensorEventListener {

    private Camera mCamera = null;
    private CameraView mCameraView = null;
    boolean shouldExecuteOnResume;

    int cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
    int camBackId = Camera.CameraInfo.CAMERA_FACING_BACK;
    int camFrontId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private SensorManager sManager;
    Sensor lightSensor;
    public PieChart pieChart;
    Sensor magnetometer;
    float lastDirection = 0;
    Sensor gyroscope;
    GraphView graphForGyro;
    DataPoint[] valuesForGyro;
    BarGraphSeries<DataPoint> seriesForGyro;
    Sensor accelerometer;
    GraphView graphForAcc;
    DataPoint[] valuesForAcc;
    BarGraphSeries<DataPoint> seriesForAcc;
    Vibrator vibrator;
    long pattern[] = { 0, 100, 200, 300, 400 };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        shouldExecuteOnResume = false;

        try{
            mCamera = Camera.open(cameraID);//you can use open(int) to use different cameras
        } catch (Exception e){
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }

        if(mCamera != null) {
            mCameraView = new CameraView(this, mCamera);//create a SurfaceView to show camera data
            FrameLayout camera_view = (FrameLayout)findViewById(R.id.camera_view);
            camera_view.addView(mCameraView);//add the SurfaceView to the layout
        }
        //Light sensor init
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        pieChart = (PieChart) findViewById(R.id.pieChart);
        //Direction
        magnetometer = sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        //Gyroscope
        gyroscope = sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        graphForGyro = (GraphView) findViewById(R.id.graphGyro);
        valuesForGyro = new DataPoint[3];
        //Accelerometer
        accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        graphForAcc= (GraphView) findViewById(R.id.graphAcc);
        valuesForAcc = new DataPoint[3];
        //Vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void FlipCamera(View view) {

        mCameraView.changeCam();
        if (cameraID==camFrontId){
            cameraID=camBackId;
        }else{
            cameraID=camFrontId;
        }
    }

    public void QuitCamera(View view) {
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sManager.registerListener((SensorEventListener) this, lightSensor, SensorManager.SENSOR_DELAY_UI);
        sManager.registerListener((SensorEventListener) this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        sManager.registerListener((SensorEventListener) this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        sManager.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_UI);

        if(shouldExecuteOnResume){
            // Your onResume Code Here
        } else{
            shouldExecuteOnResume = true;
        }

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
        lightSensor(event);
        direction(event);
        GyroSensor(event);
        AccSensor(event);



    }


    public void lightSensor(SensorEvent event) {
        float luminicity;
        float max_luminicity;

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            luminicity = event.values[0];
            if (luminicity > 100) {
                max_luminicity = 200;
            } else {
                max_luminicity = 100;
            }

            ArrayList<Entry> entries = new ArrayList<Entry>();
            if ((max_luminicity - luminicity) < 0) {
                entries.add(new Entry(0, 0));
            } else {
                entries.add(new Entry((max_luminicity - luminicity), 0));
            }
            entries.add(new Entry(luminicity, 1));

            PieDataSet dataSet = new PieDataSet(entries, "");
            ArrayList<String> labels = new ArrayList<String>();

            labels.add("Light");
            labels.add("Dark");

            PieData data = new PieData(labels, dataSet);
            pieChart.setData(data);
            pieChart.setDrawSliceText(false);
            pieChart.getLegend().setEnabled(false);
            pieChart.setDescription("");
            if (luminicity < 15) {
                dataSet.setColors(new int[]{Color.TRANSPARENT, Color.RED});
                vibrator.vibrate(pattern, 0);
            }
            else {
                dataSet.setColors(new int[]{Color.TRANSPARENT, Color.GREEN});
                vibrator.cancel();
            }


            pieChart.setTouchEnabled(false);
            pieChart.setCenterText(Integer.toString(Math.round(luminicity)) + " lx");
            pieChart.setCenterTextSize(20);
            pieChart.setHoleColor(Color.WHITE);
            pieChart.removeAllViews();

            dataSet.setDrawValues(false);

        }
    }
    public void direction(SensorEvent event) {
        float actualDirection = 0;
        float azimuth, pitch, roll;
        ImageView image = (ImageView) findViewById(R.id.imageView);
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            azimuth = Math.round(event.values[0]);
            pitch = event.values[1];
            roll = event.values[2];

            RotateAnimation rotate = new RotateAnimation(lastDirection, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(120);
            //rotate.setInterpolator(new LinearInterpolator());
            rotate.setFillAfter(true);
            image.startAnimation(rotate);
            lastDirection = -azimuth;
        }
    }
    public void GyroSensor(SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            graphForGyro.removeAllSeries();
            for (int i = 0; i < 3; i++) {
                DataPoint v = new DataPoint(i + 1, event.values[i]);
                valuesForGyro[i] = v;
            }
            seriesForGyro = new BarGraphSeries<>(valuesForGyro);
            seriesForGyro.setSpacing(50);
            graphForGyro.addSeries(seriesForGyro);


            GridLabelRenderer gridLabel = graphForGyro.getGridLabelRenderer();
            gridLabel.setHorizontalLabelsVisible(false);
            gridLabel.setGridStyle(GridLabelRenderer.GridStyle.NONE);
            gridLabel.setVerticalAxisTitle("Gyrosckope");
            gridLabel.setHorizontalAxisTitle("               x        y        z");

            graphForGyro.getViewport().setXAxisBoundsManual(true);
            graphForGyro.getViewport().setMinX(0);
            graphForGyro.getViewport().setMaxX(4);
            graphForGyro.getViewport().setMinY(-10);
            graphForGyro.getViewport().setMaxY(10);
            seriesForGyro.setValueDependentColor(new ValueDependentColor<DataPoint>() {
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

        }


    }

    public void AccSensor(SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            graphForAcc.removeAllSeries();
            for (int i = 0; i < 3; i++) {
                DataPoint v = new DataPoint(i + 1, event.values[i]);
                valuesForAcc[i] = v;
            }
            seriesForAcc = new BarGraphSeries<>(valuesForAcc);
            seriesForAcc.setSpacing(50);
            seriesForAcc.setTitle("Accelerometer");
            graphForAcc.addSeries(seriesForAcc);

            GridLabelRenderer gridLabel = graphForAcc.getGridLabelRenderer();
            gridLabel.setHorizontalLabelsVisible(false);
            gridLabel.setGridStyle(GridLabelRenderer.GridStyle.NONE);
            gridLabel.setHighlightZeroLines(true);
            gridLabel.setVerticalAxisTitle("Accelerometer");
            gridLabel.setHorizontalAxisTitle("               x        y        z");


            graphForAcc.getViewport().setXAxisBoundsManual(true);
            graphForAcc.getViewport().setMinX(0);
            graphForAcc.getViewport().setMaxX(4);
            graphForAcc.getViewport().setMinY(-10);
            graphForAcc.getViewport().setMaxY(10);
            seriesForAcc.setValueDependentColor(new ValueDependentColor<DataPoint>() {
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

        }

    }



}
