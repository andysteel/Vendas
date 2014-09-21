package com.gmail.andersoninfonet.vendas;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements Runnable{

	private ProgressDialog pgd;
	private Cursor cursor;
	private SQLiteDatabase db;
	private String error = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
	
		SQLiteDatabase dbs = openOrCreateDatabase("vendas.db", Context.MODE_PRIVATE, null);
		
		StringBuilder sqlProduto = new StringBuilder();
		sqlProduto.append("CREATE TABLE IF NOT EXISTS produto(");
		sqlProduto.append("_id integer PRIMARY KEY AUTOINCREMENT,");
		sqlProduto.append("nome varchar (100),");
		sqlProduto.append("preco double (10,2));");
		dbs.execSQL(sqlProduto.toString());
		
		/*db.execSQL("INSERT INTO produto (nome, preco) VALUES ('Coca-cola','2.50');");
		db.execSQL("INSERT INTO produto (nome, preco) VALUES ('Red Bull','4.50');");*/
		
		StringBuilder sqlVenda = new StringBuilder();
		sqlVenda.append("CREATE TABLE IF NOT EXISTS venda(");
		sqlVenda.append("_id integer PRIMARY KEY AUTOINCREMENT,");
		sqlVenda.append("produto integer,");
		sqlVenda.append("preco double (10,2),");
		sqlVenda.append("la double (10,9),");
		sqlVenda.append("lo double (10,9));");
		dbs.execSQL(sqlVenda.toString());
		
		dbs.close();
		
	}
	
	public void novaVendaClick(View v){
		startActivity(new Intent(getBaseContext(), NovaVendaActivity.class));
	}
	
	public void listarVendasClick(View v){
		startActivity(new Intent(getBaseContext(), ListarVendasActivity.class));
	}
	
	public void ReplicarClick(View v){
		//startService(new Intent("Iniciar_Replicacao"));
		//stopService(new Intent("Iniciar_Replicacao"));
		db = openOrCreateDatabase("vendas.db", Context.MODE_PRIVATE, null);
		
		cursor = db.rawQuery("SELECT * FROM venda", null);
		
		pgd = new ProgressDialog(MainActivity.this);
		pgd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pgd.setCancelable(true);
		pgd.setTitle("Replicando dados ...");
		pgd.setMax(cursor.getCount());
		pgd.show();
		new Thread(MainActivity.this).start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		TextView txtStatus = (TextView) findViewById(R.id.txtStatusConexao);
		
		ConnectivityManager con = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		
		if(con.getNetworkInfo(0).isConnected()){
			txtStatus.setText("Status da Conexão: 3G");
		}else if(con.getNetworkInfo(1).isConnected()){
			txtStatus.setText("Status da Conexão: wifi");
		}else{
			((Button)findViewById(R.id.btnReplicar)).setEnabled(false);
			txtStatus.setText("Status da Conexão: desconectado");
		}
	}
	
	@Override
	public void run() {
		
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
				
				String linhaRetorno = line.readLine();
				
				if(linhaRetorno.equals("y")){
					db.delete("venda", "_id=?", new String[]{String.valueOf(cursor.getInt(0))});
					totalReplicado ++;
					hl.sendEmptyMessage(0);
					Log.d("main", "ok1");
				}
			}catch(Exception e){
				error = e.getMessage();
				hl.sendEmptyMessage(2);
			}
		}
		
		db.close();
		
		if(totalDB == totalReplicado){
			hl.sendEmptyMessage(1);
		}else{
			error = "ocorreu algum erro no sistema";
			hl.sendEmptyMessage(2);
		}
		
	}
	
	public Handler hl = new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what == 0){
				pgd.setProgress(pgd.getProgress() + 1);
			}else if(msg.what == 1){
				pgd.dismiss();
				Toast.makeText(MainActivity.this, "Sucesso na replicação", Toast.LENGTH_LONG).show();
			}else if(msg.what == 2){
				pgd.dismiss();
				Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
			}
			
		}
	};

}
