package com.valuarte.dtracking.ElementosGraficos;

import android.content.ContentValues;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Representa una gestion
 * @version 1.0
 */
public class Gestion implements Serializable{
    /**
     * Zona en la que se realiza la gestion
     */
    private String zona;
    /**
     * Es el tipo de gestion al que pertenece la gestion
     */
    private int tipoGestion;
    /**
     * Departamento en donde se realiza la gestion
     */
    private String departamento;
    /**
     * Dirección en la que se va a reaalizar la gestion
     */
    private String direccion;
    /**
     * identificador de la gestion desde el web service
     */
    private int idgestion;
    /**
     * barrio en el que se va a realizar la gestion
     */
    private String barrio;
    /**
     * Municipio en el que se va a realizar
     */
    private String municipio;
    /**
     * Destinatario de la gestion
     */
    private String destinatario;
    private int id;
    /**
     * Indica si la gestion es un borrador
     */
    private boolean esBorrador;
    /**
     * Formulario al que se encuentra asociado la gestion
     */
    private Formulario formulario;
    /**
     * Telefono de la gestion
     */
    private String telefono;
    /**
     * El estado en el que esta la gestion
     */
    private int estadoGestion;
    /**
     * Si no se ha enviado la gestion
     */
    public static final int SINENVIAR=1;
    /**
     * si ya se envio completamente la gestion
     */
    public static final int ENVIADO=2;
    /**
     * si falta por enviar las imagenes
     */
    public static final int SINENVIARIMAGENES=3;
    /**
     * Fecha en la que se registro la gestion
     */
    private String fecha;
    /**
     * Latitud en la que se registro la gestion
     */
    private double latitud;
    /**
     * Latitud en la que se registro la gestion
     */
    private double longitud;
    /**
     * El codigo de barras asociado a la gestion
     */
    private String codigoBarras;
    public Gestion(String zona, int tipoGestion, String departamento, String direccion, String barrio,
                   int idgestion, String municipio, String destinatario, String telefono,int estadoGestion,
                   String fecha,double latitud,double longitud,String codigoBarras,boolean esBorrador,
                   Formulario formulario) {
        this.zona = zona;
        this.tipoGestion = tipoGestion;
        this.departamento = departamento;
        this.direccion = direccion;
        this.barrio = barrio;
        this.idgestion = idgestion;
        this.municipio = municipio;
        this.destinatario = destinatario;
        this.telefono=telefono;
        this.setEsBorrador(esBorrador);
        this.setFormulario(formulario);
        this.estadoGestion=estadoGestion;
        this.setFecha(fecha);
        this.setLatitud(latitud);
        this.setLongitud(longitud);
        this.codigoBarras=codigoBarras;
    }

    public Gestion(String zona, int tipoGestion, String departamento, String direccion, int idgestion,
                   String barrio, String municipio, String destinatario,String telefono, int estadoGestion,
            String fecha,double latitud,double longitud,String codigoBarras,boolean esBorrador, int id, Formulario formulario) {
        this.zona = zona;
        this.tipoGestion = tipoGestion;
        this.departamento = departamento;
        this.direccion = direccion;
        this.idgestion = idgestion;
        this.barrio = barrio;
        this.municipio = municipio;
        this.destinatario = destinatario;
        this.telefono=telefono;
        this.setEsBorrador(esBorrador);
        this.setId(id);
        this.setFormulario(formulario);
        this.estadoGestion=estadoGestion;
        this.setFecha(fecha);
        this.setLongitud(longitud);
        this.setLatitud(latitud);
        this.codigoBarras=codigoBarras;
    }

    /**
     * Genera una fecha en formato String YYYY-MM-DD HH:MM:SS desde un calendar
     * @param calendar el calendar a convertir
     * @return una cadena con el formato YYYY-MM-DD HH:MM:SS
     */
    public static String generarFechaDesdeCalendar(Calendar calendar)
    {
        String fecha="";
        if(calendar!=null)
        {
            fecha=calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH)
                    +" "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
        }
        return fecha;
    }
    /**
     * Crea sentencia sql para la tabla de gestiones
     * @return cadena con la sentencia sql
     */
    public static final String crearTablaSlqite()
    {
        ColumnasTablaSql clm=new ColumnasTablaSql();
        String tabla=" create table "+clm.TABLENAME+" ("+clm.ID+" "+ RecursosBaseDatos.INT_TYPE+" primary key,"
                +clm.ZONA+" "+RecursosBaseDatos.STRING_TYPE+","+clm.IDGESTION+" "+RecursosBaseDatos.INT_TYPE+","
                +clm.DEPARTAMENTO+" "+RecursosBaseDatos.STRING_TYPE+","+clm.DIRECCION+" "+RecursosBaseDatos.STRING_TYPE+","
                +clm.BARRIO+" "+RecursosBaseDatos.STRING_TYPE+","+clm.MUNICIPIO+" "+RecursosBaseDatos.STRING_TYPE+","
                +clm.DESTINATARIO+" "+RecursosBaseDatos.STRING_TYPE+","+clm.CODIGOBARRAS+" "+RecursosBaseDatos.STRING_TYPE+","+clm.IDFORMULARIO+" "+RecursosBaseDatos.INT_TYPE+","+clm.ESTADO+" "+RecursosBaseDatos.INT_TYPE+","
                +clm.TIPOGESTION+" "+RecursosBaseDatos.INT_TYPE+","+clm.ESBORRADOR+" "+RecursosBaseDatos.BOOLEAN_TYPE+","+clm.TELEFONO+" "+RecursosBaseDatos.STRING_TYPE+","+
                clm.FECHA+" "+RecursosBaseDatos.DATE_TIME_TYPE+","+clm.LATITUD+" "+RecursosBaseDatos.DOUBLE_TYPE
                +","+clm.LONGITUD+" "+RecursosBaseDatos.DOUBLE_TYPE+",FOREIGN KEY("+clm.TIPOGESTION+") REFERENCES "+TipoGestion.ColumnasTablaSql.TABLENAME+" ("+TipoGestion.ColumnasTablaSql.ID+")," +
                "FOREIGN KEY ("+clm.IDFORMULARIO+") REFERENCES "+Formulario.ColumnasTablaSql.TABLE_NAME+" ("
                +Formulario.ColumnasTablaSql.ID+"))";
        return tabla;
    }

    /**
     * Zona en la que se realiza la gestion
     */
    public String getZona() {
        return zona;
    }

    /**
     * Es el tipo de gestion al que pertenece la gestion
     */
    public int getTipoGestion() {
        return tipoGestion;
    }

    /**
     * Departamento en donde se realiza la gestion
     */
    public String getDepartamento() {
        return departamento;
    }

    /**
     * Dirección en la que se va a reaalizar la gestion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * identificador de la gestion desde el web service
     */
    public int getIdgestion() {
        return idgestion;
    }

    /**
     * barrio en el que se va a realizar la gestion
     */
    public String getBarrio() {
        return barrio;
    }

    /**
     * Municipio en el que se va a realizar
     */
    public String getMunicipio() {
        return municipio;
    }

    /**
     * Destinatario de la gestion
     */
    public String getDestinatario() {
        return destinatario;
    }

    /**
     * Es el identificador de la gestion en la base de datos local
     */
    public int getId() {
        return id;
    }

    /**
     * Indica si la gestion es un borrador
     */
    public boolean isEsBorrador() {
        return esBorrador;
    }

    /**
     * Formulario al que se encuentra asociado la gestion
     */
    public Formulario getFormulario() {
        return formulario;
    }

    /**
     * Obtiene el nombre de la tabal con que se identifica la clase en la base de datos
     * @return
     */
    public String getNombreTabla()
    {
        return ColumnasTablaSql.TABLENAME;
    }

    /**
     * Obtiene el contenedor de valores, con los valores del objeto actual
     * @return  el contenedor de valores
     */
    public ContentValues getContenedorValores()
    {
        ContentValues contentValues=new ContentValues();
        ColumnasTablaSql c=new ColumnasTablaSql();
        contentValues.put(c.ID,idgestion);
        contentValues.put(c.ZONA,zona);
        contentValues.put(c.DEPARTAMENTO,departamento);
        contentValues.put(c.DIRECCION,direccion);
        contentValues.put(c.IDGESTION,idgestion);
        contentValues.put(c.BARRIO,barrio);
        contentValues.put(c.MUNICIPIO,municipio);
        contentValues.put(c.DESTINATARIO,destinatario);
        contentValues.put(c.ESBORRADOR,esBorrador);
        contentValues.put(c.IDFORMULARIO,formulario.getId());
        contentValues.put(c.TIPOGESTION,tipoGestion);
        contentValues.put(c.TELEFONO, getTelefono());
        contentValues.put(c.ESTADO, getEstadoGestion());
        contentValues.put(c.FECHA,fecha);
        contentValues.put(c.LATITUD,latitud);
        contentValues.put(c.LONGITUD,longitud);
        contentValues.put(c.CODIGOBARRAS,codigoBarras);
        return contentValues;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEsBorrador(boolean esBorrador) {
        this.esBorrador = esBorrador;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    /**
     * Telefono de la gestion
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * El estado en el que esta la gestion
     */
    public int getEstadoGestion() {
        return estadoGestion;
    }

    /**
     * Fecha en la que se registro la gestion
     */
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * Latitud en la que se registro la gestion
     */
    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    /**
     * Latitud en la que se registro la gestion
     */
    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    /**
     * El codigo de barras asociado a la gestion
     */
    public String getCodigoBarras() {
        return codigoBarras;
    }

    public final static class ColumnasTablaSql
    {
        public static final String TABLENAME="Gestiones";
        public static final String ID="id";
        public static final String ZONA="zona";
        public static final String DEPARTAMENTO="departamento";
        public static final String DIRECCION="direccion";
        public static final String IDGESTION="idGestion";
        public static final String BARRIO="barrio";
        public static final String MUNICIPIO="municipio";
        public static final String DESTINATARIO="destinatario";
        public static final String ESBORRADOR="esBorrador";
        public static final String IDFORMULARIO="idFormulario";
        public static final String TIPOGESTION="tipoGestion";
        public static final String TELEFONO="telefono";
        public static final String ESTADO="estado";
        public static final String FECHA="fecha";
        public static final String LATITUD="latitud";
        public static final String LONGITUD="longitud";
        public static final String CODIGOBARRAS="codigoBarras";
    }
    public Gestion clone()
    {
        Gestion gestion=new Gestion(zona, tipoGestion, departamento, direccion, idgestion,
        barrio, municipio, destinatario, getTelefono(), getEstadoGestion(),fecha,latitud,longitud,codigoBarras,esBorrador, id,
        formulario.clone());
        return gestion;
    }
}
