package com.valuarte.dtracking.ElementosGraficos;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.valuarte.dtracking.R;
import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.Excepciones.NoSoportaValorException;
import com.valuarte.dtracking.Excepciones.ValorRequeridoException;

import java.io.File;

/**
 * Representa el campo de firma en el formulario
 * @version 1.0
 */
public class FirmaDigital extends Vista{
    /**
     * El valor de la firma digital, que en este caso sera la ruta de la imagen con la firma
     */
    private String valor;
    /**
     * Escucha eventos en el boton
     */
    private EventoBotonFirma listener;
    /**
     * Titulo del campo
     */
    private String titulo;
    /**
     * nombre de la variable
     */
    private String nombreVariable;
    public FirmaDigital(int ancho, int alto, int idPantalla, int idLayout, boolean requerido, boolean habilitado, String valor,String titulo,String nombreVariable) {
        super(ancho, alto, idPantalla, idLayout, requerido, habilitado);
        this.setValor(valor);
        this.titulo=titulo;
        this.nombreVariable=nombreVariable;
    }

    public FirmaDigital(int id, int ancho, int alto, int idPantalla, int idLayout, boolean requerido, boolean habilitado, String valor,String titulo,String nombreVariable) {
        super(id, ancho, alto, idPantalla, idLayout, requerido, habilitado);
        this.setValor(valor);
        this.titulo=titulo;
        this.nombreVariable=nombreVariable;
    }

    public FirmaDigital() {
    }

    /**
     * Construye el widget(campo) que va ir en el formulario
     *
     * @param context
     * @return View  campo del formulario
     */
    @Override
    public View construirVista(Context context) {
        LinearLayout linearLayout=new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        listener=(EventoBotonFirma)context;
        ImageView imageView=new ImageView(context);
        imageView.setId(idPantalla);
        FloatingActionButton floatingActionButton=new FloatingActionButton(context);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.enFirmaSeleccionado(idPantalla);
            }
        });
        this.view=imageView;
        TextView textView=new TextView(context);
        textView.setText(getTitulo());
        linearLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        floatingActionButton.setImageResource(R.drawable.ic_pen);
        linearLayout.addView(imageView,new CoordinatorLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight()/3));
        if(valor!=null)
        {
            if(!valor.equals(""))
            {
                Bitmap bitmap = BitmapFactory.decodeFile(valor);
                if(bitmap!=null) {
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth(), ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight() / 3, false));
                }
            }
        }
        linearLayout.addView(floatingActionButton,new CoordinatorLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        return linearLayout;
    }

    /**
     * Obtiene el valor de la vista, si es que soporta valor
     *
     * @return Object  objeto que representa el valor
     * @throws NoSoportaValorException en caso de que la vista no soporte el valor
     */
    @Override
    public Object getValor() throws NoSoportaValorException,ValorRequeridoException {
        if(requerido)
        {
            if(valor.trim().equals(""))
            {
                throw new ValorRequeridoException("El campo " + getTitulo() + " esta sin completar y es requerido", nombreVariable,
                        getTitulo(), view.getX(), view.getY());
            }
            File file=new File(valor);
            if(file.exists())
            {
                return null;
            }
            else {
                throw new ValorRequeridoException("El campo " + getTitulo() + " tiene una imagen que no existeo", nombreVariable,
                        getTitulo(), view.getX(), view.getY());
            }
        }
        return null;
    }

    /**
     * Obtiene el valor de la imagen, que es la ruta
     * @return la cadena que representa la ruta
     */
    public String getVal()
    {
        return valor;
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
        String tabla="create table "+ ColumnasTablaSql.TABLE_NAME+"("+c.ID+" "+ RecursosBaseDatos.INT_TYPE+" primary key autoincrement ,"
                +c.ANCHO+" "+RecursosBaseDatos.INT_TYPE+","+c.ALTO+" "+RecursosBaseDatos.INT_TYPE+","
                +c.VALOR+" "+RecursosBaseDatos.STRING_TYPE+","+c.REQUERIDO+" "+RecursosBaseDatos.BOOLEAN_TYPE+","
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
        contentValues.put(c.TITULO, getTitulo());
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

    }

    /**
     * Clona el objeto actual
     *
     * @return la vita clonadad
     */
    @Override
    public Vista clone() {
        return new FirmaDigital(id,ancho, alto, idPantalla, idLayout,requerido, habilitado, valor, getTitulo(),nombreVariable);
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    /**
     * Titulo del campo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Clase que contiene los nombres de las columnas de la tabla
     */
    public final static class ColumnasTablaSql {
        public static final String TABLE_NAME = "FirmasDigitales";
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
    public interface EventoBotonFirma
    {
        void enFirmaSeleccionado(int idFirma);
    }

}
