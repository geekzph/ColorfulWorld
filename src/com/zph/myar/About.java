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
        mytext.setText("����\n"+
        		"ColorfulWorld��һ�������̽����Χ������ֻ�Ӧ�á�ͨ��ʹ����ǿ��ʵ������������Ϣ����ʵ������е��ӣ��Ӷ��ﵽ��ǿ��ʵ��Ч�����û�ֻ��Ҫ���ֻ�������ͷ��׼��Χ�Ľ�������߿��ſռ䣬�������ֻ���Ļ�·�������֮��ص���ʵ���ݡ�\n"+
        		"ʹ�÷���\n"+
        		"ֱ�ӵ��������ť������Ի����Χ������������Ϣ������������������ã�����������������������Ȥ����Ϣ�����ͣ����ߵ���·���ͼ�꣬��ɻ����Ӧ���͵���Ϣ����ʵ��ģʽ�£������Ļ�·����ڹȸ��ͼ����ʾ����λ�á�ͨ��menu���������ò˵����ɶ�������Χ����λ�������á�");
    }
}
