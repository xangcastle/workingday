package com.valuarte.dtracking.Util;

import android.content.Context;
import android.os.AsyncTask;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import android.util.Log;

/**
 * Se encarga de la sincronizacion de imagenes al servidor
 * @version 1.0
 */
public class SincronizacionImagenes extends AsyncTask<Void, Void, Integer> {
    /**
     * Constante que la sincronización va tomar como codigo resultado, cuando la sincronizacion se crea
     */
    public static final int SINCRONIZACIONCREADA=1;
    /**
     * Constante que la sincronización va tomar como codigo resultado, cuando la sincronización esta en progreso
     */
    public static final int ENPROGRESO=2;
    /**
     * Constante que la sincronización va tomar como codigo resultado, cuando la imagen ya se subio
     */
    public static final int IMAGENSUBIDA=3;
    /**
     * Constante que la sincronización va tomar como codigo resultado, cuando ocurre un error en la
     * sincronización
     */
    public static final int ERROR=4;
    /**
     * Ruta web a la que se va a realizar la sincronización
     */
    public static final String UPLOAD_URL = "http://www.deltacopiers.com/dtracking/movil/cargar_media/";
    /**
     * Nombre del campo del identificador de la gestion en el servicio web
     */
    public static final String GESTION="gestion";
    /**
     * Nombre del campo de la variable en el web service
     */
    public static final String VARIABLE="variable";
    /**
     * Nombre del campo de la variable imagen en el web service
     */
    public static final String IMAGEN="imagen";
    /**
     * Identificador de la gestion, al cual estan asociados las imagenes
     */
    private int idGestion;
    /**
     * Nombre de la variable
     */
    private String variable;
    /**
     * Ruta de la imagen a sincronizar
     */
    private String rutaImagen;
    /**
     * Codigo del resultado que arrojo la sincronizacion
     */
    private int codigoResultado;
    /**
     * Escucha eventos cuando finaliza la sincronizacion
     */
    private ListenerSincronizacionImagenes listenerSincronizacionImagenes;
    /**
     * Titulo del campo
     */
    private String titulo;

    private Context context;

    public SincronizacionImagenes(int idGestion, String variable, String rutaImagen,
                                  String titulo, ListenerSincronizacionImagenes listenerSincronizacionImagenes,
                                  Context context) {
        this.idGestion = idGestion;
        this.variable = variable;
        this.rutaImagen = rutaImagen;
        this.codigoResultado=SINCRONIZACIONCREADA;
        this.listenerSincronizacionImagenes=listenerSincronizacionImagenes;
        this.titulo=titulo;
        this.context=context;
    }

    /**
     * Antes de ejecutar la tarea
     */
    protected void onPreExecute() {

    }

    @Override
    protected Integer doInBackground(Void... voids) {
        /*ConexionHttp conexionHttp=null;
        try {
            conexionHttp=new ConexionHttp();
        } catch (MalformedURLException e) {
            return ERROR;
        } catch (FileNotFoundException e) {
           return ERROR;
        }*/

        String resultado =  Utilidades.cargarImagen_Gestion(idGestion,variable,rutaImagen,context, null);
        if(resultado!=null)
            return  IMAGENSUBIDA;
        else
            return ERROR;
        //return conexionHttp.enviarInformacion();
    }

    /**
     * Despues de ejecutar la tarea
     * @param result  el codigo con el que termino la carga
     */
    protected void onPostExecute(Integer result) {
        this.codigoResultado=result;
        Log.e("CODIGO",Integer.toString(codigoResultado));
        listenerSincronizacionImagenes.enSincronizacionFinalizada(codigoResultado,titulo);

    }

    /**
     * Identificador de la gestion, al cual estan asociados las imagenes
     */
    public int getIdGestion() {
        return idGestion;
    }

    /**
     * Nombre de la variable
     */
    public String getVariable() {
        return variable;
    }

    /**
     * Ruta de la imagen a sincronizar
     */
    public String getRutaImagen() {
        return rutaImagen;
    }

    /**
     * Codigo del resultado que arrojo la sincronizacion
     */
    public int getCodigoResultado() {
        return codigoResultado;
    }


    /**
     * Permite comunicar la sincronizacion con quien lo llama
     */
    public interface ListenerSincronizacionImagenes
    {
        /**
         * En caso de que la sincronizacion haya sido finalizado, bien o mal
         * @param codigo  el codigo que arrojo la sincronizacion
         * @param titulo  el titulo del campo que se sincronizo
         */
        void enSincronizacionFinalizada(int codigo,String titulo);
    }
}
