package com.bignerdranch.android.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by yls on 15-5-22.
 */
public class CrimeCameraFragment extends Fragment {
    private static final String TAG = "CrimeCameraFragment";
    public static final String EXTRA_PHOTO_FILENAME = "com.bignerdranch.android.criminalintent.photo_filename";
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_camera, parent, false);
        mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        Button takePictureButton = (Button)v
                .findViewById(R.id.crime_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mCamera.enableShutterSound(false);
                mCamera.takePicture(shutterCallback,null,pictureCallback);
            }
        });
        mSurfaceView = (SurfaceView)v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback(){
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    if(mCamera != null){
                        mCamera.enableShutterSound(false);
                        mCamera.setPreviewDisplay(surfaceHolder);
                    }
                }catch (IOException e)
                {
                    Log.e(TAG, "Error setting up preview display", e);
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
                if (mCamera == null)
                    return;
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(),i2,i3);
                parameters.setPreviewSize(s.width,s.height);
                s = getBestSupportedSize(parameters.getSupportedPictureSizes(), i2, i3);
                parameters.setPictureSize(s.width, s.height);
                mCamera.setParameters(parameters);
                try{
                    mCamera.startPreview();
                }catch (Exception e){
                    Log.e(TAG, "Could not start preview", e);
                    mCamera.release();
                    mCamera = null;
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                }
            }
        });

        return v;
    }

    @TargetApi(8)
    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mCamera = Camera.open(0);
        } else {
            mCamera = Camera.open();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }

    // 按下快门回调，显示进度条
    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };

    // JEPG图片准备好回调，保存图片
    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
           // 定义文件名
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmssSS");
            String fileName = dateFormat.format(date)+".jpg";
            FileOutputStream os = null;
            boolean success = true;
            try{
                String state = Environment.getExternalStorageState();
                File path = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                File file = new File(path, fileName);

                //String path = Environment.().toString()+"/CrimeIntent/";
                path.mkdirs();

                if (path.exists()){
                    os = new FileOutputStream(file);
                }else {
                    Log.e(TAG,"Create path failed");
                    return;
                }
                // 保存到内部储存
                //os = getActivity().openFileOutput(path, Context.MODE_PRIVATE);
                os.write(bytes);

                // 刷新目录，以便及时在相册中看到新拍的图片
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath().toString())));
                //MediaScannerConnection.scanFile(getActivity(), new String[]{file.getAbsolutePath().toString()}, null, null);
            }catch (IOException e){
                success = false;
                Log.e(TAG, "Error writing to file " + fileName, e);
            }finally {
                try{
                    if (os != null){
                        os.close();
                    }
                }catch (IOException e){
                    Log.e(TAG, "Error closing file " + fileName, e);
                    success = false;
                }
            }
            if (success) {
                Intent i = new Intent();
                i.putExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME, fileName);
                getActivity().setResult(Activity.RESULT_OK, i);
            } else {
                getActivity().setResult(Activity.RESULT_CANCELED);
            }
            getActivity().finish();
        }
    };
}
