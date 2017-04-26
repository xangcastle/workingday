package com.valuarte.dtracking.MensajeTextos;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Representado un escuchador para saber si el mensaje se pudo enviar
 *
 * @version 1.0
 */
public class MensajeEntregadoIntent extends BroadcastReceiver {
    /**
     * Permite la comunicacion con los activities
     */
    private ListenerMensajeEntregado listenerMensajeEntregado;

    public MensajeEntregadoIntent(ListenerMensajeEntregado listenerMensajeEntregado) {
        this.listenerMensajeEntregado = listenerMensajeEntregado;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        context.unregisterReceiver(this);
    Log.e("se llamo","se llamo");
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Log.e("mensaje", "mensaje entregado");
               // listenerMensajeEntregado.enMensajeEntregado();
                break;
            case Activity.RESULT_CANCELED:
                Log.e("No","No entregado");
                break;
        }


    }

    /**
     * Escuchador eventos cuando el mensaje llega
     */
    public interface ListenerMensajeEntregado {
        /**
         * En caso de que el mensaje si se haya entregado
         */
        void enMensajeEntregado();

        /**
         * En caso de que el mensaje no se haya entregado
         */
        void enMensajeNoEntregado();
    }
}
