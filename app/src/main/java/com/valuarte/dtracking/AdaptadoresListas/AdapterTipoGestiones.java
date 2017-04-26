package com.valuarte.dtracking.AdaptadoresListas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.valuarte.dtracking.ElementosGraficos.TipoGestion;
import com.valuarte.dtracking.R;

import java.util.ArrayList;

/**
 * Representa un adapatdor para la lista de formularios
 *
 * @version 1.0
 */
public class AdapterTipoGestiones extends BaseAdapter {
    /**
     * formularios a mostrar
     */
    private ArrayList<TipoGestion> tipoGestions;
    /**
     * Contexto de la aplicacion
     */
    private Context context;
    /**
     * Escucha eventos del boton ver
     */
    private ListenerVer listenerVer;
    public AdapterTipoGestiones(ArrayList<TipoGestion> tipogestions, Context context) {
        this.tipoGestions = tipogestions;
        this.context = context;
        listenerVer=(ListenerVer)context;
    }

    @Override
    public int getCount() {
        return tipoGestions.size();
    }

    @Override
    public Object getItem(int i) {
        return tipoGestions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_tipogestion, viewGroup, false);
        }
        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(tipoGestions.get(i).getNombre());
        TextView cantidad=(TextView)view.findViewById(R.id.cantidad);
        cantidad.setText(Integer.toString(tipoGestions.get(i).getCantidadGestiones()));
        Button button=(Button)view.findViewById(R.id.ver);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenerVer.enVerClickeado(i);
            }
        });
        return view;
    }
    public interface ListenerVer
    {
        /**
         * Cuando se clickea el boton ver
         * @param position
         */
        void enVerClickeado(int position);
    }
}
