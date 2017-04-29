package com.valuarte.dtracking.ElementosGraficos;

import android.content.ContentValues;
import android.text.InputType;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Representa un formulario general
 *
 * @version 1.0
 */
public class Formulario implements Serializable{
    /**
     * identificadpr del formulario en la base de datos
     */
    private int id;
    /**
     * Indica si el formulario es un borrador o no
     */
    private boolean esBorrador;
    /**
     * contenedores(layouts) que posee el formulario
     */
    private ArrayList<Contenedor> contenedores;
    /**
     * acceso a la base de datos
     */
    private int idFormulario;
    /**
     * Nombre del formulario
     */
    private String name;
    /**
     * Fecha de creacion del formulario
     */
    private Calendar fechaCreacion;
    public Formulario( boolean esBorrador,int idFormulario,String name) {
        this.setEsBorrador(esBorrador);
        this.idFormulario=idFormulario;
        this.name=name;
        fechaCreacion=Calendar.getInstance();
    }

    public Formulario(ArrayList<Contenedor> contenedores, int id, boolean esBorrador,
                      int idFormulario,String name,String fechaCreacion) {
        this.setContenedores(contenedores);
        this.setId(id);
        this.setEsBorrador(esBorrador);
        this.idFormulario=idFormulario;
        this.name=name;
        try {
            this.fechaCreacion=Calendar.getInstance();
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fechaCreacion);
            this.getFechaCreacion().setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Crea un formulario desde un objeto json
     * @return  arreglo con los contendores del formulario
     */
    public void crearFormularioDesdeJson(JSONObject jsonObject,RecursosBaseDatos recursosBaseDatos) {
        this.contenedores=new ArrayList<>();
        int idFormulario=recursosBaseDatos.guardarFormulario(this);
        this.setId(idFormulario);
        Contenedor contenedor = new Contenedor(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, recursosBaseDatos.agregarIdPantalla()
                , -1, false,  "LinearLayout", "vertical", true, idFormulario);
        int idLayout = recursosBaseDatos.guardarVista(contenedor);
        JSONArray campos = null;
        try {
            campos = jsonObject.getJSONArray("campos");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (campos != null) {
            JSONObject jsonObject1;
            JSONArray elementos;
            JSONObject jsonObject2;
            ArrayList<ElementoCombo> elementoCombos;
            ArrayList<RadioBoton> radioBotons;
            Vista vista = null;
            ElementoCombo elementoCombo;
            RadioBoton radioBoton;
            int idSele;
            for (int i = 0; i < campos.length(); i++) {
                try {
                    jsonObject1 = campos.getJSONObject(i);
                    String tipo = jsonObject1.getString("tipo");
                    switch (tipo.toLowerCase()) {
                        case "checkbox":
                            vista = new CheckCaja(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, recursosBaseDatos.agregarIdPantalla(), idLayout, jsonObject1.getBoolean("requerido"),
                                     jsonObject1.getString("nombreVariable"), jsonObject1.getString("titulo"), false
                                    , jsonObject1.getBoolean("habilitado"));
                            contenedor.agregarVista(vista);
                            vista.setId(recursosBaseDatos.guardarVista(vista));
                            break;
                        case "input":
                            vista = new Input(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, recursosBaseDatos.agregarIdPantalla(), idLayout, jsonObject1.getBoolean("requerido"),
                                    InputType.TYPE_CLASS_TEXT, "", 300,
                                    jsonObject1.getString("nombreVariable"), jsonObject1.getString("titulo"), jsonObject1.getBoolean("habilitado"));
                            contenedor.agregarVista(vista);
                            vista.setId(recursosBaseDatos.guardarVista(vista));
                            break;
                        case "radio":
                            elementos = jsonObject1.getJSONArray("elementos");
                            vista = new RadioGrupo(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT,
                                    recursosBaseDatos.agregarIdPantalla(), idLayout, jsonObject1.getBoolean("requerido"),
                                    jsonObject1.getString("orientacion"), jsonObject1.getString("nombreVariable"), jsonObject1.getString("titulo"), true);
                            int idGroup=recursosBaseDatos.guardarVista(vista);
                            vista.setId(idGroup);
                            radioBotons = new ArrayList<>();
                            boolean seleccionado;
                            for (int j = 0; j < elementos.length(); j++) {
                                jsonObject2 = elementos.getJSONObject(j);
                                if (-1 == jsonObject2.getInt("id")) {
                                    seleccionado = true;
                                } else {
                                    seleccionado = false;
                                }
                                radioBoton=new RadioBoton(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT
                                        , recursosBaseDatos.agregarIdPantalla(), idLayout, jsonObject1.getBoolean("requerido"),seleccionado,
                                        jsonObject1.getString("nombreVariable"), jsonObject2.getString("valor"),true,
                                        jsonObject2.getInt("id"),idGroup);
                                radioBoton.setId(recursosBaseDatos.guardarVista(radioBoton));
                                radioBotons.add(radioBoton);
                            }
                            ((RadioGrupo)vista).setRadioBotons(radioBotons);
                            contenedor.agregarVista(vista);

                            break;
                        case "combobox":
                            elementos = jsonObject1.getJSONArray("elementos");
                            vista = new ComboCaja(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, recursosBaseDatos.agregarIdPantalla(), idLayout, jsonObject1.getBoolean("requerido")
                                    ,jsonObject1.getString("nombreVariable"), jsonObject1.getString("titulo")
                                    , jsonObject1.getBoolean("habilitado"));
                            int idCombo=recursosBaseDatos.guardarVista(vista);
                            elementoCombos = new ArrayList<>();
                            idSele = -1;
                            for (int j = 0; j < elementos.length(); j++) {
                                jsonObject2 = elementos.getJSONObject(j);
                                if (jsonObject2.getInt("id") == idSele) {
                                    elementoCombo=new ElementoCombo(jsonObject2.getInt("id"), jsonObject2.getString("valor"), true,idCombo);
                                } else {
                                    elementoCombo=new ElementoCombo(jsonObject2.getInt("id"), jsonObject2.getString("valor"), false,idCombo);
                                }
                                elementoCombo.setId(recursosBaseDatos.guardarElementoCombo(elementoCombo));
                                elementoCombos.add(elementoCombo);
                            }

                            contenedor.agregarVista(vista);
                            vista.setId(idCombo);
                            ((ComboCaja) vista).setElementoCombos(elementoCombos);

                            break;
                        case "textarea":
                            vista = new TextArea(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                                    recursosBaseDatos.agregarIdPantalla(), idLayout, jsonObject1.getBoolean("requerido"),
                                    InputType.TYPE_CLASS_TEXT,"", 300,
                                    jsonObject1.getString("nombreVariable"), jsonObject1.getString("titulo"), jsonObject1.getBoolean("habilitado"));
                            contenedor.agregarVista(vista);
                            vista.setId(recursosBaseDatos.guardarVista(vista));
                            break;
                        case "foto":
                            vista=new Imagen(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                                    recursosBaseDatos.agregarIdPantalla(), idLayout, jsonObject1.getBoolean("requerido"), jsonObject1.getBoolean("habilitado"),"",jsonObject1.getString("titulo"),
                                    jsonObject1.getString("nombreVariable"));
                            contenedor.agregarVista(vista);
                            vista.setId(recursosBaseDatos.guardarVista(vista));
                            break;
                        case "multi foto":
                            vista=new MultiImagen(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                                    recursosBaseDatos.agregarIdPantalla(), idLayout, jsonObject1.getBoolean("requerido"), jsonObject1.getBoolean("habilitado"),"",jsonObject1.getString("titulo"),
                                    jsonObject1.getString("nombreVariable"));
                            contenedor.agregarVista(vista);
                            vista.setId(recursosBaseDatos.guardarVista(vista));
                            break;
                        case "firma":
                            vista=new FirmaDigital(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                                    recursosBaseDatos.agregarIdPantalla(), idLayout, jsonObject1.getBoolean("requerido"),
                                    jsonObject1.getBoolean("habilitado"),"",jsonObject1.getString("titulo"),jsonObject1.getString("nombreVariable"));
                            contenedor.agregarVista(vista);
                            vista.setId(recursosBaseDatos.guardarVista(vista));
                            break;
                    }
                } catch (JSONException e) {
                    Log.e("excepcion", e.getMessage());
                    continue;
                }
            }
            getContenedores().add(contenedor);
        }
    }

    public ArrayList<Contenedor> getContenedores() {
        return contenedores;
    }

    /**
     * Es el identificador del formulario desde el web service
     */
    public int getIdFormulario() {
        return idFormulario;
    }

    /**
     * Es el nombre del formulario
     */
    public String getName() {
        return name;
    }

    /**
     * Fecha de creacion del formulario
     */
    public Calendar getFechaCreacion() {
        return fechaCreacion;
    }

    public void setEsBorrador(boolean esBorrador) {
        this.esBorrador = esBorrador;
    }

    public void setContenedores(ArrayList<Contenedor> contenedores) {
        this.contenedores = contenedores;
    }

    /**
     * identificadpr del formulario en la base de datos
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Columnas de la tabla sql
     */
    public static final class ColumnasTablaSql {
        public static final String TABLE_NAME = "formularios";
        public static final String ID = "id";
        public static final String BORRADOR = "esBorrador";
        public static final String IDFORMULARIO="idFormulario";
        public static final String NAME="name";
        public static final String FECHACREACION="fechaCreacion";
    }

    /**
     * Genera una cadena para la creacion de la tabla
     *
     * @return la cadena con la sentencia sql para la creación de la cadena
     */
    public static final String construirTablaSqlite() {
        ColumnasTablaSql c = new ColumnasTablaSql();
        String tabla = "create table " + c.TABLE_NAME + " (" + c.ID + " " + RecursosBaseDatos.INT_TYPE + "  primary key autoincrement,"
                + c.BORRADOR + " " + RecursosBaseDatos.BOOLEAN_TYPE+","+c.FECHACREACION+" "+RecursosBaseDatos.DATE_TIME_TYPE +","+c.NAME+" "+RecursosBaseDatos.STRING_TYPE+","+ c.IDFORMULARIO + " " + RecursosBaseDatos.INT_TYPE +")";
        return tabla;
    }

    /**
     * Obtiene el contenedor de valores del formulario para crearlo
     *
     * @return el contendor de valores
     */
    public ContentValues getContenedorValores() {
        ContentValues contentValues = new ContentValues();
        ColumnasTablaSql c = new ColumnasTablaSql();
        contentValues.put(c.BORRADOR, esBorrador);
        contentValues.put(c.IDFORMULARIO, getIdFormulario());
        contentValues.put(c.NAME, getName());
        fechaCreacion=Calendar.getInstance();
        contentValues.put(c.FECHACREACION,getFechaCreacionString());
        return contentValues;
    }

    /**
     * Clona el formulario actual
     * @return el formulario clonado
     */
    public Formulario clone()
    {
        ArrayList<Contenedor> cs=new ArrayList<>();
        for(Contenedor c:contenedores)
        {
            cs.add((Contenedor)c.clone());
        }
        Formulario f=new Formulario(cs, getId(),esBorrador,
        idFormulario,name,getFechaCreacionString());
        return f;
    }
    /**
     * Obtiene la fecha registrada en formato String
     * @return
     */
    public String getFechaCreacionString()
    {

        String fecha= getFechaCreacion().get(Calendar.YEAR)+"-"+ getFechaCreacion().get(Calendar.MONTH)+"-"+ getFechaCreacion().get(Calendar.DAY_OF_MONTH) +" "
                + getFechaCreacion().get(Calendar.HOUR_OF_DAY)+":"+ getFechaCreacion().get(Calendar.MINUTE)+":"+ getFechaCreacion().get(Calendar.SECOND);
        return fecha;
    }


}
