package com.gmail.andersoninfonet.vendas;

import java.io.BufferedReader;


import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class ExportarVendasService extends Service implements Runnable{
	
	@Override
	public void onCreate() {
		new Thread(ExportarVendasService.this).start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run() {
		SQLiteDatabase db = openOrCreateDatabase("vendas.db", Context.MODE_PRIVATE, null);
		
		Cursor cursor = db.rawQuery("SELECT * FROM venda", null);
		int totalDB = cursor.getCount();
		int totalReplicado = 0;
		
		while(cursor.moveToNext()){
			StringBuilder strURL = new StringBuilder();
			strURL.append("http://192.168.0.104:9000/vendas/inserir.php?produto=");
			strURL.append(cursor.getInt(cursor.getColumnIndex("produto")));
			strURL.append("&preco=");
			strURL.append(cursor.getDouble(cursor.getColumnIndex("preco")));
			strURL.append("&latitude=");
			strURL.append(cursor.getDouble(cursor.getColumnIndex("la")));
			strURL.append("&longitude=");
			strURL.append(cursor.getDouble(cursor.getColumnIndex("lo")));
			Log.d("ExportarVendasService", strURL.toString());
			
			try{
				URL url = new URL(strURL.toString());
				HttpURLConnection http = (HttpURLConnection) url.openConnection();
				InputStreamReader ips = new InputStreamReader(http.getInputStream());
				BufferedReader line =  new BufferedReader(ips);
				Log.d("ExportarVendasService", "OK1");
				String linhaRetorno = line.readLine();
				
				if(linhaRetorno.equals("y")){
					db.delete("venda", "_id=?", new String[]{String.valueOf(cursor.getInt(0))});
					totalReplicado ++;
					Log.d("ExportarVendasService", "OK2");
				}
			}catch(Exception e){
				Log.d("ExportarVendasService", e.getMessage());
			}
			Log.d("total replicado: ", String.valueOf(totalReplicado));
		}
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification nt;
		
		if(totalDB == totalReplicado){
			nt = new Notification(R.drawable.ic_launcher, "status replicação", System.currentTimeMillis());
			nt.flags = Notification.FLAG_AUTO_CANCEL;
			
			PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this.getApplicationContext(), MainActivity.class), 0);
			
			nt.setLatestEventInfo(this, "status replicação", "a replicação foi feita com sucesso, total: " + totalReplicado, pi);
		}else{
			nt = new Notification(R.drawable.ic_launcher, "status replicação", System.currentTimeMillis());
			nt.flags = Notification.FLAG_AUTO_CANCEL;
			
			PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this.getApplicationContext(), MainActivity.class), 0);
			
			nt.setLatestEventInfo(this, "status replicação", "a replicação não foi feita com sucesso, total: " + totalReplicado + " de " + totalDB, pi);
		}
		
		nt.vibrate = new long[] {100,2000,1000,2000};
		nt.audioStreamType = Notification.DEFAULT_SOUND;
		
		notificationManager.notify((int) Math.round(Math.random()),nt);
		
		db.close();
		
	}

}
