package com.valuarte.dtracking.ElementosGraficos;

import android.content.ContentValues;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.Excepciones.NoSoportaValorException;
import com.valuarte.dtracking.Excepciones.ValorRequeridoException;

import java.util.ArrayList;

/**
 * Representa un grupo de radio buttons para el formulario
 *
 * @version 1.0
 */
public class RadioGrupo extends Vista {
    /**
     * nombre de la variable con que se identifica el campo
     */
    private String nombreVariable;
    /**
     * Titulo del grupo de radio buttons
     */
    private String titulo;
    private ArrayList<RadioBoton> radioBotons;
    /**
     * Orientacion del radiogroup
     */
    private String orientacion;

    public RadioGrupo() {
        radioBotons = new ArrayList<>();
    }

    public RadioGrupo(int ancho, int alto, int idPantalla, int idLayout, boolean requerido,
                      String orientacion, String nombreVariable, String titulo, boolean habilitado) {
        super(ancho, alto, idPantalla, idLayout, requerido, habilitado);
        radioBotons = new ArrayList<>();
        this.nombreVariable = nombreVariable;
        this.titulo = titulo;
        this.orientacion = orientacion;
    }

    public RadioGrupo(int id, int ancho, int alto, int idPantalla, int idLayout, boolean requerido
            , String orientacion, String nombreVariable, String titulo, boolean habilitado) {
        super(id, ancho, alto, idPantalla, idLayout, requerido, habilitado);
        radioBotons = new ArrayList<>();
        this.nombreVariable = nombreVariable;
        this.titulo = titulo;
        this.orientacion = orientacion;
    }

    /**
     * Construye el widget(campo) que va ir en el formulario
     *
     * @return View  campo del formulario
     */
    @Override
    public View construirVista(Context context) {
        TextInputLayout textInputLayout = new TextInputLayout(context);
        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setEnabled(habilitado);
        if (orientacion.toLowerCase().equals("horizontal")) {
            radioGroup.setOrientation(RadioGroup.HORIZONTAL);
        } else {
            radioGroup.setOrientation(RadioGroup.VERTICAL);
        }
        for (RadioBoton radioBoton : getRadioBotons()) {

            radioGroup.addView(radioBoton.construirVista(context),
                    new RadioGroup.LayoutParams(radioBoton.getAncho(), radioBoton.getAlto()));
            if (radioBoton.getVal()) {

                radioGroup.check(radioBoton.getIdPantalla());
            }
        }
        TextView textView = new TextView(context);

        this.view = radioGroup;
        textView.setText(titulo);
        radioGroup.setId(idPantalla);
        textInputLayout.addView(textView, new TextInputLayout.LayoutParams(ancho, alto));
        textInputLayout.addView(radioGroup, new TextInputLayout.LayoutParams(ancho, alto));

        return textInputLayout;
    }

    /**
     * Obtiene el valor de la vista, si es que soporta valor
     *
     * @return Object  objeto que representa el valor
     * @throws NoSoportaValorException en caso de que la vista no soporte el valor
     */
    @Override
    public Object getValor() throws NoSoportaValorException, ValorRequeridoException {
        int id = ((RadioGroup) view).getCheckedRadioButtonId();
        if (id != -1) {
            int idElemento = -1;
            for (RadioBoton radioBoton : radioBotons) {
                if (radioBoton.getIdPantalla() == id) {
                    idElemento = radioBoton.getIdElemento();
                }
            }
            if (isRequerido()) {
                if (idElemento != -1) {
                    return idElemento;
                } else {
                    throw new ValorRequeridoException("El campo " + titulo + " esta sin completar y es requerido", nombreVariable, titulo, view.getX(), view.getY());
                }
            } else {
                return idElemento;
            }
        } else {
            if (isRequerido()) {
                throw new ValorRequeridoException("El campo " + titulo + " esta sin completar y es requerido", nombreVariable, titulo, view.getX(), view.getY());
            } else {
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
        boolean valor = false;
        //TODO
        return nombreVariable + "=" + Boolean.toString(valor);
    }

    /**
     * Obtiene la cadena para crear la tabla sqlite
     *
     * @return String la cadena con la sentencia sql para crear la tabla en la base de datos
     */
    @Override
    public String crearTablaSqlite() {
        ColumnasTablaSql c = new ColumnasTablaSql();
        String tabla = "create table " + ColumnasTablaSql.TABLE_NAME + "(" + c.ID + " " + RecursosBaseDatos.INT_TYPE + " primary key autoincrement,"
                + c.ANCHO + " " + RecursosBaseDatos.INT_TYPE + "," + c.ALTO + " " + RecursosBaseDatos.INT_TYPE + ","
                + c.REQUERIDO + " " + RecursosBaseDatos.BOOLEAN_TYPE + ","
                + c.LAYOUT + " " + RecursosBaseDatos.INT_TYPE + "," + c.PANTALLA + " " + RecursosBaseDatos.INT_TYPE + ","
                + c.NOMBRE_VARIABLE + " " + RecursosBaseDatos.STRING_TYPE + "," + c.ORIENTACION + " " + RecursosBaseDatos.STRING_TYPE + "," + c.TITULO + " " + RecursosBaseDatos.STRING_TYPE + "," + c.HABILITADO + " " + RecursosBaseDatos.BOOLEAN_TYPE
                + ",FOREIGN KEY (" + c.LAYOUT + ") REFERENCES " + Contenedor.ColumnasTablaSql.TABLE_NAME + "(" + Contenedor.ColumnasTablaSql.ID + "), FOREIGN KEY("
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
        contentValues.put(c.NOMBRE_VARIABLE, (nombreVariable != null) ? nombreVariable : "");
        contentValues.put(c.TITULO, (titulo != null) ? titulo : "");
        contentValues.put(c.HABILITADO, habilitado);
        contentValues.put(c.ORIENTACION, (orientacion != null) ? orientacion : "vertical");
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

    }

    /**
     * Clona el objeto actual
     *
     * @return la vita clonadad
     */
    @Override
    public Vista clone() {
        RadioGrupo r=new RadioGrupo(id, ancho, alto,  idPantalla,idLayout, requerido
        ,  orientacion,  nombreVariable, titulo, habilitado);
        ArrayList<RadioBoton> botons=new ArrayList<>();
        for(RadioBoton radio:radioBotons)
        {
            botons.add((RadioBoton)radio.clone());
        }
        r.setRadioBotons(radioBotons);
        return r;
    }

    /**
     * Asigna los radio buttons correspondientes a este radio group
     *
     * @param radioBotons
     */
    public void setRadioBotons(ArrayList<RadioBoton> radioBotons) {
        this.radioBotons = radioBotons;
    }

    /**
     * Radio buttons pertenecientes al radio group
     */
    public ArrayList<RadioBoton> getRadioBotons() {
        return radioBotons;
    }

    /**
     * Clase que contiene los nombres de las columnas de la tabla
     */
    public final static class ColumnasTablaSql {
        public static final String TABLE_NAME = "RadioGrupos";
        public static final String ID = "id";
        public static final String ANCHO = "ancho";
        public static final String ALTO = "alto";
        public static final String REQUERIDO = "requerido";
        public static final String LAYOUT = "idLayout";
        public static final String PANTALLA = "idPantalla";
        public static final String NOMBRE_VARIABLE = "nombreVariable";
        public static final String TITULO = "titulo";
        public static final String HABILITADO = "habilitado";
        public static final String ORIENTACION = "orientacion";
    }
}
