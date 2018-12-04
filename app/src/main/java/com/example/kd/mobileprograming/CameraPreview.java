package com.example.kd.mobileprograming;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ImageView;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;


public class CameraPreview extends Thread {
    String filepath = Environment.getExternalStorageDirectory()+"/Capture/";
    String path;
    private final static String TAG = "CameraPreview : ";
    ImageView galleryPreview;


    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private Size mPreviewSize;
    private Context mContext;
    private CameraDevice mCameradevice;
    private CaptureRequest.Builder mCamptureRequestBuilder;
    private CameraCaptureSession mCameraCaptureSession;
    private TextureView mTextureView;
    private StreamConfigurationMap map;


    public CameraPreview(Context context,TextureView textureView){
        mContext = context;
        mTextureView = textureView;

    }

    // Camera2 API 사용 하기 위한 첫번째 class
    // getSystemServie()로 카메라 사용 취득

    public CameraManager MyCameraManager(Context context) {
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        return cameraManager;
    }

    // cameraId 와 Size 설정
    private String MyCameraCharacteristics(CameraManager cameraManager){

        try {

            //getCameraList() 사용가능한 카메라 리스트(String[]) 보통은 크기가 2 (전면/후면)
            //getCameraCharacteristics(cameraId) 각 카메라 정보(prameter)를 얻음

            for (String cameraId : cameraManager.getCameraIdList()) {

                // cameraId에 따른 카메라 하드웨어정보 => cameraCharacteristics
                // LENS_FACING은 카메라의 렌즈 방향 Key (FRONT = 0 전면, BACK = 1 후면, EXTERNAL = 2 기타)
                // 후면 카메라(LENS_FACING_BACK)일경우 cameraId를 반환 (후면 카메라만 제어)

                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {

                    //StreamConfigurationMap CaptureSession을 사용할때  surface를 설정하기 위한 출력 포맷등을 가지는 class
                    //카메라의 각종 지원 정보가 담겨 있음 특히 카메라에서 지원 하는 크기목록(배열)인 getOutPutSize() 이 값을 이용하여 촬영시 크기 지정가능
                    //현재는 가장 큰 사이즈만 출력가능하게 설정

                    map = cameraCharacteristics.get(cameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    Size[] sizes = map.getOutputSizes(SurfaceTexture.class);

                    mPreviewSize = sizes[0];

                    for(Size size : sizes){
                        if(size.getWidth()>mPreviewSize.getWidth()){
                            mPreviewSize=size;
                        }
                    }
                    return cameraId;
                }
            }
            }catch (CameraAccessException e){
                e.printStackTrace();
        }return null;
    }

    //실질적인 해당 카메라를 나타냄
    //CameraManager에 의해 Callback 취득 => 카메라 사용 가능일 경우
    //CameraDevice.StateCallback onOpened()로 취득 null => MainThread 이용, 개선하기 위해서는 handlerThread, handler 이용해야함
    //cameraId = 후면 카메라

    public void MyCameraDevice(){
        CameraManager cameraManager = MyCameraManager(mContext);
        try{
            String camreaId = MyCameraCharacteristics(cameraManager);
            cameraManager.openCamera(camreaId,mCameraStateCallback ,null);

        }catch (CameraAccessException|SecurityException e){
            e.printStackTrace();
        }
    }


    private CameraDevice.StateCallback mCameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // onOpened에서 취득한 CameraDevice로 CaptureSession, CaptureRequest가 이루어짐
            // onCameraDeviceOpened 이용하여 같이 처리
            mCameradevice = cameraDevice;
            onCameraDeviceOpened();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameradevice=null;
            mCameradevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            mCameradevice=null;
            mCameradevice.close();
        }
    };

    TextureView.SurfaceTextureListener textureListener= new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            MyCameraDevice();
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
    };

    // CameraRequest, CaptureSession 실행모음

    protected void onCameraDeviceOpened() {

        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(),mPreviewSize.getHeight());
        Surface surface = new Surface(surfaceTexture);

        MyCameraRequest(mCameradevice,surface);
        MyCaptureSession(mCameradevice,surface);

    }

    public void MyCameraRequest(CameraDevice cameraDevice,Surface surface){
        try {
            // 카메라 미리보기에 적합한 창을 생성
            // addTarget으로 창 연결
            mCamptureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCamptureRequestBuilder.addTarget(surface);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    //CameraDevice에 의해 이미지캡쳐를 위한 연결(session)
    //해당 세션이 연결된 Surface에 전달 -> preview
    //세션이 생성된 후에는 CameraDevice에서 새로운 세션이 생성되거나 종료되기 전에는 유효

    public void MyCaptureSession (CameraDevice cameraDevice,Surface surface){

        HandlerThread thread = new HandlerThread("CameraPreview");
        thread.start();
        Handler backgroundHandler = new Handler(thread.getLooper());

        try {
            cameraDevice.createCaptureSession(Collections.singletonList(surface),mCaptureStateCallback,backgroundHandler);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }


    private CameraCaptureSession.StateCallback mCaptureStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
            try {

                mCameraCaptureSession = cameraCaptureSession;
                // Auto Focus를 지속적으로 유지
                mCamptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                // mCamptureRequestBuilder -> 해당세션에 설정된 세팅 / 이미지를 반복적(Repeating)요청(Request) -> 지속적으로 갱신됨
                mCameraCaptureSession.setRepeatingRequest(mCamptureRequestBuilder.build(), mCaptureCallback, null);
            }catch (CameraAccessException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

        }
    };

    // ㅋ
    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }
    };


   public void setSurfaceTextureListner(){
        mTextureView.setSurfaceTextureListener(textureListener);
    }

    public void onResume(){
        setSurfaceTextureListner();
    }

    // Semaphore 공유자원에 여러 프로세스가 접근하여 발생하는 문제 방지
    // 하나의 데이터에 하나의 프로세스만 접근하게 제한

    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    public void onPause() {

        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCameradevice) {
                mCameradevice.close();
                mCameradevice = null;

            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    // 카메라 촬영 함수
    protected void takePicture(){
        try{
            Size[] jpegSizes =null;
            if(map!=null){
                jpegSizes = map.getOutputSizes(ImageFormat.JPEG);
        }
            int width =640;
            int height = 480;
            if(jpegSizes!=null&&0<jpegSizes.length){
                // jpeg 로 촬영할 수 있는 최대 사이즈
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

            final ImageReader imageReader = ImageReader.newInstance(width,height,ImageFormat.JPEG,1);
            List<Surface> outputSurface = new ArrayList<Surface>(2);
            outputSurface.add(imageReader.getSurface());
            outputSurface.add(new Surface(mTextureView.getSurfaceTexture()));

            // 캡쳐 기능 추가
            final CaptureRequest.Builder captureBuilder = mCameradevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(imageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            int rotation = ((Activity)mContext).getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));


            // 사진을 찍을때 날짜 시각을 구하여 파일명에 적용용
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmss");
            String getTime =sdf.format(date);
            path = filepath+getTime+".jpg";




            // 파일 경로 설정
            final File file = new File(path);


            //
            ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    Image image= null;
                    try{
                        image = imageReader.acquireLatestImage();
                        ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[byteBuffer.capacity()];
                        byteBuffer.get(bytes);
                        save(bytes);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }finally {
                        if(image!=null){
                            image.close();
                            imageReader.close();
                        }
                    }
                }

                // 이미지 배열을 Write 하여 저장
                private void save (byte[] bytes) throws IOException {
                    OutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(file);
                        outputStream.write(bytes);

                    }finally {
                        if(null!=outputStream){
                            outputStream.close();
                        }
                    }
                }
            };


            HandlerThread handlerThread = new HandlerThread("CameraPicture");
            handlerThread.start();
            final Handler backgroundhandler = new Handler(handlerThread.getLooper());
            imageReader.setOnImageAvailableListener(onImageAvailableListener,backgroundhandler);


            final CameraCaptureSession.CaptureCallback captureCallback= new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    onCameraDeviceOpened();
                }
            };

            mCameradevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try {
                        cameraCaptureSession.capture(captureBuilder.build(), captureCallback, backgroundhandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            },backgroundhandler);


        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }



     public void string(String s) {


        if (s == null) {
             Log.e("-------", "이미지 없음");
         } else {
             Log.e("--------", "이미지 존재");
         }

     }

 }








