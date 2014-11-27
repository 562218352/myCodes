package com.zh.baiduMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MainActivity extends Activity {

	private BMapManager mapManager = null;
	private MapView mapView = null;
	private MapController mapController = null;
	
	private double latitude = 32.2901;
	private double longtitude = 111.1111;
	
	private LocationClient mLocClient = null;  
    private LocationData mLocData;  
    private MyLocationOverlay myLocationOverlay = null;  
    private PopupOverlay mPopupOverlay  = null;  
	      
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//这两行的初始化过程要在setContentView
		mapManager = new BMapManager(getApplicationContext());
		mapManager.init("35527A479A48F14F402BA9F287B76759E26D4EC7", null);
		
		setContentView(R.layout.activity_main);
		
		mapView = (MapView)findViewById(R.id.map);
		
		mapView.setBuiltInZoomControls(true);
		
		mapController = mapView.getController();
//		GeoPoint point = new GeoPoint((int)(latitude*1E6), (int)(longtitude*1E6));
//		mapController.setCenter(point);
		mapController.setZoom(13);
		
		locateSomeWhere();
		
	}

	private void locateSomeWhere() {
		mPopupOverlay = new PopupOverlay(mapView, new PopupClickListener() {  
          @Override  
          public void onClickedPopup(int arg0) {  
              mPopupOverlay.hidePop();  
          }  
      });  
		
		mLocClient = new LocationClient(getApplicationContext());
		
		mLocClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceivePoi(BDLocation arg0) {
				
			}
			
			@Override
			public void onReceiveLocation(BDLocation location) {
				mLocData.latitude = location.getLatitude();  
	            mLocData.longitude = location.getLongitude();  
	            //如果不显示定位精度圈，将accuracy赋值为0即可  
	            mLocData.accuracy = location.getRadius();  
	            mLocData.direction = location.getDerect();  
	            //将定位数据设置到定位图层里  
	            myLocationOverlay.setData(mLocData);  
	            System.out.println(location.getLatitude()+"--"+location.getLongitude());
	            //更新图层数据执行刷新后生效  
	            GeoPoint point = new GeoPoint((int)(location.getLatitude() * 1E6),(int)(location.getLongitude()*1E6));
	           
	            Drawable marker = getResources().getDrawable(R.drawable.ic_launcher);
	    		MyOverItems items = new MyOverItems(marker, mapView);
	    		OverlayItem item1 = new OverlayItem(point, "OI", "OI");
	    		items.addItem(item1);
	            mapView.getOverlays().add(items);
	    		
	            mapController.setCenter(point);
	            
//	            mPopupOverlay.showPopup(getBitMapForView(), point, 32);
	            mapView.refresh();  
	            
			}
		});
		
		LocationClientOption option = new LocationClientOption();
		option.setAddrType("all");
		option.setCoorType("bd09ll");
		option.setOpenGps(true);
		option.setPriority(LocationClientOption.GpsFirst);
		option.setScanSpan(5000);

		mLocClient.setLocOption(option);
		mLocClient.start(); 
		 
		myLocationOverlay = new MyLocationOverlay(mapView);
		mLocData = new LocationData();
		myLocationOverlay.setData(mLocData);  
		mapView.getOverlays().add(myLocationOverlay);
		mapView.refresh();
		
		
	}

	protected Bitmap getBitMapForView() {
		View view = findViewById(R.layout.activity_tip);
		TextView txt = (TextView)view.findViewById(R.id.tip);
		txt.setText("百度地图测试");
		
		view.destroyDrawingCache();
		view.setDrawingCacheEnabled(true); 
	    Bitmap bitmap = view.getDrawingCache(true); 
		return bitmap;
	}

	@Override
	protected void onResume() {
		mapView.onResume();
	        if (mapManager != null) {
	        	mapManager.start();
	        }
	    super.onResume();
	}

	@Override
	protected void onPause() {
		mapView.onPause();
		if(mapManager!=null){
			mapManager.stop();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		mapView.destroy();
		if(mapManager!=null){
			mapManager.destroy();
			mapManager=null;
		}
		super.onDestroy();
	}

	private class MyOverItems extends ItemizedOverlay<OverlayItem>{

		public MyOverItems(Drawable marker, MapView view) {
			super(marker, view);
		}
	}
	
}
