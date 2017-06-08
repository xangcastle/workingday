package com.valuarte.dtracking.Util;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;


import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.ElementosGraficos.Gestion;
import com.valuarte.dtracking.FormularioActivity;
import com.valuarte.dtracking.MainActivity;
import com.valuarte.dtracking.MensajeTextos.MensajeEnviadoIntent;
import com.valuarte.dtracking.Mensajes;
import com.valuarte.dtracking.R;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.rest.spring.annotations.RestService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Representa el sincronizador de gestiones eliminadas que se consultan al web service
 *
 * @version 1.0
 */
@EBean
public class SincronizadorGestionesEliminadas extends BroadcastReceiver implements MensajeEnviadoIntent.ListenerMensajeEnviado {
    /**
     * Contexto de la aplicacion
     */
    private Context context;
    /**
     * Acceso a la base de datos
     */
    private RecursosBaseDatos recursosBaseDatos;
    /**
     * Los mensajes que se van a enviar
     */
    private ArrayList<ContenidoMensaje> contenidoMensajes;
    /**
     * Indice en el que se lleva el sms a enviar
     */
    private int indiceSms;
    /**
     * Para saber si el mensaje fue enviado
     */
    private MensajeEnviadoIntent mensajeEnviadoIntent;

    @RestService
    RestClient restClient;
    @Bean
    MyRestErrorHandler myErrorhandler;

    @Override
    public void onReceive(Context context, Intent intent) {
        indiceSms=0;
        if (this.context == null) {
            this.context = context;
            recursosBaseDatos = new RecursosBaseDatos(this.context);
            ArrayList<ContenidoMensaje> c=recursosBaseDatos.getContenidoMensajes();
            getGestionesEliminadas();
            resetearReceiverSMS();
        }
        reiniciarSincronizacion();
    }

    /**
     * Resetea los receiver para saber si el mensaje fue entregado o no
     */
    private void resetearReceiverSMS() {
        if (mensajeEnviadoIntent == null) {
            mensajeEnviadoIntent = new MensajeEnviadoIntent(this);
        }

        try {
            context.getApplicationContext().unregisterReceiver(mensajeEnviadoIntent);
        } catch (IllegalArgumentException e) {
        }
        context.getApplicationContext().registerReceiver(mensajeEnviadoIntent, new IntentFilter(FormularioActivity.SENT));

    }

    private void getGestionesEliminadas() {
        String respuesta=restClient.mensajeria();
        if(respuesta!=null){
            Log.e("respuesta", respuesta);
            if (!respuesta.equals("[]")) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(respuesta);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    jsonArray = jsonObject.getJSONArray("gestiones_eliminadas");
                    jsonArray = jsonArray.getJSONArray(0);
                    String arreglo=jsonArray.toString();
                    arreglo=arreglo.replace('[','(');
                    arreglo=arreglo.replace(']',')');
                    int cantidad=recursosBaseDatos.getCantidadOcurrenciasGestiones(arreglo);
                    int estado=ContenidoMensaje.SINENVIAR;
                    if(cantidad==0)
                    {
                        estado=ContenidoMensaje.SOLOENVIAR;
                    }
                    else
                    {
                        recursosBaseDatos.eliminarGestiones(arreglo);
                        construirNotificacion(context, jsonObject.getString("mensaje_gps"));
                        if(cantidad==jsonArray.length())
                        {
                            estado=ContenidoMensaje.RECIBIDO;
                        }
                    }
                    ContenidoMensaje contenidoMensaje = new ContenidoMensaje(
                            jsonObject.getString("mensaje_gps"), jsonArray, estado,
                            jsonObject.getString("numero"), Gestion.generarFechaDesdeCalendar(Calendar.getInstance()), jsonObject.getString("codUsuario"));
                    recursosBaseDatos.guardarContenidoMensaje(contenidoMensaje);

                } catch (JSONException e) {
                    Log.e("error", e.getMessage());
                }

            }
            enviarContenidos();
        }
    }

    /**
     * Envia los contenidos de mensaje que estan sin enviar
     */
    @UiThread
    void enviarContenidos() {
        contenidoMensajes = recursosBaseDatos.getContenidoMensajePorEstado("("+ContenidoMensaje.SINENVIAR+","+ContenidoMensaje.SOLOENVIAR+")");
        Log.e("contenidos",Integer.toString(contenidoMensajes.size()));
        Intent intent = new Intent(FormularioActivity.SENT);
        PendingIntent sentPI = PendingIntent.getBroadcast(context.getApplicationContext(), 0,
                intent, 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context.getApplicationContext(), 0,
                new Intent(FormularioActivity.DELIVERED), 0);
        SincronizacionGestionSMS sincronizacionGestionSMS = new SincronizacionGestionSMS(sentPI, deliveredPI);
        for (ContenidoMensaje contenidoMensaje : contenidoMensajes) {
            enviarContenidoViaSMS(contenidoMensaje, sincronizacionGestionSMS);
        }

    }

    /**
     * Envia via sms el mensaje que se le envia por parametro
     *
     * @param contenidoMensaje         el contenido del mensaje a enviar
     * @param sincronizacionGestionSMS el sincronizador via sms
     */
    private void enviarContenidoViaSMS(ContenidoMensaje contenidoMensaje, SincronizacionGestionSMS sincronizacionGestionSMS) {
        JSONObject jsonObject = contenidoMensaje.construirJsonParaEnviar();
        String mensaje = "MEN" + EncodingJSON.convertirJSONParaEnviar(jsonObject.toString()) + "MEN";
        String numero = contenidoMensaje.getNumero();
        sincronizacionGestionSMS.enviarMensaje(mensaje, numero);
    }
    /**
     * Reinicia la sincronizacion de las gestiones eliminadas
     */
    private void reiniciarSincronizacion()
    {
        if(Build.VERSION.SDK_INT>=19) {
            Intent alarmIntent = new Intent(context, SincronizadorPosicionActual.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + MainActivity.INTERVALOGESTIONESELIMINADAS, pendingIntent);
        }
    }
    /**
     * En caso de que el mensaje si se haya podido enviar
     */
    @Override
    public void enMensajeEnviado() {
        indiceSms = indiceSms + 1;
        if (indiceSms == contenidoMensajes.size()) {
            context.unregisterReceiver(mensajeEnviadoIntent);
        }

        if (indiceSms <= contenidoMensajes.size()) {
            ContenidoMensaje c=contenidoMensajes.get(indiceSms-1);
            if(c.getEstado()==ContenidoMensaje.SOLOENVIAR)
            {
                recursosBaseDatos.actualizarEstadoContenidoMensaje(c.getId(), ContenidoMensaje.NoMOSTRAR);
            }
            else {
                recursosBaseDatos.actualizarEstadoContenidoMensaje(c.getId(), ContenidoMensaje.ENVIADO);
            }
            }
    }

    /**
     * En caso de que el mensaje no se haya podido enviar
     *
     * @param mensaje el mensaje de la razon
     */
    @Override
    public void enMensajeNoEnviado(String mensaje) {
        indiceSms = indiceSms + 1;
        if (indiceSms == contenidoMensajes.size()) {
            context.unregisterReceiver(mensajeEnviadoIntent);
        }
    }

    /**
     * Construir notificacion
     * @param context el contexto
     * @param mensaje el mensaje a mostrar
     */
    public static void construirNotificacion(Context context,String mensaje)
    {
        NotificationManager nm = (NotificationManager)context.getSystemService(Mensajes.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        Intent notificationIntent = new Intent(context, Mensajes.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,0,notificationIntent,0);

        //set
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.drawable.logo_mensaje);
        builder.setContentText(mensaje);
        builder.setContentTitle("Mensaje Banpro");
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);

        Notification notification = builder.build();
        nm.notify((int) System.currentTimeMillis(), notification);
    }
}
