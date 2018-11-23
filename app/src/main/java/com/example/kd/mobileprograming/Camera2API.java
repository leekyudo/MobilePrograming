package com.example.kd.mobileprograming;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Camera;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.annotation.NonNull;

import android.util.Log;
import android.util.Size;
import android.view.Surface;

import java.util.Collections;

public class Camera2API {


    interface CameraInterface {
        void onCamerDeviceOpened(CameraDevice cameraDevice, Size cameraSize);
    }

    private CameraInterface mCameraInterface;
    private Size mCameraSize;
    private CameraCaptureSession mCaptureSession;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private Context mContext;


    public Camera2API(CameraInterface cameraInterface) {
        mCameraInterface = cameraInterface;
    }

    public CameraManager MyCameraManager(Activity activity) {
        CameraManager cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        return cameraManager;
    }

    public String MyCameraCharacteristics(CameraManager cameraManager) {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                //getCameraList() 사용가능한 카메라 리스트

                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                // cameraId에 따른 카메라 하드웨어정보 => cameraCharacteristics
                // LENS_FACING_BACK => 후면 카메라
                // 후면 카메라일경우 cameraId를 리턴
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {

                    //StreamConfigurationMap CaptureSession을 사용할때  surface를 설정하기 위한 출력 포맷등을 가지는 class
                    StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    Size[] sizes = map.getOutputSizes(SurfaceTexture.class);

                    //카메라 디바이스 레벨 판별(참고사항)
                    CameraCharacteristics.Key<Integer> hardwareLevel = cameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL;
                    Log.e("*******************************", String.valueOf(cameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL));

                    mCameraSize = sizes[0];
                    // 사용 가능한 사이즈중 가장 큰 사이즈 출력
                    for (Size size : sizes) {
                        if (size.getWidth() > mCameraSize.getWidth()) {
                            mCameraSize = size;
                        }
                    }
                    return cameraId;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;

    }

    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            mCameraInterface.onCamerDeviceOpened(cameraDevice, mCameraSize);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
        }
    };


    public void MyCameraDevice(CameraManager cameraManager, String cameraId) {
        try {
            cameraManager.openCamera(cameraId, mCameraDeviceStateCallback, null);
        } catch (CameraAccessException | SecurityException e) {
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.StateCallback mCaptureSessionStataCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
            try {
                mCaptureSession = cameraCaptureSession;
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                cameraCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), mCaptureSessionCallback, null);
            }catch (CameraAccessException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

        }
    };


    public void MyCaptureSession(CameraDevice cameraDevice, Surface surface){
        try{
            cameraDevice.createCaptureSession(Collections.singletonList(surface),mCaptureSessionStataCallback,null);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }
    public void MyCaptureRequest(CameraDevice cameraDevice, Surface surface){
        try{
            mPreviewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.CaptureCallback mCaptureSessionCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }
    };

    public void closeCamera(){

        if(null != mCaptureSession){
            mCaptureSession.close();
            mCaptureSession=null;
        }
        if(null!= mCameraDevice){
            mCameraDevice.close();
            mCameraDevice=null;
        }
    }
}




