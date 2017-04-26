package com.valuarte.dtracking.ElementosGraficos;

import android.content.ContentValues;
import android.content.Context;
import android.view.View;
import android.widget.RadioButton;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.Excepciones.NoSoportaValorException;

/**
 * Representa un campo radio button en el formulario
 * @version 1.0
 */
public class RadioBoton extends Vista{
    /**
     * El valor que posee el radio button
     */
    private boolean valor;
    /**
     * nhombre de la variable con que se identifica el campo
     */
    private String nombreVariable;
    /**
     * Titulo correspondiente al radio button
     */
    private String titulo;
    /**
     * Identificador del radio button, desde el web service
     */
    private int idElemento;
    private int idGroup;
    public RadioBoton() {
    }

    public RadioBoton(int ancho, int alto, int idPantalla, int idLayout, boolean requerido
    ,boolean valor,String nombreVariable,String titulo,boolean habilitado,int idElemento,int idGroup) {
        super(ancho, alto, idPantalla, idLayout, requerido,habilitado);
        this.valor=valor;
        this.nombreVariable=nombreVariable;
        this.titulo=titulo;
        this.idElemento=idElemento;
        this.setIdGroup(idGroup);
    }

    public RadioBoton(int id, int ancho, int alto, int idPantalla, int idLayout, boolean requerido
    ,boolean valor,String nombreVariable,String titulo,boolean habilitado,int idElemento,int idGroup) {
        super(id, ancho, alto, idPantalla, idLayout, requerido, habilitado);
        this.valor=valor;
        this.nombreVariable=nombreVariable;
        this.titulo=titulo;
        this.idElemento=idElemento;
        this.setIdGroup(idGroup);
        this.setIdGroup(idGroup);
    }

    /**
     * Construye el widget(campo) que va ir en el formulario
     *
     * @return View  campo del formulario
     */
    @Override
    public View construirVista(Context context) {
        RadioButton radioButton=new RadioButton(context);
        radioButton.setText(titulo);
        radioButton.setEnabled(habilitado);
        radioButton.setId(idPantalla);
        this.view=radioButton;
        return radioButton;
    }

    /**
     * Obtiene el valor de la vista, si es que soporta valor
     *
     * @return Object  objeto que representa el valor
     * @throws NoSoportaValorException en caso de que la vista no soporte el valor
     */
    @Override
    public Object getValor() throws NoSoportaValorException {
        return new Boolean(valor);
    }

    /**
     * Obtiene el valor del radio button por defecto
     * @return
     */
    public boolean getVal()
    {
        return valor;
    }
    /**
     * Obtiene el valor, con su respectivo valor en formato json
     *
     * @return String  cadena del valor en formato json, o vacio en caso de que no soporte valor
     */
    @Override
    public String getValorJson() {
        return nombreVariable+"="+Boolean.toString(valor);
    }

    /**
     * Obtiene la cadena para crear la tabla sqlite
     *
     * @return String la cadena con la sentencia sql para crear la tabla en la base de datos
     */
    @Override
    public String crearTablaSqlite() {
        ColumnasTablaSql c=new ColumnasTablaSql();
        String tabla="create table "+ ColumnasTablaSql.TABLE_NAME+"("+c.ID+" "+ RecursosBaseDatos.INT_TYPE+" primary key autoincrement,"
                +c.ANCHO+" "+RecursosBaseDatos.INT_TYPE+","+c.ALTO+" "+RecursosBaseDatos.INT_TYPE+","
                +c.VALOR+" "+RecursosBaseDatos.INT_TYPE+","
                +c.GROUP+" "+RecursosBaseDatos.INT_TYPE+","+c.LAYOUT+" "+RecursosBaseDatos.INT_TYPE+","
                +c.IDELEMENTO+" "+RecursosBaseDatos.INT_TYPE+","+c.PANTALLA+" "+RecursosBaseDatos.INT_TYPE+","
                +c.TITULO+" "+RecursosBaseDatos.STRING_TYPE+","+c.HABILITADO+" "+RecursosBaseDatos.BOOLEAN_TYPE
                +",FOREIGN KEY ("+c.GROUP+") REFERENCES "+RadioGrupo.ColumnasTablaSql.TABLE_NAME+"("+RadioGrupo.ColumnasTablaSql.ID+"), FOREIGN KEY("
                +c.PANTALLA+") REFERENCES IdsPantalla(id))";
        return tabla;
    }

    /**
     * Obtiene el contenedor de valores del objeto actual para guardarlo en la base de datos
     *
     * @return ContentValues   el contenedor de valores del objeto actual
     */
    @Override
    public ContentValues getContenedorValores() {
        ContentValues contentValues=new ContentValues();
        ColumnasTablaSql c=new ColumnasTablaSql();
        contentValues.put(c.ANCHO,ancho);
        contentValues.put(c.ALTO,alto);
        contentValues.put(c.VALOR,valor);
        contentValues.put(c.PANTALLA,idPantalla);
        contentValues.put(c.TITULO,(titulo!=null)?titulo:"");
        contentValues.put(c.HABILITADO,habilitado);
        contentValues.put(c.GROUP,idGroup);
        contentValues.put(c.IDELEMENTO, getIdElemento());
        contentValues.put(c.LAYOUT,idLayout);
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
        if(view!=null)
        {
            valor=((RadioButton)view).isChecked();
        }
    }

    /**
     * Clona el objeto actual
     *
     * @return la vita clonadad
     */
    @Override
    public Vista clone() {
        return new RadioBoton(id,ancho,alto, idPantalla, idLayout,requerido
        ,valor,nombreVariable,titulo,habilitado,idElemento,idGroup);
    }

    public void setIdGroup(int idGroup) {
        this.idGroup = idGroup;
    }

    /**
     * Identificador del radio button, desde el web service
     */
    public int getIdElemento() {
        return idElemento;
    }

    /**
     * Clase que contiene los nombres de las columnas de la tabla
     */
    public final static class ColumnasTablaSql {
        public static final String TABLE_NAME = "RadioBotones";
        public static final String ID = "id";
        public static final String ANCHO = "ancho";
        public static final String ALTO="alto";
        public static final String VALOR="valorDefecto";
        public static final String GROUP="idGroup";
        public static final String PANTALLA="idPantalla";
        public static final String TITULO="titulo";
        public static final String HABILITADO="habilitado";
        public static final String IDELEMENTO="idELemento";
        public static final String LAYOUT="idLayout";
    }
}
