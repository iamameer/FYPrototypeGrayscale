package fyp.fyprototypegrayscale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Photohandler implements PictureCallback{
    private final Context context;
    public final static String DEBUG_TAG = "MakePhotoActivity";

    public Photohandler(Context context) {
        this.context = context;
    }


    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        File pictureFileDir = getDir();

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

            Log.d(MainActivity.DEBUG_TAG, "Can't create directory to save image.");
            Toast.makeText(context, "Can't create directory to save image.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String photoFile = "Picture_" /*+ date*/ + ".jpg";

        String filename = pictureFileDir.getPath() + File.separator + photoFile;

        File pictureFile = new File(filename);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(bytes);
            fos.close();
            Toast.makeText(context, "New Image saved:" + photoFile,
                    Toast.LENGTH_LONG).show();
            Log.d(DEBUG_TAG,"filename: "+filename);
        } catch (Exception error) {
            Log.d(MainActivity.DEBUG_TAG, "File" + filename + "not saved: "
                    + error.getMessage());
            Toast.makeText(context, "Image could not be saved.",
                    Toast.LENGTH_LONG).show();
        }

        try{
            //read first
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            //convert to gray
            Mat omat = new Mat(bmp.getWidth(),bmp.getHeight(), CvType.CV_8UC1);
            Mat gmat = new Mat(bmp.getWidth(),bmp.getHeight(), CvType.CV_8UC1);
            Utils.bitmapToMat(bmp,omat);
            //omat.put(0,0,bytes);

            Imgproc.cvtColor(omat,gmat,Imgproc.COLOR_RGB2GRAY);

            //save gray
            String path = Environment.getExternalStorageDirectory()+"/Pictures/CameraAPIDemo/";//Picture.jpg";
            File file = new File(path, "Picture.jpg");
            Log.d(DEBUG_TAG,"saving: "+file.getAbsolutePath());

            Imgcodecs.imwrite(file.getAbsolutePath(),gmat);
        }catch (Exception e){
            Log.d(DEBUG_TAG,"err: "+e.toString());
        }
    }

    private File getDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "CameraAPIDemo");
    }
}
