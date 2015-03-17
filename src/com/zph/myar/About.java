package com.zph.myar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class About extends Activity
{
	TextView mytext;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        mytext=(TextView) findViewById(R.id.textView1);
        mytext.setText("概览\n"+
        		"ColorfulWorld是一款帮助你探索周围世界的手机应用。通过使用增强现实技术将虚拟信息和现实世界进行叠加，从而达到增强现实的效果。用户只需要将手机的摄像头对准周围的建筑物或者开放空间，就能在手机屏幕下方看到与之相关的现实数据。\n"+
        		"使用方法\n"+
        		"直接点击搜索按钮，则可以获得周围的所有类型信息（在市区繁华区域可用）。在搜索框中输入您感兴趣的信息的类型，或者点击下方的图标，则可获得相应类型的信息。在实景模式下，点击屏幕下方可在谷歌地图上显示所在位置。通过menu键进入设置菜单，可对搜索范围，单位进行设置。");
    }
}
