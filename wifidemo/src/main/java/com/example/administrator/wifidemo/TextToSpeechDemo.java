package com.example.administrator.wifidemo;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2016/12/23.
 */

public class TextToSpeechDemo extends AppCompatActivity {

    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                //判断当前手机是否支持语音
            }
        });
        Button btn = new Button(this);
        btn.setText("播放");
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        final EditText et = new EditText(this);
        et.setHint("播放内容");
        ll.addView(et);
        ll.addView(btn);
        setContentView(ll);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = et.getText().toString();
                textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

}
