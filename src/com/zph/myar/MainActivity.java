package com.zph.myar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;




public class MainActivity extends Activity implements OnItemClickListener{
    /** Called when the activity is first created. */
	private String texts[] = null;
	private Integer images[] = null;
	private EditText mytext;
	private TextView infotext;
	private CheckBox mycheckbox;
	public static final String PREFS_NAME = "MyARSettings";
	// set preference
	SharedPreferences set1;
	SharedPreferences.Editor editor;
	View layout;
	
	//显示对话框
	   private void iDialog()
	   {
		   
		   LayoutInflater inflater = LayoutInflater.from(this);
		   layout = inflater.inflate(R.layout.idialog,null);
		   mycheckbox=(CheckBox) layout.findViewById(R.id.checkBox1); 
		   infotext=(TextView) layout.findViewById(R.id.textView1); 
		   infotext.setText("概览\n"+
	        		"ColorfulWorld是一款帮助你探索周围世界的手机应用。通过使用增强现实技术将虚拟信息和现实世界进行叠加，从而达到增强现实的效果。用户只需要将手机的摄像头对准周围的建筑物或者开放空间，就能在手机屏幕下方看到与之相关的现实数据。\n"+
	        		"使用方法\n"+
	        		"直接点击搜索按钮，则可以获得周围的所有类型信息（在市区繁华区域可用）。在搜索框中输入您感兴趣的信息的类型，或者点击下方的图标，则可获得相应类型的信息。在实景模式下，点击屏幕下方可在谷歌地图上显示所在位置。通过menu键进入设置菜单，可对搜索范围，单位进行设置。");
	    	Dialog dialog=new AlertDialog.Builder(this).setTitle("概览").setView(layout)
	  	     .setPositiveButton("继续",new DialogInterface.OnClickListener() {
			        @Override
			        public void onClick(DialogInterface dialog, int which) {
			        	if(mycheckbox.isChecked()==true)
			        	{
			        		editor.putInt("ifshow",1);  
			        		editor.commit();
			        	}
			        }
		        })
	  	     .create();
	    	  dialog.show();
	   }
	
	//menu菜单
	 @Override
		public boolean onCreateOptionsMenu(Menu menu) {
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.options_menu, menu);
		    return true;
		}
	 
	 @Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    // Handle item selection
		    switch (item.getItemId()) {
		    case R.id.new_game:
		    {
		    	Intent intent=new Intent();
		    	intent.setClass(MainActivity.this,Settings.class);
		    	startActivity(intent);
		    	return true;
		    }
		    case R.id.help:
		    	
		    	Intent intent=new Intent();
		    	intent.setClass(MainActivity.this,About.class);
		    	startActivity(intent);
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
		    }
		}
	 
	 
	 String[] menu={"相关评价","带我去那儿"};
	 ListView listView;
   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.mymain);
    // 初始化图片数组和文本数组
       images = new Integer[]{ R.drawable.school,
         R.drawable.atm, R.drawable.bar,
         R.drawable.coffee, R.drawable.gas,
         R.drawable.restaurant, R.drawable.supermarket, R.drawable.bank };
       
       texts = new String[]{ "学校", "ATM机", "酒吧", "咖啡", "加油站", "餐馆",
       "超市" ,"银行"};

       // 填充GridView
       GridView gridView = (GridView) findViewById(R.id.homeGrid);
       
       SimpleAdapter simpleAdapter = new SimpleAdapter(this, fillMap(),
         R.layout.griditme, new String[] { "imageView", "imageTitle" },
         new int[] { R.id.imageView, R.id.imageTitle });
       gridView.setAdapter(simpleAdapter);
       // 监听onItemClick事件
       gridView.setOnItemClickListener(this);
       
       set1 = getSharedPreferences(PREFS_NAME, 0);
	    editor = set1.edit();
       //imagebutton
       ImageButton imagebutoon=(ImageButton) findViewById(R.id.imageButton1);
       imagebutoon.setOnClickListener(new MyButtonListener());
       
       mytext=(EditText) findViewById(R.id.editText1);
       mytext.clearFocus();
       //editor.putInt("ifshow",0);  
       //editor.commit();
       if(set1.getInt("ifshow",0)==0)
       {
    	   iDialog();
       }
       
   }
   
   public class ListViewAdapter extends BaseAdapter {
		View[] itemViews;

		public ListViewAdapter(String[] itemTitles) {
			itemViews = new View[itemTitles.length];

			for (int i = 0; i < itemTitles.length; i++) {
				itemViews[i] = makeItemView(itemTitles[i]);
			}
		}

		public int getCount() {
			return itemViews.length;
		}

		public View getItem(int position) {
			return itemViews[position];
		}

		public long getItemId(int position) {
			return position;
		}

		private View makeItemView(String strTitle) {
			LayoutInflater inflater = (LayoutInflater) MainActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// 使用View的对象itemView与R.layout.item关联
			View itemView = inflater.inflate(R.layout.itemview2, null);

			// 通过findViewById()方法实例R.layout.item内各组件
			TextView title = (TextView) itemView.findViewById(R.id.textView1);
			title.setText(strTitle);
			return itemView;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				return itemViews[position];
			return convertView;
		}
	}
   
   
   android.content.DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
	     @Override
	       public void onClick(DialogInterface dialog, int which)
	     {
	       //TODO 点击项处理 
	    	 switch(which)
	    	 {
	    	 case 0:
	    		 finish();
	    		 break;
	    	 default:
	    		break;
	    	 }
	     } 
	};
   
   
   //检查网络状态
   boolean flag = false;
   private boolean CheckNetwork() {
       
       ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
       State mobile = cwjManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();   
       State wifi = cwjManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
       if(mobile==State.CONNECTED||wifi==State.CONNECTED)
    	   flag=true;
       if (!flag) {
    	   Dialog dialog = new AlertDialog.Builder(this).setTitle("无可用的网络").setMessage("请开启GPRS或WIFI网络连接").setPositiveButton("设置", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int whichButton) {
                   Intent mIntent = new Intent("/");
                   ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                   mIntent.setComponent(comp);
                   mIntent.setAction("android.intent.action.VIEW");
                   startActivity(mIntent);
               }
           }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int whichButton) {
                   dialog.cancel();
               }
           }).create();
    	   dialog.show();
       }
 
       return flag;
   }


   
   
	
   class MyButtonListener implements Button.OnClickListener
   {
	   	 
	   	 @Override
	   	 public void onClick(View v) 
	   	 {
	   	 // TODO Auto-generated method stub
	   		CheckNetwork();
	   		intent.setClass(MainActivity.this,AndroidCamera.class);
	   		bundle.putString("KeyWord",mytext.getText().toString());
	   		bundle.putInt("Radius",set1.getInt("radius", 1000));
	   		intent.putExtras(bundle);
	   		startActivity(intent);
	   		 
	   	 }
	}
   
   public List<Map<String, Object>> fillMap() {
   	  List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
   	  for (int i = 0, j = texts.length; i < j; i++) {
   	   Map<String, Object> map = new HashMap<String, Object>();
   	   map.put("imageView", images[i]);
   	   map.put("imageTitle", texts[i]);
   	   list.add(map);
   	  }
   	  return list;
   	 } 
   
   public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("yao","MainActivity.onKeyDown");
		 // 按下键盘上返回按钮    
	       if (keyCode == KeyEvent.KEYCODE_BACK) {    
	   
	           new AlertDialog.Builder(this)    
	                   .setMessage("确定退出ColorfulWorld吗？")    
	                   .setNegativeButton("取消",    
	                           new DialogInterface.OnClickListener() {    
	                               public void onClick(DialogInterface dialog,    
	                                       int which) {    
	                               }    
	                           })    
	                   .setPositiveButton("确定",    
	                           new DialogInterface.OnClickListener() {    
	                               public void onClick(DialogInterface dialog,    
	                                       int whichButton) {    
	                                   finish();    
	                               }    
	                           }).show();    
	   
	           return true;    
	       } else {    
	           return super.onKeyDown(keyCode, event);    
	       }    
	}
   
   
   Intent intent=new Intent();
   Bundle bundle=new Bundle();
   
   @Override
   public void onItemClick(AdapterView<?> arg0, View arg1, int idx, long arg3) 
   {
    // TODO Auto-generated method stub
	   CheckNetwork();
	   if(flag==true)
	   {
		   intent.setClass(MainActivity.this,AndroidCamera.class);
		   switch (idx) {
		   case 0:
			   bundle.putString("KeyWord","学校");
			   bundle.putInt("Radius",set1.getInt("radius", 1000));
			   intent.putExtras(bundle);
			   startActivity(intent);
			   break;
		   case 1:
			   bundle.putString("KeyWord","atm");
			   bundle.putInt("Radius",set1.getInt("radius", 1000));
			   intent.putExtras(bundle);
			   startActivity(intent);
			   break;
		   case 2:
			   bundle.putString("KeyWord","酒吧");
			   bundle.putInt("Radius",set1.getInt("radius", 1000));
			   intent.putExtras(bundle);
			   startActivity(intent);
			   break;
		   case 3:
			   bundle.putString("KeyWord","咖啡");
			   bundle.putInt("Radius",set1.getInt("radius", 1000));
			   intent.putExtras(bundle);
			   startActivity(intent);
			   break;
		   case 4:
			   bundle.putString("KeyWord","加油站");
			   bundle.putInt("Radius",set1.getInt("radius", 1000));
			   intent.putExtras(bundle);
			   startActivity(intent);
			   break;
		   case 5:
			   bundle.putString("KeyWord","餐馆");
			   bundle.putInt("Radius",set1.getInt("radius", 1000));
			   intent.putExtras(bundle);
			   startActivity(intent);
			   break;
		   case 6:
			   bundle.putString("KeyWord","超市");
			   bundle.putInt("Radius",set1.getInt("radius", 1000));
			   intent.putExtras(bundle);
			   startActivity(intent);
			   break;
		   case 7:
			   bundle.putString("KeyWord","银行");
			   bundle.putInt("Radius",set1.getInt("radius", 1000));
			   intent.putExtras(bundle);
			   startActivity(intent);
			   break;
		   default:
			   break;
		   			}
	   }
   }
 
}
