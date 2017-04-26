package com.valuarte.dtracking.BaseDatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.valuarte.dtracking.ElementosGraficos.CheckCaja;
import com.valuarte.dtracking.ElementosGraficos.ComboCaja;
import com.valuarte.dtracking.ElementosGraficos.Contenedor;
import com.valuarte.dtracking.ElementosGraficos.ElementoCombo;
import com.valuarte.dtracking.ElementosGraficos.FirmaDigital;
import com.valuarte.dtracking.ElementosGraficos.Formulario;
import com.valuarte.dtracking.ElementosGraficos.Gestion;
import com.valuarte.dtracking.ElementosGraficos.Imagen;
import com.valuarte.dtracking.ElementosGraficos.Input;
import com.valuarte.dtracking.ElementosGraficos.RadioBoton;
import com.valuarte.dtracking.ElementosGraficos.RadioGrupo;
import com.valuarte.dtracking.ElementosGraficos.TextArea;
import com.valuarte.dtracking.ElementosGraficos.TipoGestion;
import com.valuarte.dtracking.MensajeTextos.Sms;
import com.valuarte.dtracking.Util.ContenidoMensaje;
import com.valuarte.dtracking.Util.Usuario;

import java.io.Serializable;

/**
 * Maneja la creación y actualización de la base de datos
 * @version 1.0
 */
public class DatabaseHelper extends SQLiteOpenHelper implements Serializable{
    public static final String DATABASE_NAME = "TcGlobal.db";
    public static final int DATABASE_VERSION = 1;
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String pantallas="create table IdsPantalla(id integer autoincremeny primary key,numero integer)";
        sqLiteDatabase.execSQL(TipoGestion.construirTablaSqlite());
        sqLiteDatabase.execSQL(Formulario.construirTablaSqlite());
        sqLiteDatabase.execSQL(Gestion.crearTablaSlqite());
        sqLiteDatabase.execSQL(pantallas);
        sqLiteDatabase.execSQL(new Contenedor().crearTablaSqlite());
        sqLiteDatabase.execSQL(new Input().crearTablaSqlite());
        sqLiteDatabase.execSQL(new TextArea().crearTablaSqlite());
        sqLiteDatabase.execSQL(new CheckCaja().crearTablaSqlite());
        sqLiteDatabase.execSQL(new RadioGrupo().crearTablaSqlite());
        sqLiteDatabase.execSQL(new RadioBoton().crearTablaSqlite());
        sqLiteDatabase.execSQL(new ComboCaja().crearTablaSqlite());
        sqLiteDatabase.execSQL(new Imagen().crearTablaSqlite());
        sqLiteDatabase.execSQL(new FirmaDigital().crearTablaSqlite());
        sqLiteDatabase.execSQL(Usuario.construirTablaSqlite());
        sqLiteDatabase.execSQL(ElementoCombo.crearTablaSqlite());
        sqLiteDatabase.execSQL(Sms.crearTablaSqlite());
        sqLiteDatabase.execSQL(ContenidoMensaje.crearTablaSqlite());

    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
