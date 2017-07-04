package com.valuarte.dtracking.ElementosGraficos;

import android.content.ContentValues;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.Excepciones.NoSoportaValorException;

import java.util.ArrayList;

/**
 * Representa un layout general
 * @version 1.0
 */
public class Contenedor extends Vista{
    /**
     * Representa el tipo de contenedor(layout) que se va a crear
     */
    private String tipoLayout;
    /**
     * La orientacion del layout
     */
    private String orientacion;
    private int idFormulario;
    public Contenedor(int ancho, int alto, int idPantalla, int idLayout, boolean requerido,
                      String tipoLayout,String orientacion,boolean habilitado,int idFormulario) {
        super(ancho, alto, idPantalla, idLayout, requerido, habilitado);
        this.tipoLayout=tipoLayout;
        this.orientacion=orientacion;
        this.setIdFormulario(idFormulario);
    }

    public Contenedor(int id, int ancho, int alto, int idPantalla, int idLayout, boolean requerido,
                      String tipoLayout,String orientacion,boolean habilitado,int idFormulario) {
        super(id, ancho, alto, idPantalla, idLayout, requerido,habilitado);
        this.tipoLayout=tipoLayout;
        this.orientacion=orientacion;
        this.setIdFormulario(idFormulario);
    }

    public Contenedor(){}

    /**
     * Construye el widget(campo) que va ir en el formulario
     *
     * @return View  campo del formulario
     */
    @Override
    public View construirVista(Context context) {
        LinearLayout linearLayout=new LinearLayout(context);
        linearLayout.setId(idPantalla);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        for(Vista vista:vistas)
        {
            linearLayout.addView(vista.construirVista(context),new ViewGroup.LayoutParams(vista.getAncho(),vista.getAlto()));
        }
        this.view=linearLayout;
        return linearLayout;
    }

    /**
     * Obtiene el valor de la vista, si es que soporta valor
     *
     * @return Object  objeto que representa el valor
     * @throws NoSoportaValorException en caso de que la vista no soporte el valor
     */
    @Override
    public Object getValor() throws NoSoportaValorException {
        return null;
    }

    /**
     * Obtiene el valor, con su respectivo valor en formato json
     *
     * @return String  cadena del valor en formato json, o vacio en caso de que no soporte valor
     */
    @Override
    public String getValorJson() {
        return null;
    }

    /**
     * Obtiene la cadena para crear la tabla sqlite
     *
     * @return String la cadena con la sentencia sql para crear la tabla en la base de datos
     */
    @Override
    public String crearTablaSqlite() {
        ColumnasTablaSql c=new ColumnasTablaSql();
        String tabla="create table "+c.TABLE_NAME+"("+c.ID+" "+ RecursosBaseDatos.INT_TYPE+" primary key autoincrement,"
                +c.ORIENTACION+" "+RecursosBaseDatos.STRING_TYPE +","+c.TIPO+" "+RecursosBaseDatos.STRING_TYPE+","+c.ANCHO+" "+RecursosBaseDatos.INT_TYPE+","
                +c.ALTO+" "+RecursosBaseDatos.INT_TYPE+","+c.PANTALLA+" "+RecursosBaseDatos.INT_TYPE+","+c.HABILITADO+" "+RecursosBaseDatos.BOOLEAN_TYPE+","
                +c.LAYOUT+" "+RecursosBaseDatos.INT_TYPE+","+c.FORMULARIO+" "+RecursosBaseDatos.INT_TYPE+","+"FOREIGN KEY ("+c.LAYOUT+") REFERENCES "+Contenedor.ColumnasTablaSql.TABLE_NAME+"("+Contenedor.ColumnasTablaSql.ID+"), FOREIGN KEY("
                +c.PANTALLA+") REFERENCES IdsPantalla(id),"+"FOREIGN KEY ("+c.FORMULARIO+") REFERENCES "+Formulario.ColumnasTablaSql.TABLE_NAME+"("+Formulario.ColumnasTablaSql.ID+"))";
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
        if(idLayout!=-1 && idLayout!=0)
        {
            contentValues.put(c.LAYOUT,idLayout);
        }
        if(idFormulario!=-1 && idFormulario!=0)
        {
            contentValues.put(c.FORMULARIO,idFormulario);
        }
        contentValues.put(c.ANCHO,ancho);
        contentValues.put(c.ALTO,alto);
        contentValues.put(c.ORIENTACION,orientacion);
        contentValues.put(c.PANTALLA,idPantalla);
        contentValues.put(c.HABILITADO,habilitado);
        contentValues.put(c.TIPO,tipoLayout);
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
        return "";
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
        Contenedor c=new Contenedor(id, ancho, alto, idPantalla, idLayout, requerido,
                tipoLayout,orientacion,habilitado,idFormulario);
        ArrayList<Vista> vis=new ArrayList<>();
        for(Vista v:vistas)
        {
            vis.add(v.clone());
        }
        c.agregarVistas(vis);
        return c;
    }

    public void setIdFormulario(int idFormulario) {
        this.idFormulario = idFormulario;
    }

    public  final static class ColumnasTablaSql
    {
        public static final String TABLE_NAME = "Contenedores";
        public static final String ID="id";
        public static final String ORIENTACION="orientacion";
        public static final String ANCHO="ancho";
        public static final String ALTO="alto";
        public static final String PANTALLA="idPantalla";
        public static final String LAYOUT="idLayout";
        public static final String HABILITADO="habilitado";
        public static final String FORMULARIO="idFormulario";
        public static final String TIPO="tipoLayout";
    }


}
