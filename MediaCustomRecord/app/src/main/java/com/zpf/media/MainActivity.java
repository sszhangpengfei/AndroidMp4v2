package com.zpf.media;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.zpf.media.camera.CameraWrapper;
import com.zpf.media.camera.CaptureConfiguration;
import com.zpf.media.camera.NativeCamera;
import com.zpf.media.camera.PredefinedCaptureConfigurations;
import com.zpf.media.tools.Constants;
import com.zpf.media.tools.VideoFile;
import com.zpf.media.tools.ViewUtils;
import com.zpf.media.video.VideoRecorder;
import com.zpf.media.video.VideoRecorderInterface;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity implements VideoRecorderInterface {

    private SurfaceView mSurfaceView;
    private View mProgressLine;
    private TextView mStartTV;

    private VideoRecorder mVideoRecorder;
    private VideoFile mVideoFile;
    private final int mRecordMaxTime = 8000;
    private boolean mIsRecording;
    private AtomicBoolean mAnimationEnd;
    private ObjectAnimator animation;
    private long mLastDownTime;
    private AtomicBoolean mHandleBoolean;

    private final static int CAMERA_OK = 10001;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.RECORD_AUDIO",
            "android.permission.CAMERA",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSurfaceView = findViewById(R.id.video_surface_view);
        mProgressLine = findViewById(R.id.progress_line);
        mStartTV = findViewById(R.id.start_record_tv);
        if (Build.VERSION.SDK_INT>22) {
            if (!checkPermissionAllGranted(PERMISSIONS_STORAGE)) {
                //先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(MainActivity.this,
                        PERMISSIONS_STORAGE, CAMERA_OK);
            }else{
                init();
            }
        }else{
            init();
        }
    }

    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    private void init(){
        initSurFaceParams();
        initRecord();
        mHandleBoolean = new AtomicBoolean();
        mAnimationEnd = new AtomicBoolean();
        initStartListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode) {
            case CAMERA_OK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以
                    init();
                } else {
                    showWaringDialog();
                }
                break;
            default:
                break;
        }
    }

    private void showWaringDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("警告！")
                .setMessage("请前往设置->应用->PermissionDemo->权限中打开相关权限，否则功能无法正常运行！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 一般情况下如果用户不授权的话，功能是无法运行的，做退出处理
                        finish();
                    }
                }).show();
    }





    private void initStartListener() {
        mStartTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getDownTime() - mLastDownTime >= 1000) {
                        mLastDownTime = event.getDownTime();
                        downAction();
                        return true;
                    }
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mHandleBoolean.set(false);
                    if (mIsRecording && !mAnimationEnd.get()) {
                        animation.cancel();
                        mProgressLine.setVisibility(View.GONE);
                        stopRecord();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private synchronized void downAction() {
        mHandleBoolean.set(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mIsRecording && mHandleBoolean.get()) {
                    mProgressLine.setVisibility(View.VISIBLE);
                    startRecord();
                    progressShorten();
                }
            }
        }, 800);
    }

    private synchronized void progressShorten() {
        animation = ObjectAnimator.ofFloat(mProgressLine, "scaleX", 1f, 0f);
        animation.setDuration(mRecordMaxTime);
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimationEnd.set(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationEnd.set(true);
//                ViewUtils.showToast(mActivity,"录制结束");
                stopRecord();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mIsRecording = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animation.start();
    }

    /**
     * 停止录制
     */
    private synchronized void stopRecord() {
        mVideoRecorder.stopRecording(null);
    }

    /**
     * 开始录制
     */
    private synchronized void startRecord() {
        mIsRecording = true;
        mVideoRecorder.toggleRecording();
    }

    private void initSurFaceParams() {
        //宽高比4:3
        ViewGroup.LayoutParams params;
        params = mSurfaceView.getLayoutParams();
        params.width = ViewUtils.getScreenWidth(this);
        params.height = (int) (params.width * 1.20);
        mSurfaceView.setLayoutParams(params);
    }

    private void initRecord() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        CaptureConfiguration config = initConfig();
        mVideoRecorder = new VideoRecorder(this, config, initFileName(), new CameraWrapper(new NativeCamera(), display.getRotation(), config),
                mSurfaceView.getHolder());
    }

    private CaptureConfiguration initConfig() {
        final PredefinedCaptureConfigurations.CaptureResolution resolution = PredefinedCaptureConfigurations.CaptureResolution.RES_480P;
        final PredefinedCaptureConfigurations.CaptureQuality quality = PredefinedCaptureConfigurations.CaptureQuality.MEDIUM;
        return new CaptureConfiguration(resolution, quality);
    }

    private VideoFile initFileName() {
        return mVideoFile = new VideoFile();
    }

    @Override
    protected void onPause() {
        if (mVideoRecorder != null) {
            mVideoRecorder.stopRecording(null);
        }
        releaseAllResources();
        super.onPause();
    }

    /**
     * 释放资源
     */
    private void releaseAllResources() {
        if (mVideoRecorder != null) {
            mVideoRecorder.releaseAllResources();
        }
    }

    @Override
    public void onRecordingSuccess() {
        Intent intent = new Intent();

        if(null != mVideoFile){
            intent.putExtra(Constants.EXTRA_VIDEO_PATH, mVideoFile.getFullPath());
        } else{
            intent.putExtra(Constants.EXTRA_IMAGE_OR_VIDEO_TYPE, Constants.EXTRA_VIDEO_TYPE);
        }
        mIsRecording = false;
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRecordingFailed(String message) {
        releaseAllResources();
    }

    @Override
    public void onRecordingStopped(String message) {
        releaseAllResources();
    }
}
