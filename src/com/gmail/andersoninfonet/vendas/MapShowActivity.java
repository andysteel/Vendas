package com.gmail.andersoninfonet.vendas;


import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapShowActivity extends FragmentActivity{
	
	private LatLng location;
	
	private GoogleMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);
		
		Intent it = getIntent();
		double latitude =  it.getDoubleExtra("latitude",0);
		double longitude =  it.getDoubleExtra("longitude",0);
		
		location = new LatLng(latitude, longitude);
		
		//System.out.println(latitude);
		
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa)).getMap();
		
		map.addMarker(new MarkerOptions().position(location).title("venda aqui"));
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 20));
		
		map.animateCamera(CameraUpdateFactory.zoomTo(10),2000, null);
		
		/*MapView map = (MapView) findViewById(R.id.mapa);
		
		map.setBuiltInZoomControls(true);
		map.displayZoomControls(true);
		
		Intent it = getIntent();
		int latitude =  (int) (it.getDoubleExtra("latitude",0)*1E6);
		int longitude = (int) (it.getDoubleExtra("longitude",0)*1E6);
		
		MapController mc = map.getController();
		
		mc.animateTo(new GeoPoint(latitude, longitude));
		mc.setZoom(30);
		
		map.invalidate();*/
	}
	
	/*@Override
	protected boolean isRouteDisplayed() {
		return false;
	}*/
	

	
}
