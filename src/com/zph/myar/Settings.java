package com.zph.myar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Settings extends Activity implements OnItemClickListener
{
	/** Called when the activity is first created. */
	
	private SeekBar mSeekBar;  
	int mSeekBarValue,mychoice;
	ListView listView,listView2;
	TextView myText;
	String[] titles={"搜索范围","距离单位"};
	String[] texts={"搜索区域的大小","公里"};
	
	String[] about={"关于"};
	String[] aboutme={"关于软件"};
	int[] resIds={R.drawable.icon,R.drawable.icon,R.drawable.icon,R.drawable.icon};
	View layout;
	
	public static final String PREFS_NAME = "MyARSettings";
	// set preference
	SharedPreferences set1;
	SharedPreferences.Editor editor;
	
	   @Override
	   public void onCreate(Bundle savedInstanceState) 
	   {
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.settings);
	       listView=(ListView)this.findViewById(R.id.listView1);
		   listView.setAdapter(new ListViewAdapter(titles,texts,resIds));
		   listView.setOnItemClickListener(this);
		   
		   listView2=(ListView)this.findViewById(R.id.listView2);
		   listView2.setAdapter(new ListViewAdapter(about,aboutme,resIds));
		   listView2.setOnItemClickListener(new MyListListener());
		   // 取得SeekBar对象  
		   LayoutInflater inflater = LayoutInflater.from(this);
		   layout = inflater.inflate(R.layout.dialog,null);
		   mSeekBar = (SeekBar) layout.findViewById(R.id.seekBar1);
		   mSeekBar.setOnSeekBarChangeListener(new MySeekBarChangeListener()); 
		    
		   set1 = getSharedPreferences(PREFS_NAME, 0);
		   editor = set1.edit();
		   mSeekBarValue=set1.getInt("radius", 1000);
		   mychoice=set1.getInt("mychoice",0);
	   }
	   private class MySeekBarChangeListener implements OnSeekBarChangeListener {
	        @Override
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	            mSeekBarValue=progress;
	            myText.setText("搜索范围："+mSeekBarValue+"m");
	        }
	 
	        @Override
	        public void onStartTrackingTouch(SeekBar seekBar) {
	            System.out.println("onStartTrackingTouch ... " + seekBar.getProgress());
	        }
	 
	        @Override
	        public void onStopTrackingTouch(SeekBar seekBar) {
	            
	        }
	    }
	   public class ListViewAdapter extends BaseAdapter {
			View[] itemViews;

			public ListViewAdapter(String[] itemTitles, String[] itemTexts,
					int[] itemImageRes) {
				itemViews = new View[itemTitles.length];

				for (int i = 0; i < itemTitles.length; i++) {
					itemViews[i] = makeItemView(itemTitles[i], itemTexts[i],
							itemImageRes[i]);
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

			private View makeItemView(String strTitle, String strText, int resId) {
				LayoutInflater inflater = (LayoutInflater) Settings.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				// 使用View的对象itemView与R.layout.item关联
				View itemView = inflater.inflate(R.layout.itemview, null);

				// 通过findViewById()方法实例R.layout.item内各组件
				TextView title = (TextView) itemView.findViewById(R.id.itemTitle11);
				title.setText(strTitle);
				TextView text = (TextView) itemView.findViewById(R.id.itemText12);
				text.setText(strText);
				return itemView;
			}

			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null)
					return itemViews[position];
				return convertView;
			}
		}
	   
	   private class ChoiceOnClickListener implements DialogInterface.OnClickListener {  
		   
	        private int which = 0;  
	        @Override  
	        public void onClick(DialogInterface dialogInterface, int which) {  
	            this.which = which;  
	        }  
	          
	        public int getWhich() {  
	            return which;  
	        }  
	    } 
	   Intent intent=new Intent();
	   private class MyListListener implements AdapterView.OnItemClickListener
	   {
		   public void onItemClick(AdapterView<?> arg0, View arg1, int idx, long arg3)
		   {
			   switch (idx) 
			   {
			   case 0:
				   intent.setClass(Settings.this,About.class);
				   startActivity(intent);
				   break;
			   default:
				   break;
			   }
		   }
	   }
	   @Override
	   public void onItemClick(AdapterView<?> arg0, View arg1, int idx, long arg3) 
	   {
		   if(arg0==listView2&&idx==1)
		   {
			   finish();
		   }
		   switch (idx) {
		   case 0:
			   LayoutInflater inflater = LayoutInflater.from(this);
			   layout = inflater.inflate(R.layout.dialog,null);
			   mSeekBar = (SeekBar) layout.findViewById(R.id.seekBar1); 
			   mSeekBar.setProgress(set1.getInt("radius", 1000)); 
			   mSeekBar.setOnSeekBarChangeListener(new MySeekBarChangeListener()); 
			    
			   myText=(TextView) layout.findViewById(R.id.myText);
			   myText.setText("搜索范围:"+set1.getInt("radius", 1000)+"m");
			
			   Dialog dialog=new AlertDialog.Builder(this).setTitle("搜索范围").setView(layout)
	  	     	.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			   @Override
			   public void onClick(DialogInterface dialog, int which) {
			        	
			        	 Toast.makeText(Settings.this, "范围为："+mSeekBarValue, Toast.LENGTH_SHORT)
			             .show();
			        	 editor.putInt("radius",mSeekBarValue);  
				         editor.commit();
			        }
		        })
	  	     .setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
	  	    @Override
			public void onClick(DialogInterface dialog, int which) {

			        }
		        }).create();
	    	  dialog.show();
	    	  
	     break;
	    case 1:
	    	android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);  
            builder.setTitle("距离单位");  
            final ChoiceOnClickListener choiceListener = new ChoiceOnClickListener();  
            builder.setSingleChoiceItems(R.array.danwei,mychoice, choiceListener);  
              
            DialogInterface.OnClickListener btnListener =   
                new DialogInterface.OnClickListener() {  
                    @Override  
                    public void onClick(DialogInterface dialogInterface, int which) {  
                        int choiceWhich = choiceListener.getWhich(); 
                        editor.putInt("mychoice",choiceWhich);  
				         editor.commit();
                    }  
                };  
            builder.setPositiveButton("确定", btnListener);  
            dialog = builder.create();  
            dialog.show();
            break;
	    default:
	     break;
	    
	   }
	   }

}
