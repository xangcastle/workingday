package com.valuarte.dtracking.Util;


import android.content.Context;
import android.util.Log;


import com.valuarte.dtracking.ElementosGraficos.Contenedor;
import com.valuarte.dtracking.ElementosGraficos.FirmaDigital;
import com.valuarte.dtracking.ElementosGraficos.Formulario;
import com.valuarte.dtracking.ElementosGraficos.Gestion;
import com.valuarte.dtracking.ElementosGraficos.Imagen;
import com.valuarte.dtracking.ElementosGraficos.MultiImagen;
import com.valuarte.dtracking.ElementosGraficos.Vista;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.rest.spring.annotations.RestService;
import org.json.JSONArray;
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
@EBean
public class SincronizacionGestionWeb implements SincronizacionImagenes.ListenerSincronizacionImagenes,
SincronizacionMultiImagenes.ListenerSincronizacionMultiImagenes{

    @RootContext
    Context context;


    /**
     * Gestion a sincronizar con el web service
     */
    public Gestion gestion;
    /**
     * Escucha los eventos cuando se termina la sincronizacion web
     */
    public ListenerSincronizacionWeb listenerSincronizacionWeb;
    /**
     * Objeto json que contiene los campos de la gestion
     */
    public JSONObject jsonObject;
    /**
     * Identificador del usuario que realizo la gestion
     */
    public int idUser;

    /**
     * Representa el titulo de las imagenes que no pudieron ser subidas
     */
    public ArrayList<String> imagenesNoSincronizadas;
    /**
     * cantidad de imagenes que se van a sincronizar
     */
    public int cantidadImagenes;

    RestClient restClient;

    public SincronizacionGestionWeb() {
        imagenesNoSincronizadas = new ArrayList<>();
        cantidadImagenes = 0;
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
    @Background
    public void sincronizarGestion() {
        if(restClient==null)
            restClient=new RestClient_(context);
        MyRestErrorHandler myRestErrorHandler=MyRestErrorHandler_.getInstance_(context);
        restClient.setRestErrorHandler(myRestErrorHandler);

        Log.e("json",jsonObject.toString());
        String json;
        try {
            json= jsonObject.getJSONObject("campos").toString();
            json=json.replace("\"","&");
            json=json.replace("&","'");
        } catch (JSONException e) {
            return;
        }
        String respuesta=restClient.cargar_gestion(
                Integer.toString(gestion.getIdgestion()),
                Double.toString(gestion.getLatitud()),
                Double.toString(gestion.getLongitud()),
                gestion.getFechaDDMMAA(),
                json,
                Integer.toString(idUser)
                );
        if(respuesta!=null){
            Log.e("responseee", respuesta);
            sincronizarImagenesDeLaGestion();
        }else {
            Log.e("error", "error");
            listenerSincronizacionWeb.enSincronizacionFallida("La sincronizacion falló",gestion.getId());
        }
    }

    /**
     * Sincroniza las imagenes de la gestion
     */
    @UiThread
    void sincronizarImagenesDeLaGestion() {
        Formulario formulario = gestion.getFormulario();
        ArrayList<Contenedor> contenedors = formulario.getContenedores();
        ArrayList<Vista> vistas;
        SincronizacionImagenes sincronizacionImagenes;
        SincronizacionMultiImagenes sincronizacionMultiImagenes;
        FirmaDigital firmaDigital;
        Imagen imagen;
        MultiImagen multi_imagen;
        for (Contenedor c : contenedors) {
            vistas = c.getVistas();
            for (Vista v : vistas) {
                if (v instanceof Imagen) {
                    imagen=(Imagen)v;
                    if(!imagen.getVal().trim().equals("")) {
                        cantidadImagenes += 1;
                        sincronizacionImagenes = new SincronizacionImagenes(gestion.getIdgestion(),
                                v.getNombreVariable(), imagen.getVal(), imagen.getTitulo(), this, context);
                        sincronizacionImagenes.execute();
                    }
                }
                if (v instanceof MultiImagen) {
                    multi_imagen=(MultiImagen)v;
                    if(!multi_imagen.getVal().trim().equals("")) {
                        String Imagenes =String.valueOf(multi_imagen.getVal());
                        if(Imagenes!=null){
                            try {
                                JSONArray arregloImagenes= new JSONArray(Imagenes);
                                cantidadImagenes += arregloImagenes.length();
                                sincronizacionMultiImagenes = new SincronizacionMultiImagenes(
                                        gestion.getIdgestion(),
                                        v.getNombreVariable(),
                                        arregloImagenes,
                                        multi_imagen.getTitulo(),
                                        this, context);
                                sincronizacionMultiImagenes.execute();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (v instanceof FirmaDigital) {
                    firmaDigital=(FirmaDigital)v;

                    if(!firmaDigital.getVal().trim().equals("")) {
                        cantidadImagenes += 1;
                        sincronizacionImagenes = new SincronizacionImagenes(gestion.getIdgestion(),
                                v.getNombreVariable(), firmaDigital.getVal(), firmaDigital.getTitulo(), this, context);
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
    @UiThread
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
