package com.valuarte.dtracking.MensajeTextos;

import android.content.ContentValues;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;

import org.json.JSONObject;

/**
 * Representa un sms
 * @version 1.0
 */
public class Sms {
    /**
     * Numero al que va dirigido el mensaje
     */
    private int id;
    /**
     * fecha del mensaje
     */
    private String fecha;
    /**
     * Contenido del mensaje
     */
    private JSONObject jsonObject;
    /**
     * Indica si el mensaje fue enviado o no
     */
    private boolean enviado;
    /**
     * numero del que viene el mensaje
     */
    private String desde;

    public Sms(String fecha, JSONObject jsonObject, boolean enviado, String desde) {
        this.fecha = fecha;
        this.jsonObject = jsonObject;
        this.enviado = enviado;
        this.desde = desde;
    }

    public Sms(int id, String fecha, JSONObject jsonObject, String desde, boolean enviado) {
        this.setId(id);
        this.fecha = fecha;
        this.jsonObject = jsonObject;
        this.desde = desde;
        this.enviado = enviado;
    }

    /**
     * Numero al que va dirigido el mensaje
     */
    public int getId() {
        return id;
    }

    /**
     * fecha del mensaje
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Contenido del mensaje
     */
    public JSONObject getJsonObject() {
        return jsonObject;
    }

    /**
     * Indica si el mensaje fue enviado o no
     */
    public boolean isEnviado() {
        return enviado;
    }

    /**
     * numero del que viene el mensaje
     */
    public String getDesde() {
        return desde;
    }

    /**
     * Crea una sentencia sql para crear la tabla de sms
     *
     * @return cadena con la sentencia
     */
    public static String crearTablaSqlite() {
        ColumnasTablaSql c = new ColumnasTablaSql();
        String tabla = "create table " + c.TABLENAME + "(" + c.ID + " " + RecursosBaseDatos.INT_TYPE + " primary key autoincrement,"
                + c.FECHA + " " + RecursosBaseDatos.DATE_TIME_TYPE + "," + c.ENVIADO + " " + RecursosBaseDatos.BOOLEAN_TYPE + ","
                + c.CUERPO + " " + RecursosBaseDatos.STRING_TYPE + "," + c.DESDE + " " + RecursosBaseDatos.STRING_TYPE + ")";
        return tabla;
    }



    /**
     * Obtiene el nombre de la tabla
     * @return  el nombre de la tabla
     */
    public String getNombreTabla()
    {
        return ColumnasTablaSql.TABLENAME;
    }
    /**
     * Obtiene el contenedor de valores para registral el objeto actual en la base de datos
     * @return el contenedor de valores
     */
    public ContentValues getContenedorDeValores()
    {
        ContentValues contentValues=new ContentValues();
        ColumnasTablaSql c=new ColumnasTablaSql();
        contentValues.put(c.FECHA,fecha);
        contentValues.put(c.ENVIADO,enviado);
        contentValues.put(c.CUERPO,jsonObject.toString());
        contentValues.put(c.DESDE,desde);
        return contentValues;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Nombre de las columnas de la tabla de sms
     */
    public static final class ColumnasTablaSql
    {
        public static final String TABLENAME="sms";
        public static final String ID="id";
        public static final String FECHA="fecha";
        public static final String ENVIADO="enviado";
        public static final String CUERPO="cuerpo";
        public static final String DESDE="desde";
    }


}
