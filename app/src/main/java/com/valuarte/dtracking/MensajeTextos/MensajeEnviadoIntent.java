package com.valuarte.dtracking.MensajeTextos;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

/**
 * Representa el escuchador para saber si el mensaje fue entregado a su destino o no
 * @version 1.0
 */
public class MensajeEnviadoIntent extends BroadcastReceiver{
    public final static String  ERROR="La gestion no pudo ser sincronizada, se sincronizara mas tarde";
    /**
     * Permite la comunicacion con los activities
     */
    private ListenerMensajeEnviado listenerMensajeEnviado;

    public MensajeEnviadoIntent(ListenerMensajeEnviado listenerMensajeEnviado) {
        this.listenerMensajeEnviado = listenerMensajeEnviado;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode())
        {

            case Activity.RESULT_OK:
                listenerMensajeEnviado.enMensajeEnviado();
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                listenerMensajeEnviado.enMensajeNoEnviado(ERROR);
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                listenerMensajeEnviado.enMensajeNoEnviado(ERROR);
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                listenerMensajeEnviado.enMensajeNoEnviado(ERROR);
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                listenerMensajeEnviado.enMensajeNoEnviado(ERROR);
                break;
            default:
                listenerMensajeEnviado.enMensajeNoEnviado(ERROR);
                break;
        }
    }

    /**
     * Escucha eventos cuando un mensaje se envia
     */
    public interface ListenerMensajeEnviado
    {
        /**
         * En caso de que el mensaje si se haya podido enviar
         */
        void enMensajeEnviado();

        /**
         * En caso de que el mensaje no se haya podido enviar
         * @param mensaje    el mensaje de la razon
         */
        void enMensajeNoEnviado(String mensaje);
    }
}
