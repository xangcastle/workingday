package com.valuarte.dtracking.ElementosGraficos;

import android.content.ContentValues;
import android.content.Context;
import android.view.View;

import com.valuarte.dtracking.Excepciones.NoSoportaValorException;
import com.valuarte.dtracking.Excepciones.ValorRequeridoException;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Representa un elemnto grafico general que va en el formulario
 *
 * @version 1.0
 */
public abstract class Vista implements Comparable<Vista>,Serializable{
    protected int id;
    /**
     * Ancho de la vista
     */
    protected int ancho;
    /**
     * Alto de la vista
     */
    protected int alto;
    /**
     * Identificador de la vista en la pantalla
     */
    protected int idPantalla;
    /**
     * Identificador del layout al que la vista pertenece
     */
    protected int idLayout;
    /**
     * widget que representa el campo para colocar en el formulario
     */
    protected View view;
    /**
     * Vistas que puede contener una vista
     */
    protected ArrayList<Vista> vistas;
    /**
     * Determina si el campo(vista) es requerido o no
     */
    protected boolean requerido;
    /**
     * Determina si el campo esta habilitado ono
     */
    protected boolean habilitado;

    public Vista() {
    }

    public Vista(int ancho, int alto, int idPantalla, int idLayout, boolean requerido,  boolean habilitado) {
        this.ancho = ancho;
        this.alto = alto;
        this.idPantalla = idPantalla;
        this.setIdLayout(idLayout);
        this.requerido = requerido;
        this.habilitado = habilitado;
        vistas = new ArrayList<>();
    }

    public Vista(int id, int ancho, int alto, int idPantalla, int idLayout, boolean requerido,boolean habilitado) {
        this.setId(id);
        this.ancho = ancho;
        this.alto = alto;
        this.idPantalla = idPantalla;
        this.setIdLayout(idLayout);
        this.requerido = requerido;
        this.habilitado=habilitado;
        vistas = new ArrayList<>();
    }

    /**
     * Agrega una vista al arrgelo de vistas
     *
     * @param vista la vista a agregar
     */
    public void agregarVista(Vista vista) {
        vistas.add(vista);
    }

    /**
     * Agrega un arrgelo de vistas, al arreglo de vistas local
     *
     * @param vistas el arreglo de vistas a agregar
     */
    public void agregarVistas(ArrayList<Vista> vistas) {
        this.vistas.addAll(vistas);
    }

    /**
     * Construye el widget(campo) que va ir en el formulario
     *
     * @return View  campo del formulario
     */
    public abstract View construirVista(Context context);

    /**
     * Obtiene el valor de la vista, si es que soporta valor
     *
     * @return Object  objeto que representa el valor
     * @throws NoSoportaValorException en caso de que la vista no soporte el valor
     */
    public abstract Object getValor() throws NoSoportaValorException,ValorRequeridoException;

    /**
     * Obtiene el valor, con su respectivo valor en formato json
     *
     * @return String  cadena del valor en formato json, o vacio en caso de que no soporte valor
     */
    public abstract String getValorJson();

    /**
     * Obtiene la cadena para crear la tabla sqlite
     *
     * @return String la cadena con la sentencia sql para crear la tabla en la base de datos
     */
    public abstract String crearTablaSqlite();

    /**
     * Obtiene el contenedor de valores del objeto actual para guardarlo en la base de datos
     *
     * @return ContentValues   el contenedor de valores del objeto actual
     */
    public abstract ContentValues getContenedorValores();

    /**
     * Obtiene nel nombre de la tabla en la que la vista va a ser guardada
     *
     * @return String la cadena con el nombre de la tabla
     */
    public abstract String getNombreTabla();

    /**
     * Obtiene el nombre de la variable asociado al campo
     * @return
     */
    public abstract String getNombreVariable();
    /**
     * Actualiza los valores de la vista
     */
    public abstract void actualizarValores();
    /**
     * Identificador de la vista en la base de datos
     */

    public int getId() {
        return id;
    }

    /**
     * Ancho de la vista
     */
    public int getAncho() {
        return ancho;
    }

    /**
     * Alto de la vista
     */
    public int getAlto() {
        return alto;
    }

    /**
     * Identificador de la vista en la pantalla
     */
    public int getIdPantalla() {
        return idPantalla;
    }

    /**
     * Identificador del layout al que la vista pertenece
     */
    public int getIdLayout() {
        return idLayout;
    }

    /**
     * widget que representa el campo para colocar en el formulario
     */
    public View getView() {
        return view;
    }

    /**
     * Vistas que puede contener una vista
     */
    public ArrayList<Vista> getVistas() {
        return vistas;
    }

    /**
     * Determina si el campo(vista) es requerido o no
     */
    public boolean isRequerido() {
        return requerido;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Vista vista)
    {
        return (new Integer(idPantalla)).compareTo(new Integer(vista.getIdPantalla()));
    }

    /**
     * Clona el objeto actual
     * @return  la vita clonadad
     */
    public abstract Vista clone();
    public void setIdLayout(int idLayout) {
        this.idLayout = idLayout;
    }
}
