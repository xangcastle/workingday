package com.valuarte.dtracking.Util;

import android.app.PendingIntent;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Sincroniza la gestion via sms
 * @version 1.0
 */
public class SincronizacionGestionSMS {
    /**
     * Intent que se encarga de escuchar eventos cuando el mensaje ha sido enviado
     */
    private PendingIntent sentPI;
    /**
     * Intent que se encarga de escuchar eventos cuando el mensaje ha llegado al otro dispositivo
     */
    private PendingIntent deliveredPI;
    /**
     * Numero al que va dirigido el mensaje
     */
    private String numero;
    /**
     * Mensaje que se va a enviar
     */
    private String mensaje;

    public SincronizacionGestionSMS(PendingIntent sentPI, PendingIntent deliveredPI, String numero, String mensaje) {
        this.sentPI = sentPI;
        this.deliveredPI = deliveredPI;
        this.numero = numero;
        this.mensaje = mensaje;
    }

    public SincronizacionGestionSMS(PendingIntent sentPI, PendingIntent deliveredPI) {
        this.sentPI = sentPI;
        this.deliveredPI = deliveredPI;
    }

    /**
     * Envia el mensaje con los datos que tiene el objeto actual
     */
    public void enviarMensaje()
    {
        enviarMensaje(mensaje,numero);
    }
    /**
     * Envia el mensaje con los datos que se le envian por parámetro
     * @
     */
    public void enviarMensaje(String mensaje,String numero){
        //SmsManager sms = SmsManager.getDefault();
        Log.e("enviando","enviando");
        //  sms.sendTextMessage(numero, null, mensaje, sentPI, deliveredPI);
        SmsManager sms = SmsManager.getDefault();
        try {
            mensaje= Html.fromHtml(new String(mensaje.getBytes("UTF-8"))).toString();
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
        sentPis.remove(sentPis.size() - 1);
        sentPis.add(sentPI);
        delPis.remove(delPis.size() - 1);
        delPis.add(deliveredPI);

        sms.sendMultipartTextMessage(numero, null, parts, sentPis, delPis);
    }
}
