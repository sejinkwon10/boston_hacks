package com.example.android.camera2basic;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.rest.RESTException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

/**
 * Created by Jerry on 2015-11-01.
 */
public class PictureAnalyzer extends AsyncTask<Void,Void,Void>{

    private Activity myActivity;
    public PictureAnalyzer(Activity a)
    {
        Log.i("************","IN PICTURE ANALYZER************");
        myActivity = a;
    }

    //Bitmap origBit;
    TextView textView;

    @Override
    protected void onPreExecute() {
        textView = (TextView)myActivity.findViewById(R.id.textView);
       // origBit = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
    }


    private FaceServiceClient faceServiceClient =
            new FaceServiceClient("b6ba8039c9944beb8c26614fc1404c30");

    boolean searching;

    @Override
    protected Void doInBackground(Void... params) {

        while (true) {
            Log.i("**************", "TAKE SCREENSHOT********");


            ByteArrayOutputStream os = new ByteArrayOutputStream();
            File file = new File("/sdcard/boston/screen.jpg");
            try
            {
                TextureView v = (TextureView)myActivity.findViewById(R.id.texture);
                if(v!=null) {
                    Bitmap bitmap = getBip(v);

                    Log.i("********************", "TAKE****************");
                    if (bitmap != null) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(-90);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getHeight(), bitmap.getWidth(), true);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                        FileOutputStream ostream = new FileOutputStream(file);
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100,ostream);
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
            Log.i("************FACEDETECT","Detecting...");

            Face[] result = new Face[0];

            try {
                result = faceServiceClient.detect(is, false, false, false, false);
            } catch (RESTException e) {
                e.printStackTrace();
                Log.i("************FACEDETECT", "Detection failed");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("************FACEDETECT", "Detection failed");
            }
            if (result == null)
            {
                Log.i("************FACEDETECT","Detection Finished. Nothing detected");
                return null;
            }
            Log.i("************FACEDETECT",String.format("Detection Finished. %d face(s) detected",result.length));

            if(result.length >=1) {
                Log.i("***********FACE RESULTS", String.valueOf(result[0].faceRectangle.top));
                double maxSize = 0;
                Face maxFace = new Face();
                for(Face face : result)
                {
                    double cur = face.faceRectangle.width * face.faceRectangle.top;
                    if (cur > maxSize);
                    {
                        maxSize = cur;
                        maxFace = face;
                    }
                }
                final FaceRectangle f = maxFace.faceRectangle;
                //drawFaceRectanglesOnBitmap(result);
                //origBit.recycle();
                myActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RelativeLayout rl = (RelativeLayout) myActivity.findViewById(R.id.rel);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(f.width + 600, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.leftMargin = f.left - 300;
                        params.topMargin = f.top + f.height;
                        textView.setLayoutParams(params);
                    }
                });
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private Bitmap getBip(TextureView view)
    {
        return view.getBitmap();
    }
    private void drawFaceRectanglesOnBitmap(Face[] faces) {

        final ImageView imageView = (ImageView)myActivity.findViewById(R.id.imageView);
        final Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(),imageView.getHeight(),Bitmap.Config.ARGB_8888);
        Paint transparent = new Paint();
        transparent.setAlpha(0);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        int stokeWidth = 2;
        paint.setStrokeWidth(stokeWidth);
        if (faces != null) {
            for (Face face : faces) {
                FaceRectangle faceRectangle = face.faceRectangle;
                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);
//                canvas.drawRect(
//                        faceRectangle.left - faceRectangle.width/2,
//                        faceRectangle.top - faceRectangle.height,
//                        faceRectangle.left + faceRectangle.width + faceRectangle.width/2,
//                        faceRectangle.top - faceRectangle.height - faceRectangle.height/2,
//                        paint);
                Log.i("Tag", "DREW RECTANGLES");
            }
        }
        myActivity.runOnUiThread(new Runnable() {
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

}
