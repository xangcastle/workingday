package com.valuarte.dtracking.ElementosGraficos;

import android.content.ContentValues;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.Excepciones.NoSoportaValorException;
import com.valuarte.dtracking.Excepciones.ValorRequeridoException;

import java.util.ArrayList;

/**
 * Representa un campo combobox del formulario
 *
 * @version 1.0
 */
public class ComboCaja extends Vista {
    /**
     * nombre de la variable con que se identifica el campo
     */
    private String nombreVariable;
    /**
     * titulo del campo
     */
    private String titulo;
    /**
     * valor del combo box
     */
    private ArrayList<ElementoCombo> elementoCombos;

    public ComboCaja() {
    }

    public ComboCaja(int ancho, int alto, int idPantalla, int idLayout, boolean requerido,
                     String nombreVariable, String titulo, boolean habilitado) {
        super(ancho, alto, idPantalla, idLayout, requerido, habilitado);
        this.nombreVariable = nombreVariable;
        this.titulo = titulo;
        this.elementoCombos = new ArrayList<>();
    }

    public ComboCaja(int id, int ancho, int alto, int idPantalla, int idLayout, boolean requerido,
                     String nombreVariable, String titulo, boolean habilitado) {
        super(id, ancho, alto, idPantalla, idLayout, requerido, habilitado);
        this.nombreVariable = nombreVariable;
        this.titulo = titulo;
        this.elementoCombos = new ArrayList<>();
    }

    /**
     * Construye el widget(campo) que va ir en el formulario
     *
     * @return View  campo del formulario
     */
    @Override
    public View construirVista(Context context) {
        Spinner spinner = new Spinner(context);
        spinner.setId(idPantalla);
        spinner.setAdapter(new ArrayAdapter<ElementoCombo>(context, android.R.layout.simple_spinner_dropdown_item, getElementoCombos()));
        spinner.setEnabled(habilitado);
        int i = 0;
        boolean selec = false;
        for (ElementoCombo e : getElementoCombos()) {
            if (e.isSeleccionado()) {
                spinner.setSelection(i, true);
                selec = true;
            }
            i++;
        }
        if (getElementoCombos().size() > 0) {
            if (!selec) {
                spinner.setSelection(0, true);
                getElementoCombos().get(0).setSeleccionado(true);
            }
        }
        TextView textView = new TextView(context);
        textView.setText(titulo);
        TextInputLayout textInputLayout = new TextInputLayout(context);
        textInputLayout.addView(textView, new TextInputLayout.LayoutParams(TextInputLayout.LayoutParams.MATCH_PARENT, TextInputLayout.LayoutParams.WRAP_CONTENT));
        textInputLayout.addView(spinner, new Spinner.LayoutParams(TextInputLayout.LayoutParams.MATCH_PARENT, Spinner.LayoutParams.WRAP_CONTENT));
        this.view = spinner;
        return textInputLayout;
    }

    /**
     * resetea los elementos del combo box a false
     */
    private void resetearElementos() {
        for (ElementoCombo e : getElementoCombos()) {
            e.setSeleccionado(false);
        }
    }

    /**
     * Obtiene el valor de la vista, si es que soporta valor
     *
     * @return Object  objeto que representa el valor
     * @throws NoSoportaValorException en caso de que la vista no soporte el valor
     */
    @Override
    public Object getValor() throws NoSoportaValorException, ValorRequeridoException {
        int id = -1;
        for (ElementoCombo elementoCombo : elementoCombos) {
            if (elementoCombo.isSeleccionado()) {
                id = elementoCombo.getId();
            }
        }
        if (id != -1) {
            return id;
        } else {
            if(isRequerido()) {
                throw new ValorRequeridoException("El campo " + titulo + " esta sin completar y es requerido", nombreVariable, titulo, view.getX(), view.getY());
            }
            else
            {
                return id;
            }
        }
    }

    /**
     * Obtiene el valor, con su respectivo valor en formato json
     *
     * @return String  cadena del valor en formato json, o vacio en caso de que no soporte valor
     */
    @Override
    public String getValorJson() {

        //TODO
        return nombreVariable + "=" + "";
    }

    /**
     * Obtiene la cadena para crear la tabla sqlite
     *
     * @return String la cadena con la sentencia sql para crear la tabla en la base de datos
     */
    @Override
    public String crearTablaSqlite() {
        ColumnasTablaSql c = new ColumnasTablaSql();
        String tabla = "create table " + c.TABLE_NAME + " (" + c.ID + " " + RecursosBaseDatos.INT_TYPE + " primary key autoincrement,"
                + c.ANCHO + " " + RecursosBaseDatos.INT_TYPE + "," + c.ALTO + " " + RecursosBaseDatos.INT_TYPE + ","
                + c.REQUERIDO + "  " + RecursosBaseDatos.BOOLEAN_TYPE + ","
                + c.LAYOUT + " " + RecursosBaseDatos.INT_TYPE + "," + c.PANTALLA + " " + RecursosBaseDatos.INT_TYPE + "," + c.HABILITADO + " " + RecursosBaseDatos.BOOLEAN_TYPE + ","
                + c.NOMBRE_VARIABLE + " " + RecursosBaseDatos.STRING_TYPE + "," + c.TITULO + " " + RecursosBaseDatos.STRING_TYPE + ",FOREIGN KEY (" + c.LAYOUT + ") REFERENCES " + Contenedor.ColumnasTablaSql.TABLE_NAME + "(" + Contenedor.ColumnasTablaSql.ID + "), FOREIGN KEY("
                + c.PANTALLA + ") REFERENCES IdsPantalla(id))";
        return tabla;
    }

    /**
     * Obtiene el contenedor de valores del objeto actual para guardarlo en la base de datos
     *
     * @return ContentValues   el contenedor de valores del objeto actual
     */
    @Override
    public ContentValues getContenedorValores() {
        ContentValues contentValues = new ContentValues();
        ColumnasTablaSql c = new ColumnasTablaSql();
        contentValues.put(c.ANCHO, ancho);
        contentValues.put(c.ALTO, alto);
        contentValues.put(c.REQUERIDO, requerido);
        contentValues.put(c.LAYOUT, idLayout);
        contentValues.put(c.PANTALLA, idPantalla);
        contentValues.put(c.NOMBRE_VARIABLE, nombreVariable);
        contentValues.put(c.TITULO, titulo);
        contentValues.put(c.HABILITADO, habilitado);
        return contentValues;
    }

    /**
     * Obtiene nel nombre de la tabla en la que la vista va a ser guardada
     *
     * @return String la cadena con el nombre de la tabla
     */
    @Override
    public String getNombreTabla() {
        return ColumnasTablaSql.TABLE_NAME;
    }

    /**
     * Obtiene el nombre de la variable asociado al campo
     *
     * @return
     */
    @Override
    public String getNombreVariable() {
        return nombreVariable;
    }

    /**
     * Actualiza los valores de la vista
     */
    @Override
    public void actualizarValores() {
        if (view != null) {
            resetearElementos();
            getElementoCombos().get(((Spinner) view).getSelectedItemPosition()).setSeleccionado(true);
        }
    }

    /**
     * Clona el objeto actual
     *
     * @return la vita clonadad
     */
    @Override
    public Vista clone() {
        ArrayList<ElementoCombo> combos=new ArrayList<>();
        for(ElementoCombo elementoCombo:elementoCombos)
        {
            combos.add(elementoCombo.clone());
        }
        ComboCaja c=new ComboCaja(id, ancho, alto, idPantalla, idLayout, requerido,
                nombreVariable,titulo, habilitado);
        c.setElementoCombos(combos);
        return c;
    }

    public void setElementoCombos(ArrayList<ElementoCombo> elementoCombos) {
        this.elementoCombos = elementoCombos;
    }

    /**
     * valor del combo box
     */
    public ArrayList<ElementoCombo> getElementoCombos() {
        return elementoCombos;
    }

    /**
     * Clase que contiene los nombres de las columnas de la tabla
     */
    public final static class ColumnasTablaSql {
        public static final String TABLE_NAME = "ComboCajas";
        public static final String ID = "id";
        public static final String ANCHO = "ancho";
        public static final String ALTO = "alto";
        public static final String REQUERIDO = "requerido";
        public static final String LAYOUT = "idLayout";
        public static final String PANTALLA = "idPantalla";
        public static final String NOMBRE_VARIABLE = "nombreVariable";
        public static final String TITULO = "titulo";
        public static final String HABILITADO = "habilitado";
    }

}
