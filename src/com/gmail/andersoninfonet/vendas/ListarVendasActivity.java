package com.gmail.andersoninfonet.vendas;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ListarVendasActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listar_vendas);
		
		ListView lvwVendas = (ListView) findViewById(R.id.lvwVendas);
		
		lvwVendas.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView ad, View v, int position,
					long id) {
				SQLiteCursor c = (SQLiteCursor) ad.getAdapter().getItem(position);
				Intent it = new Intent(getBaseContext(), MapShowActivity.class);
				it.putExtra("latitude", c.getDouble(c.getColumnIndex("la")));
				it.putExtra("longitude", c.getDouble(c.getColumnIndex("lo")));
				startActivity(it);
			}
		
		});
		
		SQLiteDatabase db = openOrCreateDatabase("vendas.db", Context.MODE_PRIVATE, null);
		
		Cursor cursor = db.rawQuery("SELECT venda._id,venda.preco,venda.la,venda.lo,produto.nome FROM venda INNER JOIN produto on produto._id = venda.produto;", null);
		
		String[] from = {"_id","nome","preco","la","lo"};
		int[] to = {R.id.txvID, R.id.txvNome, R.id.txvPreco,R.id.txvLa,R.id.txvLo};
		
		
		SimpleCursorAdapter ad = new SimpleCursorAdapter(getBaseContext(),R.layout.model_listar, cursor,from,to);
		
		lvwVendas.setAdapter(ad);
		
		db.close();
	}
}
