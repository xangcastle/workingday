package com.valuarte.dtracking;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.valuarte.dtracking.AdaptadoresListas.AdaptadorHistorial;
import com.valuarte.dtracking.Base.BaseActivity;
import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.ElementosGraficos.Gestion;
import com.valuarte.dtracking.Util.Usuario;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Representa el historial que posee el usuario, respecto a sus gestiones
 * @version 1.0
 */
public class Historial extends BaseActivity implements SearchView.OnQueryTextListener{
    /**
     * Maneja el acceso a la base de datos
     */
    private RecursosBaseDatos recursosBaseDatos;
    /**
     * Lista que muestra el historial
     */
    private ListView listaHistorial;
    /**
     * Filtrador para la busqueda
     */
    private SearchView buscador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_historial);
            ButterKnife.bind(this);
            setupToolbar();
            recursosBaseDatos = new RecursosBaseDatos(this);
            listaHistorial = (ListView) findViewById(R.id.listaGestiones);
            listaHistorial.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            buscador = (SearchView) findViewById(R.id.search);
            ArrayList<Gestion> gestiones = recursosBaseDatos.getGestionesBorradores();
            AdaptadorHistorial adaptadorHistorial = new AdaptadorHistorial(gestiones, this);
            listaHistorial.setAdapter(adaptadorHistorial);
            listaHistorial.setTextFilterEnabled(true);
            setupSearchView();
            LinearLayout cont = (LinearLayout) findViewById(R.id.empty);
            listaHistorial.setEmptyView(cont);
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View vie = navigationView.getHeaderView(0);
            TextView perfil = (TextView) vie.findViewById(R.id.perfil);
            Usuario usuario = recursosBaseDatos.getUsuario();
            if (usuario != null) {
                perfil.setText(usuario.getNommbreUsuario());
            }
        }
        catch (Exception e){}
    }

    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
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
            listaHistorial.clearTextFilter();
        } else {
            listaHistorial.setFilterText(newText);
        }

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_historial, menu);
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
        return R.id.nav_historial;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }
}
