package com.valuarte.dtracking.AdaptadoresListas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.valuarte.dtracking.ElementosGraficos.Gestion;
import com.valuarte.dtracking.R;

import java.util.ArrayList;

/**
 * Representa el adapatador de las gestiones
 * @version 1.0
 */
public class AdaptadorGestiones extends BaseAdapter implements Filterable {
    /**
     * Contexto de la aplicacion
     */
    private Context context;
    /**
     * Comunicador entre la activity y el boton ver del item
     */
    private ListenerBotonVer listenerBotonVer;
    /**
     * Es el arreglo que se genera cuando se copia en el filtrador
     */
    private ArrayList<Gestion> gestionesCopia;
    /**
     * Es el arreglo original de las gestiones
     */
    private ArrayList<Gestion> gestionesOriginal;
    public AdaptadorGestiones(Context context, ArrayList<Gestion> gestions) {
        this.context = context;
        this.listenerBotonVer=(ListenerBotonVer)context;
        this.gestionesCopia = gestions;
        this.gestionesOriginal=gestions;
    }

    @Override
    public int getCount() {
        return gestionesCopia.size();
    }

    @Override
    public Object getItem(int i) {
        return gestionesCopia.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_gestiones, viewGroup, false);
        }
        Gestion g=gestionesCopia.get(i);
        TextView nombre=(TextView)view.findViewById(R.id.nombre);
        nombre.setText(g.getDestinatario());
        TextView direccion=(TextView)view.findViewById(R.id.direccion);
        direccion.setText(g.getDireccion());
        TextView telefono=(TextView)view.findViewById(R.id.telefono);
        telefono.setText(g.getTelefono());
        TextView id=(TextView)view.findViewById(R.id.id);
        id.setText(Integer.toString(g.getIdgestion()));
        Button button=(Button)view.findViewById(R.id.ver);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenerBotonVer.enBotonVerClickeado(i);
            }
        });
        return view;

    }
    /**
     * Filtrador de busqueda
     * @return
     */
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                try {
                    final ArrayList<Gestion> results = new ArrayList<Gestion>();
                    if (gestionesOriginal == null)
                        gestionesOriginal = gestionesCopia;
                    if (constraint != null) {
                        if (gestionesOriginal != null && gestionesOriginal.size() > 0) {
                            for (final Gestion g : gestionesOriginal) {
                                if (Integer.toString(g.getIdgestion()).contains(constraint.toString()) ||
                                        g.getDestinatario().contains(constraint.toString()) ||
                                        g.getDestinatario().toLowerCase().contains(constraint.toString())) {
                                    results.add(g);
                                }
                            }
                        }
                        oReturn.values = results;
                    }
                }
                catch (Exception e)
                {
                    oReturn.values=gestionesOriginal;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                gestionesCopia = (ArrayList<Gestion>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
    public interface ListenerBotonVer
    {
       void enBotonVerClickeado(int i);
    }
}
