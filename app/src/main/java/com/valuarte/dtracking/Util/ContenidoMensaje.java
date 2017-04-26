package com.valuarte.dtracking.Util;

import android.content.ContentValues;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Representa el contenido del mensaje que se obtiene al consultar el web serviec
 * http://www.deltacopiers.com/dtracking/movil/mensajeria/
 *
 * @version 1.0
 */
public class ContenidoMensaje {
    /**
     * Identificador del contenido del mensaje en la base de datos local
     */
    private int id;
    /**
     * Mensaje que trae la respuesta del web service
     */
    private String mensaje;
    /**
     * Gestiones que se eliminaron
     */
    private JSONArray gestionesElimninadas;
    /**
     * Indica el estado en el que se encuentra el mensaje
     */
    private int estado;
    /**
     * Indica si es un contenido que esta sin enviar al número que esta asociado
     */
    public static final int SINENVIAR = 1;
    /**
     * Indica si es un contenido que ha sido enviado al número que esta asociado
     */
    public static final int ENVIADO = 2;
    /**
     * Indica si es un contenido que se recibio desde un mensaje de texto
     */
    public static final int RECIBIDO = 3;
    /**
     * Indica que el mensaje solo se debe enviar, no se debe mostrar
     */
    public static final int SOLOENVIAR=4;
    /**
     * Indica que el mensaje se ha enviado via sms, pero que no se debe mostrar
     */
    public  static  final int NoMOSTRAR=5;
    /**
     * número al que se va a enviar el mensaje
     */
    private String numero;
    /**
     * Fecha del contenido del mensaje
     */
    private String fecha;
    /**
     * codigo de usuario
     */
    private String codigoUsuario;

    public ContenidoMensaje(String mensaje, JSONArray gestionesElimninadas, int estado, String numero,
                            String fecha,String codigoUsuario) {
        this.mensaje = mensaje;
        this.gestionesElimninadas = gestionesElimninadas;
        this.estado = estado;
        this.numero = numero;
        this.codigoUsuario = codigoUsuario;
        this.fecha = fecha;
    }

    public ContenidoMensaje(int id, String mensaje, JSONArray gestionesElimninadas, int estado,
                            String numero, String fecha, String codigoUsuario) {
        this.id = id;
        this.mensaje = mensaje;
        this.gestionesElimninadas = gestionesElimninadas;
        this.estado = estado;
        this.numero = numero;
        this.fecha = fecha;
        this.codigoUsuario = codigoUsuario;
    }

    /**
     * Mensaje que trae la respuesta del web service
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Gestiones que se eliminaron
     */
    public JSONArray getGestionesElimninadas() {
        return gestionesElimninadas;
    }

    /**
     * número al que se va a enviar el mensaje
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Crea una sentencia sql para crear la tabla de contenidos de mensaje
     *
     * @return cadena con la sentencia
     */
    public static String crearTablaSqlite() {
        ColumnasTablaSql c = new ColumnasTablaSql();
        String tabla = "create table " + c.TABLENAME + "(" + c.ID + " " + RecursosBaseDatos.INT_TYPE + " primary key autoincrement,"
                + c.FECHA + " " + RecursosBaseDatos.DATE_TIME_TYPE + "," + c.ESTADO + " " + RecursosBaseDatos.INT_TYPE + ","
                + c.GESTIONESELIMINADAS + " " + RecursosBaseDatos.STRING_TYPE + "," + c.NUMERO + " " +
                RecursosBaseDatos.STRING_TYPE + "," + c.MENSAJE + " " + RecursosBaseDatos.STRING_TYPE +
                "," + c.CODIGOUSUARIO + " " + RecursosBaseDatos.STRING_TYPE + ")";
        return tabla;
    }

    /**
     * Obtiene el nombre de la tabla
     *
     * @return el nombre de la tabla
     */
    public String getNombreTabla() {
        return ColumnasTablaSql.TABLENAME;
    }

    /**
     * Obtiene el contenedor de valores para registrar el objeto actual en la base de datos
     *
     * @return el contenedor de valores
     */
    public ContentValues getContenedorValores() {
        ContentValues contentValues = new ContentValues();
        ColumnasTablaSql c = new ColumnasTablaSql();
        contentValues.put(c.FECHA, getFecha());
        contentValues.put(c.ESTADO, getEstado());
        contentValues.put(c.GESTIONESELIMINADAS, gestionesElimninadas.toString());
        contentValues.put(c.NUMERO,numero);
        contentValues.put(c.MENSAJE, mensaje);
        contentValues.put(c.CODIGOUSUARIO,codigoUsuario);
        return contentValues;
    }

    /**
     * Identificador del contenido del mensaje en la base de datos local
     */
    public int getId() {
        return id;
    }

    /**
     * Indica el estado en el que se encuentra el mensaje
     */
    public int getEstado() {
        return estado;
    }

    /**
     * Fecha del contenido del mensaje
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Nombre de las columnas de la tabla de contenidos de mensaje
     */
    public static final class ColumnasTablaSql {
        public static final String TABLENAME = "contenidoMensajes";
        public static final String ID = "id";
        public static final String FECHA = "fecha";
        public static final String ESTADO = "estado";
        public static final String GESTIONESELIMINADAS = "gestionesEliminadas";
        public static final String NUMERO = "numero";
        public static final String MENSAJE = "mensaje";
        public static final String CODIGOUSUARIO = "codigoUsuario";
    }

    /**
     * Construye el json que se va a enviar via sms
     * @return el objeto json
     */
    public JSONObject construirJsonParaEnviar()
    {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("g",gestionesElimninadas);
            jsonObject.put("c",codigoUsuario);
            jsonObject.put("m",mensaje);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContenidoMensaje that = (ContenidoMensaje) o;

        return id == that.id;

    }
}
