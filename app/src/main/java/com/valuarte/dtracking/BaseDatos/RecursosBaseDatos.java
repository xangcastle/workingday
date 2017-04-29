package com.valuarte.dtracking.BaseDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.valuarte.dtracking.ElementosGraficos.CheckCaja;
import com.valuarte.dtracking.ElementosGraficos.ComboCaja;
import com.valuarte.dtracking.ElementosGraficos.Contenedor;
import com.valuarte.dtracking.ElementosGraficos.ElementoCombo;
import com.valuarte.dtracking.ElementosGraficos.FirmaDigital;
import com.valuarte.dtracking.ElementosGraficos.Formulario;
import com.valuarte.dtracking.ElementosGraficos.Gestion;
import com.valuarte.dtracking.ElementosGraficos.Imagen;
import com.valuarte.dtracking.ElementosGraficos.Input;
import com.valuarte.dtracking.ElementosGraficos.MultiImagen;
import com.valuarte.dtracking.ElementosGraficos.RadioBoton;
import com.valuarte.dtracking.ElementosGraficos.RadioGrupo;
import com.valuarte.dtracking.ElementosGraficos.TextArea;
import com.valuarte.dtracking.ElementosGraficos.TipoGestion;
import com.valuarte.dtracking.ElementosGraficos.Vista;
import com.valuarte.dtracking.MensajeTextos.Sms;
import com.valuarte.dtracking.Util.ContenidoMensaje;
import com.valuarte.dtracking.Util.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Representa el control de l abase de atos, como alamcenamiento y obtención de registros de la
 * base de datos
 *
 * @version 1.0
 */
public class RecursosBaseDatos implements Serializable {
    /**
     * Tipo de dato cadena que maneja la base de datos
     */
    public static final String STRING_TYPE = "text";
    /**
     * Tipo de dato entero que maneja la base de datos
     */
    public static final String INT_TYPE = "integer";
    /**
     * Tipo de dato booleano que maneja la base de datos
     */
    public static final String BOOLEAN_TYPE = "boolean";
    /**
     * Tipo de dato fecha que maneja la base de datos
     */
    public static final String DATE_TIME_TYPE = "datetime";
    /**
     * Tipo de dato double que maneja la base de datos
     */
    public static final String DOUBLE_TYPE = "double";
    /**
     * Lector de la base de datos
     */
    private SQLiteDatabase database;
    /**
     * Conexión y creación con la base de datos
     */
    private DatabaseHelper databaseHelper;
    /**
     * contexto de la aplicación
     */
    private Context context;

    public RecursosBaseDatos(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(this.context);
        database = databaseHelper.getWritableDatabase();
    }

    /**
     * Guarda una vista en la base de datos
     *
     * @param vista la vista a guardar
     * @return el id de la vista guardada
     */
    public int guardarVista(Vista vista) {
        int id = -1;
        try {
            id = (int) database.insertOrThrow(vista.getNombreTabla(), null, vista.getContenedorValores());
        } catch (SQLException ex) {
            Log.e("error", ex.getMessage());
        }
        return id;
    }

    /**
     * Guarda el sms en la base de datos local
     * @param sms el sms a guardar
     * @return el id del sms guardado
     */
    public int guardarSms(Sms sms)
    {
        int id = -1;
        try {
            id = (int) database.insertOrThrow(sms.getNombreTabla(), null, sms.getContenedorDeValores());
        } catch (SQLException ex) {
            Log.e("error", ex.getMessage());
        }
        return id;
    }

    /**
     * actualiza el estado de enviado el mensaje al servidor
     * @param   idSms id del sms
     * @param enviado   el estado del mensaje
     */
    public void actualizarEstadoSMS(int idSms,boolean enviado)
    {
        Sms.ColumnasTablaSql clm = new Sms.ColumnasTablaSql();
        String consulta = clm.ID + "=?";
        ContentValues contentValues=new ContentValues();
        contentValues.put(clm.ENVIADO, enviado);
        String[] params={Integer.toString(idSms)};
        database.update(clm.TABLENAME,contentValues,consulta,params);

    }

    /**
     * Obtiene los mensajes que estan contenidos en el cursor
     * @param c  el cursor que contiene los mensajes
     * @return arreglo de mensajes
     */
    public ArrayList<Sms> getMensajes(Cursor c)
    {
        ArrayList<Sms> smses=new ArrayList<>();
        Sms.ColumnasTablaSql clm = new Sms.ColumnasTablaSql();
        c.moveToFirst();
        Sms sms;
        if(c.getCount()>0)
        {
            do {
                int indiceId=c.getColumnIndex(clm.ID);
                int indiceFecha=c.getColumnIndex(clm.FECHA);
                int indiceEnviado=c.getColumnIndex(clm.ENVIADO);
                int indiceCuerpo=c.getColumnIndex(clm.CUERPO);
                int indiceDesde=c.getColumnIndex(clm.DESDE);
                JSONObject jsonObject;
                try {
                    jsonObject=new JSONObject(c.getString(indiceCuerpo));
                } catch (JSONException e) {
                    jsonObject=new JSONObject();
                }
                sms=new Sms(c.getInt(indiceId),c.getString(indiceFecha),jsonObject,c.getString(indiceDesde),c.getInt(indiceEnviado)>0);
            }while(c.moveToNext());
        }
        return smses;
    }

    /**
     * Obtiene los mensaje penidentes por enviar
     * @return  arrgelo con los mensaje no enviados
     */
    public ArrayList<Sms> getMensajesPendientes()
    {
        Sms.ColumnasTablaSql clm=new Sms.ColumnasTablaSql();
        String consulta = "select * from " + clm.TABLENAME + " where "  + clm.ENVIADO+" =?";
        String[] params = {"0"};
        Cursor c = database.rawQuery(consulta, params);
        return getMensajes(c);
    }
    /**
     * Guarda el usuario en la base de datos
     *
     * @param usuario el usuario a guardar
     * @return el id que genera al guardar al usuario
     */
    public int guardarUsuario(Usuario usuario) {
        int id = -1;
        try {
            id = (int) database.insertOrThrow(usuario.getNombreTabla(), null, usuario.getContenedorValores());
        } catch (SQLException ex) {
            Log.e("error", ex.getMessage());
        }
        return id;
    }

    /**
     * Actualiza la gestion en la base de datos
     *
     * @param gestion la gestion a actualizar
     */
    public void actualizarGestion(Gestion gestion) {
        Gestion.ColumnasTablaSql clm = new Gestion.ColumnasTablaSql();
        String consulta = clm.ID + "=?";
        String[] params = {Integer.toString(gestion.getIdgestion())};
        ContentValues contentValues = new ContentValues();
        contentValues.put(clm.IDFORMULARIO, gestion.getFormulario().getId());
        contentValues.put(clm.ESBORRADOR, gestion.isEsBorrador());
        contentValues.put(clm.FECHA,gestion.getFecha());
        contentValues.put(clm.LATITUD, gestion.getLatitud());
        contentValues.put(clm.LONGITUD,gestion.getLongitud());
        database.update(clm.TABLENAME, contentValues, consulta, params);
    }

    /**
     * Guarda el tipo de gestion, y las gestiones asociadas
     *
     * @param tipoGestion el tipo de gestion a guardar
     * @return el id del tipo de gestion
     */
    public int guardarTipoGestion(TipoGestion tipoGestion) {
        int id = -1;
        try {
            String[] params = {Integer.toString(tipoGestion.getId())};
            Cursor c = database.rawQuery("select " + TipoGestion.ColumnasTablaSql.ID + " from " + TipoGestion.ColumnasTablaSql.TABLENAME + " where " + TipoGestion.ColumnasTablaSql.ID + "=?", params);
            c.moveToFirst();
            if (c.getCount() == 0) {
                id = (int) database.insert(tipoGestion.getNombreTabla(), null, tipoGestion.getContenedorValores());
            } else {
                id = tipoGestion.getId();
            }
            for (Gestion gestion : tipoGestion.getGestiones()) {
                int idGestion = guardarGestion(gestion);
                gestion.setId(idGestion);
            }
            c.close();
        } catch (SQLiteConstraintException ex) {
            Log.e("Inserccion fallida tipo", "fallida");
            return tipoGestion.getId();
        }
        return id;
    }

    /**
     * Guarda la gestion que se pasa por parametro, donde esta gestion ya tiene todos los datos para guardarlo
     *
     * @param gestion la gestion a guardar
     * @return el id de la gestion
     */
    public int guardarGestion(Gestion gestion) {
        int id = -1;
        try {
            String[] params = {Integer.toString(gestion.getIdgestion())};
            Cursor c = database.rawQuery("select " + Gestion.ColumnasTablaSql.ESBORRADOR + " from " + Gestion.ColumnasTablaSql.TABLENAME + " where " + Gestion.ColumnasTablaSql.ID + "=?", params);
            c.moveToFirst();
            if (c.getCount() == 0) {
                id = (int) database.insertOrThrow(gestion.getNombreTabla(), null, gestion.getContenedorValores());
            } else {

                id = gestion.getIdgestion();
                if(c.getInt(0)==0) {
                    params[0] = Integer.toString(gestion.getIdgestion());
                    String consulta = Gestion.ColumnasTablaSql.ID + "=?";
                    database.update(gestion.getNombreTabla(), gestion.getContenedorValores(), consulta, params);
                }
                }
            c.close();
        } catch (SQLException ex) {
            Log.e("inserccion fallida", "inserccion fallida");
            return gestion.getIdgestion();
        }
        return id;
    }

    /**
     * Obtiene los tipos de gestiones desde la base de datos
     *
     * @return un arreglo con los tipos de gestiones
     */
    public ArrayList<TipoGestion> getTipoGestiones() {
        TipoGestion.ColumnasTablaSql clm = new TipoGestion.ColumnasTablaSql();
        String consulta = "select * from " + TipoGestion.ColumnasTablaSql.TABLENAME;
        String[] params = {};
        Cursor c = database.rawQuery(consulta, params);
        ArrayList<TipoGestion> tipoGestions = new ArrayList<>();
        c.moveToFirst();
        TipoGestion tipoGestion;
        if (c.getCount() > 0) {
            int indiceId = c.getColumnIndex(clm.ID);
            int indiceNombre = c.getColumnIndex(clm.NOMBRE);
            do {
                tipoGestion = new TipoGestion(c.getInt(indiceId), c.getString(indiceNombre));
                ArrayList<Formulario> formularios = getFormulariosNoBorradores();
                for (Formulario formulario : formularios) {
                    if (formulario.getIdFormulario() == tipoGestion.getId()) {
                        tipoGestion.setGestiones(getGestionesDesdeTipoGestiones(formulario));
                        break;
                    }
                }
                Log.e("desde aca", Integer.toString(tipoGestion.getCantidadGestiones()));
                tipoGestions.add(tipoGestion);
            } while (c.moveToNext());

        }
        c.close();
        return tipoGestions;
    }



    /**
     * Obtiene las gestiones asociadas a un tipo de gestion
     *
     * @param formulario el formulario que le corresponde al tipo de gestion
     * @return un arreglo de las getiones
     */
    public ArrayList<Gestion> getGestionesDesdeTipoGestiones(Formulario formulario) {
        Gestion.ColumnasTablaSql clm = new Gestion.ColumnasTablaSql();
        String consulta = "select * from " + clm.TABLENAME + " where " + clm.TIPOGESTION + "=? AND " + clm.ESBORRADOR + " =?";
        String[] params = {Integer.toString(formulario.getIdFormulario()), "0"};
        Cursor c = database.rawQuery(consulta, params);
        ArrayList<Gestion> gestions = new ArrayList<>();
        Gestion gestion;
        c.moveToFirst();
        if (c.getCount() > 0) {

            int indiceZona = c.getColumnIndex(clm.ZONA);
            int indiceTipoGestion = c.getColumnIndex(clm.TIPOGESTION);
            int indiceDepartamento = c.getColumnIndex(clm.DEPARTAMENTO);
            int indiceDireccion = c.getColumnIndex(clm.DIRECCION);
            int indiceGestion = c.getColumnIndex(clm.IDGESTION);
            int indiceBarrio = c.getColumnIndex(clm.BARRIO);
            int indiceMunicipio = c.getColumnIndex(clm.MUNICIPIO);
            int indiceDestinario = c.getColumnIndex(clm.DESTINATARIO);
            int indiceBorrador = c.getColumnIndex(clm.ESBORRADOR);
            int indiceId = c.getColumnIndex(clm.ID);
            int indiceTelefono = c.getColumnIndex(clm.TELEFONO);
            int indiceEstado=c.getColumnIndex(clm.ESTADO);
            int indiceFecha=c.getColumnIndex(clm.FECHA);
            int indiceLatitud=c.getColumnIndex(clm.LATITUD);
            int indiceLongitud=c.getColumnIndex(clm.LONGITUD);
            int indiceCodigoBarras=c.getColumnIndex(clm.CODIGOBARRAS);
            do {
                gestion = new Gestion(c.getString(indiceZona), c.getInt(indiceTipoGestion), c.getString(indiceDepartamento)
                        , c.getString(indiceDireccion), c.getInt(indiceGestion), c.getString(indiceBarrio), c.getString(indiceMunicipio)
                        , c.getString(indiceDestinario), c.getString(indiceTelefono),c.getInt(indiceEstado),c.getString(indiceFecha),
                        c.getDouble(indiceLatitud),c.getDouble(indiceLongitud), c.getString(indiceCodigoBarras),c.getInt(indiceBorrador) > 0, c.getInt(indiceId), formulario);
                gestions.add(gestion);
            } while (c.moveToNext());
        }
        c.close();
        return gestions;
    }
    /**
     * Obtiene las gestiones que son borradores, es decir que ya fueron llenadas
     * @return arreglo con la gestion;
     */
    public ArrayList<Gestion> getGestionesBorradores() {

        Gestion.ColumnasTablaSql clm=new Gestion.ColumnasTablaSql();
        String consulta = "select * from " + clm.TABLENAME + " where "  + clm.ESBORRADOR + " =?";
        String[] params = {"1"};
        Cursor c = database.rawQuery(consulta, params);
        return getGestiones(c);
    }

    /**
     * Obtiene las gestiones pendientes por sincronizar, ya sea en su totalidad o solo las imagenes
     * @return un arreglo con las gestiones pendientes por sincronizar
     */
    public ArrayList<Gestion> getGestionesPendientes()
    {
        Gestion.ColumnasTablaSql clm=new Gestion.ColumnasTablaSql();
        String consulta = "select * from " + clm.TABLENAME + " where "  + clm.ESBORRADOR + " =? AND "+clm.ESTADO+" !=?";
        String[] params = {"1",Integer.toString(Gestion.ENVIADO)};
        Cursor c = database.rawQuery(consulta, params);
        return getGestiones(c);
    }
    /**
     * Obtiene las gestiones contenidas en el cursor
     * @param c  el cursor que contiene las gestiones
     * @return un arreglo con las gestiones
     */
    public ArrayList<Gestion> getGestiones(Cursor c)
    {

            Gestion.ColumnasTablaSql clm = new Gestion.ColumnasTablaSql();
            ArrayList<Gestion> gestiones = new ArrayList<>();
        try {
            Gestion gestion;
            ArrayList<Formulario> formulario;
            c.moveToFirst();
            Cursor c1;
            if (c.getCount() > 0) {

                int indiceZona = c.getColumnIndex(clm.ZONA);
                int indiceTipoGestion = c.getColumnIndex(clm.TIPOGESTION);
                int indiceDepartamento = c.getColumnIndex(clm.DEPARTAMENTO);
                int indiceDireccion = c.getColumnIndex(clm.DIRECCION);
                int indiceGestion = c.getColumnIndex(clm.IDGESTION);
                int indiceBarrio = c.getColumnIndex(clm.BARRIO);
                int indiceMunicipio = c.getColumnIndex(clm.MUNICIPIO);
                int indiceDestinario = c.getColumnIndex(clm.DESTINATARIO);
                int indiceBorrador = c.getColumnIndex(clm.ESBORRADOR);
                int indiceId = c.getColumnIndex(clm.ID);
                int indiceTelefono = c.getColumnIndex(clm.TELEFONO);
                int indiceFormulario = c.getColumnIndex(clm.IDFORMULARIO);
                int indiceEstado = c.getColumnIndex(clm.ESTADO);
                int indiceFecha = c.getColumnIndex(clm.FECHA);
                int indiceLatitud = c.getColumnIndex(clm.LATITUD);
                int indiceLongitud = c.getColumnIndex(clm.LONGITUD);
                int indiceCodigoBarras = c.getColumnIndex(clm.CODIGOBARRAS);
                do {
                    c1 = database.rawQuery("select * from " + Formulario.ColumnasTablaSql.TABLE_NAME + " where " + Formulario.ColumnasTablaSql.ID + "=?", new String[]{Integer.toString(c.getInt(indiceFormulario))});
                    formulario = getFormularios(c1);
                    if (formulario.size() > 0) {
                        gestion = new Gestion(c.getString(indiceZona), c.getInt(indiceTipoGestion), c.getString(indiceDepartamento)
                                , c.getString(indiceDireccion), c.getInt(indiceGestion), c.getString(indiceBarrio), c.getString(indiceMunicipio)
                                , c.getString(indiceDestinario), c.getString(indiceTelefono), c.getInt(indiceEstado),
                                c.getString(indiceFecha), c.getDouble(indiceLatitud), c.getDouble(indiceLongitud), c.getString(indiceCodigoBarras), c.getInt(indiceBorrador) > 0, c.getInt(indiceId), formulario.get(0));
                        gestiones.add(gestion);
                    }
                    c1.close();
                } while (c.moveToNext());

            }
            c.close();

        }
        catch (Exception e){}
        return gestiones;
    }
    /**
     * Actualiza el estado de la gestion
     * @param idGestion  el id de la gestion a actualizar
     * @param estado  el estado a actualizar
     */
    public void actualizarEstadoGestion(int idGestion,int estado)
    {
       Gestion.ColumnasTablaSql clm = new Gestion.ColumnasTablaSql();
        String consulta =clm.ID+"=?";
        String[] params = {Integer.toString(idGestion)};
        ContentValues contentValues = new ContentValues();
        contentValues.put(clm.ESTADO, estado);
        database.update(clm.TABLENAME, contentValues, consulta, params);
    }
    /**
     * Obtiene el usuario desde la base de datos
     *
     * @return el ususario que esta registrado
     */
    public Usuario getUsuario() {
        Usuario.ColumnasTablaSql clm = new Usuario.ColumnasTablaSql();
        String consulta = "select * from " + Usuario.ColumnasTablaSql.TABLENAME;
        String[] params = {};
        Cursor c = database.rawQuery(consulta, params);
        Usuario usuario = null;
        c.moveToFirst();
        if (c.getCount() > 0) {
            int indiceId = c.getColumnIndex(clm.ID);
            int indiceNombreUsuario = c.getColumnIndex(clm.NOMBREUSUARIO);
            int indiceNombre = c.getColumnIndex(clm.NOMBRE);
            int indiceFoto = c.getColumnIndex(clm.FOTO);
            int indiceNumero = c.getColumnIndex(clm.NUMERO);
            int indiceEstado = c.getColumnIndex(clm.ESTADO);
            int indiceContrasenia = c.getColumnIndex(clm.CONTRASENIA);
            int indiceConexiones = c.getColumnIndex(clm.CONEXIONESSERVIDOR);
            int indiceIntervaloGps=c.getColumnIndex(clm.INTERVALOGPS);
            usuario = new Usuario(c.getInt(indiceId), c.getString(indiceNombreUsuario), c.getString(indiceNombre)
                    , c.getString(indiceFoto), c.getString(indiceNumero), c.getString(indiceContrasenia), c.getString(indiceConexiones),
                    c.getInt(indiceIntervaloGps),c.getInt(indiceEstado));
        }
        c.close();
        return usuario;
    }

    /**
     * Agrega un nuevo registro a la tabla de ids de para la pantalla
     *
     * @return el id generado
     */
    public int agregarIdPantalla() {
        int id = -1;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("numero", 1);
            id = (int) database.insertOrThrow("IdsPantalla", null, contentValues);
        } catch (SQLException ex) {
            Log.e("error", ex.getMessage());
        }
        return id;
    }

    /**
     * Guarda el formulario en la base de datos
     *
     * @param formulario el formulario a guardar
     * @return el id del formulario generado
     */
    public int guardarFormulario(Formulario formulario) {
        int id = -1;
        try {
            id = (int) database.insertOrThrow(Formulario.ColumnasTablaSql.TABLE_NAME, null, formulario.getContenedorValores());
        } catch (SQLException ex) {
            Log.e("error", ex.getMessage());
        }
        return id;
    }

    /**
     * Guarda el elemento de un combo box en la base de datos
     *
     * @param elementoCombo el elemento a guardar
     * @return el id que se le asigno al elemento
     */
    public int guardarElementoCombo(ElementoCombo elementoCombo) {
        int id = -1;
        try {
            id = (int) database.insertOrThrow(ElementoCombo.ColumnasTablaSql.TABLE_NAME, null, elementoCombo.getContenedorValores());
        } catch (SQLException ex) {
            Log.e("error", ex.getMessage());
        }
        return id;
    }

    /**
     * Obtiene un arreglo de formularios de acuerdo al cursor que recibe
     *
     * @param cursor contiene los datos de los formularios
     * @return un arreglo que contiene todos los formularios obtenidos
     */
    public ArrayList<Formulario> getFormularios(Cursor cursor) {
        ArrayList<Formulario> formularios = new ArrayList<>();
        try {
            Formulario.ColumnasTablaSql clm = new Formulario.ColumnasTablaSql();
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                int indiceId = cursor.getColumnIndex(clm.ID);
                int indiceBorrador = cursor.getColumnIndex(clm.BORRADOR);
                int indcieIdForm = cursor.getColumnIndex(clm.IDFORMULARIO);
                int indiceName = cursor.getColumnIndex(clm.NAME);
                int indiceFecha = cursor.getColumnIndex(clm.FECHACREACION);

                Formulario formulario;
                do {
                    formulario = new Formulario(getContenedoresDesdeFormulario(cursor.getInt(indiceId)),
                            cursor.getInt(indiceId), cursor.getInt(indiceBorrador) > 0
                            , cursor.getInt(indcieIdForm), cursor.getString(indiceName), cursor.getString(indiceFecha));
                    formularios.add(formulario);
                } while (cursor.moveToNext());
            }
        }
        catch (Exception e){}
        cursor.close();
        return formularios;
    }

    /**
     * Obtiene los formularios que se guardaron como borradores
     *
     * @return arreglo de formularios que son borradores
     */
    public ArrayList<Formulario> getFormulariosBorradores() {
        String consulta = "select * from " + Formulario.ColumnasTablaSql.TABLE_NAME + " where " + Formulario.ColumnasTablaSql.BORRADOR + "=?";
        String[] params = {"1"};
        Cursor c = database.rawQuery(consulta, params);
        ArrayList<Formulario> formularios = getFormularios(c);
        return formularios;
    }

    /**
     * Obtiene lso formularios que no se guardaron como borradores
     *
     * @return arreglo de formularios que no son borradores
     */
    public ArrayList<Formulario> getFormulariosNoBorradores() {
        String consulta = "select * from " + Formulario.ColumnasTablaSql.TABLE_NAME + " where " + Formulario.ColumnasTablaSql.BORRADOR + "=?";
        String[] params = {"0"};
        Cursor c = database.rawQuery(consulta, params);
        ArrayList<Formulario> formularios = getFormularios(c);
        return formularios;
    }

    /**
     * Obtiene todos los contenedores(layouts) externos del formularios
     *
     * @param idFormulario el identificador del formulario en la base de datos
     * @return el arreglo de lso contenedores
     */
    public ArrayList<Contenedor> getContenedoresDesdeFormulario(int idFormulario) {
        ArrayList<Contenedor> contenedors = new ArrayList<>();
        Contenedor.ColumnasTablaSql clm = new Contenedor.ColumnasTablaSql();
        String consulta = "select * from " + clm.TABLE_NAME + " where " + clm.FORMULARIO + "=?";
        String[] params = {Integer.toString(idFormulario)};
        Cursor c = database.rawQuery(consulta, params);
        c.moveToFirst();
        Contenedor contenedor;
        if (c.getCount() > 0) {
            int indiceId = c.getColumnIndex(clm.ID);
            int indiceAncho = c.getColumnIndex(clm.ANCHO);
            int indiceAlto = c.getColumnIndex(clm.ALTO);
            int indiceOrientacion = c.getColumnIndex(clm.ORIENTACION);
            int indiceHabilitado = c.getColumnIndex(clm.HABILITADO);
            int indiceTipo = c.getColumnIndex(clm.TIPO);
            int indiceLayout = c.getColumnIndex(clm.LAYOUT);
            int indicePantalla = c.getColumnIndex(clm.PANTALLA);
            do {
                contenedor = new Contenedor(c.getInt(indiceId), c.getInt(indiceAncho), c.getInt(indiceAlto),
                        c.getInt(indicePantalla), c.getInt(indiceLayout), true, c.getString(indiceTipo), c.getString(indiceOrientacion),
                        c.getInt(indiceHabilitado) > 0, idFormulario);
                contenedor.agregarVistas(getInputsDesdeContenedor(c.getInt(indiceId)));
                contenedor.agregarVistas(getTextAreasDesdeContenedor(c.getInt(indiceId)));
                contenedor.agregarVistas(getRadioGruposDesdeContenedor(c.getInt(indiceId)));
                contenedor.agregarVistas(getCombosDesdeContenedor(c.getInt(indiceId)));
                contenedor.agregarVistas(getCheckCajasDesdeContenedor(c.getInt(indiceId)));
                contenedor.agregarVistas(getImagenesDesdeContenedor(c.getInt(indiceId)));
                contenedor.agregarVistas(getMultiImagenesDesdeContenedor(c.getInt(indiceId)));
                contenedor.agregarVistas(getFirmasDigitalesDesdeContenedor(c.getInt(indiceId)));
                Collections.sort(contenedor.getVistas());
                contenedors.add(contenedor);
            } while (c.moveToNext());
        }
        c.close();
        return contenedors;
    }

    /**
     * Elimina todos los usuarios registrados localmente
     */
    public void eliminarUsuariosRegistrados() {
        Usuario.ColumnasTablaSql clm = new Usuario.ColumnasTablaSql();
        String consulta = "";
        String[] params = {};
        database.delete(clm.TABLENAME, consulta, params);
    }

    /**
     * actualiza a los usuarios registrados localmente al estado que se le indica
     *
     * @param estado el estaod al que se va a actualizar
     */
    public void actualizarEstadoUsuario(int estado) {
        Usuario.ColumnasTablaSql clm = new Usuario.ColumnasTablaSql();
        String consulta = "";
        String[] params = {};
        ContentValues contentValues = new ContentValues();
        contentValues.put(clm.ESTADO, estado);
        database.update(clm.TABLENAME, contentValues, consulta, params);
    }

    /**
     * Obtiene todos los contenedores(layouts) desde otros contenedores
     *
     * @param idContenedor el identificador del contenedor en la base de datos
     * @return el arreglo de los contenedores
     */
    public ArrayList<Contenedor> getContenedoresDesdeContenedor(int idContenedor) {
        ArrayList<Contenedor> contenedors = new ArrayList<>();
        //TODO
        return contenedors;
    }

    /**
     * Obtiene todos los inputs desde un contenedor
     *
     * @param idContenedor el id del contenedor
     * @return arreglo con los inputs
     */
    public ArrayList<Vista> getInputsDesdeContenedor(int idContenedor) {
        ArrayList<Vista> inputs = new ArrayList<>();
        Input.ColumnasTablaSql clm = new Input.ColumnasTablaSql();
        String consulta = "select * from " + clm.TABLE_NAME + " where " + clm.LAYOUT + "=?";
        String[] params = {Integer.toString(idContenedor)};
        Cursor c = database.rawQuery(consulta, params);
        c.moveToFirst();
        Input input;
        if (c.getCount() > 0) {
            int indiceId = c.getColumnIndex(clm.ID);
            int indiceAncho = c.getColumnIndex(clm.ANCHO);
            int indiceAlto = c.getColumnIndex(clm.ALTO);
            int indiceHabilitado = c.getColumnIndex(clm.HABILITADO);
            int indiceLayout = c.getColumnIndex(clm.LAYOUT);
            int indicePantalla = c.getColumnIndex(clm.PANTALLA);
            int indiceRequerido = c.getColumnIndex(clm.REQUERIDO);
            int indiceNombreVariable = c.getColumnIndex(clm.NOMBRE_VARIABLE);
            int indiceTitulo = c.getColumnIndex(clm.TITULO);
            int indiceEntrada = c.getColumnIndex(clm.TIPO_ENTRADA);
            int indiceLongitudMaxima = c.getColumnIndex(clm.LONGITUD_MAXIMA);
            int indiceValor = c.getColumnIndex(clm.VALOR);
            do {
                input = new Input(c.getInt(indiceId), c.getInt(indiceAncho), c.getInt(indiceAlto),
                        c.getInt(indicePantalla), c.getInt(indiceLayout), c.getInt(indiceRequerido) > 0
                        , c.getInt(indiceEntrada), c.getString(indiceValor), c.getInt(indiceLongitudMaxima),
                        c.getString(indiceNombreVariable), c.getString(indiceTitulo), c.getInt(indiceHabilitado) > 0);
                inputs.add(input);
            } while (c.moveToNext());

        }
        c.close();
        return inputs;
    }

    /**
     * Obtiene todos los text area desde un contenedor
     *
     * @param idContenedor el id del contenedor
     * @return arreglo con los text area
     */
    public ArrayList<Vista> getTextAreasDesdeContenedor(int idContenedor) {
        ArrayList<Vista> textAreas = new ArrayList<>();
        TextArea.ColumnasTablaSql clm = new TextArea.ColumnasTablaSql();
        String consulta = "select * from " + clm.TABLE_NAME + " where " + clm.LAYOUT + "=?";
        String[] params = {Integer.toString(idContenedor)};
        Cursor c = database.rawQuery(consulta, params);
        c.moveToFirst();
        TextArea textArea;
        if (c.getCount() > 0) {
            int indiceId = c.getColumnIndex(clm.ID);
            int indiceAncho = c.getColumnIndex(clm.ANCHO);
            int indiceAlto = c.getColumnIndex(clm.ALTO);
            int indiceHabilitado = c.getColumnIndex(clm.HABILITADO);
            int indiceLayout = c.getColumnIndex(clm.LAYOUT);
            int indicePantalla = c.getColumnIndex(clm.PANTALLA);
            int indiceRequerido = c.getColumnIndex(clm.REQUERIDO);
            int indiceNombreVariable = c.getColumnIndex(clm.NOMBRE_VARIABLE);
            int indiceTitulo = c.getColumnIndex(clm.TITULO);
            int indiceEntrada = c.getColumnIndex(clm.TIPO_ENTRADA);
            int indiceLongitudMaxima = c.getColumnIndex(clm.LONGITUD_MAXIMA);
            int indiceValor = c.getColumnIndex(clm.VALOR);
            do {
                textArea = new TextArea(c.getInt(indiceId), c.getInt(indiceAncho), c.getInt(indiceAlto),
                        c.getInt(indicePantalla), c.getInt(indiceLayout), c.getInt(indiceRequerido) > 0
                        , c.getInt(indiceEntrada), c.getString(indiceValor), c.getInt(indiceLongitudMaxima),
                        c.getString(indiceNombreVariable), c.getString(indiceTitulo), c.getInt(indiceHabilitado) > 0);
                textAreas.add(textArea);
            } while (c.moveToNext());

        }
        c.close();
        return textAreas;
    }

    /**
     * Obtiene los radio grupos pertenecientes a un contenedor especifico
     *
     * @param idContenedor el id del contenedor
     * @return un arreglo con los radio grupos
     */
    public ArrayList<Vista> getRadioGruposDesdeContenedor(int idContenedor) {
        ArrayList<Vista> radioGrupos = new ArrayList<>();
        RadioGrupo.ColumnasTablaSql clm = new RadioGrupo.ColumnasTablaSql();
        String consulta = "select * from " + clm.TABLE_NAME + " where " + clm.LAYOUT + "=?";
        String[] params = {Integer.toString(idContenedor)};
        Cursor c = database.rawQuery(consulta, params);
        RadioGrupo radioGrupo;
        c.moveToFirst();
        if (c.getCount() > 0) {
            int indiceId = c.getColumnIndex(clm.ID);
            int indiceAncho = c.getColumnIndex(clm.ANCHO);
            int indiceAlto = c.getColumnIndex(clm.ALTO);
            int indiceHabilitado = c.getColumnIndex(clm.HABILITADO);
            int indiceLayout = c.getColumnIndex(clm.LAYOUT);
            int indicePantalla = c.getColumnIndex(clm.PANTALLA);
            int indiceRequerido = c.getColumnIndex(clm.REQUERIDO);
            int indiceTitulo = c.getColumnIndex(clm.TITULO);
            int indiceNombreVariable = c.getColumnIndex(clm.NOMBRE_VARIABLE);
            int indiceOrientacion = c.getColumnIndex(clm.ORIENTACION);
            do {
                radioGrupo = new RadioGrupo(c.getInt(indiceId), c.getInt(indiceAncho), c.getInt(indiceAlto),
                        c.getInt(indicePantalla), c.getInt(indiceLayout), c.getInt(indiceRequerido) > 0, c.getString(indiceOrientacion),
                        c.getString(indiceNombreVariable), c.getString(indiceTitulo), c.getInt(indiceHabilitado) > 0);
                radioGrupo.setRadioBotons(getRadioBotonsDesdeGrupo(c.getInt(indiceId), idContenedor,
                        c.getInt(indiceRequerido) > 0, c.getString(indiceNombreVariable)));
                radioGrupos.add(radioGrupo);
            } while (c.moveToNext());
        }
        c.close();
        return radioGrupos;
    }

    /**
     * Obtiene los radio botones asociados a un radio grupo
     *
     * @param idGrupo        id del radio grupo
     * @param idLayout       id del contenedor
     * @param requerido      indica si el radio grupo es requerido o no
     * @param nombreVariable indica el nombre de la variable con que se identifica el radio grupo
     * @return arreglo que contiene los radio botones del radio grupo
     */
    public ArrayList<RadioBoton> getRadioBotonsDesdeGrupo(int idGrupo, int idLayout, boolean requerido, String nombreVariable) {
        ArrayList<RadioBoton> radioBotons = new ArrayList<>();
        RadioBoton.ColumnasTablaSql clm = new RadioBoton.ColumnasTablaSql();
        String consulta = "select * from " + clm.TABLE_NAME + " where " + clm.GROUP + "=?";
        String[] params = {Integer.toString(idGrupo)};
        Cursor c = database.rawQuery(consulta, params);
        c.moveToFirst();
        RadioBoton radioBoton;
        if (c.getCount() > 0) {
            int indiceId = c.getColumnIndex(clm.ID);
            int indiceAncho = c.getColumnIndex(clm.ANCHO);
            int indiceAlto = c.getColumnIndex(clm.ALTO);
            int indiceHabilitado = c.getColumnIndex(clm.HABILITADO);
            int indiceGroup = c.getColumnIndex(clm.GROUP);
            int indicePantalla = c.getColumnIndex(clm.PANTALLA);
            int indiceValor = c.getColumnIndex(clm.VALOR);
            int indiceTitulo = c.getColumnIndex(clm.TITULO);
            int indiceIdElemento = c.getColumnIndex(clm.IDELEMENTO);
            do {
                radioBoton = new RadioBoton(c.getInt(indiceId), c.getInt(indiceAncho), c.getInt(indiceAlto),
                        c.getInt(indicePantalla), idLayout, requerido, c.getInt(indiceValor) > 0,
                        nombreVariable, c.getString(indiceTitulo), c.getInt(indiceHabilitado) > 0, c.getInt(indiceIdElemento)
                        , c.getInt(indiceGroup));
                radioBotons.add(radioBoton);
            } while (c.moveToNext());
        }
        c.close();
        return radioBotons;
    }

    /**
     * Obtiene los combo box desde un contenedor(layout)
     *
     * @param idContenedor identificador del contenedor
     * @return arreglo con los combobox
     */
    public ArrayList<Vista> getCombosDesdeContenedor(int idContenedor) {
        ArrayList<Vista> comboCajas = new ArrayList<>();
        ComboCaja.ColumnasTablaSql clm = new ComboCaja.ColumnasTablaSql();
        String consulta = "select * from " + clm.TABLE_NAME + " where " + clm.LAYOUT + "=?";
        String[] params = {Integer.toString(idContenedor)};
        Cursor c = database.rawQuery(consulta, params);
        c.moveToFirst();
        ComboCaja comboCaja;
        if (c.getCount() > 0) {
            int indiceId = c.getColumnIndex(clm.ID);
            int indiceAncho = c.getColumnIndex(clm.ANCHO);
            int indiceAlto = c.getColumnIndex(clm.ALTO);
            int indiceHabilitado = c.getColumnIndex(clm.HABILITADO);
            int indiceLayout = c.getColumnIndex(clm.LAYOUT);
            int indicePantalla = c.getColumnIndex(clm.PANTALLA);
            int indiceRequerido = c.getColumnIndex(clm.REQUERIDO);
            int indiceTitulo = c.getColumnIndex(clm.TITULO);
            int indiceNombreVariable = c.getColumnIndex(clm.NOMBRE_VARIABLE);
            do {
                comboCaja = new ComboCaja(c.getInt(indiceId), c.getInt(indiceAncho), c.getInt(indiceAlto),
                        c.getInt(indicePantalla), c.getInt(indiceLayout), c.getInt(indiceRequerido) > 0,
                        c.getString(indiceNombreVariable), c.getString(indiceTitulo), c.getInt(indiceHabilitado) > 0);
                comboCaja.setElementoCombos(getElementosComboDesdeCombo(c.getInt(indiceId)));
                comboCajas.add(comboCaja);
            } while (c.moveToNext());
        }
        c.close();
        return comboCajas;

    }

    /**
     * Obtiene los elementos de un combo determinado
     *
     * @param idCombo identificador del combo
     * @return arreglo con los elementos del combo
     */
    public ArrayList<ElementoCombo> getElementosComboDesdeCombo(int idCombo) {
        ArrayList<ElementoCombo> elementoCombos = new ArrayList<>();
        ElementoCombo.ColumnasTablaSql clm = new ElementoCombo.ColumnasTablaSql();
        String consulta = "select * from " + clm.TABLE_NAME + " where " + clm.COMBO + "=?";
        String[] params = {Integer.toString(idCombo)};
        Cursor c = database.rawQuery(consulta, params);
        c.moveToFirst();
        ElementoCombo elementoCombo;
        if (c.getCount() > 0) {
            int indiceId = c.getColumnIndex(clm.ID);
            int indiceIdElemento = c.getColumnIndex(clm.ID_ELEMENTO);
            int indiceValor = c.getColumnIndex(clm.VALOR);
            int indcieSeleccionado = c.getColumnIndex(clm.SELECCIONADO);
            do {

                elementoCombo = new ElementoCombo(c.getInt(indiceId), c.getString(indiceValor), c.getInt(indiceIdElemento)
                        , c.getInt(indcieSeleccionado) > 0, idCombo);
                elementoCombos.add(elementoCombo);
            } while (c.moveToNext());
        }
        c.close();
        return elementoCombos;
    }

    /**
     * Obtiene los check box pertenecientes a un contenedor
     *
     * @param idContenedor identificador del contenedor
     * @return arreglo con los check box
     */
    public ArrayList<Vista> getCheckCajasDesdeContenedor(int idContenedor) {
        ArrayList<Vista> checkCajas = new ArrayList<>();
        CheckCaja.ColumnasTablaSql clm = new CheckCaja.ColumnasTablaSql();
        String consulta = "select * from " + clm.TABLE_NAME + " where " + clm.LAYOUT + "=?";
        String[] params = {Integer.toString(idContenedor)};
        Cursor c = database.rawQuery(consulta, params);
        c.moveToFirst();
        CheckCaja checkCaja;
        if (c.getCount() > 0) {
            int indiceId = c.getColumnIndex(clm.ID);
            int indiceAncho = c.getColumnIndex(clm.ANCHO);
            int indiceAlto = c.getColumnIndex(clm.ALTO);
            int indiceHabilitado = c.getColumnIndex(clm.HABILITADO);
            int indiceLayout = c.getColumnIndex(clm.LAYOUT);
            int indicePantalla = c.getColumnIndex(clm.PANTALLA);
            int indiceRequerido = c.getColumnIndex(clm.REQUERIDO);
            int indiceTitulo = c.getColumnIndex(clm.TITULO);
            int indiceNombreVariable = c.getColumnIndex(clm.NOMBRE_VARIABLE);
            int indiceValor = c.getColumnIndex(clm.VALOR);
            do {
                checkCaja = new CheckCaja(c.getInt(indiceId), c.getInt(indiceAncho), c.getInt(indiceAlto),
                        c.getInt(indicePantalla), c.getInt(indiceLayout), c.getInt(indiceRequerido) > 0,
                        c.getString(indiceNombreVariable), c.getString(indiceTitulo), c.getInt(indiceValor) > 0, c.getInt(indiceHabilitado) > 0);
                checkCajas.add(checkCaja);
            } while (c.moveToNext());
        }
        c.close();
        return checkCajas;
    }

    /**
     * Obtiene las imagenes asociadas a un contenedor desde la base de datos
     *
     * @param idContenedor el identificador del contenedor al que se encuentran asociadas las imagenes
     * @return un arreglo con las imagenes
     */
    public ArrayList<Vista> getImagenesDesdeContenedor(int idContenedor) {
        ArrayList<Vista> imagenes = new ArrayList<>();
        Imagen.ColumnasTablaSql clm = new Imagen.ColumnasTablaSql();
        String consulta = "select * from " + clm.TABLE_NAME + " where " + clm.LAYOUT + "=?";
        String[] params = {Integer.toString(idContenedor)};
        Cursor c = database.rawQuery(consulta, params);
        c.moveToFirst();
        Imagen imagen;
        if (c.getCount() > 0) {
            int indiceId = c.getColumnIndex(clm.ID);
            int indiceAncho = c.getColumnIndex(clm.ANCHO);
            int indiceAlto = c.getColumnIndex(clm.ALTO);
            int indiceHabilitado = c.getColumnIndex(clm.HABILITADO);
            int indiceLayout = c.getColumnIndex(clm.LAYOUT);
            int indicePantalla = c.getColumnIndex(clm.PANTALLA);
            int indiceRequerido = c.getColumnIndex(clm.REQUERIDO);
            int indiceTitulo = c.getColumnIndex(clm.TITULO);
            int indiceNombreVariable = c.getColumnIndex(clm.NOMBRE_VARIABLE);
            int indiceValor = c.getColumnIndex(clm.VALOR);
            do {
                imagen = new Imagen(c.getInt(indiceId), c.getInt(indiceAncho), c.getInt(indiceAlto),
                        c.getInt(indicePantalla), c.getInt(indiceLayout), c.getInt(indiceRequerido) > 0,
                        c.getInt(indiceHabilitado) > 0, c.getString(indiceValor), c.getString(indiceTitulo), c.getString(indiceNombreVariable));
                imagenes.add(imagen);
            } while (c.moveToNext());
        }
        c.close();
        return imagenes;
    }

    public ArrayList<Vista> getMultiImagenesDesdeContenedor(int idContenedor) {
        ArrayList<Vista> imagenes = new ArrayList<>();
        MultiImagen.ColumnasTablaSql clm = new MultiImagen.ColumnasTablaSql();
        String consulta = "select * from " + clm.TABLE_NAME + " where " + clm.LAYOUT + "=?";
        String[] params = {Integer.toString(idContenedor)};
        Cursor c = database.rawQuery(consulta, params);
        c.moveToFirst();
        MultiImagen imagen;
        if (c.getCount() > 0) {
            int indiceId = c.getColumnIndex(clm.ID);
            int indiceAncho = c.getColumnIndex(clm.ANCHO);
            int indiceAlto = c.getColumnIndex(clm.ALTO);
            int indiceHabilitado = c.getColumnIndex(clm.HABILITADO);
            int indiceLayout = c.getColumnIndex(clm.LAYOUT);
            int indicePantalla = c.getColumnIndex(clm.PANTALLA);
            int indiceRequerido = c.getColumnIndex(clm.REQUERIDO);
            int indiceTitulo = c.getColumnIndex(clm.TITULO);
            int indiceNombreVariable = c.getColumnIndex(clm.NOMBRE_VARIABLE);
            int indiceValor = c.getColumnIndex(clm.VALOR);
            do {
                imagen = new MultiImagen(c.getInt(indiceId), c.getInt(indiceAncho), c.getInt(indiceAlto),
                        c.getInt(indicePantalla), c.getInt(indiceLayout), c.getInt(indiceRequerido) > 0,
                        c.getInt(indiceHabilitado) > 0, c.getString(indiceValor), c.getString(indiceTitulo), c.getString(indiceNombreVariable));
                imagenes.add(imagen);
            } while (c.moveToNext());
        }
        c.close();
        return imagenes;
    }

    /**
     * Obtiene las firmas digitales asociadas al contenedor
     *
     * @param idContenedor el identificador del contenedor
     * @return un arreglo con las firmas digitales
     */
    public ArrayList<Vista> getFirmasDigitalesDesdeContenedor(int idContenedor) {
        ArrayList<Vista> firmasDigitales = new ArrayList<>();
        FirmaDigital.ColumnasTablaSql clm = new FirmaDigital.ColumnasTablaSql();
        String consulta = "select * from " + clm.TABLE_NAME + " where " + clm.LAYOUT + "=?";
        String[] params = {Integer.toString(idContenedor)};
        Cursor c = database.rawQuery(consulta, params);
        c.moveToFirst();
        FirmaDigital firmaDigital;
        if (c.getCount() > 0) {
            int indiceId = c.getColumnIndex(clm.ID);
            int indiceAncho = c.getColumnIndex(clm.ANCHO);
            int indiceAlto = c.getColumnIndex(clm.ALTO);
            int indiceHabilitado = c.getColumnIndex(clm.HABILITADO);
            int indiceLayout = c.getColumnIndex(clm.LAYOUT);
            int indicePantalla = c.getColumnIndex(clm.PANTALLA);
            int indiceRequerido = c.getColumnIndex(clm.REQUERIDO);
            int indiceTitulo = c.getColumnIndex(clm.TITULO);
            int indiceNombreVariable = c.getColumnIndex(clm.NOMBRE_VARIABLE);
            int indiceValor = c.getColumnIndex(clm.VALOR);
            do {
                firmaDigital = new FirmaDigital(c.getInt(indiceId), c.getInt(indiceAncho), c.getInt(indiceAlto),
                        c.getInt(indicePantalla), c.getInt(indiceLayout), c.getInt(indiceRequerido) > 0,
                        c.getInt(indiceHabilitado) > 0, c.getString(indiceValor), c.getString(indiceTitulo), c.getString(indiceNombreVariable));
                firmasDigitales.add(firmaDigital);
            } while (c.moveToNext());
        }
        c.close();
        return firmasDigitales;
    }

    public void eliminarFormulariosNoBorradores() {
        Formulario.ColumnasTablaSql clm = new Formulario.ColumnasTablaSql();
        String consulta = "select " + clm.ID + " from " + clm.TABLE_NAME + " where " + clm.BORRADOR + "=?";
        String[] params = {Integer.toString(0)};
        Cursor c = database.rawQuery(consulta, params);
        c.moveToFirst();
        int id;
        if (c.getCount() > 0) {
            do {
                id = c.getInt(0);
                eliminarFormulario(id);
            } while (c.moveToNext());
        }
        c.close();
    }

    /**
     * Borra el formulario y los elementos que tiene asociados
     *
     * @param idFormulario el id del formulario a borrar
     */
    public void eliminarFormulario(int idFormulario) {
        Formulario.ColumnasTablaSql clm = new Formulario.ColumnasTablaSql();
        eliminarContenedoresDesdeFormulario(idFormulario);
        String consulta = clm.ID + " =?";
        String[] params = {Integer.toString(idFormulario)};
        int ci = database.delete(clm.TABLE_NAME, consulta, params);
    }

    /**
     * Borra los contenedores que estan asociados al formulario
     *
     * @param idFormulario identificador del formulario
     */
    public void eliminarContenedoresDesdeFormulario(int idFormulario) {
        Contenedor.ColumnasTablaSql clm = new Contenedor.ColumnasTablaSql();
        String consulta = "select " + clm.ID + " from " + clm.TABLE_NAME + " where " + clm.FORMULARIO + "=?";
        String[] params = {Integer.toString(idFormulario)};
        Cursor c = database.rawQuery(consulta, params);
        c.moveToFirst();
        int id;
        if (c.getCount() > 0) {
            do {
                id = c.getInt(0);
                eliminarCheckCajasDesdeContendeor(id);
                eliminarRadioBotonesDesdeContenedor(id);
                eliminarComboCajasDesdeContenedor(id);
                eliminarRadioGruposDesdeContenedor(id);
                eliminarFirmasDigitalesDesdeContenedor(id);
                eliminarImagenesDesdeContenedor(id);
                eliminarInputDesdeContenedor(id);
                eliminarTextAreaDesdeContenedor(id);
            } while (c.moveToNext());
        }
        consulta = clm.FORMULARIO + " =?";
        int ci = database.delete(clm.TABLE_NAME, consulta, params);
        c.close();
    }

    /**
     * Borra las checkcajas que estan asociados al contenedor
     *
     * @param idContenedor identificador del contenedor
     */
    public void eliminarCheckCajasDesdeContendeor(int idContenedor) {
        CheckCaja.ColumnasTablaSql clm = new CheckCaja.ColumnasTablaSql();
        String consulta = clm.LAYOUT + " =?";
        String[] params = {Integer.toString(idContenedor)};
        int c = database.delete(clm.TABLE_NAME, consulta, params);
    }

    /**
     * Borra las imagenes que estan asociadas al conetenedor
     *
     * @param idContenedor identificador del contenedor
     */
    public void eliminarImagenesDesdeContenedor(int idContenedor) {
        Imagen.ColumnasTablaSql clm = new Imagen.ColumnasTablaSql();
        String consulta = clm.LAYOUT + " =?";
        String[] params = {Integer.toString(idContenedor)};
        int c = database.delete(clm.TABLE_NAME, consulta, params);
    }

    /**
     * Borra los campos input que estan asociadas al contenedor
     *
     * @param idContenedor identificador del contenedor
     */
    public void eliminarInputDesdeContenedor(int idContenedor) {
        Input.ColumnasTablaSql clm = new Input.ColumnasTablaSql();
        String consulta = clm.LAYOUT + " =?";
        String[] params = {Integer.toString(idContenedor)};
        int c = database.delete(clm.TABLE_NAME, consulta, params);
    }

    /**
     * Borra los campos text area que estan asociados al contenedor
     *
     * @param idContenedor identificador del contenedor
     */
    public void eliminarTextAreaDesdeContenedor(int idContenedor) {
        TextArea.ColumnasTablaSql clm = new TextArea.ColumnasTablaSql();
        String consulta = clm.LAYOUT + " =?";
        String[] params = {Integer.toString(idContenedor)};
        int c = database.delete(clm.TABLE_NAME, consulta, params);
    }

    /**
     * Borra las firmas digitales asociadas al contenedor
     *
     * @param idContenedor identificador del contenedor
     */
    public void eliminarFirmasDigitalesDesdeContenedor(int idContenedor) {
        FirmaDigital.ColumnasTablaSql clm = new FirmaDigital.ColumnasTablaSql();
        String consulta = clm.LAYOUT + " =?";
        String[] params = {Integer.toString(idContenedor)};
        int c = database.delete(clm.TABLE_NAME, consulta, params);
    }

    /**
     * Borra los radio botones asociados al contenedor
     *
     * @param idContenedor identificador del contenedor
     */
    public void eliminarRadioBotonesDesdeContenedor(int idContenedor) {
        RadioBoton.ColumnasTablaSql clm = new RadioBoton.ColumnasTablaSql();
        String consulta = clm.LAYOUT + " =?";
        String[] params = {Integer.toString(idContenedor)};
        int c = database.delete(clm.TABLE_NAME, consulta, params);
    }

    /**
     * Borra los radio grupos asociados al contenedor
     *
     * @param idContenedor identificador del contenedor
     */
    public void eliminarRadioGruposDesdeContenedor(int idContenedor) {
        RadioGrupo.ColumnasTablaSql clm = new RadioGrupo.ColumnasTablaSql();
        String consulta = clm.LAYOUT + " =?";
        String[] params = {Integer.toString(idContenedor)};
        int c = database.delete(clm.TABLE_NAME, consulta, params);
    }

    /**
     * Borra los combo cox que estan asociados al contenedor
     *
     * @param idContenedor identificador del contenedor
     */
    public void eliminarComboCajasDesdeContenedor(int idContenedor) {
        ComboCaja.ColumnasTablaSql clm = new ComboCaja.ColumnasTablaSql();
        String consulta = "select " + clm.ID + " from " + clm.TABLE_NAME + " where " + clm.LAYOUT + "=?";
        String[] params = {Integer.toString(idContenedor)};
        Cursor c = database.rawQuery(consulta, params);
        c.moveToFirst();
        int id;
        if (c.getCount() > 0) {
            do {
                id = c.getInt(0);
                eliminarElementosComboDesdeCombo(id);
            } while (c.moveToNext());
        }
        consulta = clm.LAYOUT + " =?";
        int ci = database.delete(clm.TABLE_NAME, consulta, params);
        c.close();
    }

    /**
     * Elimina los elementos del combo
     *
     * @param idCombo el identificador del combo
     */
    public void eliminarElementosComboDesdeCombo(int idCombo) {
        ElementoCombo.ColumnasTablaSql clm = new ElementoCombo.ColumnasTablaSql();
        String consulta = clm.COMBO + " =?";
        String[] params = {Integer.toString(idCombo)};
        int c = database.delete(clm.TABLE_NAME, consulta, params);
    }

    /**
     * Elimina todos los tipos de gestiones
     */
    public void eliminarTipoGestiones() {
        TipoGestion.ColumnasTablaSql clm = new TipoGestion.ColumnasTablaSql();
        String consulta = "";
        String[] params = {};
        int c = database.delete(clm.TABLENAME, consulta, params);
    }

    /**
     * elimina las gestiones que no son borradores
     */
    public void eliminarGestionesNoBorradores() {
        Gestion.ColumnasTablaSql clm = new Gestion.ColumnasTablaSql();
        String consulta = clm.ESBORRADOR + "=?";
        String[] params = {"0"};
        int c = database.delete(clm.TABLENAME, consulta, params);
    }

    /**
     * Elimina la gestion desde la base de datos
     *
     * @param gestion la gestion a eliminar
     */
    public void eliminarGestion(Gestion gestion) {
        Gestion.ColumnasTablaSql clm = new Gestion.ColumnasTablaSql();
        String consulta = clm.IDGESTION + "=?";
        String[] params = {Integer.toString(gestion.getIdgestion())};
        database.delete(clm.TABLENAME, consulta, params);
        eliminarFormulario(gestion.getFormulario().getId());
    }

    /**
     * Guarda el contenido del mensaje en la base de datos
     * @param contenidoMensaje  el contendio de mensaje a guardar
     * @return el id que le corresponde al mensaje
     */
    public int guardarContenidoMensaje(ContenidoMensaje contenidoMensaje)
    {
        int id = -1;
        try {
            id = (int) database.insertOrThrow(contenidoMensaje.getNombreTabla(), null, contenidoMensaje.getContenedorValores());
        } catch (SQLException ex) {
            Log.e("error", ex.getMessage());
        }
        return id;
    }

    /**
     * Obtiene un arreglo de contenidos de mensaje, a partir del cursor que se le pasa por parametro
     * @param c el cursor que contiene la información
     * @return el arreglo con los contenidos de mensaje
     */
    public ArrayList<ContenidoMensaje> getContenidoMensajes(Cursor c)
    {
        ArrayList<ContenidoMensaje> contenidoMensajes=new ArrayList<>();
        c.moveToFirst();
        ContenidoMensaje contenidoMensaje;
        JSONArray jsonArray;
        if(c.getCount()>0)
        {
            ContenidoMensaje.ColumnasTablaSql clm=new ContenidoMensaje.ColumnasTablaSql();
            int indiceId=c.getColumnIndex(clm.ID);
            int indiceFecha=c.getColumnIndex(clm.FECHA);
            int indiceEstado=c.getColumnIndex(clm.ESTADO);
            int indiceGestionesEliminadas=c.getColumnIndex(clm.GESTIONESELIMINADAS);
            int indiceNumero=c.getColumnIndex(clm.NUMERO);
            int indiceMensaje=c.getColumnIndex(clm.MENSAJE);
            int indiceCodigoUsuario=c.getColumnIndex(clm.CODIGOUSUARIO);
            do {
                try {
                    jsonArray=new JSONArray(c.getString(indiceGestionesEliminadas));
                } catch (JSONException e) {
                    jsonArray=new JSONArray();
                }
                contenidoMensaje=new ContenidoMensaje(c.getInt(indiceId),c.getString(indiceMensaje),
                        jsonArray,c.getInt(indiceEstado),c.getString(indiceNumero),c.getString(indiceFecha)
                ,c.getString(indiceCodigoUsuario));
                contenidoMensajes.add(contenidoMensaje);
            }while(c.moveToNext());
        }
        c.close();
        return contenidoMensajes;
    }

    /**
     * Obtiene todos los contenidos de mensaje
     * @return arreglo con los contenidos del mensaje
     */
    public ArrayList<ContenidoMensaje> getContenidoMensajes()
    {
        ContenidoMensaje.ColumnasTablaSql clm=new ContenidoMensaje.ColumnasTablaSql();
        String consulta = "select * from " + clm.TABLENAME +" where "+clm.ESTADO +" NOT IN ("+ContenidoMensaje.SOLOENVIAR+","+ ContenidoMensaje.NoMOSTRAR+") ORDER BY "+clm.ID+" DESC";
        Cursor c = database.rawQuery(consulta, new String[]{});
        return getContenidoMensajes(c);
    }

    /**
     * Obtiene todos los mensajes asociados al estado
     * @param arreglo el arreglo de estado por el que se filtra la búsqueda
     * @return arreglo con los contenidos del mensaje
     */
    public ArrayList<ContenidoMensaje> getContenidoMensajePorEstado(String arreglo)
    {
        ContenidoMensaje.ColumnasTablaSql clm=new ContenidoMensaje.ColumnasTablaSql();
        String consulta = "select * from " + clm.TABLENAME+" where "+clm.ESTADO + " IN "+arreglo;
        String[] params={};
        Cursor c = database.rawQuery(consulta,params);
        return getContenidoMensajes(c);
    }

    /**
     * Actualiza el contenido del mensaje
     * @param id  el identificador del contenido del mensaje
     * @param estado  el estado a actualizar
     */
    public void actualizarEstadoContenidoMensaje(int id,int estado)
    {
        ContenidoMensaje.ColumnasTablaSql clm = new ContenidoMensaje.ColumnasTablaSql();
        String consulta = clm.ID + "=?";
        ContentValues contentValues=new ContentValues();
        contentValues.put(clm.ESTADO,estado);
        String[] params={Integer.toString(id)};
        database.update(clm.TABLENAME,contentValues,consulta,params);
    }

    /**
     * Obtiene la cantidad de ocurrencias que se presenta en la base de datos con respecto al arreglo
     * que se envia por parametro
     * @param arreglo  el arreglo a evaluar
     * @return la cantidad ocurrencias
     */
    public int getCantidadOcurrenciasGestiones(String arreglo)
    {
        Gestion.ColumnasTablaSql clm=new Gestion.ColumnasTablaSql();
        String consulta = "select"+" count("+clm.ID+") from " + clm.TABLENAME + " where "  + clm.ID+" IN "+arreglo;
        String[] params = {};
        Cursor c = database.rawQuery(consulta, params);
        c.moveToFirst();
        int j=c.getInt(0);
        c.close();
        return j;

    }

    /**
     * Elimina las gestiones que tienen asoicado como id, algun elemento del arreglo que se pasa
     * por parametro
     * @param arreglo el arreglo que contiene los id
     * @return la cantidad de filas afectadas
     */
    public int eliminarGestiones(String arreglo)
    {
        Gestion.ColumnasTablaSql clm = new Gestion.ColumnasTablaSql();
        String consulta = clm.ID + " IN "+arreglo;
        String[] params = {};
        return database.delete(clm.TABLENAME, consulta, params);
    }

    /**
     * Elimina un contenido de un mensaje
     * @param id el identificador al que esta asoicado el mensaje
     */
    public void eliminarContenidoMensaje(int id)
    {
        ContenidoMensaje.ColumnasTablaSql clm = new ContenidoMensaje.ColumnasTablaSql();
        String consulta = clm.ID + " =? ";
        String[] params = {Integer.toString(id)};
        database.delete(clm.TABLENAME, consulta, params);
    }
}
