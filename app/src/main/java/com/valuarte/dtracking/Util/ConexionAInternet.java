package com.valuarte.dtracking.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Comprueba si el dispositivo tiene conexión a internete o no
 * @author Jorge Galvis Cardenas
 * @version 1.0
 */
public class ConexionAInternet {
    /**
     * Determina si el movil se encuentra conecta a la red wifi
     * @param activity  activity desde la que se hace la comprobacion
     * @return true si esta conectado, false si no
     */
    public boolean conectadoWifi(Context activity){
        ConnectivityManager connectivity = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determina si el movil esta conectado a la red de datos
     * @param activity   activity desde dond se hace la validación
     * @return  true si esta conectado, false si no
     */
    public boolean conectadoRedDatos(Context activity){
        ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determina si el movil tiene conexión a internet
     * @param activity activity desde la que se hace la validación
     * @return true si esta conectado, false si no
     */
    public boolean estaConectado(Context activity)
    {
        return (conectadoWifi(activity) || conectadoRedDatos(activity));
    }
}
