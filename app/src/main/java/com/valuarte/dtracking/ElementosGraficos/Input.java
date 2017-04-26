package com.valuarte.dtracking.ElementosGraficos;

import android.content.ContentValues;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.Excepciones.NoSoportaValorException;
import com.valuarte.dtracking.Excepciones.ValorRequeridoException;
import com.valuarte.dtracking.R;

/**
 * Representa un campo de tipo Input en el formulario
 * @version 1.0
 */
public class Input extends Vista{
    /**
     * Representa la constante del tipo de entrada, perteneciente a la clase Input
     */
    private int tipoEntrada;
    /**
     * El valor que contiene el input
     */
    private String valor;
    /**
     * La longitud máxima que soporta el input
     */
    private int longitudMaxima;
    /**
     * Nombre de la variable, a la que reponde el campo
     */
    private String nombreVariable;
    /**
     * Titulo del campo
     */
    private String titulo;
    public Input(){}

    public Input(int ancho, int alto, int idPantalla, int idLayout, boolean requerido,
                 int tipoEntrada,String valor,int longitudMaxima,String nombreVariable,String titulo,
                 boolean habilitado) {
        super(ancho, alto, idPantalla, idLayout, requerido, habilitado);
        this.tipoEntrada=tipoEntrada;
        this.valor=valor;
        this.longitudMaxima=longitudMaxima;
        this.nombreVariable=nombreVariable;
        this.titulo=titulo;
    }

    public Input(int id, int ancho, int alto, int idPantalla, int idLayout, boolean requerido
    ,int tipoEntrada,String valor,int longitudMaxima,String nombreVariable,String titulo,boolean habilitado) {
        super(id, ancho, alto, idPantalla, idLayout, requerido,habilitado);
        this.tipoEntrada=tipoEntrada;
        this.valor=valor;
        this.longitudMaxima=longitudMaxima;
        this.nombreVariable=nombreVariable;
        this.titulo=titulo;
    }

    /**
     * Construye el widget(campo) que va ir en el formulario
     *
     * @return View  campo del formulario
     */
    @Override
    public View construirVista(Context context) {

        TextInputLayout textInputLayout=new TextInputLayout(context);
        EditText editText=new EditText(context);
        editText.setText(valor);
        editText.setId(idPantalla);
        editText.setEnabled(habilitado);
        editText.setHint(titulo);
        editText.setInputType(tipoEntrada);
        editText.setSingleLine(true);
        editText.setBackgroundResource(R.drawable.background_edittext);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(longitudMaxima);
        editText.setFilters(FilterArray);
        editText.setFocusableInTouchMode(true);
        this.view=editText;
        TextInputLayout.LayoutParams l=new TextInputLayout.LayoutParams(ancho, alto);
        l.setMargins(10,30,10,10);
        textInputLayout.addView(editText, l);
        return textInputLayout;
    }

    /**
     * Obtiene el valor de la vista, si es que soporta valor
     *
     * @return Object  objeto que representa el valor
     * @throws NoSoportaValorException en caso de que la vista no soporte el valor
     */
    @Override
    public Object getValor() throws NoSoportaValorException,ValorRequeridoException {
        if(valor!=null)
        {
            if(isRequerido())
            {
                if(!valor.trim().equals(""))
                {
                    return valor;
                }
                else
                {
                    throw new ValorRequeridoException("EL campo "+titulo+" esta sin completar y es requerido",nombreVariable,titulo
                            ,view.getX(),view.getY());
                }
            }
            else
            {
                return valor;
            }

        }
        else
        {
            if(isRequerido())
            {
                throw new ValorRequeridoException("EL campo "+titulo+" esta sin completar y es requerido",nombreVariable,titulo
                ,view.getX(),view.getY());
            }
            else
            {
                return "";
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
        return nombreVariable+"="+valor;
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
                +c.VALOR+" "+RecursosBaseDatos.STRING_TYPE+","+c.REQUERIDO+" "+RecursosBaseDatos.BOOLEAN_TYPE+","
                +c.LAYOUT+" "+RecursosBaseDatos.INT_TYPE+","+c.PANTALLA+" "+RecursosBaseDatos.INT_TYPE+","
                +c.NOMBRE_VARIABLE+" "+RecursosBaseDatos.STRING_TYPE+","+c.TITULO+" "+RecursosBaseDatos.STRING_TYPE+","+c.LONGITUD_MAXIMA+" "+RecursosBaseDatos.INT_TYPE
                +","+c.TIPO_ENTRADA+" "+RecursosBaseDatos.INT_TYPE+","+c.HABILITADO+" "+RecursosBaseDatos.BOOLEAN_TYPE
                +",FOREIGN KEY ("+c.LAYOUT+") REFERENCES "+Contenedor.ColumnasTablaSql.TABLE_NAME+"("+Contenedor.ColumnasTablaSql.ID+"), FOREIGN KEY("
                +c.PANTALLA+") REFERENCES IdsPantalla(id)"+"FOREIGN KEY ("+c.TIPO_ENTRADA+")"+"REFERENCES TipoEntradas(id))";
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
        contentValues.put(c.VALOR,(valor!=null)?valor:"");
        contentValues.put(c.REQUERIDO,requerido);
        contentValues.put(c.LAYOUT,idLayout);
        contentValues.put(c.PANTALLA,idPantalla);
        contentValues.put(c.NOMBRE_VARIABLE,(nombreVariable!=null)?nombreVariable:"");
        contentValues.put(c.TITULO,(titulo!=null)?titulo:"");
        contentValues.put(c.TIPO_ENTRADA,tipoEntrada);
        contentValues.put(c.LONGITUD_MAXIMA, longitudMaxima);
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
        if(view!=null) {
            valor = ((EditText)view).getText().toString();
        }
    }

    /**
     * Clona el objeto actual
     *
     * @return la vita clonadad
     */
    @Override
    public Vista clone() {
        return new Input( id,  ancho,  alto, idPantalla, idLayout,requerido
        ,tipoEntrada,valor,longitudMaxima,nombreVariable,titulo,habilitado);
    }

    /**
     * Clase que contiene los nombres de las columnas de la tabla
     */
    public final static class ColumnasTablaSql {
        public static final String TABLE_NAME = "Inputs";
        public static final String ID = "id";
        public static final String ANCHO = "ancho";
        public static final String ALTO="alto";
        public static final String VALOR="valorDefecto";
        public static final String REQUERIDO="requerido";
        public static final String LAYOUT="idLayout";
        public static final String PANTALLA="idPantalla";
        public static final String NOMBRE_VARIABLE="nombreVariable";
        public static final String TITULO="titulo";
        public static final String LONGITUD_MAXIMA="longitudMaxima";
        public static final String TIPO_ENTRADA="tipoEntrada";
        public static final String HABILITADO="habilitado";
    }
}
