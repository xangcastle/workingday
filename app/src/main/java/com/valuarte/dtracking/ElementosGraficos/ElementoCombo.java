package com.valuarte.dtracking.ElementosGraficos;

import android.content.ContentValues;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;

import java.io.Serializable;

/**
 * Representa un elemento de un ComboCaja(combobox)
 * @version 1.0
 */
public class ElementoCombo implements Serializable{
    /**
     * identificador del elemento en la base de datos
     */
    private int id;
    /**
     * identificador del elemento desde el web service
     */
    private int idElemento;
    /**
     * cadena que representa el valor del elemento
     */
    private String valor;
    /**
     * Indica si este elemento es el elemento selecciponado por defecto
     */
    private boolean seleccionado;
    /**
     * Id del combobox al que pertenece el elmento
     */
    private int idCombo;
    public ElementoCombo(int id, String valor,int idElemento,boolean seleccionado,int idCombo) {
        this.setId(id);
        this.valor = valor;
        this.idElemento=idElemento;
        this.setSeleccionado(seleccionado);
        this.setIdCombo(idCombo);
    }

    public ElementoCombo(int idElemento, String valor, boolean seleccionado,int idCombo) {
        this.idElemento = idElemento;
        this.valor = valor;
        this.setSeleccionado(seleccionado);
        this.setIdCombo(idCombo);
    }

    /**
     * identificador del elemento en la base de datos
     */
    public int getId() {
        return id;
    }
    /**
     * cadena que representa el valor del elemento
     */
    public String getValor() {
        return valor;
    }

    /**
     * Indica si este elemento es el elemento selecciponado por defecto
     */
    public boolean isSeleccionado() {
        return seleccionado;
    }

    /**
     * identificador del elemento desde el web service
     */
    public int getIdElemento() {
        return idElemento;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public void setIdCombo(int idCombo) {
        this.idCombo = idCombo;
    }

    public void setId(int id) {
        this.id = id;
    }
    public ElementoCombo clone()
    {
        return new ElementoCombo(id, valor,idElemento,seleccionado,idCombo);
    }
    /**
     * Representa las columnas de la tabla
     */
    public static final class ColumnasTablaSql
    {
        public static final String TABLE_NAME="ElementosCombo";
        public static final String ID="id";
        public static final String ID_ELEMENTO="idELemento";
        public static final String VALOR="valor";
        public static final String SELECCIONADO="seleccionado";
        public static final String COMBO="idCombo";
    }
    /**
     * Genera una cadena para crear la tabla sql
     */
    public static final String crearTablaSqlite()
    {
        ColumnasTablaSql c=new ColumnasTablaSql();
        String tabla="create table "+c.TABLE_NAME+" ("+c.ID+" "+ RecursosBaseDatos.INT_TYPE+" primary key autoincrement,"
                +c.ID_ELEMENTO+" "+RecursosBaseDatos.INT_TYPE+","+c.COMBO+" "+RecursosBaseDatos.INT_TYPE+","+
                c.VALOR+" "+RecursosBaseDatos.STRING_TYPE+","+c.SELECCIONADO+" "+RecursosBaseDatos.BOOLEAN_TYPE+
                ",FOREIGN KEY ("+c.COMBO+") REFERENCES "+ComboCaja.ColumnasTablaSql.TABLE_NAME+" ("+ComboCaja.ColumnasTablaSql.ID+"))";
        return tabla;
    }
    public ContentValues getContenedorValores()
    {
        ContentValues contentValues=new ContentValues();
        ColumnasTablaSql c=new ColumnasTablaSql();
        contentValues.put(c.ID_ELEMENTO,idElemento);
        contentValues.put(c.VALOR,valor);
        contentValues.put(c.SELECCIONADO,seleccionado);
        contentValues.put(c.COMBO,idCombo);
        return contentValues;
    }

    @Override
    public String toString() {
        return valor;
    }
}
