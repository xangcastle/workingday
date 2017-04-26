package com.valuarte.dtracking.Util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.ElementosGraficos.Gestion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Representa el sincronizador de la posicion actual al servidor
 *
 * @version 1.0
 */
public class SincronizadorPosicionActual extends BroadcastReceiver implements LocationListener {
    /**
     * Contexto de la aplicacion
     */
    private Context context;
    /**
     * Manejador de la posicion actual
     */
    private LocationManager locationManager;
    /**
     * Acceso a la base de datos
     */
    private RecursosBaseDatos recursosBaseDatos;
    /**
     * Determina si se btiene una conexion a internet o no
     */
    private ConexionAInternet conexionAInternet;
    /**
     * El usuario en sesion
     */
    private Usuario usuario;
    @Override
    public void onReceive(Context context, Intent intent) {
        recursosBaseDatos = new RecursosBaseDatos(context);
        usuario = recursosBaseDatos.getUsuario();
        try {
            Log.e("GPSSSS", "GPSSSSS");
            this.context = context;
            conexionAInternet = new ConexionAInternet();
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1.0f, this);
            double latitudActual;
            double longitudActual;
            JSONObject jsonObject;
            //obtenemos la posicion actual
            if (tieneGpsActivado()) {
                Log.e("activado", "activado");
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
                    if (usuario != null) {
                        latitudActual = location.getLatitude();
                        longitudActual = location.getLongitude();
                        jsonObject = new JSONObject();
                        try {
                            jsonObject.put("user", usuario.getId());
                            jsonObject.put("latitude", latitudActual);
                            jsonObject.put("longitude", longitudActual);
                            jsonObject.put("fecha", Gestion.generarFechaDesdeCalendar(Calendar.getInstance()));
                            sincronizarPosicionActual(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(context, "El usuario es nulo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "La localizacion esta nula primero", Toast.LENGTH_SHORT).show();
                    //reiniciarServicio(context, usuario);
                }
            } else {
                turnGPSOn();
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    if (usuario != null) {
                        latitudActual = location.getLatitude();
                        longitudActual = location.getLongitude();
                        jsonObject = new JSONObject();
                        try {
                            jsonObject.put("user", usuario.getId());
                            jsonObject.put("latitude", latitudActual);
                            jsonObject.put("longitude", longitudActual);
                            jsonObject.put("fecha", Gestion.generarFechaDesdeCalendar(Calendar.getInstance()));
                            sincronizarPosicionActual(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(context, "El usuario es nulo", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(context, "La localizacion esta nula segundo", Toast.LENGTH_SHORT).show();
                   // reiniciarServicio(context,usuario);
                }

            }
        }catch (Exception e){Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();}
reiniciarSincronizacion(usuario);
    }

    /**
     * Reinicia la sincronizacion de la posicion actual
     */
    private void reiniciarSincronizacion(Usuario usuario) {
        if (Build.VERSION.SDK_INT >= 19) {
            Intent alarmIntent = new Intent(context, SincronizadorPosicionActual.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pendingIntent);
            alarmIntent = new Intent(context, SincronizadorPosicionActual.class);
            pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + usuario.getIntervaloSincroinizacionGPS(), pendingIntent);
        }
    }
    /**
     * Sincroniza la posicion que se le manda en el json al servidro
     * @param requestQueue cola de peticiones al servidor
     * @param jsonObject objketo json con la latitud, longitud, id del usuario y fecha
     */
    public static void sincronizarPosicionViaWeb(RequestQueue requestQueue, final JSONObject jsonObject,final Context context)
    {
        String url="http://www.deltacopiers.com/dtracking/movil/seguimiento_gps/";
        /**
         * solicitud post al servidor
         */
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    /**
                     * obtiene la respuesta que envia el servior
                     *
                     * @param response
                     */
                    @Override
                    public void onResponse(String response) {
                       Log.e("respuesta", response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("user",Integer.toString(jsonObject.getInt("user")));
                    params.put("latitude",Double.toString(jsonObject.getDouble("latitude")));
                    params.put("longitude",Double.toString(jsonObject.getDouble("longitude")));
                    params.put("fecha", jsonObject.getString("fecha"));
                } catch (JSONException e) {
                    params = new HashMap<>();
                }
                return params;
            }
        };
        requestQueue.add(postRequest);
    }
    /**
     * Indica si se puede sincronizar por el medio que se le manda
     *
     * @param medio el medio a evaluar
     * @return false si se puede sincronizar, false si no
     */
    private boolean sePuedeSincronizarPorEsteMedio(String medio) {
        medio = medio.trim();
        switch (medio) {
            case "SMS":
                if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                    return true;
                } else {
                    return false;
                }
            case "WIFI":
                return conexionAInternet.conectadoWifi(context);
            case "3G":
                return conexionAInternet.conectadoRedDatos(context);
        }
        return false;
    }
    /**
     * Sincroniza la posicion al servidor, ya sea por sms, 3g o wifi
     *
     * @param jsonObject objeto json que contiene la información a enviar
     */
    private void sincronizarPosicionActual(JSONObject jsonObject) {
     //   Toast.makeText(context,"Iniciando sincronizacion posicion",Toast.LENGTH_SHORT).show();
        RequestQueue requestQueue=Volley.newRequestQueue(context);
        String[] conexiones = usuario.getConexionesServidor().split("\\+");
        //String[] conexiones={"SMS"};
        if (conexiones.length == 1) {
            if (sePuedeSincronizarPorEsteMedio(conexiones[0])) {
                if (conexiones[0].trim().equals("SMS")) {
                   sincronizarPoscionViaSms(jsonObject,usuario.getNumero());
                } else {
                    sincronizarPosicionViaWeb(requestQueue, jsonObject,context);
                }
            }
        }
        if (conexiones.length == 2) {
            if (conexiones[0].trim().equals("3G") || conexiones[0].trim().equals("WIFI")) {
                if (sePuedeSincronizarPorEsteMedio(conexiones[0])) {
                    sincronizarPosicionViaWeb(requestQueue, jsonObject,context);
                } else {
                    if (sePuedeSincronizarPorEsteMedio(conexiones[1])) {
                        sincronizarPoscionViaSms(jsonObject, usuario.getNumero());
                    }
                }
            } else {
                if (conexiones[1].trim().equals("3G") || conexiones[1].trim().equals("WIFI")) {
                    if (sePuedeSincronizarPorEsteMedio(conexiones[1])) {
                        sincronizarPosicionViaWeb(requestQueue, jsonObject,context);
                    }
                    else {
                        if (sePuedeSincronizarPorEsteMedio(conexiones[0])) {
                            sincronizarPoscionViaSms(jsonObject,usuario.getNumero());
                        }
                    }
                }
            }
        }
    }

    /**
     * Sincroniza la posicion actual via SMS
     * @param jsonObject  el objeto json que es el emnsaje a enviar
     * @param numero el numero al que se le va a enviar la infromacion
     */
    private void sincronizarPoscionViaSms(JSONObject jsonObject,String numero)
    {
       // Toast.makeText(context,"Enviando ubicacion via sms",Toast.LENGTH_SHORT).show();
        String mensaje=jsonObject.toString();
        mensaje="POS"+EncodingJSON.convertirJSONParaEnviar(mensaje)+"POS";
        SincronizacionGestionSMS sincronizacionGestionSMS=new SincronizacionGestionSMS(null,null,numero,mensaje);
        sincronizacionGestionSMS.enviarMensaje();
    }
    /**
     * Determina si el gps esta activado o no
     *
     * @return true si esta activado, false si no
     */
    private boolean tieneGpsActivado() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void turnGPSOn() {
        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        Toast.makeText(context,"Por favor habilite el GPS",Toast.LENGTH_LONG).show();

    }

}
