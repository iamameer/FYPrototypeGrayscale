package fyp.fyprototypegrayscale;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    public final static String DEBUG_TAG = "MakePhotoActivity";
    private Camera camera;
    private int cameraId = 0;
    private Bitmap bitmap, bmpGray, bmpTreshold, bmpCanny, bmpFindcontour, bmpContourArea;
    private Button button;
    private ImageView imgGrayscale, imgTreshold, imgCanny, imgFindcontour, imgContourArea,imgResult;
    Mat mRGBA, mGrayscale, mTreshold, mCanny, mFindcontour, mContourArea, mResult;

    static{
        if(OpenCVLoader.initDebug()) {
            Log.d(DEBUG_TAG,"cv loaded");
        }else{
            Log.d(DEBUG_TAG,"cv failed");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // do we have a camera?
        if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
                    .show();
        } else {
            cameraId = findFacingCamera();
            camera = Camera.open(cameraId);
        }

        imgResult = (ImageView) findViewById(R.id.imgOriginal);
        imgGrayscale = (ImageView) findViewById(R.id.imgGrayscale);

        button = (Button) findViewById(R.id.btnCapture);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(DEBUG_TAG,"btn onClick");
                camera.startPreview();
                Log.d(DEBUG_TAG,"started Prev");
                camera.takePicture(null, null, new Photohandler(getApplicationContext()));
                Log.d(DEBUG_TAG,"pic taken");

                loadpic();
            }
        });

    }

    private int findFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.d(DEBUG_TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    private void loadpic() {
        Log.d(DEBUG_TAG, "in loadpic");
        try {
            String path = Environment.getExternalStorageDirectory()+"/Pictures/CameraAPIDemo/Picture_.jpg";
            Log.d(DEBUG_TAG,"ma: "+path);
            imgResult.setImageDrawable(Drawable.createFromPath(path));

            //open gray
            String path2 = Environment.getExternalStorageDirectory()+"/Pictures/CameraAPIDemo/Picture.jpg";
            Log.d(DEBUG_TAG,"gy: "+path2);
            imgGrayscale.setImageDrawable(Drawable.createFromPath(path2));
        } catch (Exception e) {
            Log.d(DEBUG_TAG, e.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(MainActivity.DEBUG_TAG,"onPause()");
        Camera.Parameters p = camera.getParameters();
        try{
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(p);
            camera.stopPreview();
        }catch (Exception e){
            Log.d(DEBUG_TAG,e.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(MainActivity.DEBUG_TAG,"onResume()");
        Camera.Parameters p = camera.getParameters();
        try{
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(p);
            camera.startPreview();
        }catch (Exception e){
            Log.d(DEBUG_TAG,e.toString());
        }

        /*if (OpenCVLoader.initDebug()){
            Log.i(DEBUG_TAG,"succ");
            loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }else{
            Log.i(DEBUG_TAG,"fail");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,this,loaderCallback);
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(MainActivity.DEBUG_TAG,"onStop()");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(MainActivity.DEBUG_TAG,"onDestroy()");
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}
