package com.valuarte.dtracking.Util;

import android.content.ContentValues;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;

/**
 * Representa el usuario que se loguea en la aplicacion
 *
 * @version 1.0
 */
public class Usuario {
    private int id;
    /**
     * Nombre de usuario
     */
    private String nommbreUsuario;
    /**
     * Nombre
     */
    private String nombre;
    /**
     * Ruta de la foto
     */
    private String foto;
    /**
     * Numero del usuario
     */
    private String numero;
    /**
     * Estado del usuario
     */
    private int estado;
    /**
     * Contraseña del usuario
     */
    private String contrasenia;
    /**
     * Conexiones al servidor
     */
    private String conexionesServidor;
    //Constantes para saber el estado del usuario registrado
    public static final int LOGUEADO = 1;
    public static final int DESLOGUEADO = 2;
    /**
     * intervalo para sincronizar via gps la posicion actual
     */
    private int intervaloSincroinizacionGPS;
    /**
     * Constante de intervalo de sincronizacion
     */
    public static final int INTERVALOSINCRONIZACIONGPS=900000;
    public Usuario(String nommbreUsuario, String nombre, String foto, String numero, String contrasenia,
                   String conexionesServidor, int intervaloSincroinizacionGPS,int estado) {
        this.nommbreUsuario = nommbreUsuario;
        this.nombre = nombre;
        this.foto = foto;
        this.numero = numero;
        this.estado = estado;
        this.contrasenia = contrasenia;
        this.conexionesServidor = conexionesServidor;
        this.intervaloSincroinizacionGPS=intervaloSincroinizacionGPS;
    }

    public Usuario(int id, String nommbreUsuario, String nombre, String foto, String numero, String contrasenia,
                   String conexionesServidor,int intervaloSincroinizacionGPS, int estado) {
        this.id = id;
        this.nommbreUsuario = nommbreUsuario;
        this.nombre = nombre;
        this.foto = foto;
        this.numero = numero;
        this.estado = estado;
        this.contrasenia = contrasenia;
        this.conexionesServidor = conexionesServidor;
        this.intervaloSincroinizacionGPS=intervaloSincroinizacionGPS;
    }

    /**
     * Obtiene el contenedor de valores  del objeto
     *
     * @return el contenedor de valores
     */
    public ContentValues getContenedorValores() {
        ContentValues contentValues = new ContentValues();
        ColumnasTablaSql c = new ColumnasTablaSql();
        contentValues.put(c.ID, getId());
        contentValues.put(c.NOMBREUSUARIO, getNommbreUsuario());
        contentValues.put(c.NOMBRE, nombre);
        contentValues.put(c.FOTO, foto);
        contentValues.put(c.NUMERO, getNumero());
        contentValues.put(c.ESTADO, getEstado());
        contentValues.put(c.CONTRASENIA, contrasenia);
        contentValues.put(c.CONEXIONESSERVIDOR, getConexionesServidor());
       contentValues.put(c.INTERVALOGPS,intervaloSincroinizacionGPS);
        return contentValues;
    }

    public static final String construirTablaSqlite() {
        ColumnasTablaSql c = new ColumnasTablaSql();
        String tabla = "create table " + c.TABLENAME + " (" + c.ID + " " + RecursosBaseDatos.INT_TYPE + " primary key ,"
                + c.NOMBREUSUARIO + " " + RecursosBaseDatos.STRING_TYPE + "," + c.NOMBRE + " " + RecursosBaseDatos.STRING_TYPE + ","
                + c.FOTO + " " + RecursosBaseDatos.STRING_TYPE + "," + c.NUMERO + " " + RecursosBaseDatos.STRING_TYPE +
                "," + c.ESTADO + " " + RecursosBaseDatos.INT_TYPE + "," + c.CONTRASENIA + " " +
                RecursosBaseDatos.STRING_TYPE + "," + c.INTERVALOGPS + " " + RecursosBaseDatos.INT_TYPE + "," + c.CONEXIONESSERVIDOR + " " + RecursosBaseDatos.STRING_TYPE + ")";
        return tabla;
    }

    /**
     * Obtiene el nombre de la tabla
     *
     * @return la cadena con el nombre de la variable
     */
    public String getNombreTabla() {
        return ColumnasTablaSql.TABLENAME;
    }

    /**
     * Nombre de usuario
     */
    public String getNommbreUsuario() {
        return nommbreUsuario;
    }

    /**
     * Estado del usuario
     */
    public int getEstado() {
        return estado;
    }

    /**
     * Contraseña del usuario
     */
    public String getContrasenia() {
        return contrasenia;
    }

    /**
     * Identificador del usuario
     */
    public int getId() {
        return id;
    }

    /**
     * Son las conexiones al servidor
     */
    public String getConexionesServidor() {
        return conexionesServidor;
    }

    /**
     * Numero del usuario
     */
    public String getNumero() {
        return numero;
    }

    /**
     * intervalo para sincronizar via gps la posicion actual
     */
    public long getIntervaloSincroinizacionGPS() {
        return intervaloSincroinizacionGPS;
    }

    public final static class ColumnasTablaSql {
        public static final String TABLENAME = "Usuarios";
        public static final String ID = "id";
        public static final String NOMBREUSUARIO = "nombreUsuario";
        public static final String NOMBRE = "nombre";
        public static final String FOTO = "foto";
        public static final String NUMERO = "numero";
        public static final String CONTRASENIA = "contrasenia";
        public static final String ESTADO = "estado";
        public static final String CONEXIONESSERVIDOR = "conexionesServidor";
        public static final String INTERVALOGPS="intervaloGps";
    }

}
