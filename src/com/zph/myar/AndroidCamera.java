package com.zph.myar;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import com.baidu.mapapi.MapView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKPoiInfo;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import android.location.Location;
import android.location.LocationManager;

@SuppressWarnings("deprecation")
public class AndroidCamera extends MapActivity {

	
	private BMapManager mapManager;
	private MapView mapView;
	private MapController mapController;
	private MKLocationManager mLocationManager;
	// 定义搜索服务类  
    private MKSearch mMKSearch;
    
    //坐标信息
    private GeoPoint geoPoint;
	//====================传感器
	private SensorManager sensorMgr;
    Sensor sensor;
    private float x, y;
    
    public static final String PREFS_NAME = "MyARSettings";
	// set preference
	SharedPreferences set1;
	SharedPreferences.Editor editor;
    int danwei=0;
	SensorListener lsn = new SensorListener() {
	    public void onSensorChanged(int sensor, float[] values) {
	    	if (sensor == SensorManager.SENSOR_ORIENTATION)
	    	{
	    	x = values[0];   
	    	y = values[1];   
	    	//z = values[2];
	    	tv.setText("");
	    	tvy.setText("");
	    	pv.setDegree(x);
	    	pv.setAngle(-y);
	    	}
	    	if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
               
            }      
	    }
	    
	    public void onAccuracyChanged(int sensor, int accuracy) {}
	};
	//====================传感器
	
	
	float longitudeStr,latitudeStr;
	//gps位置信息监听
	LocationListener mLocationListener = new LocationListener(){

		int locateflag=0;
		@Override
		public void onLocationChanged(Location location) {
			if(location!=null&&!KeyWord.equals("")&&locateflag==0){
				SCountView.setText("正在定位");
				
				longitudeStr=(float)location.getLongitude();
				latitudeStr=(float)location.getLatitude();
				geoPoint = new GeoPoint((int)(location.getLatitude()* 1E6),(int)(location.getLongitude()* 1E6));
				mMKSearch.poiSearchNearBy(KeyWord, geoPoint, Radius);
				
				locateflag=1;

			}
			else if(location!=null&&KeyWord.equals("")&&locateflag==0)
			{
				SCountView.setText("正在定位");
				longitudeStr=(float)location.getLongitude();
				latitudeStr=(float)location.getLatitude();
				geoPoint = new GeoPoint((int)(location.getLatitude()* 1E6),(int)(location.getLongitude()* 1E6));
				
				mMKSearch.reverseGeocode(geoPoint);
				
				locateflag=1;
			}
			
		}
    };//create时注册此listener，Destroy时需要Remove
    
    
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
	    	mMKSearch.poiSearchNearBy(KeyWord, geoPoint, Radius);
	    	MyTextView.setText("ABCDEFGHIJKLMNOPQRSTUVWXYZ A1B1C1D1E1F1G1H1I1J1K1L1L1M1N1O1P1Q1R1S1T1U1V1W1X1Y1Z1");
	    	
	        return true;
	    }
	    case R.id.help:
	        
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private pSurfaceView pv;
	private CameraView cv;
	//准备一个相机对象
	private Camera mCamera = null;
	
	RotateTextviewActivity text;
	 //获取手机屏幕分辨率的类  
    private DisplayMetrics dm;  
    TextView tv,tvy;
    RotateTextviewActivity MyTextView;
    RotateTextviewActivity2 SCountView;
    String KeyWord;
    int Radius;
    
    public static final int MK_GPS_PROVIDER=0;
    public static final int MK_NETWORK_PROVIDER=1;
    
    public static final int HORIZONTAL=0;
    public static final int VERTICLA=1;
	//Activity的创建方法
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle=this.getIntent().getExtras();
		KeyWord=bundle.getString("KeyWord");
		Radius=bundle.getInt("Radius");
		//settins
		//if(KeyWord.equals(""))
			//finish();
		set1 = getSharedPreferences(PREFS_NAME, 0);
	    editor = set1.edit();
	    danwei=set1.getInt("mychoice",1);
	    
		MyTextView=new RotateTextviewActivity(this);
		MyTextView.setTextSize(14);
		SCountView=new RotateTextviewActivity2(this);
		SCountView.setTextSize(14);
		SCountView.setText("正在定位");
		dm = new DisplayMetrics(); 
		getWindowManager().getDefaultDisplay().getMetrics(dm);  
		//窗口去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //窗口设置为全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //设置窗口为半透明
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        
        //提供一个帧布局
        FrameLayout  fl = new FrameLayout(this);      
        //创建一个照相预览用的SurfaceView子类，并放在帧布局的底层
        cv = new CameraView(this);
		fl.addView(cv);
		
		pv=new pSurfaceView(this);
		
		TScreenWidth=dm.widthPixels;
		TScreenHeight=dm.heightPixels;
		pv.ScreenHeight=dm.heightPixels;
		pv.ScreenWidth=dm.widthPixels;
		fl.addView(pv);
		//创建一个文本框添加在帧布局中，我们可以看到，文字自动出现在了SurfaceView的前面，由此你可以在预览窗口做出各种特殊效果
		tv = new TextView(this);
		tvy = new TextView(this);
		RotateTextviewActivity text=new RotateTextviewActivity(this);
		text.setText("x轴:"+Float.toString(x));
		fl.addView(tv);
		fl.addView(tvy);
		
		mapView=new MapView(this);
		mapView.setVisibility(8);//设置其属性为gone
		
		//设置Activity的根内容视图
		fl.addView(mapView);
		fl.addView(SCountView);
		fl.addView(MyTextView);
		setContentView(fl);
		
		//获取GPS信息
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
		boolean GPS_status = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); 
		
		//注册传感器
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		//sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		//注册listener，第三个参数是检测的精确度
		sensorMgr.registerListener(lsn, SensorManager.SENSOR_ORIENTATION |SensorManager.SENSOR_ACCELEROMETER,SensorManager.SENSOR_DELAY_NORMAL);
		
		 //地图图层初始化
		// 初始化MapActivity
		mapManager = new BMapManager(getApplication());
		// init方法的第一个参数需填入申请的API Key
		mapManager.init("F0D9C143CC395CEAC26BB73429D5F0FDA942E4A9", null);
		super.initMapActivity(mapManager);
		
		// 初始化MKSearch  
        mMKSearch = new MKSearch();  
        mMKSearch.init(mapManager, new MySearchListener());  
     // 设置地图模式为交通地图
		mapView.setTraffic(true);
		// 设置启用内置的缩放控件
		mapView.setBuiltInZoomControls(true);

		// 用给定的经纬度构造一个GeoPoint（纬度，经度）
		GeoPoint point = new GeoPoint((int) (47.118440 * 1E6), (int) (87.493147 * 1E6));

		// 取得地图控制器对象，用于控制MapView
		mapController = mapView.getController();
		// 设置地图的中心
		mapController.setCenter(point);
		// 设置地图默认的缩放级别
		mapController.setZoom(12);
		mLocationManager = mapManager.getLocationManager();
		if(GPS_status)
		{
			mLocationManager.enableProvider(MK_GPS_PROVIDER);
			mLocationManager.disableProvider(MK_NETWORK_PROVIDER);
		}
		else
		{
			mLocationManager.enableProvider(MK_NETWORK_PROVIDER);
			mLocationManager.disableProvider(MK_GPS_PROVIDER);
			Toast.makeText(AndroidCamera.this, "GPS未开启", Toast.LENGTH_SHORT)
            .show();
		}
		mLocationManager.requestLocationUpdates(mLocationListener);
		

	}
	
	//===========================================================
	@Override
    protected void onResume() {
		if (mapManager != null) {
			mapManager.start();
		}
		//注册监听
		mLocationManager.requestLocationUpdates(mLocationListener);
		// register this class as a listener for the orientation and accelerometer sensors
        sensorMgr.registerListener(lsn, SensorManager.SENSOR_ORIENTATION |SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL);
		super.onResume();
        
      
    }
    
    @Override
    protected void onStop() {
    	// 不需要时移除监听
    	sensorMgr.unregisterListener(lsn);
        super.onStop();
    }    
    @Override  
    protected void onDestroy() {   
        //
    	//mCamera.release();
		//mCamera = null;
		//cv.surfaceDestroyed();
    	if (mapManager != null) {
			mapManager.destroy();
			mapManager = null;
		}
		// 不需要时移除监听
		mLocationManager.removeUpdates(mLocationListener);
    	sensorMgr.unregisterListener(lsn);   
        super.onDestroy();   
    }   
    @Override
	protected void onPause() {
		if (mapManager != null) {
			mapManager.stop();
		}
		// 不需要时移除监听
		mLocationManager.removeUpdates(mLocationListener);
		sensorMgr.unregisterListener(lsn);  
		super.onPause();
	}
    //==============================================================

	//按下返回键
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if (keyCode == KeyEvent.KEYCODE_BACK)
		 {
			 finish();  
		 }
		 return true;
	}

	String[] menu={"带我去那儿"};
	//显示对话框
	   private void myDialog()
	   {
		   
		   AlertDialog.Builder builder = new AlertDialog.Builder(this);
	      
	       builder.setTitle("请选择");
	       builder.setItems(menu,listener);
	       builder.create();
	       builder.show();
	   }
	   
	   private String GEO="";
	   
	   android.content.DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		     @Override
		       public void onClick(DialogInterface dialog, int which)
		     {
		       //TODO 点击项处理 
		    	 GEO="geo:0,0?q="+(list.get(paintI).weidu-0.006229)+","+(list.get(paintI).jingdu-0.006467);
		    	 switch(which)
		    	 {
		    	 case 0:
		    		 Uri uri = Uri.parse(GEO);
			 		 Intent it = new Intent(Intent.ACTION_VIEW,uri);
			 		 startActivity(it);
			    	 break;
		    	 default:
		    		 break;
		    	 }
		     } 
		};
	// 照相视图
	class CameraView extends SurfaceView {

		private SurfaceHolder holder = null;

		
		//构造函数
		public CameraView(Context context) {
			super(context);
			Log.i("yao","CameraView");

			// 操作surface的holder
			holder = this.getHolder();
			// 创建SurfaceHolder.Callback对象
			holder.addCallback(new SurfaceHolder.Callback() {

				@Override
				public void surfaceDestroyed(SurfaceHolder holder) {
					// 停止预览
					mCamera.stopPreview();
					// 释放相机资源并置空
					mCamera.release();
					mCamera = null;
				}

				@Override
				public void surfaceCreated(SurfaceHolder holder) {
					//当预览视图创建的时候开启相机
					mCamera = Camera.open();
					try {
						//设置预览
						mCamera.setPreviewDisplay(holder);
					} catch (IOException e) {
						// 释放相机资源并置空
						mCamera.release();
						mCamera = null;
					}

				}

				//当surface视图数据发生变化时，处理预览信息
				@Override
				public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

					Camera.Parameters parameters = mCamera.getParameters();  
		            //  设置照片格式  
		            parameters.setPictureFormat(PixelFormat.JPEG);  
		            // 根据屏幕方向设置预览尺寸  
		            if (getWindowManager().getDefaultDisplay().getOrientation() == 0)  
		                parameters.setPreviewSize(TScreenHeight, TScreenWidth);  
		            else  
		                parameters.setPreviewSize(TScreenWidth, TScreenHeight);  
		            //  设置拍摄照片的实际分辨率，本例中的分辨率是1024×768  
		            parameters.setPictureSize(320, 240);  
		            // 设置保存的图像大小  
		            mCamera.setDisplayOrientation(90);
		            mCamera.setParameters(parameters);  
		            //  开始拍照  
		            
		            mCamera.startPreview();  
				}
			});
			// 设置Push缓冲类型，说明surface数据由其他来源提供，而不是用自己的Canvas来绘图，在这里是由摄像头来提供数据
			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

}
	
	
//==================================================================================	
	
class RotateTextviewActivity extends TextView{

	public RotateTextviewActivity(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public RotateTextviewActivity(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public RotateTextviewActivity(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see android.widget.TextView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.translate(40,TScreenHeight*27/32);
		//canvas.rotate(270);
		super.onDraw(canvas);
	}

	/* (non-Javadoc)
	 * @see android.widget.TextView#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);		
		setMeasuredDimension(TScreenWidth, TScreenHeight);
	}
}

class RotateTextviewActivity2 extends TextView{

	public RotateTextviewActivity2(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public RotateTextviewActivity2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public RotateTextviewActivity2(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see android.widget.TextView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.translate(6, 30);
		//canvas.rotate(270);
		super.onDraw(canvas);
	}

	/* (non-Javadoc)
	 * @see android.widget.TextView#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);		
		setMeasuredDimension(110, 60);
	}
}

//=======================================================================================


@Override
protected boolean isRouteDisplayed() {
	// TODO Auto-generated method stub
	return false;
}

//计算夹角
private String computeAngle(float x,float y,float myx,float myy)
{
	double VectorX,VectorY;
	VectorX=x-myx;
	VectorY=y-myy;
	double m=Math.sqrt(VectorX*VectorX+VectorY*VectorY)*Math.sqrt(myy*myy);
	double cosAngle=(VectorX*0+VectorY*myy)/m;
	if(VectorX<0&&VectorY>0)
	{
		return String.valueOf(360-Math.acos(cosAngle)*180/Math.PI);
	}
	else if(VectorX<0&&VectorY<0)
	{
		return String.valueOf(360-Math.acos(cosAngle)*180/Math.PI);
	}
	else
	{
		return String.valueOf(Math.acos(cosAngle)*180/Math.PI);
	}
	
	
	
}

/*a表示原来的数据，b表示截取的位数*/ 
private double   roundChange(double   a   ,int   b)   { 
if(b   <   0)   return   a; 
int   k   =   1; 
for(int   i   =   0;   i   <   b;   i++)   { 
k   =   k   *   10; 
} 
return   ((double)Math.round(a*k))/k; 
} 

//计算距离
private final double EARTH_RADIUS = 6378.137;//地球半径
private static double rad(double d)
{
   return d * Math.PI / 180.0;
}
public String GetDistance(double lng1, double lat1, double lng2, double lat2)
{
   double radLat1 = rad(lat1);
   double radLat2 = rad(lat2);
   double a = radLat1 - radLat2;
   double b = rad(lng1) - rad(lng2);
   double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
    Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
   s = s * EARTH_RADIUS*1000;
   if(danwei==1)
	   s=roundChange(s/1000,2);
   if(danwei==0)
	   s=Math.round(s);
   //s = Math.round(s * 10000) / 10000;
   return String.valueOf(s);
}


class MySearchListener implements MKSearchListener {
	/**
	 * 根据经纬度搜索地址信息结果
	 * @param result 搜索结果
	 * @param iError 错误号（0表示正确返回）
	 */
	@Override
	public void onGetAddrResult(MKAddrInfo result, int iError) 
	{
		 SCountView.setText("获取结果");
		 if (result == null) {  
             return;  
         }  
         StringBuffer sb = new StringBuffer();  
         // 经纬度所对应的位置  
         sb.append(result.strAddr).append("\n");  

         // 判断该地址附近是否有POI（Point of Interest,即兴趣点）  
         if (null != result.poiList) {  
        	 
             // 遍历所有的兴趣点信息  
             for (MKPoiInfo poiInfo : result.poiList) {  
            	 sb.append("----------------------------------------").append("\n");  
                 sb.append("名称：").append(poiInfo.name).append("\n");  
                 sb.append("地址：").append(poiInfo.address).append("\n");  
                 sb.append("经度：").append(poiInfo.pt.getLongitudeE6() / 1000000.0f).append("\n");  
                 sb.append("纬度：").append(poiInfo.pt.getLatitudeE6() / 1000000.0f).append("\n");  
                 sb.append("电话：").append(poiInfo.phoneNum).append("\n");  
                 poiInfo.city=computeAngle(poiInfo.pt.getLongitudeE6()/ 1000000.0f,poiInfo.pt.getLatitudeE6()/ 1000000.0f,longitudeStr,latitudeStr);
                 poiInfo.postCode=GetDistance(poiInfo.pt.getLongitudeE6()/ 1000000.0f,poiInfo.pt.getLatitudeE6()/ 1000000.0f,longitudeStr,latitudeStr);
                 //angle=computeAngle(poiInfo.pt.getLongitudeE6()/ 1000000.0f,poiInfo.pt.getLatitudeE6()/ 1000000.0f,113.306637f,33.741863f);
                 sb.append("角度：").append(poiInfo.city).append("\n");  
                 // poi类型，0：普通点，1：公交站，2：公交线路，3：地铁站，4：地铁线路  
                 sb.append("距离：").append(poiInfo.postCode).append("\n");  
                 list.add(new MyPoiInfo(poiInfo.name,poiInfo.address,poiInfo.postCode,poiInfo.phoneNum,Float.parseFloat(poiInfo.city),poiInfo.pt.getLongitudeE6()/ 1000000.0f,poiInfo.pt.getLatitudeE6()/ 1000000.0f));
             }  
             SCountView.setText("共"+(list.size()+"条结果"));
             InfoFlag=1;
         }
         else
         {
        	 SCountView.setText("内容获取失败");
        	 Toast.makeText(AndroidCamera.this, "内容获取失败，请尝试调整搜索范围!", Toast.LENGTH_SHORT)
             .show();
         }
	}

	/**
	 * 驾车路线搜索结果
	 * @param result 搜索结果
	 * @param iError 错误号（0表示正确返回）
	 */
	@Override
	public void onGetDrivingRouteResult(MKDrivingRouteResult result, int iError) {
	}

	
	
	/**
	 * POI搜索结果（范围检索、城市POI检索、周边检索）
	 * @param result 搜索结果
	 * @param type 返回结果类型（11,12,21:poi列表 7:城市列表）
	 * @param iError 错误号（0表示正确返回）
	 */
	@Override
	public void onGetPoiResult(MKPoiResult result, int type, int iError) {
		SCountView.setText("获取结果");
		if (result == null) {  
            return;  
        }  
		
        StringBuffer sb = new StringBuffer();  
        // 经纬度所对应的位置  
        //sb.append(result.strAddr).append("/n");  
        //String angle="";
        // 判断该地址附近是否有POI（Point of Interest,即兴趣点）  
        if (null != result.getAllPoi()) { 
        	
            // 遍历所有的兴趣点信息  
            for (MKPoiInfo poiInfo : result.getAllPoi()) {  
                sb.append("----------------------------------------").append("\n");  
                sb.append("名称：").append(poiInfo.name).append("\n");  
                sb.append("地址：").append(poiInfo.address).append("\n");  
                sb.append("经度：").append(poiInfo.pt.getLongitudeE6() / 1000000.0f).append("\n");  
                sb.append("纬度：").append(poiInfo.pt.getLatitudeE6() / 1000000.0f).append("\n");  
                sb.append("电话：").append(poiInfo.phoneNum).append("\n");  
                poiInfo.city=computeAngle(poiInfo.pt.getLongitudeE6()/ 1000000.0f,poiInfo.pt.getLatitudeE6()/ 1000000.0f,longitudeStr,latitudeStr);
                poiInfo.postCode=GetDistance(poiInfo.pt.getLongitudeE6()/ 1000000.0f,poiInfo.pt.getLatitudeE6()/ 1000000.0f,longitudeStr,latitudeStr);
                //angle=computeAngle(poiInfo.pt.getLongitudeE6()/ 1000000.0f,poiInfo.pt.getLatitudeE6()/ 1000000.0f,113.306637f,33.741863f);
                sb.append("角度：").append(poiInfo.city).append("\n");  
                // poi类型，0：普通点，1：公交站，2：公交线路，3：地铁站，4：地铁线路  
                sb.append("距离：").append(poiInfo.postCode).append("\n");  
                list.add(new MyPoiInfo(poiInfo.name,poiInfo.address,poiInfo.postCode,poiInfo.phoneNum,Float.parseFloat(poiInfo.city),poiInfo.pt.getLongitudeE6()/ 1000000.0f,poiInfo.pt.getLatitudeE6()/ 1000000.0f));
                
            }  
            //Comparator comp = new Mycomparator();
            //Collections.sort(list,comp);
            SCountView.setText("共"+(list.size()+"条结果"));
            InfoFlag=1;
        }
        else
        {
        	SCountView.setText("内容获取失败");
        	 Toast.makeText(AndroidCamera.this, "内容获取失败，请尝试调整搜索范围!", Toast.LENGTH_SHORT)
             .show();
        }
        // 将地址信息、兴趣点信息显示在TextView上  
        //addressTextView.setText(sb.toString());
	}

	/**
	 * 公交换乘路线搜索结果
	 * @param result 搜索结果
	 * @param iError 错误号（0表示正确返回）
	 */
	@Override
	public void onGetTransitRouteResult(MKTransitRouteResult result, int iError) {
	}

	/**
	 * 步行路线搜索结果
	 * @param result 搜索结果
	 * @param iError 错误号（0表示正确返回）
	 */
	@Override
	public void onGetWalkingRouteResult(MKWalkingRouteResult result, int iError) {
	}
}

//圆形与信息显示flag
int InfoFlag=0;

//地图相关信息的泛型列表


ArrayList<MyPoiInfo> list = new ArrayList<MyPoiInfo>();
int paintI=0;

public int TScreenWidth,TScreenHeight;
//信息显示图层
class pSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder holder = null;
    public float degree,angle;
    public int ScreenWidth,ScreenHeight;
    int px = getMeasuredWidth();
	//构造函数
	public pSurfaceView(Context context) {
		super(context);
		//this.ScreenWidth=width;
		//this.ScreenHeight=height;
		Log.i("yao","CameraView");

		// 操作surface的holder
		holder = this.getHolder();
		// 创建SurfaceHolder.Callback对象
		//surfaceview透明
		holder.setFormat(PixelFormat.TRANSPARENT);
		//surfceview放置在顶层，即始终位于最上层
		setZOrderOnTop(true);
		holder.addCallback(this); 
		
	}
	
	//线程
	Thread t,t2;
	int STOP_FLAG=0;
	
	Bitmap mBitmap,mBitmap2;
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		STOP_FLAG=1;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		//InfoFlag=1;
		
		 //read the icon.png into buffer   
        InputStream is = getResources().openRawResource(R.drawable.location);   
        InputStream is2 = getResources().openRawResource(R.drawable.mylocation);   
        //decode   
        mBitmap = BitmapFactory.decodeStream(is);   
        mBitmap2 = BitmapFactory.decodeStream(is2); 
        
		STOP_FLAG=0;
		CircleX=ScreenWidth-ScreenHeight/11;
	    CircleY=ScreenHeight/10;
		t = new MyThread();  
		t.start();  
		t2=new InfoThread();
		t2.start();


	}

	//当surface视图数据发生变化时，处理预览信息
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {	
	}
	
	
	//Path path = new Path(); //定义一条路径
   

	//显示详细信息
	protected void onDisaplayInfo()
	{
		try
    	{
			
			
			 /*去锯齿*/
        	mPaint.setAntiAlias(true);
    		//获取画布  
			canvas = holder.lockCanvas(new Rect(0,ScreenHeight*27/32,ScreenWidth,ScreenHeight));
			RectF rect = new RectF(0,ScreenHeight*27/32,ScreenWidth,ScreenHeight);
			canvas.save();
			canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mPaint.setColor(Color.BLACK); 
            mPaint.setTextSize(24);
            //设置透明度
            mPaint.setAlpha(40);
            canvas.drawRoundRect(rect,20, 20,mPaint);
            
            float MiniAngle=0;
            paintI=0;
            MiniAngle=Math.abs(degree-list.get(0).angle);
            for(int i=0;i<list.size(); i++)
            {
            	
            	if(Math.abs(degree-list.get(i).angle)<MiniAngle)
            	{
            		MiniAngle=Math.abs(degree-list.get(i).angle);
            		paintI=i;
            	}
            }
            if(MiniAngle<6)
            {
            	
            	if(danwei==0)
            		MyTextView.setText("名称："+list.get(paintI).name+"\n"+"地址："+list.get(paintI).address+"\n"+"电话："+list.get(paintI).phoneNum+"\n"+"距离："+list.get(paintI).distance+"m");
            	if(danwei==1)
                	MyTextView.setText("名称："+list.get(paintI).name+"\n"+"地址："+list.get(paintI).address+"\n"+"电话："+list.get(paintI).phoneNum+"\n"+"距离："+list.get(paintI).distance+"km");
            	
            }
            else
            {
            	MyTextView.setText("");
            }
    		canvas.restore();
            
    	}
    	catch(Exception ex){}
    	finally
    	{
    		if(canvas!=null)
    		holder.unlockCanvasAndPost(canvas);//解锁画布，提交画好的图像 
    	}
	}
	
	
	//屏幕中心的气泡
    protected void onDraw() {
            // TODO Auto-generated method stub
            //super.onDraw(canvas);
            //invalidate();
    	try
    	{
    		//获取画布  
    		 /*去锯齿*/
        	mPaint.setAntiAlias(true);
			//canvas = holder.lockCanvas(new Rect(ScreenWidth/5,0,ScreenWidth*7/8,ScreenHeight));
			canvas = holder.lockCanvas(new Rect(0,ScreenHeight/10+ScreenHeight/11,ScreenWidth,ScreenHeight*27/32));
			//canvas.translate(ScreenWidth/2,i++); 
			canvas.save();
			canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mPaint.setColor(Color.rgb(57,73,156));  
            int i=0;
            for(MyPoiInfo info:list)
            {
            	if(i==paintI)
            	{
            		mPaint.setColor(Color.RED);
            		if(info.angle>335&&degree<25)       		
            			//canvas.drawCircle((info.angle-degree-335)*ScreenWidth/50,ScreenHeight*angle/180,15, mPaint);
            		 	canvas.drawBitmap(mBitmap2, (info.angle-degree-335)*ScreenWidth/50,ScreenHeight*angle/180, mPaint);
            		else
            			//canvas.drawCircle((info.angle-degree+25)*ScreenWidth/50,ScreenHeight*angle/180,15, mPaint);
            		 	canvas.drawBitmap(mBitmap2, (info.angle-degree+25)*ScreenWidth/50,ScreenHeight*angle/180, mPaint); 
            	}
            	else 
            	{
            		mPaint.setColor(Color.rgb(57,73,156));
            		if(info.angle>335&&degree<25)       		
            			//canvas.drawCircle((info.angle-degree-335)*ScreenWidth/50,ScreenHeight*angle/180,15, mPaint);
            		 	canvas.drawBitmap(mBitmap, (info.angle-degree-335)*ScreenWidth/50,ScreenHeight*angle/180, mPaint); 
            		else
            			//canvas.drawCircle((info.angle-degree+25)*ScreenWidth/50,ScreenHeight*angle/180,15, mPaint);
            		 	canvas.drawBitmap(mBitmap, (info.angle-degree+25)*ScreenWidth/50,ScreenHeight*angle/180, mPaint); 
            	}
            	i++;
            }  
    		canvas.restore();
    	}
    	catch(Exception ex){}
    	finally
    	{
    		if(canvas!=null)
    		holder.unlockCanvasAndPost(canvas);//解锁画布，提交画好的图像 
    	}
           
    }
	
    
    
    
    //雷达图
    float CircleX,CircleY,rate;
    Paint lPanti=new Paint();
    protected void onDrawCircle() {
        try
        {
        	 /*去锯齿*/
        	mPaint.setAntiAlias(true);
        	
        	//缩小比例
        	if(danwei==1)
        		rate=ScreenHeight*1000/(11*(float)Radius);
        	else
        		rate=ScreenHeight/(11*(float)Radius);
        	//canvas = holder.lockCanvas(new Rect(ScreenWidth-ScreenHeight*2/11,0,ScreenWidth,ScreenHeight/10+ScreenHeight/11));
        	canvas = holder.lockCanvas(new Rect(0,0,ScreenWidth,ScreenHeight/10+ScreenHeight/11));
        	canvas.save();
        	
        	canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        	mPaint.setColor(Color.BLACK); 
            //设置透明度
            mPaint.setAlpha(140);
        	canvas.drawCircle(CircleX, CircleY,ScreenHeight/11,mPaint);
        	//test
        	RectF rect = new RectF(5,30,120,60);
        	//RectF rect = new RectF(5,30,ScreenWidth/4,ScreenHeight*3/20);
        	canvas.drawRoundRect(rect,10, 10,mPaint);
        	//test
        	mPaint.setColor(Color.RED); 
        	for(int i=0;i<list.size(); i++)
        	{
                	//canvas.drawCircle((float) (CircleX+Float.parseFloat(list.get(i).distance)*rate*Math.cos(Math.PI*(list.get(i).angle-degree)/180.0)),(float) (CircleY-Float.parseFloat(list.get(i).distance)*rate*Math.sin(Math.PI*(list.get(i).angle-degree)/180.0)),3,mPaint);
        		canvas.drawCircle((float) (CircleX+Float.parseFloat(list.get(i).distance)*rate*Math.sin(Math.PI*(list.get(i).angle-degree)/180.0)),(float) (CircleY-Float.parseFloat(list.get(i).distance)*rate*Math.cos(Math.PI*(list.get(i).angle-degree)/180.0)),3,mPaint);
        	}
        	 /*设置paint的 style 为STROKE：空心*/
        	lPanti.setStyle(Paint.Style.STROKE);
        	/*设置画笔为白色*/
        	lPanti.setColor(Color.WHITE); 
        	lPanti.setAntiAlias(true);
            //canvas.drawColor(Color.WHITE);
        	canvas.drawCircle(CircleX, CircleY,ScreenHeight/11,lPanti);
        	canvas.drawCircle(CircleX, CircleY,ScreenHeight/(11*3),lPanti);
        	canvas.drawCircle(CircleX, CircleY,ScreenHeight*2/(11*3),lPanti);
        	//canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            
        	canvas.restore();
        }
        catch(Exception ex){}
    	finally
    	{
    		if(canvas!=null)
    		holder.unlockCanvasAndPost(canvas);//解锁画布，提交画好的图像 
    	}
    	
    	 

    }

    protected void onDrawCount()
    {
    	//canvas = holder.lockCanvas(new Rect(0,0,100,100));
		//canvas.translate(100,100); 
		//canvas.rotate(90);
		RectF rect = new RectF(5,30,100,100);
		canvas.save();
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mPaint.setColor(Color.BLACK); 
        mPaint.setTextSize(24);
        //设置透明度
        mPaint.setAlpha(125);
        canvas.drawRoundRect(rect,10, 10,mPaint);
    }
    
    
    //触摸事件处理

	 @Override  
	 public boolean onTouchEvent(MotionEvent event)
	 {
		 setFocusableInTouchMode(true);
		 if (event.getAction() == MotionEvent.ACTION_DOWN&&event.getY()>ScreenHeight*27/32)
		 {
			 //finish();
			 myDialog();
			 return true;
		 }
		return true;
		 
	 }


	Paint mPaint = new Paint();
	int i=0;
	Canvas canvas;
	
	//圆点绘制线程
	class MyThread extends Thread{  
		  
        @Override  
        public void run() {  
        	//canvas = holder.lockCanvas();
        	//onDraw(canvas);
        	//holder.unlockCanvasAndPost(canvas);//解锁画布，提交画好的图像 
        		while(STOP_FLAG!=1){
            
        			InfoFlag=1;
        			if(InfoFlag==1)
        			{
        				onDraw();
        				//onDrawCount();
        				
        			}
        			/**if(angle<-110)
        			{
        				onDrawText(1);
        			}
        			else
        			{
        				onDrawText(0);
        			}**/
        			
        		}
              
        }  
          
    } 
	//信息显示线程
	class InfoThread extends Thread{  
		  
        @Override  
        public void run() {  
        	//canvas = holder.lockCanvas();
        	//onDraw(canvas);
        	//holder.unlockCanvasAndPost(canvas);//解锁画布，提交画好的图像 
        		while(STOP_FLAG!=1){
            
        			
        			if(InfoFlag==1)
        			{
        				onDisaplayInfo();
        				onDrawCircle();
        			}
        		}
              
        }  
          
    }
	// 设置Push缓冲类型，说明surface数据由其他来源提供，而不是用自己的Canvas来绘图，在这里是由摄像头来提供数据
	//holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
	public void setDegree(float degree)
	{
		this.degree=degree;
	}
	
	public void setAngle(float angle)
	{
		this.angle=angle;
	}
	
	
	}
	//自定义poi
	class MyPoiInfo
	{
		// POI名称
		public String name;
		// POI地址
		public String address;
		// POI距离
		public String distance;
		// POI联系电话
		public String phoneNum;
		// POI角度
		public float angle;
		//经度
		public float jingdu;
		//纬度
		public float weidu;
	
		MyPoiInfo(String name,String address,String distance,String phonenum,float angle,float jingdu,float weidu)
		{
			this.name=name;
			this.address=address;
			this.distance=distance;
			this.phoneNum=phonenum;
			this.angle=angle;
			this.jingdu=jingdu;
			this.weidu=weidu;
		}
	}
}

 