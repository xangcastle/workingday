package com.valuarte.dtracking.Util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.text.Html;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.ElementosGraficos.Contenedor;
import com.valuarte.dtracking.ElementosGraficos.Gestion;
import com.valuarte.dtracking.ElementosGraficos.Vista;
import com.valuarte.dtracking.Excepciones.NoSoportaValorException;
import com.valuarte.dtracking.Excepciones.ValorRequeridoException;
import com.valuarte.dtracking.FormularioActivity;
import com.valuarte.dtracking.MainActivity;
import com.valuarte.dtracking.MensajeTextos.MensajeEntregadoIntent;
import com.valuarte.dtracking.MensajeTextos.MensajeEnviadoIntent;
import com.valuarte.dtracking.MensajeTextos.Sms;
import com.valuarte.dtracking.MensajeTextos.SmsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Sincroniza en background las gestiones
 *
 * @version 1.0
 */
public class SincronizadorBackground extends BroadcastReceiver implements MensajeEnviadoIntent.ListenerMensajeEnviado
        , MensajeEntregadoIntent.ListenerMensajeEntregado, SincronizacionGestionWeb.ListenerSincronizacionWeb {
    /**
     * Acceso a la base de datos
     */
    private RecursosBaseDatos recursosBaseDatos;
    /**
     * Son las gestiones que se van a sincronizar
     */
    private ArrayList<Gestion> gestiones;
    /**
     * contexto de la aplicacion
     */
    private Context context;
    /**
     * El usuario asociado a la pp
     */
    private Usuario usuario;
    /**
     * Sirve para saber si el mensaje fue entregado
     */
    private MensajeEntregadoIntent mensajeEntregadoIntent;
    /**
     * Para saber si el mensaje fue entregado
     */
    private MensajeEnviadoIntent mensajeEnviadoIntent;
    /**
     * Conexion a internet
     */
    private ConexionAInternet conexionAInternet;
    /**
     * Indice que indica que gestion fue enviada por sms
     */
    private int indiceSms;
    public static final String ACTION_SMS_SENT = "com.mycompany.myapp.SMS_SENT";
    public static final String ACTION_SMS_DELIVERED = "com.mycompany.myapp.SMS_DELIVERED";

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        recursosBaseDatos = new RecursosBaseDatos(context);
        gestiones = recursosBaseDatos.getGestionesPendientes();
        usuario = recursosBaseDatos.getUsuario();
        conexionAInternet = new ConexionAInternet();
        indiceSms = 0;
        resetearReceiverSMS();
        iniciarSincronizacion();
        reiniciarSincronizacion();
    }

    /**
     * Resetea los receiver para saber si el mensaje fue entregado o no
     */
    private void resetearReceiverSMS() {
        if (mensajeEnviadoIntent == null) {
            mensajeEnviadoIntent = new MensajeEnviadoIntent(this);
            mensajeEntregadoIntent = new MensajeEntregadoIntent(this);
        }

        try {
            context.getApplicationContext().unregisterReceiver(mensajeEntregadoIntent);
            context.getApplicationContext().unregisterReceiver(mensajeEnviadoIntent);
        } catch (IllegalArgumentException e) {
        }
        context.getApplicationContext().registerReceiver(mensajeEnviadoIntent, new IntentFilter(FormularioActivity.SENT));
        context.getApplicationContext().registerReceiver(mensajeEntregadoIntent, new IntentFilter(FormularioActivity.DELIVERED));

    }

    /**
     * Inicia la sincronizacion al servidor o al movil asociado via sms
     */
    private void iniciarSincronizacion() {
        JSONObject jsonObject;
        for (Gestion gestion : gestiones) {
            try {
                jsonObject = obtenerCampos(gestion);
                sincronizarGestion(jsonObject, gestion);
            } catch (ValorRequeridoException e) {
                continue;
            }
        }
        iniciarSincronizacionSmsFaltantes();
    }

    /**
     * Inicia la sincronizacion de los mensajes de texto que se recibieron y no pudieron ser
     * enviados al servidor
     */
    private void iniciarSincronizacionSmsFaltantes() {
        ArrayList<Sms> smses=recursosBaseDatos.getMensajesPendientes();
        SmsListener smsListener;
        for(Sms sms:smses)
        {
            smsListener=new SmsListener();
            smsListener.enviarCuerpoFormularioAServidor(sms,context,recursosBaseDatos);
        }
    }

    /**
     * Obtiene formato json de la gestion
     *
     * @param gestion la gestion a convertir
     * @return el json de la gestion
     * @throws ValorRequeridoException en caso de que contenga un valor sin completar
     */
    private JSONObject obtenerCampos(Gestion gestion) throws ValorRequeridoException {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1;
        try {
            jsonObject.put("gestion", gestion.getIdgestion());
            jsonObject.put("usuario", usuario.getId());
            jsonObject.put("latitud", gestion.getLatitud());
            jsonObject.put("longitud", gestion.getLongitud());
            jsonObject.put("fecha", gestion.getFecha());
            jsonObject1 = new JSONObject();
            ArrayList<Vista> vistas = new ArrayList<>();
            Object object;
            for (Contenedor c : gestion.getFormulario().getContenedores()) {
                vistas = c.getVistas();
                for (Vista v : vistas) {
                    try {
                        object = v.getValor();
                        if (object != null) {
                            jsonObject1.put(v.getNombreVariable(), object);
                        }
                    } catch (NoSoportaValorException e) {
                        continue;
                    }
                }
            }
            jsonObject.put("campos", jsonObject1);
        } catch (JSONException ex) {

        }
        return jsonObject;
    }

    /**
     * Sincroniza la gestion al servidor, ya sea por sms, 3g o wifi
     *
     * @param jsonObject objeto json que contiene la información a enviar
     */
    private void sincronizarGestion(JSONObject jsonObject, Gestion gestion) {
        String[] conexiones = usuario.getConexionesServidor().split("\\+");
        if (conexiones.length == 1) {
            if (sePuedeSincronizarPorEsteMedio(conexiones[0])) {
                if (conexiones[0].trim().equals("SMS")) {
                    sincronizarViaSMS(jsonObject, gestion);
                } else {
                    sincronizarViaIntenet(jsonObject, gestion);
                }
            }
        }
        if (conexiones.length == 2) {
            if (conexiones[0].trim().equals("3G") || conexiones[0].trim().equals("WIFI")) {
                if (sePuedeSincronizarPorEsteMedio(conexiones[0])) {
                    sincronizarViaIntenet(jsonObject, gestion);
                } else if (sePuedeSincronizarPorEsteMedio(conexiones[1])) {
                        sincronizarViaIntenet(jsonObject, gestion);
                }else
                    sincronizarViaSMS(jsonObject, gestion);
            } else {
                if (conexiones[1].trim().equals("3G") || conexiones[1].trim().equals("WIFI")) {
                    if (sePuedeSincronizarPorEsteMedio(conexiones[1])) {
                        sincronizarViaIntenet(jsonObject, gestion);
                    }
                    else {
                        if (sePuedeSincronizarPorEsteMedio(conexiones[0])) {
                            sincronizarViaSMS(jsonObject, gestion);
                        }
                    }
                }
            }
        }
    }

    /**
     * Sincroniza la gestion via sms
     *
     * @param jsonObject objeto json que contiene la infromacion para enviar via sms
     */
    private void sincronizarViaSMS(JSONObject jsonObject, Gestion gestion) {
        if (gestion.getEstadoGestion() == Gestion.SINENVIAR) {
            String mensaje = "INICIO" + EncodingJSON.convertirJSONParaEnviar(jsonObject.toString()) + "FIN";
            String numero = usuario.getNumero();
            enviarMensaje(mensaje, numero);
        }
    }
    /**
     * Reinicia la sincronizacion en background
     */
    private void reiniciarSincronizacion()
    {
        if(Build.VERSION.SDK_INT>=19) {
            Intent alarmIntent = new Intent(context, SincronizadorBackground.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + MainActivity.INTERVALOTIEMPOSINCRONIZACION, pendingIntent);
        }
    }
    /**
     * El formulario fue enviado
     * Sincroniza la gestion via internet, ya seaq conred 3g o wifi
     *
     * @param jsonObject objeto json que contiene la infromacion para enviar via sms
     */
    private void sincronizarViaIntenet(JSONObject jsonObject, Gestion gestion) {
        SincronizacionGestionWeb sincronizacionGestionWeb = SincronizacionGestionWeb_.getInstance_(context);
        sincronizacionGestionWeb.gestion=gestion;
        sincronizacionGestionWeb.jsonObject=jsonObject;
        sincronizacionGestionWeb.listenerSincronizacionWeb=this;
        sincronizacionGestionWeb.idUser=usuario.getId();
        sincronizacionGestionWeb.sincronizar();
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
     * En caso de que el mensaje si se haya entregado
     */
    @Override
    public void enMensajeEntregado() {
        //TODO
    }

    /**
     * En caso de que el mensaje no se haya entregado
     */
    @Override
    public void enMensajeNoEntregado() {
        //TODO
    }

    /**
     * En caso de que el mensaje si se haya podido enviar
     */
    @Override
    public void enMensajeEnviado() {
        indiceSms = indiceSms + 1;
        if (gestiones.size() > 0) {
            recursosBaseDatos.actualizarEstadoGestion(gestiones.get(indiceSms - 1).getIdgestion(), Gestion.SINENVIARIMAGENES);
        }
        if (indiceSms == gestiones.size()) {
            context.unregisterReceiver(mensajeEnviadoIntent);
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
        if (gestiones.size() > 0) {
            recursosBaseDatos.actualizarEstadoGestion(gestiones.get(indiceSms - 1).getIdgestion(), Gestion.SINENVIAR);
        }
        if (indiceSms == gestiones.size()) {
            context.unregisterReceiver(mensajeEnviadoIntent);
        }
    }

    /**
     * En caso de que la sincronización haya sido completada
     *
     * @param mensaje
     */
    @Override
    public void enSincronizacionCompletada(String mensaje, int idGestion) {
        recursosBaseDatos.actualizarEstadoGestion(idGestion, Gestion.ENVIADO);
    }

    /**
     * En caso de que la sincronización fuera sido fallida
     *
     * @param mensaje
     */
    @Override
    public void enSincronizacionFallida(String mensaje, int idGestion) {
        recursosBaseDatos.actualizarEstadoGestion(idGestion, Gestion.SINENVIAR);
    }

    public void enviarMensaje(String mensaje, String numero) {
        SmsManager sms = SmsManager.getDefault();
        try {
            mensaje = Html.fromHtml(new String(mensaje.getBytes("UTF-8"))).toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final ArrayList<String> parts = sms.divideMessage(mensaje);
        final int ct = parts.size();

        final ArrayList<PendingIntent> sentPis = new ArrayList<PendingIntent>(ct);
        final ArrayList<PendingIntent> delPis = new ArrayList<PendingIntent>(ct);

        for (int i = 0; i < ct; i++) {


            sentPis.add(null);
            delPis.add(null);
        }
        Intent intent = new Intent(FormularioActivity.SENT);
        PendingIntent sentPI = PendingIntent.getBroadcast(context.getApplicationContext(), 0,
                intent, 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context.getApplicationContext(), 0,
                new Intent(FormularioActivity.DELIVERED), 0);
        sentPis.remove(sentPis.size() - 1);
        sentPis.add(sentPI);
        delPis.remove(delPis.size() - 1);
        delPis.add(deliveredPI);

        sms.sendMultipartTextMessage(numero, null, parts, sentPis, delPis);
    }
}
