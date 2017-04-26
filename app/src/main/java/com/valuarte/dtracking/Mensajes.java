package com.valuarte.dtracking;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.valuarte.dtracking.AdaptadoresListas.AdaptadorMensajes;
import com.valuarte.dtracking.Base.BaseActivity;
import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.Util.ContenidoMensaje;
import com.valuarte.dtracking.Util.Usuario;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Representa el activity de los mensajes a mostrar
 * @version 1.0
 */
public class Mensajes extends BaseActivity implements SearchView.OnQueryTextListener {
    /**
     * Acceso a la base de datos
     */
    private RecursosBaseDatos recursosBaseDatos;
    /**
     * Lista que muestra los mensajes
     */
    private ListView listaMensajes;
    /**
     * Filtrador para la busqueda
     */
    private SearchView buscador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_mensajes);
            ButterKnife.bind(this);
            setupToolbar();
            recursosBaseDatos = new RecursosBaseDatos(this);
            Usuario usuario = recursosBaseDatos.getUsuario();
            if(usuario!=null) {
                if(usuario.getEstado()==Usuario.LOGUEADO) {
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    View vie = navigationView.getHeaderView(0);
                    TextView perfil = (TextView) vie.findViewById(R.id.perfil);
                    if (usuario != null) {
                        perfil.setText(usuario.getNommbreUsuario());
                    }
                    listaMensajes = (ListView) findViewById(R.id.listaMensajes);
                    ArrayList<ContenidoMensaje> contenidoMensajes = recursosBaseDatos.getContenidoMensajes();
                    AdaptadorMensajes adaptadorMensajes = new AdaptadorMensajes(this, contenidoMensajes, recursosBaseDatos);
                    listaMensajes.setAdapter(adaptadorMensajes);
                    listaMensajes.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            view.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });
                    LinearLayout cont = (LinearLayout) findViewById(R.id.empty);
                    listaMensajes.setEmptyView(cont);
                    buscador = (SearchView) findViewById(R.id.search);
                    listaMensajes.setTextFilterEnabled(true);
                    setupSearchView();
                }
                else
                {
                    Intent i=new Intent(this, Login.class);
                    startActivity(i);
                    finish();
                }
            }
            else
            {
                Intent i=new Intent(this, Login.class);
                startActivity(i);
                finish();
            }
        }
        catch (Exception e){}
    }

    /**
     * Setea el buscador de la lista
     */
    private void setupSearchView() {
        buscador.setIconifiedByDefault(false);
        buscador.setOnQueryTextListener(this);
        buscador.setSubmitButtonEnabled(true);
        buscador.setQueryHint("Buscar");
        buscador.setFocusable(false);
        buscador.setFocusableInTouchMode(true);
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (TextUtils.isEmpty(newText)) {
            listaMensajes.clearTextFilter();
        } else {
            listaMensajes.setFilterText(newText);
        }

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mensajes, menu);
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
        return R.id.nav_mensajes;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }
}
