package com.example.viktordluhos.hmir_demogui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import static android.R.attr.animation;
import static com.example.viktordluhos.hmir_demogui.R.id.image;

public class MagnetActivity extends Menu {

    private SensorManager sManager;
    Sensor magnetometer;
    public TextView magnet_tv;
    float lastDirection = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnet);
        magnet_tv = (TextView) findViewById(R.id.textViewMagnet_layout);
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magnetometer = sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        magnet_tv.setText("wait for it");

        //RotateAnimation rotate ;//= new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

       /* rotate.setDuration(5000);
        rotate.setInterpolator(new LinearInterpolator());
*/
        //ImageView image= (ImageView) findViewById(R.id.imageView);

        //image.startAnimation(rotate);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sManager.registerListener((SensorEventListener) this, magnetometer, SensorManager.SENSOR_DELAY_UI);
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
        float actualDirection = 0;
        float azimuth, pitch, roll;
        ImageView image = (ImageView) findViewById(R.id.imageView);
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            azimuth = Math.round(event.values[0]);
            //
            pitch = event.values[1];
            roll = event.values[2];
            //
            magnet_tv.setText(Html.fromHtml("<b>Orientation (deg) | Software</b><br/>" +
                    "X-axis (Roll): " + String.format("%.2f", event.values[0]) + "<br/>" +
                    "Y-axis (Pitch): " + String.format("%.2f", event.values[1]) + "<br/>" +
                    "Z-axis (Yaw): " + String.format("%.2f", event.values[2]) + "<br/>"));
            magnet_tv.setText(Html.fromHtml("<b>Orientation (deg) | Software</b><br/>" +
                    "Azimuth: " + String.format("%.2f", azimuth) + "<br/>" +
                    "Last Direction: " + String.format("%.2f", lastDirection) + "<br/>"));

            RotateAnimation rotate = new RotateAnimation(lastDirection, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(120);
            rotate.setInterpolator(new LinearInterpolator());
            //
            //rotate.setFillAfter(true);
            //
            image.startAnimation(rotate);
            lastDirection = -azimuth;

        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Magnet Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


}


