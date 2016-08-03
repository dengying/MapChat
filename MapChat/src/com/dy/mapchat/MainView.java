package com.dy.mapchat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainView extends FragmentActivity implements LocationListener {

	private GoogleMap googleMap;
	private boolean isFirst=true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_view);
		
		LocationManager locMan=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Location location1=locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(location1==null){
		    location1=locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if(location1!=null){
			double latitude=location1.getLatitude();//获取纬度
			double longitude=location1.getLongitude();//获取经度
			System.out.println("纬度："+latitude+"-经度："+longitude);
		}
		
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
		 
        // Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
 
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
 
        }else { // Google Play Services are available
 
            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
 
            // Getting GoogleMap object from the fragment
            googleMap = fm.getMap();
 
            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);
 
            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
 
            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();
 
            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);
 
            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);
 
            if(location!=null){
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
            
            googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
				
				@Override
				public boolean onMarkerClick(Marker marker) {
					// TODO Auto-generated method stub
					return false;
				}
			});
            googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {
				
				@Override
				public View getInfoWindow(Marker marker) {
					return null;
				}
				
				@Override
				public View getInfoContents(Marker marker) {
					
					LinearLayout infoview=null;
					try{
						infoview=(LinearLayout) LayoutInflater.from(MainView.this).inflate(R.layout.infowindow_view, null);
						infoview.setBackgroundColor(getResources().getColor(android.R.color.white));
						TextView title=(TextView) infoview.findViewById(R.id.info_title);
						TextView text=(TextView) infoview.findViewById(R.id.info_text);
						title.setText(marker.getTitle());
						text.setText(marker.getSnippet());
					}catch (Exception e) {
						// TODO: handle exception
					}
					
					return infoview;
				}
			});
            
        }
        
        //初始化切换图层
        ImageButton changeLayers=(ImageButton) this.findViewById(R.id.btn_changelayers);
        changeLayers.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				showLayers();
				
			}
		});
		
	}
        
    @Override
    public void onLocationChanged(Location location) {
 
    	if(isFirst){
	        double latitude = location.getLatitude();//纬度
	        double longitude = location.getLongitude();//经度
	        LatLng latLng = new LatLng(latitude, longitude);
	        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
	        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
	        googleMap.addMarker(new MarkerOptions().position(latLng).title("您").snippet("你好！！！").icon(BitmapDescriptorFactory.fromResource(R.drawable.markerico)));
	        LatLng newLatLng=new LatLng(latitude+0.0000001, latitude+0.0000000001);
	        googleMap.addMarker(new MarkerOptions().position(newLatLng).title("刘宏").snippet("Hello!!!!").icon(BitmapDescriptorFactory.fromResource(R.drawable.markerico)));
	        isFirst=false;
    	}
 
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 显示图层选择界面
	 */
	private void showLayers(){
		
		AlertDialog.Builder b = new AlertDialog.Builder(MainView.this);
		final AlertDialog dialog=b.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		Window window=dialog.getWindow();
		LinearLayout view=(LinearLayout) LayoutInflater.from(this).inflate(R.layout.selectlayers_view, null);
		RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rg_views);
		switch(googleMap.getMapType()){
			case GoogleMap.MAP_TYPE_NORMAL:{
				radioGroup.check(R.id.rb_normal);
				break;
			}
			case GoogleMap.MAP_TYPE_SATELLITE:{
				radioGroup.check(R.id.rb_satellite);
				break;
			}
			case GoogleMap.MAP_TYPE_TERRAIN:{
				radioGroup.check(R.id.rb_terrain);
				break;
			}
		}
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				if(checkedId == R.id.rb_normal){
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }else if(checkedId == R.id.rb_satellite){
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }else if(checkedId == R.id.rb_terrain){
                    googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }
				dialog.dismiss();
			}
		});
		window.setContentView(view);
			
		
	}

}
