package kioli.facerecogntest;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
//import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;
import android.app.Notification;
import android.app.NotificationManager;


public class MainActivity extends Activity implements SurfaceHolder.Callback {

    private TextView text;
    private View background;
//    private Button button;
    private Camera camera;

    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;

    private boolean preview = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.text);
        background = findViewById(R.id.background);
//        button = (Button) findViewById(R.id.button);
//        button.setOnClickListener(this);

        surfaceView = (SurfaceView) findViewById(R.id.preview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

         // no need to call prepare(); create() does that for you


    }


    Camera.FaceDetectionListener faceDetectionListener = new Camera.FaceDetectionListener() {
        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            if (faces.length >= 1) {
                text.setText(getString(R.string.detected_text));
                text.setTextColor(getResources().getColor(R.color.looking));
//                background.setBackgroundDrawable(getResources().getDrawable(R.drawable.positive_bg));

            } else {
                text.setText(getString(R.string.default_text));
                text.setTextColor(getResources().getColor(R.color.not_looking));

            }
        }
    };



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int n = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < n; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                camera = Camera.open(i);
                camera.setFaceDetectionListener(faceDetectionListener);
                try {
                    camera.setPreviewDisplay(surfaceHolder);
                } catch (Exception e) {
                    Log.e("KIOLI", "Could not preview the image.", e);
                }
                return;
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(preview){
            camera.stopFaceDetection();
            camera.stopPreview();
            preview = false;
        }

        if (camera != null){
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.setDisplayOrientation(90);
                camera.startPreview();
                camera.startFaceDetection();
                preview = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            camera.stopFaceDetection();
            camera.stopPreview();
            camera.release();
            camera = null;
            preview = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
