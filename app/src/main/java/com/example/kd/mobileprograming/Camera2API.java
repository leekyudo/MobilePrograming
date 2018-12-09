package com.example.kd.mobileprograming;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.ImageFormat;
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
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;

import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Camera2API extends Thread {


    interface CameraInterface {
        void onCameraDeviceOpened(CameraDevice cameraDevice, Size cameraSize);
    }

    private CameraInterface mCameraInterface;
    private Size mCameraSize;
    private CameraCaptureSession mCaptureSession;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private Context mContext;
    private StreamConfigurationMap map;

    public Camera2API(CameraInterface cameraInterface) {
        mCameraInterface = cameraInterface;

    }

    //CameraManger Camera2API의 시작 클래스
    //getSysytemService(CAMERA_SERVICE)로 취득
    public CameraManager MyCameraManager(Activity activity) {
        CameraManager cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        return cameraManager;
    }

    public String MyCameraCharacteristics(CameraManager cameraManager) {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {

                //getCameraList() 사용가능한 카메라 리스트(String[]) 보통은 크기가 2 (전면/후면)
                //getCameraCharacteristics(cameraId) 각 카메라 정보(prameter)를 얻음

                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);

                // cameraId에 따른 카메라 하드웨어정보 => cameraCharacteristics
                // LENS_FACING은 카메라의 렌즈 방향 Key (FRONT = 0 전면, BACK = 1 후면, EXTERNAL = 2 기타)
                // 후면 카메라(LENS_FACING_BACK)일경우 cameraId를 반환 (후면 카메라만 제어)

                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {

                    //StreamConfigurationMap CaptureSession을 사용할때  surface를 설정하기 위한 출력 포맷등을 가지는 class
                    //카메라의 각종 지원 정보가 담겨 있음 특히 카메라에서 지원 하는 크기목록(배열)인 getOutPutSize() 이 값을 이용하여 촬영시 크기 지정가능
                    //현재는 가장 큰 사이즈만 출력가능하게 설정

                    map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    Size[] sizes = map.getOutputSizes(SurfaceTexture.class);

                    //카메라 디바이스 레벨 판별(참고사항)
                    CameraCharacteristics.Key<Integer> hardwareLevel = cameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL;
                    Log.e("*******************************", String.valueOf(hardwareLevel));

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

    //실질적인 해당 카메라를 나타냄
    //CameraManager에 의해 Callback 취득
    //CameraDevice.StateCallback onOpened()로 취득 null => MainThread 이용, 개선하기 위해서는 handlerThread, handler 이용해야함
    // cameraId = 후면 카메라

    public void MyCameraDevice(CameraManager cameraManager, String cameraId) {
        try {
            cameraManager.openCamera(cameraId, mCameraDeviceStateCallback,null);
        } catch (CameraAccessException | SecurityException e) {
            e.printStackTrace();
        }
    }

    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) { // 카메라와 접속 완료
            mCameraDevice = cameraDevice;

            // onOpened에서 취득한 CameraDevice로 CaptureSession, CaptureRequest가 이루어짐
            // CameraActivity 에서 onCameraDeviceOpened interface 이용하여 같이 처리

            mCameraInterface.onCameraDeviceOpened(cameraDevice, mCameraSize);

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) { // 카메라와 접속이 끊어짐
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) { // 에러
            cameraDevice.close();
        }
    };

    //CameraDevice에 의해 이미지캡쳐를 위한 연결
    //해당 세션이 연결된 Surface에 전달 -> preview
    //세션이 생성된 후에는 CameraDevice에서 새로운 세션이 생성되거나 종료되기 전에는 유효

    public void MyCaptureSession(CameraDevice cameraDevice, Surface surface){


        try{
            cameraDevice.createCaptureSession(Collections.singletonList(surface),mCaptureSessionStataCallback,null);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.StateCallback mCaptureSessionStataCallback = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {

            try {
                mCaptureSession = cameraCaptureSession;

                // 프리뷰 화면은 연속적이기 때문에 CONTROL_AF_MODE_CONTINUOUS_PICTURE로 지속적인 AutoFocus

                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                // 해당 세션에 설정된 세팅으로 이미지를 Repeating(지속/반복) 요청
                // null -> MainThread , backgroundThread 설정 하는것이 더 좋은 방법

                cameraCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), mCaptureSessionCallback, null);
            }catch (CameraAccessException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

        }
    };
    //  캡쳐된 정보 및 metadata 전달
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

    //
    public void MyCaptureRequest(CameraDevice cameraDevice, Surface surface){
        try{
            mPreviewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            mPreviewRequestBuilder.addTarget(surface);

        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }


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




