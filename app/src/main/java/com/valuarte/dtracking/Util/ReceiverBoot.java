package com.valuarte.dtracking.Util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.MainActivity;

/**
 * Reinicia la sincronizacion en background, cuando el dispositivo es reinicado
 */
public class ReceiverBoot extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345465;

    @Override
    public void onReceive(Context context, Intent intent) {
        RecursosBaseDatos recursosBaseDatos = new RecursosBaseDatos(context);
        Usuario usuario = recursosBaseDatos.getUsuario();
        Intent alarmIntent;
        PendingIntent pendingIntent;
        AlarmManager manager;
        if (usuario != null) {
            if (Build.VERSION.SDK_INT < 19) {
                alarmIntent = new Intent(context, SincronizadorBackground.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                manager.cancel(pendingIntent);
                alarmIntent = new Intent(context, SincronizadorBackground.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), MainActivity.INTERVALOTIEMPOSINCRONIZACION, pendingIntent);
                alarmIntent = new Intent(context, SincronizadorPosicionActual.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                manager.cancel(pendingIntent);
                alarmIntent = new Intent(context, SincronizadorPosicionActual.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), usuario.getIntervaloSincroinizacionGPS(), pendingIntent);
                alarmIntent = new Intent(context, SincronizadorGestionesEliminadas_.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                manager.cancel(pendingIntent);
                alarmIntent = new Intent(context, SincronizadorGestionesEliminadas_.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), MainActivity.INTERVALOGESTIONESELIMINADAS, pendingIntent);
            } else {
                alarmIntent = new Intent(context, SincronizadorBackground.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                 manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                manager.cancel(pendingIntent);
                alarmIntent = new Intent(context, SincronizadorBackground.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
                alarmIntent = new Intent(context, SincronizadorPosicionActual.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                manager.cancel(pendingIntent);
                alarmIntent = new Intent(context, SincronizadorPosicionActual.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),pendingIntent);
                alarmIntent = new Intent(context, SincronizadorGestionesEliminadas_.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                manager.cancel(pendingIntent);
                alarmIntent = new Intent(context, SincronizadorGestionesEliminadas_.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
            }
        }
    }
}
