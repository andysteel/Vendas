package com.gmail.andersoninfonet.vendas;

import android.app.Activity;

import android.app.ProgressDialog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class NovaVendaActivity extends Activity implements LocationListener{

	private double la;
	private double lo;
	private LocationManager lm = null;
	private ProgressDialog pdg = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nova_venda);
		
		Spinner spProdutos = (Spinner)findViewById(R.id.spProdutos);
		
		SQLiteDatabase db = openOrCreateDatabase("vendas.db", Context.MODE_PRIVATE, null);
		
		Cursor cursor = db.rawQuery("SELECT * FROM produto ORDER BY nome ASC;", null);
		
		String[] from = {"_id","nome","preco"};
		int[] to = {R.id.txtVendaID, R.id.txtVendaNome, R.id.txtVendaPreco};
		
		
		SimpleCursorAdapter ad = new SimpleCursorAdapter(getBaseContext(),R.layout.spinner, cursor,from,to);
		
		spProdutos.setAdapter(ad);
		
		
		db.close();
	}
	
	public void salvarClick(View v) {
		
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		String provider = "gps";
		
		lm.requestLocationUpdates(provider, 4000, 0, this);
		pdg = ProgressDialog.show(NovaVendaActivity.this, "aguarde...", "Buscando localizacao!!", true, false,null);
		
		/*LocationManager locationM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationM.getBestProvider(criteria, false);
		Location location = locationM.getLastKnownLocation(provider);
		
		la = location.getLatitude();
		lo = location.getLongitude();*/
		
		/*SQLiteDatabase db = openOrCreateDatabase("vendas.db", Context.MODE_PRIVATE, null);
		
		Spinner spProdutos = (Spinner)findViewById(R.id.spProdutos);
		
		SQLiteCursor dados = (SQLiteCursor) spProdutos.getAdapter().getItem(spProdutos.getSelectedItemPosition());
		
		ContentValues ctv = new ContentValues();
		ctv.put("produto", dados.getInt(0));
		ctv.put("preco", dados.getDouble(2));
		ctv.put("la", la);
		ctv.put("lo", lo);
		
		if(db.insert("venda", "_id", ctv) > 0){
			Toast.makeText(getBaseContext(), "Venda salva com sucesso !!", Toast.LENGTH_LONG).show();
		}*/
	}

	@Override
	public void onLocationChanged(Location location) {
		pdg.dismiss();
		
		 la = location.getLatitude();
	     lo = location.getLongitude();
	     
	     SQLiteDatabase db = openOrCreateDatabase("vendas.db", Context.MODE_PRIVATE, null);
			
		Spinner spProdutos = (Spinner)findViewById(R.id.spProdutos);
			
		SQLiteCursor dados = (SQLiteCursor) spProdutos.getAdapter().getItem(spProdutos.getSelectedItemPosition());
			
		ContentValues ctv = new ContentValues();
		ctv.put("produto", dados.getInt(0));
		ctv.put("preco", dados.getDouble(2));
		ctv.put("la", la);
		ctv.put("lo", lo);
			
		if(db.insert("venda", "_id", ctv) > 0){
			Toast.makeText(getBaseContext(), "Venda salva com sucesso !!", Toast.LENGTH_LONG).show();
		}
		
		db.close();
		
	     lm.removeUpdates(this);
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(getBaseContext(), "Por favor abilite o GPS", Toast.LENGTH_LONG).show();
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
