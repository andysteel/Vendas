package com.gmail.andersoninfonet.vendas;

import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class ExemploBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
			Bundle bundle = intent.getExtras();
			
			if(bundle != null){
				Object[] pdus = (Object[]) bundle.get("pdus");
				
				final SmsMessage[] messages = new SmsMessage[pdus.length];
				
				for(int i=0; i < pdus.length; i++){
					messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}
				
				if(messages.length > -1){
					if(messages[0].getMessageBody().equals("replicar")){
						Toast.makeText(context, "replicacao iniciada", Toast.LENGTH_LONG).show();
						Intent it = new Intent("Iniciar_Replicacao");
						context.startService(it);
						context.stopService(it);
						
					}
					
				}
			}
		}
		
	}
}
