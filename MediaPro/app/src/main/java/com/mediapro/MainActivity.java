package com.mediapro;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.mediamodule.media.Player;
import com.mediamodule.util.MediaLog;

public class MainActivity extends AppCompatActivity {

    private TextView mTv;

    private Player mPlayer;
    private int index=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTv=findViewById(R.id.tv);

        mPlayer=new Player();
//        mPlayer.setDataByAssets("order_tip.mp3");
//        mPlayer.setDataByRaw(R.raw.order_tip);
//        mPlayer.setDataByUrl("http://mp3.djwma.com/mp3/爆袭全站欢快节奏感觉那是杠杠滴.mp3");

        mTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaLog.i("=======我点击了=======");

                mPlayer.setDataByAssets("order_tip.mp3");

//                if(index==0){
//                    mPlayer.setDataByAssets("order_tip.mp3");
//                }else if(index==1){
//                    mPlayer.setDataByRaw(R.raw.order_tip);
//                }else if(index==2){
//                    mPlayer.setDataByUrl("http://np01.sycdn.kuwo.cn/7591a48f2fddfd3e8ab64601e133d2fe/5e3adec5/resource/n1/28/66/1638975979.mp3");
//                }
                mPlayer.start(null);
                index++;
                if(index>2){
                    index=0;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        mPlayer.release();
    }
}
