package com.example.bomber;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	@Override
    public void onReceive(Context context, Intent intent) {
		// Especificar explicitamente que GcmIntentService debe manejar el intent
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        // Se inicia el servicio, manteniento el dispositovo despierto mientras se esta lanzando
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
