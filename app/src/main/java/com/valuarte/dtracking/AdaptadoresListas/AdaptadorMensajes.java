package com.valuarte.dtracking.AdaptadoresListas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.R;
import com.valuarte.dtracking.Util.ContenidoMensaje;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Representa el adaptador de la lista de mensajes
 */
public class AdaptadorMensajes extends BaseAdapter implements Filterable {
    /**
     * Contexto de la aplicacion
     */
    private Context context;
    /**
     * Son los mensajes a mostrar
     */
    private ArrayList<ContenidoMensaje> contenidoMensajesCopia;
    /**
     * Son los mensajes que siempre permanecen
     */
    private ArrayList<ContenidoMensaje> contenidoMensajesOriginal;
    /**
     * Acceso a la base de dtaos
     */
    private RecursosBaseDatos recursosBaseDatos;

    public AdaptadorMensajes(Context context, ArrayList<ContenidoMensaje> contenidoMensajes, RecursosBaseDatos recursosBaseDatos) {
        this.context = context;
        this.recursosBaseDatos = recursosBaseDatos;
        this.contenidoMensajesCopia = contenidoMensajes;
        this.contenidoMensajesOriginal = contenidoMensajes;
    }

    @Override
    public int getCount() {
        return contenidoMensajesCopia.size();
    }

    @Override
    public Object getItem(int i) {
        return contenidoMensajesCopia.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_mensaje, viewGroup, false);
        }
        ContenidoMensaje c = contenidoMensajesCopia.get(i);
        TextView mensaje = (TextView) view.findViewById(R.id.mensaje);
        mensaje.setText(c.getMensaje());
        TextView fecha = (TextView) view.findViewById(R.id.fecha);
        TextView hora = (TextView) view.findViewById(R.id.hora);
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(c.getFecha());
            calendar.setTime(date);
        } catch (ParseException e) {
            Log.e("error", e.getMessage());
        }
        fecha.setText(calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR));
        hora.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
        Button eliminar = (Button) view.findViewById(R.id.eliminar);
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder confir = new AlertDialog.Builder(context);
                confir.setTitle("Confirmación");
                confir.setMessage("Desea eliminar el mensaje?");
                confir.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        recursosBaseDatos.eliminarContenidoMensaje(contenidoMensajesCopia.get(i).getId());
                        ContenidoMensaje contenidoMensaje=contenidoMensajesCopia.get(i);
                        contenidoMensajesCopia.remove(i);
                        eliminarContenidoMensaje(contenidoMensaje);
                        notifyDataSetChanged();
                    }
                });
                confir.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                confir.show();
            }
        });
        return view;
    }

    /**
     * Elimina un contenido de mensaje del arreglo de contenido de mensajes original
     * @param contenidoMensaje
     */
    private void eliminarContenidoMensaje(ContenidoMensaje contenidoMensaje)
    {
        int i=0;
        for(ContenidoMensaje c:contenidoMensajesOriginal)
        {
            if(c.equals(contenidoMensaje))
            {
                contenidoMensajesOriginal.remove(i);
                break;
            }
            i++;
        }
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
                    final ArrayList<ContenidoMensaje> results = new ArrayList<ContenidoMensaje>();
                    if (contenidoMensajesOriginal == null)
                        contenidoMensajesOriginal = contenidoMensajesCopia;
                    if (constraint != null) {
                        if (contenidoMensajesOriginal != null && contenidoMensajesOriginal.size() > 0) {
                            for (final ContenidoMensaje c:contenidoMensajesOriginal) {
                                if (c.getMensaje().contains(constraint.toString()) ||
                                        c.getFecha().contains(constraint.toString()) ) {
                                    results.add(c);
                                }
                            }
                        }
                        oReturn.values = results;
                    }
                }
                catch (Exception e)
                {
                    oReturn.values=contenidoMensajesOriginal;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                contenidoMensajesCopia = (ArrayList<ContenidoMensaje>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}
