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
	
	//��ʾ�Ի���
	   private void iDialog()
	   {
		   
		   LayoutInflater inflater = LayoutInflater.from(this);
		   layout = inflater.inflate(R.layout.idialog,null);
		   mycheckbox=(CheckBox) layout.findViewById(R.id.checkBox1); 
		   infotext=(TextView) layout.findViewById(R.id.textView1); 
		   infotext.setText("����\n"+
	        		"ColorfulWorld��һ�������̽����Χ������ֻ�Ӧ�á�ͨ��ʹ����ǿ��ʵ������������Ϣ����ʵ������е��ӣ��Ӷ��ﵽ��ǿ��ʵ��Ч�����û�ֻ��Ҫ���ֻ�������ͷ��׼��Χ�Ľ�������߿��ſռ䣬�������ֻ���Ļ�·�������֮��ص���ʵ���ݡ�\n"+
	        		"ʹ�÷���\n"+
	        		"ֱ�ӵ��������ť������Ի����Χ������������Ϣ������������������ã�����������������������Ȥ����Ϣ�����ͣ����ߵ���·���ͼ�꣬��ɻ����Ӧ���͵���Ϣ����ʵ��ģʽ�£������Ļ�·����ڹȸ��ͼ����ʾ����λ�á�ͨ��menu���������ò˵����ɶ�������Χ����λ�������á�");
	    	Dialog dialog=new AlertDialog.Builder(this).setTitle("����").setView(layout)
	  	     .setPositiveButton("����",new DialogInterface.OnClickListener() {
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
	
	//menu�˵�
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
	 
	 
	 String[] menu={"�������","����ȥ�Ƕ�"};
	 ListView listView;
   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.mymain);
    // ��ʼ��ͼƬ������ı�����
       images = new Integer[]{ R.drawable.school,
         R.drawable.atm, R.drawable.bar,
         R.drawable.coffee, R.drawable.gas,
         R.drawable.restaurant, R.drawable.supermarket, R.drawable.bank };
       
       texts = new String[]{ "ѧУ", "ATM��", "�ư�", "����", "����վ", "�͹�",
       "����" ,"����"};

       // ���GridView
       GridView gridView = (GridView) findViewById(R.id.homeGrid);
       
       SimpleAdapter simpleAdapter = new SimpleAdapter(this, fillMap(),
         R.layout.griditme, new String[] { "imageView", "imageTitle" },
         new int[] { R.id.imageView, R.id.imageTitle });
       gridView.setAdapter(simpleAdapter);
       // ����onItemClick�¼�
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

			// ʹ��View�Ķ���itemView��R.layout.item����
			View itemView = inflater.inflate(R.layout.itemview2, null);

			// ͨ��findViewById()����ʵ��R.layout.item�ڸ����
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
	       //TODO ������ 
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
   
   
   //�������״̬
   boolean flag = false;
   private boolean CheckNetwork() {
       
       ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
       State mobile = cwjManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();   
       State wifi = cwjManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
       if(mobile==State.CONNECTED||wifi==State.CONNECTED)
    	   flag=true;
       if (!flag) {
    	   Dialog dialog = new AlertDialog.Builder(this).setTitle("�޿��õ�����").setMessage("�뿪��GPRS��WIFI��������").setPositiveButton("����", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int whichButton) {
                   Intent mIntent = new Intent("/");
                   ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                   mIntent.setComponent(comp);
                   mIntent.setAction("android.intent.action.VIEW");
                   startActivity(mIntent);
               }
           }).setNeutralButton("ȡ��", new DialogInterface.OnClickListener() {
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
		 // ���¼����Ϸ��ذ�ť    
	       if (keyCode == KeyEvent.KEYCODE_BACK) {    
	   
	           new AlertDialog.Builder(this)    
	                   .setMessage("ȷ���˳�ColorfulWorld��")    
	                   .setNegativeButton("ȡ��",    
	                           new DialogInterface.OnClickListener() {    
	                               public void onClick(DialogInterface dialog,    
	                                       int which) {    
	                               }    
	                           })    
	                   .setPositiveButton("ȷ��",    
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
			   bundle.putString("KeyWord","ѧУ");
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
			   bundle.putString("KeyWord","�ư�");
			   bundle.putInt("Radius",set1.getInt("radius", 1000));
			   intent.putExtras(bundle);
			   startActivity(intent);
			   break;
		   case 3:
			   bundle.putString("KeyWord","����");
			   bundle.putInt("Radius",set1.getInt("radius", 1000));
			   intent.putExtras(bundle);
			   startActivity(intent);
			   break;
		   case 4:
			   bundle.putString("KeyWord","����վ");
			   bundle.putInt("Radius",set1.getInt("radius", 1000));
			   intent.putExtras(bundle);
			   startActivity(intent);
			   break;
		   case 5:
			   bundle.putString("KeyWord","�͹�");
			   bundle.putInt("Radius",set1.getInt("radius", 1000));
			   intent.putExtras(bundle);
			   startActivity(intent);
			   break;
		   case 6:
			   bundle.putString("KeyWord","����");
			   bundle.putInt("Radius",set1.getInt("radius", 1000));
			   intent.putExtras(bundle);
			   startActivity(intent);
			   break;
		   case 7:
			   bundle.putString("KeyWord","����");
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
