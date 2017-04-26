package com.valuarte.dtracking.ElementosGraficos;

import android.content.ContentValues;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Representa el tipo de gestion
 * @version 1.0
 */
public class TipoGestion implements Serializable{
    private int id;
    private String nombre;
    private ArrayList<Gestion> gestiones;
    public TipoGestion(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        gestiones=new ArrayList<>();
    }

    /**
     * Agrega la gestion al arreglo de gestiones
     * @param gestion  la gestion a guardar
     */
    public void agregarGestion(Gestion gestion)
    {
        getGestiones().add(gestion);
    }
    /***
     * Construye la tabla slqite para la base de datos local
     * @return cadena con la sentencia sql
     */
    public static final String construirTablaSqlite()
    {
        ColumnasTablaSql clm=new ColumnasTablaSql();
        String tabla="create table "+clm.TABLENAME+" ("+clm.ID+" "+ RecursosBaseDatos.INT_TYPE+" primary key,"
            +clm.NOMBRE+" "+RecursosBaseDatos.STRING_TYPE+")";
        return tabla;
    }
    /**
     * Obtiene el nombre de la tabal con que se identifica la clase en la base de datos
     * @return
     */
    public String getNombreTabla()
    {
        return ColumnasTablaSql.TABLENAME;
    }
    /**
     * Obtiene el contenedor de valores del objeto actual
     * @return  el contendor de valores
     */
    public ContentValues getContenedorValores()
    {
        ColumnasTablaSql c=new ColumnasTablaSql();
        ContentValues contentValues=new ContentValues();
        contentValues.put(c.ID, getId());
        contentValues.put(c.NOMBRE, getNombre());
        return contentValues;
    }

    public void setGestiones(ArrayList<Gestion> gestiones) {
        this.gestiones = gestiones;
    }

    /**
     * Identificador del tipo de gestion
     */
    public int getId() {
        return id;
    }

    /**
     * Nombre del tipo de gestion
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Gestiones a las que estan asociados el tipo de gestion
     */
    public ArrayList<Gestion> getGestiones() {
        return gestiones;
    }

    /**
     * Obtiene la cantidad de gestiones
     * @return cantidad de gestiones
     */
    public int getCantidadGestiones()
    {
        return gestiones.size();
    }
    public final static class ColumnasTablaSql
    {
        public static final String TABLENAME="TipoGestiones";
        public static final String ID="id";
        public static final String NOMBRE="nombre";
    }
    public TipoGestion clone()
    {
        TipoGestion tipoGestion=new TipoGestion(id,nombre);
        for(Gestion gestion:gestiones)
        {
            tipoGestion.agregarGestion(gestion.clone());
        }
        return tipoGestion;
    }

}
