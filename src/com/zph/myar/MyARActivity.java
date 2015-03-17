package com.zph.myar;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class MyARActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //窗口去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //窗口设置为全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
        Handler x = new Handler();  
        x.postDelayed(new splashhandler(), 2000);  
          
    }  
    class splashhandler implements Runnable{  
  
        public void run() {  
            startActivity(new Intent(getApplication(),MainActivity.class));  
            MyARActivity.this.finish();  
        }  
    }
}