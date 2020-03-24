package com.jch.soundtouchdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.audiofx.AudioEffect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener , View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.et_speed)
    EditText etSpeed;
    @BindView(R.id.et_tempo)
    EditText etSwmpo;
    @BindView(R.id.et_pitch)
    EditText etPitch;
    @BindView(R.id.btn_sound)
    Button btnSound;
    @BindView(R.id.btn_play_file)
    Button btnPlayFile;

    private ArrayList<String> unGrantedPermissions;

    private static final String[] MANDATORY_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public interface ViewPresent {

        void init(Context context, float speed, float pitch, float tempo);

        void setSpeed(float speed);

        void setTempo(float tempo);

        void setPitch(float pitch);

        void startSoundTouch();

        void stopSoundTouch();

        void playAssertFile(String fileName);

        void release();
    }

    ViewPresent viewPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        viewPresent = new ViewPresentImp();
//        viewPresent.init(getApplicationContext());
        btnSound.setOnTouchListener(this);
        btnPlayFile.setOnClickListener(this);
        etPitch.addTextChangedListener(new MyTextWatcher(etPitch.getId()));
        etSpeed.addTextChangedListener(new MyTextWatcher(etSpeed.getId()));
        etSwmpo.addTextChangedListener(new MyTextWatcher(etSwmpo.getId()));
        checkPermissions();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                viewPresent.startSoundTouch();
                break;
            }
            case MotionEvent.ACTION_UP: {
                viewPresent.stopSoundTouch();
                break;
            }
        }

        return false;
    }

    class MyTextWatcher implements TextWatcher {


        private int viewId;

        public MyTextWatcher(int viewId) {
            this.viewId = viewId;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d(TAG, "afterTextChanged: " + s.toString());

            //todo checking the ability for the ability of et converting to float from edit
            float value = checkFloat(s.toString());

            switch (viewId) {

                case R.id.et_pitch: {
                    if (value != -100)
                        viewPresent.setPitch(value);
                    break;
                }

                case R.id.et_speed: {
                    if (value != -100)
                        viewPresent.setSpeed(value);
                    break;
                }

                case R.id.et_tempo: {
                    if (value != -100)
                        viewPresent.setTempo(value);
                    break;
                }
                default: {

                }
            }
        }
    }

    private float checkFloat(String str) {
        float result = 0;

        try {
            result = Float.valueOf(str);
        } catch (NumberFormatException e) {
            return result = -100;
        }

        return result;
    }

    private void checkPermissions() {
        unGrantedPermissions = new ArrayList();
        for (String permission : MANDATORY_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                unGrantedPermissions.add(permission);
            }
        }
        if (unGrantedPermissions.size() == 0) {//已经获得了所有权限，开始加入聊天室
            viewPresent.init(getApplicationContext(), Float.valueOf(etSwmpo.getText().toString()),
                    Float.valueOf(etSpeed.getText().toString()),
                    Float.valueOf(etPitch.getText().toString()));
        } else {//部分权限未获得，重新请求获取权限
            String[] array = new String[unGrantedPermissions.size()];
            ActivityCompat.requestPermissions(this, unGrantedPermissions.toArray(array), 0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                shouldShowRequestPermissionRationale(permissions[i]);
                finish();
            } else {
                unGrantedPermissions.remove(permissions[i]);
            }
        }

        if (unGrantedPermissions.size() == 0) {
            viewPresent.init(getApplicationContext(), Float.valueOf(etSwmpo.getText().toString()),
                    Float.valueOf(etSpeed.getText().toString()),
                    Float.valueOf(etPitch.getText().toString()));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play_file:{
                viewPresent.playAssertFile("text_wav.wav");

            }
        }
    }


}
