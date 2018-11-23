package com.example.kd.mobileprograming;


import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;


public class CameraActivity extends AppCompatActivity implements Camera2API.CameraInterface,TextureView.SurfaceTextureListener{

    private TextureView mTextureView;
    private Camera2API mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);

        mTextureView = (TextureView)findViewById(R.id.textureview);
        mCamera = new Camera2API(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onCamerDeviceOpened(CameraDevice cameraDevice, Size cameraSize) {

        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        texture.setDefaultBufferSize(cameraSize.getWidth(), cameraSize.getHeight());
        Surface surface = new Surface(texture);

        mCamera.MyCaptureSession(cameraDevice, surface);
        mCamera.MyCaptureRequest(cameraDevice, surface);
    }
    public void openCamera(){
        CameraManager cameraManager = mCamera.MyCameraManager(this);
        String cameraId = mCamera.MyCameraCharacteristics(cameraManager);
        mCamera.MyCameraDevice(cameraManager,cameraId);
    }

    protected void onResume(){
        super.onResume();
        if(mTextureView.isAvailable()){
            openCamera();
        }
        else{
            mTextureView.setSurfaceTextureListener(this);
        }
    }
    private void closeCamera() {
        mCamera.closeCamera();
    }

    @Override
    protected void onPause() {
        closeCamera();
        super.onPause();
    }

    public void close(View view){
        closeCamera();
    }
    public void capture(View view){

    }
}