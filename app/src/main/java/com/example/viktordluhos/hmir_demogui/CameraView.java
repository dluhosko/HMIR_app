package com.example.viktordluhos.hmir_demogui;



import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import java.util.List;
import java.io.IOException;


public class CameraView extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private static final String TAG = "CameraView";
    private List<Camera.Size> mSupportedPreviewSizes;
    private Camera.Size mPreviewSize;
    private boolean previewing = false;

    int cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
    int camBackId = Camera.CameraInfo.CAMERA_FACING_BACK;
    int camFrontId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    public CameraView(Context context, Camera camera){
        super(context);

        mCamera = camera;
        mCamera.setDisplayOrientation(90);
        //get the holder and set this class as the callback, so we can get camera data here
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);

        // supported preview sizes
        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        for(Camera.Size str: mSupportedPreviewSizes)
            Log.e(TAG, str.width + "/" + str.height);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try{

            // This try Camera.open(cameraID) solves crash when app is restored after being minimized
            try{
                mCamera = Camera.open(cameraID);//you can use open(int) to use different cameras
            } catch (Exception e){
                Log.d("ERROR", "Failed to get camera: " + e.getMessage());
            }

            //when the surface is created, we can set the camera to draw images in this surfaceholder
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            previewing = true;
        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceCreated " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.e(TAG, "surfaceChanged => w=" + w + ", h=" + h);
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
            previewing = false;
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or reformatting changes here
        // start preview with new settings
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            if (cameraID==camBackId) parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            previewing = true;

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }

        float ratio;
        if(mPreviewSize.height >= mPreviewSize.width)
            ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
        else
            ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;

        setMeasuredDimension(width, (int) (width * ratio));
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {

        if (sizes==null) return null;

        Camera.Size optimalSize = null;
        double ratio = (double)h/w;
        double minDiff = Double.MAX_VALUE;
        double newDiff;
        for (Camera.Size size : sizes) {
            newDiff = Math.abs((double)size.width/size.height - ratio);
            if (newDiff < minDiff) {
                optimalSize = size;
                minDiff = newDiff;
            }
        }
        return optimalSize;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //our app has only one screen, so we'll destroy the camera in the surface
        //if you are unsing with more screens, please move this code your activity
        mCamera.stopPreview();
        previewing = false;
        mCamera.release();
    }

    public void messageFcn() {
        Toast.makeText(this.getContext(), "Camera changed!", Toast.LENGTH_LONG).show();
    }

    public void changeCam() {
        // TODO Auto-generated method stub
        if(mCamera != null && previewing){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;

            previewing = false;

            if (cameraID==camFrontId){
                cameraID=camBackId;
            }else{
                cameraID=camFrontId;
            }
            mCamera = Camera.open(cameraID);

            if (mCamera != null){
                try {

                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                    if (cameraID==camBackId) parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    mCamera.setParameters(parameters);
                    mCamera.setDisplayOrientation(90);
                    mCamera.setPreviewDisplay(mHolder);
                    mCamera.startPreview();
                    previewing = true;

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else {

            if (cameraID==camFrontId){
                cameraID=camBackId;
            }else{
                cameraID=camFrontId;
            }

        }
    }
}