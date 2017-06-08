package com.valuarte.dtracking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.valuarte.dtracking.AdaptadoresListas.AdapterTipoGestiones;
import com.valuarte.dtracking.Base.BaseActivity;
import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.ElementosGraficos.Formulario;
import com.valuarte.dtracking.ElementosGraficos.Gestion;
import com.valuarte.dtracking.ElementosGraficos.TipoGestion;
import com.valuarte.dtracking.Util.ConexionAInternet;
import com.valuarte.dtracking.Util.MyRestErrorHandler;
import com.valuarte.dtracking.Util.RestClient;
import com.valuarte.dtracking.Util.Usuario;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.rest.spring.annotations.RestService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * Representa los tipos de gestiones, las cuales poseen las gestiones
 * @version 1.0
 */
@EActivity
public class MainActivity extends BaseActivity implements AdapterTipoGestiones.ListenerVer {
    /**
     * mensajes que se muestran cuando no se visualizan gestiones
     */
    public static final String MENSAJELOCAL = "Las gestiones ya fueron registradas pero aun no han sido enviadas";
    public static final String MENSAJEWEB = "No hay gestiones por registrar";
    /**
     * Intervalo de tiempo, con el cual se va a sincronizar las gestiones pendientes
     */
    public static final long INTERVALOTIEMPOSINCRONIZACION = 600000;
    /**
     * Intervalo de tiempo con el cual se va a consultar las gestiones que se eliminaron, en los
     * ultimos 5 minutos
     */
    public static final long INTERVALOGESTIONESELIMINADAS=300000;
    private static final int CAMERA_REQUEST = 1888;

    /**
     * Lista grafica de formularios
     */
    private ListView listaTipoGestiones;
    /**
     * Dialogo de progreso para la sincronizacion de los formularios desde la web
     */
    private ProgressDialog dialogoCargaFormularios;
    /**
     * Son los tipos de gestiones
     */
    private ArrayList<TipoGestion> tipoGestions;
    /**
     * Boton para sincronizar
     */
    private FloatingActionButton sincronizar;
    /**
     * Acceso a la base de datos
     */
    private RecursosBaseDatos recursosBaseDatos;
    /**
     * Error que se puede presentar
     */
    private AlertDialog.Builder error;
    /**
     * Verifica la conexion a internet
     */
    private ConexionAInternet conexionAInternet;
    /**
     * Mensaje que se le puede colocar a la lista cuano esta este vacia
     */
    private TextView mensajeLista;

    @RestService
    RestClient restClient;
    @Bean
    MyRestErrorHandler myErrorhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            ButterKnife.bind(this);
            setupToolbar();
        }
        catch (Exception e){}
    }
    @AfterViews
    void cargarControles(){
        restClient.setRestErrorHandler(myErrorhandler);
        LinearLayout cont = (LinearLayout) findViewById(R.id.empty);
        mensajeLista = (TextView) findViewById(R.id.emptyMensaje);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View vie = navigationView.getHeaderView(0);
        TextView perfil = (TextView) vie.findViewById(R.id.perfil);

        listaTipoGestiones = (ListView) findViewById(R.id.listaTipoGetsiones);
        listaTipoGestiones.setEmptyView(cont);
        conexionAInternet = new ConexionAInternet();
        recursosBaseDatos = new RecursosBaseDatos(this);
        Usuario usuario = recursosBaseDatos.getUsuario();
        if (usuario != null) {
            perfil.setText(usuario.getNommbreUsuario());
        }
        Intent intent = getIntent();
        int inicioSesion = intent.getIntExtra("inicioSesion", -1);
        if (inicioSesion != -1) {
            if (conexionAInternet.estaConectado(this)) {
                dialogoCargaFormularios = new ProgressDialog(MainActivity.this);
                dialogoCargaFormularios.setMessage("Sincronizando...");
                dialogoCargaFormularios.setCancelable(true);
                dialogoCargaFormularios.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialogoCargaFormularios.show();
                consultarFormularios();
            } else {
                tipoGestions = recursosBaseDatos.getTipoGestiones();
                filtarTipoGestiones();
                listaTipoGestiones.setAdapter(new AdapterTipoGestiones(tipoGestions, MainActivity.this));
                mensajeLista.setText(MENSAJELOCAL);
            }
        } else {
            tipoGestions = recursosBaseDatos.getTipoGestiones();
            filtarTipoGestiones();
            listaTipoGestiones.setAdapter(new AdapterTipoGestiones(tipoGestions, MainActivity.this));
            mensajeLista.setText(MENSAJELOCAL);
        }
        sincronizar = (FloatingActionButton) findViewById(R.id.sincronizar);
        sincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conexionAInternet.estaConectado(MainActivity.this)) {
                    dialogoCargaFormularios = new ProgressDialog(MainActivity.this);
                    dialogoCargaFormularios.setMessage("Sincronizando...");
                    dialogoCargaFormularios.setCancelable(true);
                    dialogoCargaFormularios.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialogoCargaFormularios.show();
                    consultarFormularios();
                } else {
                    construirError("No tiene conexión a internet");
                }
            }
        });
        listaTipoGestiones.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            tipoGestions = recursosBaseDatos.getTipoGestiones();
            filtarTipoGestiones();
            listaTipoGestiones.setAdapter(new AdapterTipoGestiones(tipoGestions, MainActivity.this));
            mensajeLista.setText(MENSAJELOCAL);
        }
        catch (Exception e){}
    }

    /**
     * Construye un error con el mensaje que recibe por parametro
     *
     * @param mensaje el mensaje a mostrar
     */
    private void construirError(String mensaje) {
        error = new AlertDialog.Builder(this);
        error.setTitle("Error");
        error.setMessage(mensaje);
        error.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        error.show();

    }

    /**
     * Consulta los formularios en la base de datos
     */
    @Background
    void consultarFormularios() {
        String respuesta = restClient.tipos_gestion();
        if(respuesta!=null){
            try {
                JSONArray jrespuesta = new JSONArray(respuesta);
                Log.e("response", respuesta);
                consultarGestiones(jrespuesta);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Consulta las gestiones
     *
     * @param response arreglo de json que contiene la informacion para construir los formularios
     */
    @Background
    public void consultarGestiones(final JSONArray response) {
        Usuario u = recursosBaseDatos.getUsuario();
        String respuesta=restClient.gestiones(String.valueOf(u.getId()));
        consultarGestiones(respuesta, response);
    }
    @UiThread
    void consultarGestiones(String respuesta, final JSONArray response){
        if(respuesta!=null){
            try {
                JSONObject jsonObject;
                Formulario formulario;
                ArrayList<Formulario> formularios = new ArrayList<>();
                tipoGestions = new ArrayList<>();
                RecursosBaseDatos recursosBaseDatos = new RecursosBaseDatos(MainActivity.this);
                recursosBaseDatos.eliminarFormulariosNoBorradores();
                for (int i = 0; i < response.length(); i++) {
                    jsonObject = response.getJSONObject(i);
                    tipoGestions.add(new TipoGestion(jsonObject.getInt("id"), jsonObject.getString("name")));
                    formulario = new Formulario(false, jsonObject.getInt("id"), jsonObject.getString("name"));
                    formulario.crearFormularioDesdeJson(jsonObject, new RecursosBaseDatos(MainActivity.this));
                    formularios.add(formulario);
                }
                JSONArray jsonArray = new JSONArray(respuesta);
                Gestion gestion;
                int tipoGestion;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    tipoGestion = jsonObject.getInt("tipo_gestion");
                    gestion = new Gestion(jsonObject.getString("zona"), tipoGestion,
                            jsonObject.getString("departamento"), jsonObject.getString("direccion"),
                            jsonObject.getString("barrio"), jsonObject.getInt("id"), jsonObject.getString("municipio"),
                            jsonObject.getString("destinatario"), jsonObject.getString("telefono"), Gestion.SINENVIAR,
                            Gestion.generarFechaDesdeCalendar(Calendar.getInstance()), 0.0, 0.0,
                            jsonObject.getString("barra"), false, getFormulario(tipoGestion, formularios));
                    agregarGestion(gestion);
                }

                guardarTipoGestiones();
                tipoGestions = recursosBaseDatos.getTipoGestiones();
                filtarTipoGestiones();
                listaTipoGestiones.setAdapter(new AdapterTipoGestiones(tipoGestions, MainActivity.this));
                dialogoCargaFormularios.dismiss();
                if (jsonArray.length() == 0) {
                    mensajeLista.setText(MENSAJEWEB);
                } else {
                    if (tipoGestions.size() == 0) {
                        mensajeLista.setText(MENSAJELOCAL);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                tipoGestions = new ArrayList<>();
            }
        }
    }

    /**
     * Obtiene el formulario al que le corresponde el tipo de gestion
     *
     * @param tipoGestion el identificador del tipo de gestion
     * @param formularios los formulario desde los cuales se va a buscar
     * @return el formulario o null, si no se encuentra
     */
    private Formulario getFormulario(int tipoGestion, ArrayList<Formulario> formularios) {
        for (Formulario f : formularios) {
            if (f.getIdFormulario() == tipoGestion) {
                return f.clone();
            }
        }
        return null;
    }

    /**
     * Elimina los tipos de gestiones que no tienen gestiones asociadas
     */
    private void filtarTipoGestiones() {
        ArrayList<TipoGestion> tgs = new ArrayList<>();
        for (TipoGestion tg : tipoGestions) {
            if (tg.getCantidadGestiones() != 0) {
                tgs.add(tg);
            }
        }
        tipoGestions = tgs;
    }

    /**
     * Agrega la gestion al tipo de gestion correspondiente
     *
     * @param gestion la gestion a agregar
     */
    private void agregarGestion(Gestion gestion) {
        for (TipoGestion tipoGestion : tipoGestions) {
            if (tipoGestion.getId() == gestion.getTipoGestion()) {
                tipoGestion.agregarGestion(gestion);
            }
        }
    }


    @Override
    public void enVerClickeado(int position) {
        Intent intent = new Intent(MainActivity.this, Gestiones.class);
        intent.putExtra("tipoGestion", tipoGestions.get(position));
        startActivity(intent);
    }

    /**
     * Guarda los tipos de gestiones en la base de datos
     */
    private void guardarTipoGestiones() {
        for (TipoGestion tipoGestion : tipoGestions) {
            recursosBaseDatos.guardarTipoGestion(tipoGestion);
        }
    }

    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                openDrawer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return R.id.nav_principal;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }
}
