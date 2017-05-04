package com.valuarte.dtracking.ElementosGraficos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.Excepciones.NoSoportaValorException;
import com.valuarte.dtracking.Excepciones.ValorRequeridoException;
import com.valuarte.dtracking.FormularioActivity;
import com.valuarte.dtracking.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Representa un campo de imagen en el formulario
 *
 * @version 1, 0
 */
public class MultiImagen extends Vista {
    /**
     * Representa el valor de la imagen, que en este caso, va a ser la ruta de la imagen
     */
    private String valor;
    /**
     * Escucha eventos
     */
    private ListenerBotonImagen listener;
    /**
     * Titulo del campo
     */
    private String titulo;
    /**
     * nombre de la variable
     */
    private String nombreVariable;


    public MultiImagen() {

    }

    public MultiImagen(int ancho, int alto, int idPantalla, int idLayout, boolean requerido, boolean habilitado,
                       String valor, String titulo, String nombreVariable) {
        super(ancho, alto, idPantalla, idLayout, requerido, habilitado);
        this.setValor(valor);
        this.titulo=titulo;
        this.nombreVariable=nombreVariable;
    }

    public MultiImagen(int id, int ancho, int alto, int idPantalla, int idLayout, boolean requerido,
                       boolean habilitado, String valor, String titulo, String nombreVariable) {
        super(id, ancho, alto, idPantalla, idLayout, requerido, habilitado);
        this.setValor(valor);
        this.titulo=titulo;
        this.nombreVariable=nombreVariable;
    }



    /**
     * Construye el widget(campo) que va ir en el formulario
     *
     * @param context
     * @return View  campo del formulario
     */
    @Override
    public View construirVista(final Context context) {
        listener = (ListenerBotonImagen) context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View inflatedLayout= inflater.inflate(R.layout.multi_foto_layout, null, false);
        FloatingActionButton fab=(FloatingActionButton) inflatedLayout.findViewById(R.id.agregarImagen);
        TextView txtTitulo=(TextView) inflatedLayout.findViewById(R.id.titulo);
        LinearLayout layoutImagnes=(LinearLayout) inflatedLayout.findViewById(R.id.LayoutImagnes);
        layoutImagnes.setId(idPantalla);

        txtTitulo.setText(titulo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickMultiImageSeleccionado(idPantalla);
            }
        });
        return inflatedLayout;
        /*

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        listener = (ListenerBotonImagen) context;
        ImageView imageView = new ImageView(context);
        imageView.setId(idPantalla);
        FloatingActionButton floatingActionButton = new FloatingActionButton(context);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickSeleccionado(idPantalla);
            }
        });
        this.view = imageView;

        TextView textView=new TextView(context);
        textView.setText(getTitulo());
        imageView.setFocusable(true);
        imageView.setFocusableInTouchMode(true);
        linearLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        floatingActionButton.setImageResource(R.drawable.ic_image_area);
        linearLayout.addView(imageView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight() / 3));
        if(valor!=null)
        {
            if(!valor.equals(""))
            {
                Bitmap bitmap = BitmapFactory.decodeFile(valor);
                if(bitmap!=null) {
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth(), ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight() / 3, false));
                }
                }
        }
        linearLayout.addView(floatingActionButton, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return linearLayout;*/
    }
    private void galeriaIntent(Context context) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity)context).startActivityForResult(
                Intent.createChooser(intent, "Seleccione Imagen"),
                FormularioActivity.GALERIA);
    }

    private void camaraIntenet(Context context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        File imageFolder = new File(Environment.getExternalStorageDirectory(), "Imagenes TcGlobal Banca 2");
        File f = new File(imageFolder + File.separator + "image-tcglobal" + timeStamp + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        ((Activity)context).startActivityForResult(intent,
                FormularioActivity.CAMARA);
    }

    /**
     * Obtiene el valor de la vista, si es que soporta valor
     *
     * @return Object  objeto que representa el valor
     * @throws NoSoportaValorException en caso de que la vista no soporte el valor
     */
    @Override
    public Object getValor() throws NoSoportaValorException,ValorRequeridoException {
        boolean encontrado=true;
       if(requerido)
       {
           if(valor.trim().equals(""))
           {
               throw new ValorRequeridoException("El campo " + getTitulo() + " esta sin completar y es requerido", nombreVariable,
                       getTitulo(), view.getX(), view.getY());
           }
           else
           {
               try {
                   JSONArray jImagenes = new JSONArray(valor);
                   for(int i=0; i<jImagenes.length();i++){
                       File file=new File(jImagenes.getString(i));
                       if(!file.exists())
                       {
                           encontrado=false;
                       }
                   }
                   if(encontrado){
                       return valor;
                   }else {
                       throw new ValorRequeridoException("El campo " + getTitulo() + " tiene una imagen que no existe ", nombreVariable,
                               getTitulo(), view.getX(), view.getY());
                   }

               } catch (JSONException e) {
                   e.printStackTrace();
               }

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
        contentValues.put(c.PANTALLA, idPantalla);
        contentValues.put(c.NOMBRE_VARIABLE, nombreVariable);
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
        return new MultiImagen(id, ancho, alto, idPantalla, idLayout, requerido,
         habilitado, valor, getTitulo(),nombreVariable);
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
        public static final String TABLE_NAME = "MultiImagenes";
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
    /**
     * Permite la comunicacion de eventos con  un activity
     */
    public interface ListenerBotonImagen {
        /**
         * Evento cuando se selecciona el boton para cargar o tomar una imagen
         *
         * @param idPantalla
         */
        void onClickMultiImageSeleccionado(int idPantalla);
    }

}
