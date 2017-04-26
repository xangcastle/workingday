package com.valuarte.dtracking.ElementosGraficos;

import android.content.ContentValues;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.Excepciones.NoSoportaValorException;
import com.valuarte.dtracking.Excepciones.ValorRequeridoException;
import com.valuarte.dtracking.FormularioActivity;

/**
 * Representa un check box del formulario
 * @version 1.0
 */
public class CheckCaja extends Vista{
    /**
     * Nombre de la variable con que se identifica el campo
     */
    private String nombreVariable;
    /**
     * Titulo para el campo
     */
    private String titulo;
    /**
     * valor del checkbox
     */
    private boolean valor;
    public CheckCaja() {
    }

    public CheckCaja(int ancho, int alto, int idPantalla, int idLayout, boolean requerido
    ,String nombreVariable,String titulo,boolean valor,boolean habilitado) {
        super(ancho, alto, idPantalla, idLayout, requerido,habilitado);
        this.nombreVariable=nombreVariable;
        this.titulo=titulo;
        this.valor=valor;
    }

    public CheckCaja(int id, int ancho, int alto, int idPantalla, int idLayout, boolean requerido
    ,String nombreVariable,String titulo,boolean valor,boolean habilitado) {
        super(id, ancho, alto, idPantalla, idLayout, requerido, habilitado);
        this.nombreVariable=nombreVariable;
        this.titulo=titulo;
        this.valor=valor;
    }

    /**
     * Construye el widget(campo) que va ir en el formulario
     *
     * @return View  campo del formulario
     */
    @Override
    public View construirVista(Context context) {
        FormularioActivity formularioActivity=(FormularioActivity)context;
        CheckBox checkBox=new CheckBox(context);
        checkBox.setText(titulo);
        checkBox.setChecked(valor);
        checkBox.setId(idPantalla);
        this.view=checkBox;
        return checkBox;
    }

    /**
     * Obtiene el valor de la vista, si es que soporta valor
     *
     * @return Object  objeto que representa el valor
     * @throws NoSoportaValorException en caso de que la vista no soporte el valor
     */
    @Override
    public Object getValor() throws ValorRequeridoException {
        if(valor)
        {
            return valor;
        }
        else
        {
            if(isRequerido()) {
                throw new ValorRequeridoException("El campo " + titulo + " esta sin completar y es requerido", nombreVariable,
                        titulo, view.getX(), view.getY());
            }
            else
            {
                return valor;
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
                String tabla="create table "+ ColumnasTablaSql.TABLE_NAME+"("+c.ID+" "+ RecursosBaseDatos.INT_TYPE+" primary key autoincrement ,"
                        +c.ANCHO+" "+RecursosBaseDatos.INT_TYPE+","+c.ALTO+" "+RecursosBaseDatos.INT_TYPE+","
                        +c.VALOR+" "+RecursosBaseDatos.BOOLEAN_TYPE+","+c.REQUERIDO+" "+RecursosBaseDatos.BOOLEAN_TYPE+","
                        +c.LAYOUT+" "+RecursosBaseDatos.INT_TYPE+","+c.PANTALLA+" "+RecursosBaseDatos.INT_TYPE+","
                        +c.NOMBRE_VARIABLE+" "+RecursosBaseDatos.STRING_TYPE+","+c.TITULO+" "+RecursosBaseDatos.STRING_TYPE+","+c.HABILITADO+" "+RecursosBaseDatos.BOOLEAN_TYPE
                        +",FOREIGN KEY ("+c.LAYOUT+") REFERENCES "+Contenedor.ColumnasTablaSql.TABLE_NAME+"("+Contenedor.ColumnasTablaSql.ID+"), FOREIGN KEY("
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
        ColumnasTablaSql c = new ColumnasTablaSql();
        contentValues.put(c.ANCHO,ancho);
        contentValues.put(c.ALTO,alto);
        contentValues.put(c.VALOR,valor);
        contentValues.put(c.REQUERIDO,requerido);
        contentValues.put(c.LAYOUT,idLayout);
        contentValues.put(c.PANTALLA,idPantalla);
        contentValues.put(c.NOMBRE_VARIABLE,nombreVariable);
        contentValues.put(c.TITULO,titulo);
        contentValues.put(c.HABILITADO,habilitado);
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
            valor=((CheckBox)view).isChecked();
        }
    }

    /**
     * Clona el objeto actual
     *
     * @return la vita clonadad
     */
    @Override
    public Vista clone() {
        return new CheckCaja(id, ancho, alto, idPantalla, idLayout,  requerido
        ,nombreVariable,titulo,valor,habilitado);
    }

    /**
     * Clase que contiene los nombres de las columnas de la tabla
     */
    public final static class ColumnasTablaSql {
        public static final String TABLE_NAME = "CheckCajas";
        public static final String ID = "id";
        public static final String ANCHO = "ancho";
        public static final String ALTO="alto";
        public static final String VALOR="valorDefecto";
        public static final String REQUERIDO="requerido";
        public static final String LAYOUT="idLayout";
        public static final String PANTALLA="idPantalla";
        public static final String NOMBRE_VARIABLE="nombreVariable";
        public static final String TITULO="titulo";
        public static final String HABILITADO="habilitado";
    }
}
