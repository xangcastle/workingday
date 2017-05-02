package com.valuarte.dtracking.Util;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Se encarga de la sincronizacion de imagenes al servidor
 * @version 1.0
 */
public class SincronizacionMultiImagenes extends AsyncTask<Void, Void, Integer> {
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
    private JSONArray rutaImagen;
    /**
     * Codigo del resultado que arrojo la sincronizacion
     */
    private int codigoResultado;
    /**
     * Escucha eventos cuando finaliza la sincronizacion
     */
    private ListenerSincronizacionMultiImagenes listenerSincronizacionImagenes;
    /**
     * Titulo del campo
     */
    private String titulo;
    public SincronizacionMultiImagenes(int idGestion, String variable,
                                       JSONArray rutaImagen, String titulo,
                                       ListenerSincronizacionMultiImagenes listenerSincronizacionMultiImagenes) {
        this.idGestion = idGestion;
        this.variable = variable;
        this.rutaImagen = rutaImagen;
        this.codigoResultado=SINCRONIZACIONCREADA;
        this.listenerSincronizacionImagenes=listenerSincronizacionMultiImagenes;
        this.titulo=titulo;
    }

    /**
     * Antes de ejecutar la tarea
     */
    protected void onPreExecute() {

    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int codigo=ENPROGRESO;
        for(int i=0; i<getRutaImagen().length();i++){
            ConexionHttp conexionHttp=null;
            try {
                conexionHttp=new ConexionHttp(i);
            } catch (MalformedURLException e) {
                return ERROR;
            } catch (FileNotFoundException e) {
                return ERROR;
            }
             codigo = conexionHttp.enviarInformacion(i);
        }
        return  codigo;
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
    public JSONArray getRutaImagen() {
        return rutaImagen;
    }

    public String getRutaImagen(int index) {
        try {
            return rutaImagen.getString(index);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Codigo del resultado que arrojo la sincronizacion
     */
    public int getCodigoResultado() {
        return codigoResultado;
    }


    public class ConexionHttp implements Runnable {
        /**
         * URL a la que se va a conectar para solicitar el servicio
         */
        private URL connectURL;
        /**
         * Archivo que contiene la imagen
         */
        private FileInputStream fileInputStream = null;

       public ConexionHttp(int index_imagen) throws MalformedURLException, FileNotFoundException {
                connectURL = new URL(UPLOAD_URL);
                fileInputStream = new FileInputStream(getRutaImagen(index_imagen));
        }

        /**
         * Envia la informacion al servidor
         */
        public int enviarInformacion(int index) {
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            String Tag = "fSnd";
            try {
                Log.e(Tag, "Starting Http File Sending to URL");
                codigoResultado=ENPROGRESO;
                // Open a HTTP connection to the URL
                HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();

                // Allow Inputs
                conn.setDoInput(true);

                // Allow Outputs
                conn.setDoOutput(true);

                // Don't use a cached copy.
                conn.setUseCaches(false);

                // Use a post method.
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Connection", "Keep-Alive");

                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\""+GESTION+"\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(Integer.toString(getIdGestion()));
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\""+VARIABLE+"\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(getVariable());
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\""+IMAGEN+
                        "\";filename=\"" + "imagen" + String.valueOf(index) + ".jpg" + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e(Tag, "Headers are written");

                // create a buffer of maximum size
                int bytesAvailable = fileInputStream.available();

                int maxBufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                // read file and write it into form...
                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // close streams
                fileInputStream.close();

                dos.flush();

                Log.e(Tag, "File Sent, Response: " + String.valueOf(conn.getResponseMessage()));

                //  imprimirBody(conn);
                InputStream is = conn.getInputStream();

                // retrieve the response from server
                int ch;

                StringBuffer b = new StringBuffer();
                while ((ch = is.read()) != -1) {
                    b.append((char) ch);
                }
                String s = b.toString();
                Log.e("Response", s);
                dos.close();
                if(conn.getResponseCode()==200 && !s.equals(""))
                {
                    return IMAGENSUBIDA;
                }
                else
                {
                    return ERROR;
                }
            } catch (MalformedURLException ex) {
                Log.e(Tag, "URL error: " + ex.getMessage(), ex);
                return ERROR;
            } catch (FileNotFoundException ioe) {
                Log.e(Tag, "File not found: " + ioe.getMessage(), ioe);
                return ERROR;
            } catch (ProtocolException e) {
                Log.e("error protocolo",e.getMessage());
                return ERROR;
            } catch (IOException e) {
                Log.e("IO Error",e.getMessage());
                return ERROR;
            }
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
        }
    }

    /**
     * Permite comunicar la sincronizacion con quien lo llama
     */
    public interface ListenerSincronizacionMultiImagenes
    {
        /**
         * En caso de que la sincronizacion haya sido finalizado, bien o mal
         * @param codigo  el codigo que arrojo la sincronizacion
         * @param titulo  el titulo del campo que se sincronizo
         */
        void enSincronizacionFinalizada(int codigo, String titulo);
    }
}
