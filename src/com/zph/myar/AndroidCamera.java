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
	// ��������������  
    private MKSearch mMKSearch;
    
    //������Ϣ
    private GeoPoint geoPoint;
	//====================������
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
	//====================������
	
	
	float longitudeStr,latitudeStr;
	//gpsλ����Ϣ����
	LocationListener mLocationListener = new LocationListener(){

		int locateflag=0;
		@Override
		public void onLocationChanged(Location location) {
			if(location!=null&&!KeyWord.equals("")&&locateflag==0){
				SCountView.setText("���ڶ�λ");
				
				longitudeStr=(float)location.getLongitude();
				latitudeStr=(float)location.getLatitude();
				geoPoint = new GeoPoint((int)(location.getLatitude()* 1E6),(int)(location.getLongitude()* 1E6));
				mMKSearch.poiSearchNearBy(KeyWord, geoPoint, Radius);
				
				locateflag=1;

			}
			else if(location!=null&&KeyWord.equals("")&&locateflag==0)
			{
				SCountView.setText("���ڶ�λ");
				longitudeStr=(float)location.getLongitude();
				latitudeStr=(float)location.getLatitude();
				geoPoint = new GeoPoint((int)(location.getLatitude()* 1E6),(int)(location.getLongitude()* 1E6));
				
				mMKSearch.reverseGeocode(geoPoint);
				
				locateflag=1;
			}
			
		}
    };//createʱע���listener��Destroyʱ��ҪRemove
    
    
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
	//׼��һ���������
	private Camera mCamera = null;
	
	RotateTextviewActivity text;
	 //��ȡ�ֻ���Ļ�ֱ��ʵ���  
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
	//Activity�Ĵ�������
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
		SCountView.setText("���ڶ�λ");
		dm = new DisplayMetrics(); 
		getWindowManager().getDefaultDisplay().getMetrics(dm);  
		//����ȥ������
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //��������Ϊȫ��
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //��Ļ����
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //���ô���Ϊ��͸��
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        
        //�ṩһ��֡����
        FrameLayout  fl = new FrameLayout(this);      
        //����һ������Ԥ���õ�SurfaceView���࣬������֡���ֵĵײ�
        cv = new CameraView(this);
		fl.addView(cv);
		
		pv=new pSurfaceView(this);
		
		TScreenWidth=dm.widthPixels;
		TScreenHeight=dm.heightPixels;
		pv.ScreenHeight=dm.heightPixels;
		pv.ScreenWidth=dm.widthPixels;
		fl.addView(pv);
		//����һ���ı��������֡�����У����ǿ��Կ����������Զ���������SurfaceView��ǰ�棬�ɴ��������Ԥ������������������Ч��
		tv = new TextView(this);
		tvy = new TextView(this);
		RotateTextviewActivity text=new RotateTextviewActivity(this);
		text.setText("x��:"+Float.toString(x));
		fl.addView(tv);
		fl.addView(tvy);
		
		mapView=new MapView(this);
		mapView.setVisibility(8);//����������Ϊgone
		
		//����Activity�ĸ�������ͼ
		fl.addView(mapView);
		fl.addView(SCountView);
		fl.addView(MyTextView);
		setContentView(fl);
		
		//��ȡGPS��Ϣ
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
		boolean GPS_status = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); 
		
		//ע�ᴫ����
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		//sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		//ע��listener�������������Ǽ��ľ�ȷ��
		sensorMgr.registerListener(lsn, SensorManager.SENSOR_ORIENTATION |SensorManager.SENSOR_ACCELEROMETER,SensorManager.SENSOR_DELAY_NORMAL);
		
		 //��ͼͼ���ʼ��
		// ��ʼ��MapActivity
		mapManager = new BMapManager(getApplication());
		// init�����ĵ�һ�����������������API Key
		mapManager.init("F0D9C143CC395CEAC26BB73429D5F0FDA942E4A9", null);
		super.initMapActivity(mapManager);
		
		// ��ʼ��MKSearch  
        mMKSearch = new MKSearch();  
        mMKSearch.init(mapManager, new MySearchListener());  
     // ���õ�ͼģʽΪ��ͨ��ͼ
		mapView.setTraffic(true);
		// �����������õ����ſؼ�
		mapView.setBuiltInZoomControls(true);

		// �ø����ľ�γ�ȹ���һ��GeoPoint��γ�ȣ����ȣ�
		GeoPoint point = new GeoPoint((int) (47.118440 * 1E6), (int) (87.493147 * 1E6));

		// ȡ�õ�ͼ�������������ڿ���MapView
		mapController = mapView.getController();
		// ���õ�ͼ������
		mapController.setCenter(point);
		// ���õ�ͼĬ�ϵ����ż���
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
			Toast.makeText(AndroidCamera.this, "GPSδ����", Toast.LENGTH_SHORT)
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
		//ע�����
		mLocationManager.requestLocationUpdates(mLocationListener);
		// register this class as a listener for the orientation and accelerometer sensors
        sensorMgr.registerListener(lsn, SensorManager.SENSOR_ORIENTATION |SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL);
		super.onResume();
        
      
    }
    
    @Override
    protected void onStop() {
    	// ����Ҫʱ�Ƴ�����
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
		// ����Ҫʱ�Ƴ�����
		mLocationManager.removeUpdates(mLocationListener);
    	sensorMgr.unregisterListener(lsn);   
        super.onDestroy();   
    }   
    @Override
	protected void onPause() {
		if (mapManager != null) {
			mapManager.stop();
		}
		// ����Ҫʱ�Ƴ�����
		mLocationManager.removeUpdates(mLocationListener);
		sensorMgr.unregisterListener(lsn);  
		super.onPause();
	}
    //==============================================================

	//���·��ؼ�
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if (keyCode == KeyEvent.KEYCODE_BACK)
		 {
			 finish();  
		 }
		 return true;
	}

	String[] menu={"����ȥ�Ƕ�"};
	//��ʾ�Ի���
	   private void myDialog()
	   {
		   
		   AlertDialog.Builder builder = new AlertDialog.Builder(this);
	      
	       builder.setTitle("��ѡ��");
	       builder.setItems(menu,listener);
	       builder.create();
	       builder.show();
	   }
	   
	   private String GEO="";
	   
	   android.content.DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		     @Override
		       public void onClick(DialogInterface dialog, int which)
		     {
		       //TODO ������ 
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
	// ������ͼ
	class CameraView extends SurfaceView {

		private SurfaceHolder holder = null;

		
		//���캯��
		public CameraView(Context context) {
			super(context);
			Log.i("yao","CameraView");

			// ����surface��holder
			holder = this.getHolder();
			// ����SurfaceHolder.Callback����
			holder.addCallback(new SurfaceHolder.Callback() {

				@Override
				public void surfaceDestroyed(SurfaceHolder holder) {
					// ֹͣԤ��
					mCamera.stopPreview();
					// �ͷ������Դ���ÿ�
					mCamera.release();
					mCamera = null;
				}

				@Override
				public void surfaceCreated(SurfaceHolder holder) {
					//��Ԥ����ͼ������ʱ�������
					mCamera = Camera.open();
					try {
						//����Ԥ��
						mCamera.setPreviewDisplay(holder);
					} catch (IOException e) {
						// �ͷ������Դ���ÿ�
						mCamera.release();
						mCamera = null;
					}

				}

				//��surface��ͼ���ݷ����仯ʱ������Ԥ����Ϣ
				@Override
				public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

					Camera.Parameters parameters = mCamera.getParameters();  
		            //  ������Ƭ��ʽ  
		            parameters.setPictureFormat(PixelFormat.JPEG);  
		            // ������Ļ��������Ԥ���ߴ�  
		            if (getWindowManager().getDefaultDisplay().getOrientation() == 0)  
		                parameters.setPreviewSize(TScreenHeight, TScreenWidth);  
		            else  
		                parameters.setPreviewSize(TScreenWidth, TScreenHeight);  
		            //  ����������Ƭ��ʵ�ʷֱ��ʣ������еķֱ�����1024��768  
		            parameters.setPictureSize(320, 240);  
		            // ���ñ����ͼ���С  
		            mCamera.setDisplayOrientation(90);
		            mCamera.setParameters(parameters);  
		            //  ��ʼ����  
		            
		            mCamera.startPreview();  
				}
			});
			// ����Push�������ͣ�˵��surface������������Դ�ṩ�����������Լ���Canvas����ͼ����������������ͷ���ṩ����
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

//����н�
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

/*a��ʾԭ�������ݣ�b��ʾ��ȡ��λ��*/ 
private double   roundChange(double   a   ,int   b)   { 
if(b   <   0)   return   a; 
int   k   =   1; 
for(int   i   =   0;   i   <   b;   i++)   { 
k   =   k   *   10; 
} 
return   ((double)Math.round(a*k))/k; 
} 

//�������
private final double EARTH_RADIUS = 6378.137;//����뾶
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
	 * ���ݾ�γ��������ַ��Ϣ���
	 * @param result �������
	 * @param iError ����ţ�0��ʾ��ȷ���أ�
	 */
	@Override
	public void onGetAddrResult(MKAddrInfo result, int iError) 
	{
		 SCountView.setText("��ȡ���");
		 if (result == null) {  
             return;  
         }  
         StringBuffer sb = new StringBuffer();  
         // ��γ������Ӧ��λ��  
         sb.append(result.strAddr).append("\n");  

         // �жϸõ�ַ�����Ƿ���POI��Point of Interest,����Ȥ�㣩  
         if (null != result.poiList) {  
        	 
             // �������е���Ȥ����Ϣ  
             for (MKPoiInfo poiInfo : result.poiList) {  
            	 sb.append("----------------------------------------").append("\n");  
                 sb.append("���ƣ�").append(poiInfo.name).append("\n");  
                 sb.append("��ַ��").append(poiInfo.address).append("\n");  
                 sb.append("���ȣ�").append(poiInfo.pt.getLongitudeE6() / 1000000.0f).append("\n");  
                 sb.append("γ�ȣ�").append(poiInfo.pt.getLatitudeE6() / 1000000.0f).append("\n");  
                 sb.append("�绰��").append(poiInfo.phoneNum).append("\n");  
                 poiInfo.city=computeAngle(poiInfo.pt.getLongitudeE6()/ 1000000.0f,poiInfo.pt.getLatitudeE6()/ 1000000.0f,longitudeStr,latitudeStr);
                 poiInfo.postCode=GetDistance(poiInfo.pt.getLongitudeE6()/ 1000000.0f,poiInfo.pt.getLatitudeE6()/ 1000000.0f,longitudeStr,latitudeStr);
                 //angle=computeAngle(poiInfo.pt.getLongitudeE6()/ 1000000.0f,poiInfo.pt.getLatitudeE6()/ 1000000.0f,113.306637f,33.741863f);
                 sb.append("�Ƕȣ�").append(poiInfo.city).append("\n");  
                 // poi���ͣ�0����ͨ�㣬1������վ��2��������·��3������վ��4��������·  
                 sb.append("���룺").append(poiInfo.postCode).append("\n");  
                 list.add(new MyPoiInfo(poiInfo.name,poiInfo.address,poiInfo.postCode,poiInfo.phoneNum,Float.parseFloat(poiInfo.city),poiInfo.pt.getLongitudeE6()/ 1000000.0f,poiInfo.pt.getLatitudeE6()/ 1000000.0f));
             }  
             SCountView.setText("��"+(list.size()+"�����"));
             InfoFlag=1;
         }
         else
         {
        	 SCountView.setText("���ݻ�ȡʧ��");
        	 Toast.makeText(AndroidCamera.this, "���ݻ�ȡʧ�ܣ��볢�Ե���������Χ!", Toast.LENGTH_SHORT)
             .show();
         }
	}

	/**
	 * �ݳ�·���������
	 * @param result �������
	 * @param iError ����ţ�0��ʾ��ȷ���أ�
	 */
	@Override
	public void onGetDrivingRouteResult(MKDrivingRouteResult result, int iError) {
	}

	
	
	/**
	 * POI�����������Χ����������POI�������ܱ߼�����
	 * @param result �������
	 * @param type ���ؽ�����ͣ�11,12,21:poi�б� 7:�����б�
	 * @param iError ����ţ�0��ʾ��ȷ���أ�
	 */
	@Override
	public void onGetPoiResult(MKPoiResult result, int type, int iError) {
		SCountView.setText("��ȡ���");
		if (result == null) {  
            return;  
        }  
		
        StringBuffer sb = new StringBuffer();  
        // ��γ������Ӧ��λ��  
        //sb.append(result.strAddr).append("/n");  
        //String angle="";
        // �жϸõ�ַ�����Ƿ���POI��Point of Interest,����Ȥ�㣩  
        if (null != result.getAllPoi()) { 
        	
            // �������е���Ȥ����Ϣ  
            for (MKPoiInfo poiInfo : result.getAllPoi()) {  
                sb.append("----------------------------------------").append("\n");  
                sb.append("���ƣ�").append(poiInfo.name).append("\n");  
                sb.append("��ַ��").append(poiInfo.address).append("\n");  
                sb.append("���ȣ�").append(poiInfo.pt.getLongitudeE6() / 1000000.0f).append("\n");  
                sb.append("γ�ȣ�").append(poiInfo.pt.getLatitudeE6() / 1000000.0f).append("\n");  
                sb.append("�绰��").append(poiInfo.phoneNum).append("\n");  
                poiInfo.city=computeAngle(poiInfo.pt.getLongitudeE6()/ 1000000.0f,poiInfo.pt.getLatitudeE6()/ 1000000.0f,longitudeStr,latitudeStr);
                poiInfo.postCode=GetDistance(poiInfo.pt.getLongitudeE6()/ 1000000.0f,poiInfo.pt.getLatitudeE6()/ 1000000.0f,longitudeStr,latitudeStr);
                //angle=computeAngle(poiInfo.pt.getLongitudeE6()/ 1000000.0f,poiInfo.pt.getLatitudeE6()/ 1000000.0f,113.306637f,33.741863f);
                sb.append("�Ƕȣ�").append(poiInfo.city).append("\n");  
                // poi���ͣ�0����ͨ�㣬1������վ��2��������·��3������վ��4��������·  
                sb.append("���룺").append(poiInfo.postCode).append("\n");  
                list.add(new MyPoiInfo(poiInfo.name,poiInfo.address,poiInfo.postCode,poiInfo.phoneNum,Float.parseFloat(poiInfo.city),poiInfo.pt.getLongitudeE6()/ 1000000.0f,poiInfo.pt.getLatitudeE6()/ 1000000.0f));
                
            }  
            //Comparator comp = new Mycomparator();
            //Collections.sort(list,comp);
            SCountView.setText("��"+(list.size()+"�����"));
            InfoFlag=1;
        }
        else
        {
        	SCountView.setText("���ݻ�ȡʧ��");
        	 Toast.makeText(AndroidCamera.this, "���ݻ�ȡʧ�ܣ��볢�Ե���������Χ!", Toast.LENGTH_SHORT)
             .show();
        }
        // ����ַ��Ϣ����Ȥ����Ϣ��ʾ��TextView��  
        //addressTextView.setText(sb.toString());
	}

	/**
	 * ��������·���������
	 * @param result �������
	 * @param iError ����ţ�0��ʾ��ȷ���أ�
	 */
	@Override
	public void onGetTransitRouteResult(MKTransitRouteResult result, int iError) {
	}

	/**
	 * ����·���������
	 * @param result �������
	 * @param iError ����ţ�0��ʾ��ȷ���أ�
	 */
	@Override
	public void onGetWalkingRouteResult(MKWalkingRouteResult result, int iError) {
	}
}

//Բ������Ϣ��ʾflag
int InfoFlag=0;

//��ͼ�����Ϣ�ķ����б�


ArrayList<MyPoiInfo> list = new ArrayList<MyPoiInfo>();
int paintI=0;

public int TScreenWidth,TScreenHeight;
//��Ϣ��ʾͼ��
class pSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder holder = null;
    public float degree,angle;
    public int ScreenWidth,ScreenHeight;
    int px = getMeasuredWidth();
	//���캯��
	public pSurfaceView(Context context) {
		super(context);
		//this.ScreenWidth=width;
		//this.ScreenHeight=height;
		Log.i("yao","CameraView");

		// ����surface��holder
		holder = this.getHolder();
		// ����SurfaceHolder.Callback����
		//surfaceview͸��
		holder.setFormat(PixelFormat.TRANSPARENT);
		//surfceview�����ڶ��㣬��ʼ��λ�����ϲ�
		setZOrderOnTop(true);
		holder.addCallback(this); 
		
	}
	
	//�߳�
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

	//��surface��ͼ���ݷ����仯ʱ������Ԥ����Ϣ
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {	
	}
	
	
	//Path path = new Path(); //����һ��·��
   

	//��ʾ��ϸ��Ϣ
	protected void onDisaplayInfo()
	{
		try
    	{
			
			
			 /*ȥ���*/
        	mPaint.setAntiAlias(true);
    		//��ȡ����  
			canvas = holder.lockCanvas(new Rect(0,ScreenHeight*27/32,ScreenWidth,ScreenHeight));
			RectF rect = new RectF(0,ScreenHeight*27/32,ScreenWidth,ScreenHeight);
			canvas.save();
			canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mPaint.setColor(Color.BLACK); 
            mPaint.setTextSize(24);
            //����͸����
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
            		MyTextView.setText("���ƣ�"+list.get(paintI).name+"\n"+"��ַ��"+list.get(paintI).address+"\n"+"�绰��"+list.get(paintI).phoneNum+"\n"+"���룺"+list.get(paintI).distance+"m");
            	if(danwei==1)
                	MyTextView.setText("���ƣ�"+list.get(paintI).name+"\n"+"��ַ��"+list.get(paintI).address+"\n"+"�绰��"+list.get(paintI).phoneNum+"\n"+"���룺"+list.get(paintI).distance+"km");
            	
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
    		holder.unlockCanvasAndPost(canvas);//�����������ύ���õ�ͼ�� 
    	}
	}
	
	
	//��Ļ���ĵ�����
    protected void onDraw() {
            // TODO Auto-generated method stub
            //super.onDraw(canvas);
            //invalidate();
    	try
    	{
    		//��ȡ����  
    		 /*ȥ���*/
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
    		holder.unlockCanvasAndPost(canvas);//�����������ύ���õ�ͼ�� 
    	}
           
    }
	
    
    
    
    //�״�ͼ
    float CircleX,CircleY,rate;
    Paint lPanti=new Paint();
    protected void onDrawCircle() {
        try
        {
        	 /*ȥ���*/
        	mPaint.setAntiAlias(true);
        	
        	//��С����
        	if(danwei==1)
        		rate=ScreenHeight*1000/(11*(float)Radius);
        	else
        		rate=ScreenHeight/(11*(float)Radius);
        	//canvas = holder.lockCanvas(new Rect(ScreenWidth-ScreenHeight*2/11,0,ScreenWidth,ScreenHeight/10+ScreenHeight/11));
        	canvas = holder.lockCanvas(new Rect(0,0,ScreenWidth,ScreenHeight/10+ScreenHeight/11));
        	canvas.save();
        	
        	canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        	mPaint.setColor(Color.BLACK); 
            //����͸����
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
        	 /*����paint�� style ΪSTROKE������*/
        	lPanti.setStyle(Paint.Style.STROKE);
        	/*���û���Ϊ��ɫ*/
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
    		holder.unlockCanvasAndPost(canvas);//�����������ύ���õ�ͼ�� 
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
        //����͸����
        mPaint.setAlpha(125);
        canvas.drawRoundRect(rect,10, 10,mPaint);
    }
    
    
    //�����¼�����

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
	
	//Բ������߳�
	class MyThread extends Thread{  
		  
        @Override  
        public void run() {  
        	//canvas = holder.lockCanvas();
        	//onDraw(canvas);
        	//holder.unlockCanvasAndPost(canvas);//�����������ύ���õ�ͼ�� 
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
	//��Ϣ��ʾ�߳�
	class InfoThread extends Thread{  
		  
        @Override  
        public void run() {  
        	//canvas = holder.lockCanvas();
        	//onDraw(canvas);
        	//holder.unlockCanvasAndPost(canvas);//�����������ύ���õ�ͼ�� 
        		while(STOP_FLAG!=1){
            
        			
        			if(InfoFlag==1)
        			{
        				onDisaplayInfo();
        				onDrawCircle();
        			}
        		}
              
        }  
          
    }
	// ����Push�������ͣ�˵��surface������������Դ�ṩ�����������Լ���Canvas����ͼ����������������ͷ���ṩ����
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
	//�Զ���poi
	class MyPoiInfo
	{
		// POI����
		public String name;
		// POI��ַ
		public String address;
		// POI����
		public String distance;
		// POI��ϵ�绰
		public String phoneNum;
		// POI�Ƕ�
		public float angle;
		//����
		public float jingdu;
		//γ��
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

 