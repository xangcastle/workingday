package com.valuarte.dtracking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;


import com.valuarte.dtracking.AdaptadoresListas.AdaptadorGestiones;
import com.valuarte.dtracking.ElementosGraficos.TipoGestion;
import com.valuarte.dtracking.Scanner.BarcodeScanner;

/**
 * Representa las gestiones que se van a llenar
 * @version 1.0
 */

public class Gestiones extends AppCompatActivity implements AdaptadorGestiones.ListenerBotonVer, SearchView.OnQueryTextListener{
    /**
     * Es el tipo de gestion sobre el que se trabaja
     */
    private TipoGestion tipoGestion;
    /**
     * Lista graficamente las gestiones
     */
    private ListView listaGestiones;
    /**
     * Filtrador para la busqueda
     */
    private SearchView buscador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_gestiones);
            Intent intent = getIntent();
            tipoGestion = (TipoGestion) intent.getSerializableExtra("tipoGestion");
            if (tipoGestion.getGestiones().size() == 0) {
                finish();
            }
            AdaptadorGestiones adaptadorGestiones = new AdaptadorGestiones(this, tipoGestion.getGestiones());
            listaGestiones = (ListView) findViewById(R.id.listaGestiones);
            buscador = (SearchView) findViewById(R.id.search);
            listaGestiones.setAdapter(adaptadorGestiones);
            listaGestiones.setTextFilterEnabled(true);
            setupSearchView();
            setToolbar((Toolbar) findViewById(R.id.toolbar));
            listaGestiones.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            FloatingActionButton scan = (FloatingActionButton) findViewById(R.id.scanner);
            scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Build.VERSION.SDK_INT<23) {
                        Intent i = new Intent(Gestiones.this, BarcodeScanner.class);
                        i.putExtra("tipoGestion", tipoGestion);
                        startActivity(i);
                    }
                    else
                    {
                        AlertDialog.Builder info=new AlertDialog.Builder(Gestiones.this);
                        info.setTitle("Información");
                        info.setMessage("Su versión de android no soporta el lector de barras");
                        info.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        info.show();
                    }
                }
            });
        }
        catch (Exception e){}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gestiones, menu);
        return true;
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
            listaGestiones.clearTextFilter();
        } else {
            listaGestiones.setFilterText(newText);
        }

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //  savedInstanceState.putSerializable("gestiones", gestions);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void enBotonVerClickeado(int i) {
        Intent intent = new Intent(this, FormularioActivity.class);
        intent.putExtra("gestion", tipoGestion.getGestiones().get(i));
        intent.putExtra("tipoGestion",tipoGestion);
        startActivity(intent);
        finish();
    }

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
