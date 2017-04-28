package com.valuarte.dtracking.Util;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.valuarte.dtracking.ElementosGraficos.Contenedor;
import com.valuarte.dtracking.ElementosGraficos.FirmaDigital;
import com.valuarte.dtracking.ElementosGraficos.Formulario;
import com.valuarte.dtracking.ElementosGraficos.Gestion;
import com.valuarte.dtracking.ElementosGraficos.Imagen;
import com.valuarte.dtracking.ElementosGraficos.Vista;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Sincroniza la gestion al web service
 *
 * @version 1.o
 */
public class SincronizacionGestionWeb implements SincronizacionImagenes.ListenerSincronizacionImagenes {
    /**
     * Ruta a la que se va hacer la sincronización
     */
    public static final String URL = "http://192.168.0.38:8000/dtracking/movil/cargar_gestion/";
    /**
     * Contexto de la aplicacion
     */
    private Context context;
    /**
     * Gestion a sincronizar con el web service
     */
    private Gestion gestion;
    /**
     * cola de peticiones web
     */
    private RequestQueue requestQueue;
    /**
     * Escucha los eventos cuando se termina la sincronizacion web
     */
    private ListenerSincronizacionWeb listenerSincronizacionWeb;
    /**
     * Objeto json que contiene los campos de la gestion
     */
    private JSONObject jsonObject;
    /**
     * Identificador del usuario que realizo la gestion
     */
    private int idUser;

    /**
     * Representa el titulo de las imagenes que no pudieron ser subidas
     */
    private ArrayList<String> imagenesNoSincronizadas;
    /**
     * cantidad de imagenes que se van a sincronizar
     */
    private int cantidadImagenes;

    public SincronizacionGestionWeb(Context context, Gestion gestion, JSONObject jsonObject,
                                    ListenerSincronizacionWeb listenerSincronizacionWeb, int idUser) {
        this.context = context;
        this.gestion = gestion;
        requestQueue = Volley.newRequestQueue(context);
        this.listenerSincronizacionWeb = listenerSincronizacionWeb;
        this.jsonObject = jsonObject;
        imagenesNoSincronizadas = new ArrayList<>();
        cantidadImagenes = 0;
        this.idUser=idUser;
    }

    /**
     * Sincroniza la gestion, de acuero al estado en que este la gestion
     */
    public void sincronizar()
    {
        int estado=gestion.getEstadoGestion();
        switch (estado)
        {
            case Gestion.SINENVIAR:
                sincronizarGestion();
                break;
            case Gestion.SINENVIARIMAGENES:
                sincronizarImagenesDeLaGestion();
                break;
        }
    }
    /**
     * Sincroniza la gestion con el web service, tanto el texto, como las imagenes
     */
    public void sincronizarGestion() {
        Log.e("json",jsonObject.toString());
        /**
         * solicitud post al servidor
         */
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    /**
                     * obtiene la respuesta que envia el servior
                     * @param response
                     */
                    @Override
                    public void onResponse(String response) {
                        Log.e("responseee", response);
                        sincronizarImagenesDeLaGestion();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", "error");
                        error.printStackTrace();
                        listenerSincronizacionWeb.enSincronizacionFallida("La sincronizacion falló",gestion.getId());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                JSONObject j = new JSONObject();
                params.put("gestion", Integer.toString(gestion.getIdgestion()));
                params.put("latitude", Double.toString(gestion.getLatitud()));
                params.put("longitude", Double.toString(gestion.getLongitud()));
                params.put("fecha", gestion.getFechaDDMMAA());
                try {
                    String json= jsonObject.getJSONObject("campos").toString();
                    json=json.replace("\"","&");
                    json=json.replace("&","'");
                    params.put("json", json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("user", Integer.toString(idUser));
                return params;
            }
        };
        requestQueue.add(postRequest);
    }

    /**
     * Sincroniza las imagenes de la gestion
     */
    private void sincronizarImagenesDeLaGestion() {
        Formulario formulario = gestion.getFormulario();
        ArrayList<Contenedor> contenedors = formulario.getContenedores();
        ArrayList<Vista> vistas;
        SincronizacionImagenes sincronizacionImagenes;
        FirmaDigital firmaDigital;
        Imagen imagen;
        for (Contenedor c : contenedors) {
            vistas = c.getVistas();
            for (Vista v : vistas) {
                if (v instanceof Imagen) {
                    imagen=(Imagen)v;
                    if(!imagen.getVal().trim().equals("")) {
                        cantidadImagenes += 1;
                        sincronizacionImagenes = new SincronizacionImagenes(gestion.getIdgestion(),
                                v.getNombreVariable(), imagen.getVal(), imagen.getTitulo(), this);
                        sincronizacionImagenes.execute();
                    }
                }
                if (v instanceof FirmaDigital) {
                    firmaDigital=(FirmaDigital)v;

                    if(!firmaDigital.getVal().trim().equals("")) {
                        cantidadImagenes += 1;
                        sincronizacionImagenes = new SincronizacionImagenes(gestion.getIdgestion(),
                                v.getNombreVariable(), firmaDigital.getVal(), firmaDigital.getTitulo(), this);
                        sincronizacionImagenes.execute();
                    }
                }
            }
        }
    }

    /**
     * En caso de que la sincronizacion haya sido finalizado, bien o mal
     *
     * @param codigo el codigo que arrojo la sincronizacion
     * @param titulo el titulo del campo que se sincronizo
     */
    @Override
    public void enSincronizacionFinalizada(int codigo, String titulo) {
            cantidadImagenes-=1;
            if (codigo != SincronizacionImagenes.IMAGENSUBIDA) {
                imagenesNoSincronizadas.add(titulo);
            }

            if(cantidadImagenes==0)
            {
                String mensaje="";
                if(imagenesNoSincronizadas.size()==0)
                {
                    mensaje="Gestión sincronizada";
                    listenerSincronizacionWeb.enSincronizacionCompletada(mensaje,gestion.getId());
                }
                else
                {
                    mensaje="Las siguientes imagenes no pudieron ser sincronizadas: ";
                    for(String s:imagenesNoSincronizadas)
                    {
                        mensaje=mensaje+"\n"+s;
                    }
                    listenerSincronizacionWeb.enSincronizacionFallida(mensaje,gestion.getId());
                }
            }
    }


    /**
     * Escucha eventos cuando la sincronizacion a terminado
     */
    public interface ListenerSincronizacionWeb {
        /**
         * En caso de que la sincronización haya sido completada
         */
        void enSincronizacionCompletada(String mensaje,int idGestion);

        /**
         * En caso de que la sincronización fuera sido fallida
         */
        void enSincronizacionFallida(String mensaje,int idGestion);
    }
}
