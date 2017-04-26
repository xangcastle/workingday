package com.valuarte.dtracking.MensajeTextos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.ElementosGraficos.Gestion;
import com.valuarte.dtracking.Util.ConexionAInternet;
import com.valuarte.dtracking.Util.ContenidoMensaje;
import com.valuarte.dtracking.Util.EncodingJSON;
import com.valuarte.dtracking.Util.SincronizacionGestionWeb;
import com.valuarte.dtracking.Util.SincronizadorGestionesEliminadas;
import com.valuarte.dtracking.Util.SincronizadorPosicionActual;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SmsListener extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from = "";
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    String msgBody = "";
                    for (int i = 0; i < msgs.length; i++) {
                        String format = bundle.getString("format");
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                        msg_from = msgs[i].getOriginatingAddress();
                        msgBody = msgBody + msgs[i].getMessageBody();
                    }
                    JSONObject jsonObject = decodificarMensajeGestion(msgBody);
                    ConexionAInternet conexionAInternet = new ConexionAInternet();
                    if (jsonObject != null) {
                        RecursosBaseDatos recursosBaseDatos = new RecursosBaseDatos(context);
                        Sms sms = new Sms(Gestion.generarFechaDesdeCalendar(Calendar.getInstance()), jsonObject,
                                false, msg_from);
                        int id = recursosBaseDatos.guardarSms(sms);
                        sms.setId(id);
                        if (conexionAInternet.estaConectado(context)) {
                            enviarCuerpoFormularioAServidor(sms, context, recursosBaseDatos);
                        }
                    } else {

                        jsonObject = decodificarMensajePosicion(msgBody);
                        if (jsonObject != null) {
                            if (conexionAInternet.estaConectado(context)) {
                                SincronizadorPosicionActual.sincronizarPosicionViaWeb(Volley.newRequestQueue(context), jsonObject,context);
                            }
                        } else {
                            jsonObject = decodificarMensajeContenidoMensaje(msgBody);
                            if (jsonObject != null) {
                                ContenidoMensaje contenidoMensaje = new ContenidoMensaje(jsonObject.getString("m"), jsonObject.getJSONArray("g"),
                                        ContenidoMensaje.RECIBIDO, "", Gestion.generarFechaDesdeCalendar(Calendar.getInstance()), jsonObject.getString("c"));
                                RecursosBaseDatos recursosBaseDatos = new RecursosBaseDatos(context);
                                recursosBaseDatos.guardarContenidoMensaje(contenidoMensaje);
                                String arreglo=contenidoMensaje.getGestionesElimninadas().toString();
                                arreglo=arreglo.replace('[','(');
                                arreglo=arreglo.replace(']',')');
                                recursosBaseDatos.eliminarGestiones(arreglo);
                                SincronizadorGestionesEliminadas.construirNotificacion(context,contenidoMensaje.getMensaje());
                            }
                        }

                    }

                } catch (Exception e) {
                    Log.e("excepcion", e.getMessage());
                }
            }
        }
    }

    /**
     * Decodifica el mensaje actual, y obtiene el json como representacion de una gestion
     *
     * @param mensaje el mesnaje a decodificar en forma de gestion
     * @return objeto json
     */
    private JSONObject decodificarMensajeGestion(String mensaje) {
        JSONObject jsonObject = null;
        if (mensaje.length() > 10) {
            String inicio = mensaje.substring(0, 6);
            String fin = mensaje.substring(mensaje.length() - 3);
            if (inicio.equals("INICIO") && fin.equals("FIN")) {
                String cuerpo = mensaje.substring(6, mensaje.length() - 3);
                try {
                    cuerpo = EncodingJSON.convertirJSONRecibido(cuerpo);
                    jsonObject = new JSONObject(cuerpo);
                } catch (JSONException e) {
                    jsonObject = null;
                }
            }
        }

        return jsonObject;
    }

    /**
     * Decodifica el mensaje actual, y obtiene el json en forma de una posicion
     *
     * @param mensaje el mesnaje a decodificar en forma de posicion
     * @return objeto json
     */
    private JSONObject decodificarMensajePosicion(String mensaje) {
        JSONObject jsonObject = null;
        mensaje=mensaje.trim();
        if (mensaje.length() > 10) {
            String inicio = mensaje.substring(0, 3);
            String fin = mensaje.substring(mensaje.length() - 3);
            if (inicio.equals("POS") && fin.equals("POS")) {
                String cuerpo = mensaje.substring(3, mensaje.length() - 3);

                    try {
                        cuerpo = EncodingJSON.convertirJSONRecibido(cuerpo);
                        jsonObject = new JSONObject(cuerpo);
                    } catch (JSONException e) {
                        jsonObject = null;
                    }


            }
        }

        return jsonObject;
    }

    /**
     * Decodifica el mensaje actual, y obtiene el json en forma de un contenido mensaje
     *
     * @param mensaje el mesnaje a decodificar en forma de contenido mensaje
     * @return objeto json
     */
    private JSONObject decodificarMensajeContenidoMensaje(String mensaje) {
        JSONObject jsonObject = null;
        if (mensaje.length() > 10) {
            String inicio = mensaje.substring(0, 3);
            String fin = mensaje.substring(mensaje.length() - 3);
            if (inicio.equals("MEN") && fin.equals("MEN")) {
                String cuerpo = mensaje.substring(3, mensaje.length() - 3);
                try {
                    cuerpo = EncodingJSON.convertirJSONRecibido(cuerpo);
                    jsonObject = new JSONObject(cuerpo);
                } catch (JSONException e) {
                    jsonObject = null;
                }
            }
        }

        return jsonObject;
    }

    /**
     * Envia solamente los campos de texto al servidor
     *
     * @param sms el objeto sms que se va a enviar
     */
    public void enviarCuerpoFormularioAServidor(final Sms sms, Context context, final RecursosBaseDatos recursosBaseDatos) {
        final JSONObject jsonObject = sms.getJsonObject();
        /**
         * solicitud post al servidor
         */
        StringRequest postRequest = new StringRequest(Request.Method.POST, SincronizacionGestionWeb.URL,
                new Response.Listener<String>() {
                    /**
                     * obtiene la respuesta que envia el servior
                     *
                     * @param response
                     */
                    @Override
                    public void onResponse(String response) {
                        recursosBaseDatos.actualizarEstadoSMS(sms.getId(), true);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        recursosBaseDatos.actualizarEstadoSMS(sms.getId(), false);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("gestion", Integer.toString(jsonObject.getInt("gestion")));

                    params.put("latitude", Double.toString(jsonObject.getDouble("latitud")));
                    params.put("longitude", Double.toString(jsonObject.getDouble("longitud")));
                    params.put("fecha", jsonObject.getString("fecha"));
                    String json = jsonObject.getJSONObject("campos").toString();
                    json = json.replace("\"", "&");
                    json = json.replace("&", "'");
                    params.put("json", json);

                    params.put("user", Integer.toString(jsonObject.getInt("usuario")));
                } catch (JSONException e) {
                    params = new HashMap<>();
                }
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(postRequest);
    }
}