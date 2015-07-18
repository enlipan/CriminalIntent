package com.itlipan.lee.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;


/**
 * Created by Lee on 2015/6/23.
 */
public class CrimeCameraFragment extends Fragment {
    private static final String TAG = "CrimeCameraFragment";
    public static final String EXTRA_PHTOTO_FILENAME="com.itlipan.criminalintent.photo_filename";


    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private Button mTakePictureButton;
    private View mProgressContainer;

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //create filename
            String filename = UUID.randomUUID().toString()+".jpg";
            //save to disk
            FileOutputStream os =null;
            boolean isSuccess = true;
            try {
                os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                os.write(data);
            }catch (Exception e){
                Log.e(TAG,"Error writing to file:"+filename,e);
                isSuccess=false;
            }finally {
                try {
                    if (os!=null){
                        os.close();
                    }
                }catch (Exception e){
                    Log.e(TAG,"Error close file:"+filename,e);
                    isSuccess=false;
                }
            }
            if (isSuccess){
                Log.i(TAG,"JPEG saved at"+filename);
                Intent i =new Intent();
                i.putExtra(EXTRA_PHTOTO_FILENAME,filename);
                getActivity().setResult(Activity.RESULT_OK,i);
            }else {
                getActivity().setResult(Activity.RESULT_CANCELED);
            }
            getActivity().finish();
        }
    };


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
        //null check to avoid application crash
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_camera, container, false);

        mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);
        Button takePictureButton = (Button) v.findViewById(R.id.crime_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();//free memory
            }
        });
        mSurfaceView = (SurfaceView) v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //tell camera to use this as its preview area
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error set preview display", e);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mCamera == null) {
                    return;
                }
                //update camera preview size  company with  surface size changed
                Camera.Parameters parameters = mCamera.getParameters();
//                Camera.Size s = null;//reset in the next section
                Camera.Size s = getBestSupportSize(parameters.getSupportedPreviewSizes(), width, height);
                parameters.setPreviewSize(s.width, s.height);
                s = getBestSupportSize(parameters.getSupportedPictureSizes(), width, height);
                parameters.setPictureSize(s.width,s.height);
                mCamera.setParameters(parameters);
                try {
                    mCamera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "Could not start preview", e);
                    mCamera.release();
                    mCamera = null;
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                }
            }
        });

        mTakePictureButton = (Button) v.findViewById(R.id.crime_camera_takePictureButton);
        mTakePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null) {
                    mCamera.takePicture(mShutterCallback, null, mJpegCallback);
                }
            }
        });
        return v;
    }

    private Camera.Size getBestSupportSize(List<Camera.Size> sizes, int width, int height) {
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
}
