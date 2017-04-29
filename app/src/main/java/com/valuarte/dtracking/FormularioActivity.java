package com.valuarte.dtracking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.ElementosGraficos.ComboCaja;
import com.valuarte.dtracking.ElementosGraficos.Contenedor;
import com.valuarte.dtracking.ElementosGraficos.ElementoCombo;
import com.valuarte.dtracking.ElementosGraficos.FirmaDigital;
import com.valuarte.dtracking.ElementosGraficos.Formulario;
import com.valuarte.dtracking.ElementosGraficos.Gestion;
import com.valuarte.dtracking.ElementosGraficos.Imagen;
import com.valuarte.dtracking.ElementosGraficos.MultiImagen;
import com.valuarte.dtracking.ElementosGraficos.RadioBoton;
import com.valuarte.dtracking.ElementosGraficos.RadioGrupo;
import com.valuarte.dtracking.ElementosGraficos.TipoGestion;
import com.valuarte.dtracking.ElementosGraficos.Vista;
import com.valuarte.dtracking.Excepciones.NoSoportaValorException;
import com.valuarte.dtracking.Excepciones.ValorRequeridoException;
import com.valuarte.dtracking.MensajeTextos.MensajeEntregadoIntent;
import com.valuarte.dtracking.MensajeTextos.MensajeEnviadoIntent;
import com.valuarte.dtracking.Util.ConexionAInternet;
import com.valuarte.dtracking.Util.EncodingJSON;
import com.valuarte.dtracking.Util.ReceiverManager;
import com.valuarte.dtracking.Util.SincronizacionGestionSMS;
import com.valuarte.dtracking.Util.SincronizacionGestionWeb;
import com.valuarte.dtracking.Util.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import butterknife.ButterKnife;

/**
 * Representa el activity que carga el formulario que va a llenar el usuario
 * @version 1.0
 */
public class FormularioActivity extends AppCompatActivity implements Imagen.ListenerBotonImagen,
        MultiImagen.ListenerBotonImagen,
        FirmaDigital.EventoBotonFirma, MensajeEnviadoIntent.ListenerMensajeEnviado,
        SincronizacionGestionWeb.ListenerSincronizacionWeb, LocationListener {
    public static final int CAMARA = 100;
    public static final int GALERIA = 200;
    /**
     * El formulario que se va a mostrar
     */
    private Formulario formulario;
    /**
     * Identificador en la pantalla de image view para una imagen
     */
    private int idImage;
    /**
     * Las rutas de las imagenes del formulario
     */
    private HashMap<Integer, String> rutas;
    /**
     * Las rutas de las imagenes del formulario
     */
    private HashMap<Integer, String> rutasMulti;
    /**
     * Identificador en la pantalla de image view para una firma
     */
    private int idFirma;
    /**
     * Archivo temporal que se genera cuando se abre la camara
     */
    private File f;
    /**
     * Carpeta en la que se guardan las imagenes
     */
    private File imageFolder;
    /**
     * Botones del dialogo para la firma
     */
    private Button mClear, mGetSign, mCancel;
    /**
     * Dialogo para conseguir la firma
     */
    private Dialog dialog;
    /**
     * Layout para dibujar la firma
     */
    private LinearLayout mContent;
    /**
     * Vista que contiene la firma
     */
    private View view;
    /**
     * Manejador del touch event para la firma
     */
    private signature mSignature;
    /**
     * Permite la conversion del lienzo de la firma en una imagen
     */
    private Bitmap bitmap;
    /**
     * Boton para enviar el formulario por mensaje de texto
     */
    private FloatingActionButton mensageTexto;
    /**
     * Boton para subir el formulario via internet
     */
    private FloatingActionButton subirServidor;
    /**
     * Boton para guardar el formulario como borrador
     */
    private FloatingActionButton guardar;
    /**
     * rutas de las imagenes de las firmas
     */
    private HashMap<Integer, String> rutasFirmas;
    /**
     * Acceso a la base de datos
     */
    private RecursosBaseDatos recursosBaseDatos;
    /**
     * Scroll que contiene el activity
     */
    private ScrollView scroll;
    /**
     * Mensaje que se muestra para confirmar una orden
     */
    private AlertDialog.Builder mensajeConfirmacion;
    /**
     * dialogo de progreso de envio de sms
     */
    private ProgressDialog cargaSMS;
    /**
     * Mensaje que se muestra para mostrar un error
     */
    private AlertDialog.Builder mensajeError;
    /**
     * Mensaje que se muestra para dar informacion
     */
    private AlertDialog.Builder mensajeInformacion;
    /**
     * Avisa cuando el mensaje ha sido enviado desde el dispositivo
     */
    private MensajeEnviadoIntent mensajeEnviadoIntent;
    /**
     * Avisa cuando el mensaje ha sido entregado al otro dispositivo
     */
    private MensajeEntregadoIntent mensajeEntregadoIntent;
    /**
     * La gestion que se va a llenar
     */
    private Gestion gestion;
    /**
     * El tipo de gestion al que pertenece la gestion
     */
    private TipoGestion tipoGestion;
    /**
     * El usuario con la sesion actual
     */
    private Usuario usuario;
    /**
     * Maneja la prueba de la conexion a internet
     */
    private ConexionAInternet conexionAInternet;
    //constantes para registrar receiver cuando se enivia un mensaje
    public final static String SENT = "SMS_SENT";
    public final static String DELIVERED = "SMS_DELIVERED";
    /**
     * Gestiona la localizacion del dispositivo
     */
    private LocationManager locationManager;
    /**
     * Latitud actual dle dispositivo
     */
    private double latitudActual;
    /**
     * Longitud actual del dispositivo
     */
    private double longitudActual;

    ReceiverManager registerReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_formulario);
            ButterKnife.bind(this);
            setupToolbar();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1.0f, this);

            recursosBaseDatos = new RecursosBaseDatos(this);
            conexionAInternet = new ConexionAInternet();
            usuario = recursosBaseDatos.getUsuario();
            imageFolder = new File(Environment.getExternalStorageDirectory(), "Imagenes TcGlobal Banca 2");
            if (!imageFolder.exists()) {
                imageFolder.mkdir();
            }

            rutas = new HashMap<>();
            rutasFirmas = new HashMap<>();
            rutasMulti=new HashMap<>();

            Intent intent = getIntent();
            gestion = (Gestion) intent.getSerializableExtra("gestion");
            tipoGestion = (TipoGestion) intent.getSerializableExtra("tipoGestion");
            formulario = gestion.getFormulario();
            setTitle(formulario.getName());
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.principal);
            for (Contenedor c : formulario.getContenedores()) {
                LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(c.getAncho(), c.getAlto());
                linearLayout.addView(c.construirVista(this), l);
            }
            linearLayout.addView(crearPieDeFormulario(), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.FILL_PARENT));
            if (savedInstanceState != null) {
                rutasMulti = (HashMap<Integer, String>) savedInstanceState.getSerializable("rutasImagenesMulti");
                rutas = (HashMap<Integer, String>) savedInstanceState.getSerializable("rutasImagenes");
                rutasFirmas = (HashMap<Integer, String>) savedInstanceState.getSerializable("rutasFirmas");
                cargarImagenes();
            }
        }
        catch (Exception e){}
    }

    public void onResume() {
        super.onResume();
        if(registerReceiver==null)
            registerReceiver = new ReceiverManager(this);

        if (mensajeEnviadoIntent == null && mensajeEntregadoIntent == null) {
            mensajeEnviadoIntent = new MensajeEnviadoIntent(FormularioActivity.this);
            mensajeEntregadoIntent = new MensajeEntregadoIntent(null);
        }else if(mensajeEnviadoIntent == null && mensajeEntregadoIntent != null) {
            mensajeEnviadoIntent = new MensajeEnviadoIntent(FormularioActivity.this);
        }else {
            mensajeEntregadoIntent = new MensajeEntregadoIntent(null);
        }

        if (!registerReceiver.isReceiverRegistered(mensajeEnviadoIntent)) {
            registerReceiver.unregisterReceiver(mensajeEnviadoIntent);
        }
        if (!registerReceiver.isReceiverRegistered(mensajeEntregadoIntent)) {
            registerReceiver.unregisterReceiver(mensajeEntregadoIntent);
        }
        //  Log.e("REGISTRANDO","REGISTRANDO");
        /**     mensajeEnviadoIntent = new MensajeEnviadoIntent(FormularioActivity.this);
         mensajeEntregadoIntent = new MensajeEntregadoIntent(null);
         registerReceiver(mensajeEnviadoIntent, new IntentFilter(SENT));
         registerReceiver(mensajeEntregadoIntent, new IntentFilter(DELIVERED));
         */
    }

    public void onPause() {
        super.onPause();
        //   Log.e("QUITANDO ", "QUITANDO");
        if (!registerReceiver.isReceiverRegistered(mensajeEnviadoIntent)) {
            registerReceiver.unregisterReceiver(mensajeEnviadoIntent);
        }
        if (!registerReceiver.isReceiverRegistered(mensajeEntregadoIntent)) {
            registerReceiver.unregisterReceiver(mensajeEntregadoIntent);
        }

    }


    /**
     * Crea el pie del formulario
     */
    private LinearLayout crearPieDeFormulario() {
        LinearLayout pie = new LinearLayout(this);
        pie.setOrientation(LinearLayout.VERTICAL);
        Button button = new Button(this);
        button.setText("Guardar");
        button.setTextColor(getResources().getColor(android.R.color.white));
        button.setBackgroundResource(R.drawable.boton_ver_presionado);
        LinearLayout.LayoutParams paramsBoton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsBoton.setMargins(10, 10, 10, 10);
        pie.addView(button, paramsBoton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tieneGpsActivado()) {
                    mensajeConfirmacion = new AlertDialog.Builder(FormularioActivity.this);
                    mensajeConfirmacion.setTitle("Confirmación");
                    mensajeConfirmacion.setMessage("Quiere sincronizar la gestion?");
                    mensajeConfirmacion.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                asignarRutasImagenesAVistas();
                                try {
                                    if (longitudActual != 0 & latitudActual != 0) {
                                        gestion.setLatitud(latitudActual);
                                        gestion.setLongitud(longitudActual);
                                    } else {
                                        forzarObtencionCoordenadas();
                                        gestion.setLatitud(latitudActual);
                                        gestion.setLongitud(longitudActual);
                                    }
                                    gestion.setFecha(Gestion.generarFechaDesdeCalendar(Calendar.getInstance()));
                                    JSONObject jsonObject = obtenerCampos(formulario, gestion);
                                    Formulario f = guardarFormularioCompleto();
                                    gestion.setFormulario(f);
                                    gestion.setEsBorrador(true);
                                    recursosBaseDatos.actualizarGestion(gestion);
                                    sincronizarGestion(jsonObject);

                                } catch (ValorRequeridoException e) {
                                    mensajeError = new AlertDialog.Builder(FormularioActivity.this);
                                    mensajeError.setTitle("Error");
                                    mensajeError.setMessage(e.getMessage());
                                    mensajeError.setCancelable(true);
                                    mensajeError.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                                    mensajeError.show();
                                }
                            }catch (Exception e){}


                        }
                    });
                    mensajeConfirmacion.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    mensajeConfirmacion.show();
                } else {
                    construirErrorGps();
                }
            }

        });
        return pie;
    }

    /**
     * Construye un error cuando no se pudo sincronizar la gestion
     */
    private void construirErrorPorRed() {
        mensajeError = new AlertDialog.Builder(FormularioActivity.this);
        mensajeError.setTitle("Error");
        mensajeError.setMessage(MensajeEnviadoIntent.ERROR);
        mensajeError.setCancelable(false);
        mensajeError.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(FormularioActivity.this, Gestiones.class);
                intent.putExtra("tipoGestion", clonarTipoGestion());
                startActivity(intent);
                finish();
            }
        });

        mensajeError.show();
    }

    /**
     * Sincroniza la gestion al servidor, ya sea por sms, 3g o wifi
     *
     * @param jsonObject objeto json que contiene la información a enviar
     */
    private void sincronizarGestion(JSONObject jsonObject) {
        String[] conexiones = usuario.getConexionesServidor().split("\\+");
        if (conexiones.length == 1) {
            if (sePuedeSincronizarPorEsteMedio(conexiones[0])) {
                if (conexiones[0].trim().equals("SMS")) {
                    sincronizarViaSMS(jsonObject);
                } else {
                    sincronizarViaIntenet(jsonObject);
                }
            } else {
                construirErrorPorRed();
            }
        }
        if (conexiones.length == 2) {
            if (conexiones[0].trim().equals("3G") || conexiones[0].trim().equals("WIFI")) {
                if (sePuedeSincronizarPorEsteMedio(conexiones[0])) {
                    sincronizarViaIntenet(jsonObject);
                } else {
                    if (sePuedeSincronizarPorEsteMedio(conexiones[1])) {
                        sincronizarViaIntenet(jsonObject);
                    } else {
                        construirErrorPorRed();
                    }
                }
            } else {

                if (conexiones[1].trim().equals("3G") || conexiones[1].trim().equals("WIFI")) {
                    if (sePuedeSincronizarPorEsteMedio(conexiones[1])) {
                        sincronizarViaIntenet(jsonObject);
                    }
                    else {
                        if (sePuedeSincronizarPorEsteMedio(conexiones[0])) {
                            sincronizarViaSMS(jsonObject);
                        } else {
                            construirErrorPorRed();
                        }
                    }
                }
            }
        }
    }

    /**
     * Sincroniza la gestion via sms
     *
     * @param jsonObject objeto json que contiene la infromacion para enviar via sms
     */
    private void sincronizarViaSMS(JSONObject jsonObject) {
        cargaSMS = new ProgressDialog(FormularioActivity.this);
        cargaSMS.setMessage("Sincronizando via SMS...");
        cargaSMS.setCancelable(false);
        cargaSMS.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        cargaSMS.show();
        String mensaje = "INICIO" + EncodingJSON.convertirJSONParaEnviar(jsonObject.toString()) + "FIN";
        String numero = usuario.getNumero();
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);
        SincronizacionGestionSMS sincronizacionGestionSMS = new SincronizacionGestionSMS(sentPI,
                deliveredPI, numero, mensaje);
        sincronizacionGestionSMS.enviarMensaje();
    }

    /**
     * Fuerza la obtencion de las coordenadas del gps
     */
    private void forzarObtencionCoordenadas()
    {
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            latitudActual = location.getLatitude();
            longitudActual = location.getLongitude();
        }
    }
    /**
     * El formulario fue enviado
     * Sincroniza la gestion via internet, ya seaq conred 3g o wifi
     *
     * @param jsonObject objeto json que contiene la infromacion para enviar via sms
     */
    private void sincronizarViaIntenet(JSONObject jsonObject) {
        cargaSMS = new ProgressDialog(FormularioActivity.this);
        cargaSMS.setMessage("Sincronizando via Internet...");
        cargaSMS.setCancelable(false);
        cargaSMS.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        cargaSMS.show();
        SincronizacionGestionWeb sincronizacionGestionWeb = new SincronizacionGestionWeb(
                FormularioActivity.this, gestion, jsonObject, FormularioActivity.this, usuario.getId());
        sincronizacionGestionWeb.sincronizar();
    }

    /**
     * Indica si se puede sincronizar por el medio que se le manda
     *
     * @param medio el medio a evaluar
     * @return false si se puede sincronizar, false si no
     */
    private boolean sePuedeSincronizarPorEsteMedio(String medio) {
        medio = medio.trim();
        switch (medio) {
            case "SMS":
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                    return true;
                } else {
                    return false;
                }
            case "WIFI":
                return conexionAInternet.conectadoWifi(this);
            case "3G":
                return conexionAInternet.conectadoRedDatos(this);
        }
        return false;
    }


    /**
     * Guarda el formulario completo, con todos los campos que este contiene
     */
    private Formulario guardarFormularioCompleto() {
        ArrayList<Vista> vistas;
        ArrayList<ElementoCombo> elementoCombos;
        ArrayList<RadioBoton> radioBotons;
        Formulario form = formulario.clone();
        form.setEsBorrador(true);
        ArrayList<Contenedor> contenedors = form.getContenedores();
        form.setContenedores(contenedors);
        int idForm = recursosBaseDatos.guardarFormulario(form);
        form.setId(idForm);
        int idVista;
        int idLayout;
        for (Contenedor c : contenedors) {
            c.setIdFormulario(idForm);
            vistas = (ArrayList<Vista>) c.getVistas().clone();
            idLayout = recursosBaseDatos.guardarVista(c);

            for (Vista v : vistas) {
                v.setIdLayout(idLayout);
                idVista = recursosBaseDatos.guardarVista(v);
                if (v instanceof ComboCaja) {
                    elementoCombos = (ArrayList<ElementoCombo>) ((ComboCaja) v).getElementoCombos().clone();
                    for (ElementoCombo elementoCombo : elementoCombos) {
                        elementoCombo.setIdCombo(idVista);
                        recursosBaseDatos.guardarElementoCombo(elementoCombo);
                    }
                }
                if (v instanceof RadioGrupo) {
                    radioBotons = (ArrayList<RadioBoton>) ((RadioGrupo) v).getRadioBotons().clone();
                    for (RadioBoton radioBoton : radioBotons) {
                        radioBoton.setIdGroup(idVista);
                        recursosBaseDatos.guardarVista(radioBoton);
                    }
                }
            }
        }
        return form;
    }

    /**
     * Asigna las rutas de las imagenes que estan guardadas en las rutas de firmas y de imagenes
     */
    private void asignarRutasImagenesAVistas() {
        Set<Integer> keys = rutas.keySet();
        for (int id : keys) {
            for (Contenedor c : formulario.getContenedores()) {
                for (Vista v : c.getVistas()) {
                    if (v.getIdPantalla() == id) {
                        ((Imagen) v).setValor(rutas.get(id));
                    }
                }
            }
        }
        keys = rutasMulti.keySet();
        for (int id : keys) {
            for (Contenedor c : formulario.getContenedores()) {
                for (Vista v : c.getVistas()) {
                    if (v.getIdPantalla() == id) {
                        ((MultiImagen) v).setValor(rutasMulti.get(id));
                    }
                }
            }
        }
        keys = rutasFirmas.keySet();
        for (int id : keys) {
            for (Contenedor c : formulario.getContenedores()) {
                for (Vista v : c.getVistas()) {
                    if (v.getIdPantalla() == id) {
                        ((FirmaDigital) v).setValor(rutasFirmas.get(id));
                    }
                }
            }
        }
    }


    /**
     * Setea el toolbar coin el button back
     */
    private void setupToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(FormularioActivity.this, Gestiones.class);
                    intent.putExtra("tipoGestion", tipoGestion);
                    startActivity(intent);
                    finish();
                }
            });

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClickMultiImageSeleccionado(int idPantalla) {
        this.idImage = idPantalla;

        final CharSequence[] items = {"Tomar Foto", "Seleccionar desde galeria",
                "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(FormularioActivity.this);
        builder.setTitle("Agregar Foto");
        builder.setIcon(R.drawable.ic_camera);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Tomar Foto")) {
                    camaraIntenet();
                } else if (items[item].equals("Seleccionar desde galeria")) {
                    galeriaIntent();

                } else if (items[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onClickSeleccionado(int idPantalla) {
        this.idImage = idPantalla;

        final CharSequence[] items = {"Tomar Foto", "Seleccionar desde galeria",
                "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(FormularioActivity.this);
        builder.setTitle("Agregar Foto");
        builder.setIcon(R.drawable.ic_camera);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Tomar Foto")) {
                    camaraIntenet();
                } else if (items[item].equals("Seleccionar desde galeria")) {
                    galeriaIntent();

                } else if (items[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    /**
     * Genera el intent para obtener la foto desde la galaeria
     */
    private void galeriaIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccione Imagen"), GALERIA);
    }

    /**
     * Genera el intent para obtener la foto desde la camara
     */
    private void camaraIntenet() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        f = new File(imageFolder + File.separator + "image-tcglobal" + timeStamp + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(intent, CAMARA);
    }

    /**
     * Cuando un intent termina su funcion
     *
     * @param requestCode indica si el inetent se llevo a cabo
     * @param resultCode  indica exactamente de que intent se recibio una respuesta
     * @param data        contendor de la información
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALERIA) {
                enGaleriaResult(data);
            } else if (requestCode == CAMARA) {
                enCamaraResult(data);
            }
        }

    }

    /**
     * Carga las imagenes
     */
    private void cargarImagenes() {
        Set<Integer> keys = rutas.keySet();
        ImageView imageView;
        for (int id : keys) {
            imageView = (ImageView) findViewById(id);
            Bitmap bitmap = BitmapFactory.decodeFile(rutas.get(id));
            if (bitmap != null) {
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth(), ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight() / 3, false));
            }
        }
        keys = rutasMulti.keySet();
        LinearLayout layoutImagenes;
        for (int id : keys) {
            layoutImagenes=(LinearLayout) findViewById(id);
            LayoutInflater inflater = LayoutInflater.from(this);

            try {
                JSONArray arrayImagenes=new JSONArray(rutasMulti.get(id));
                for(int i=0; i<arrayImagenes.length();i++){
                    View ImageLayout= inflater.inflate(R.layout.item_image_grid, null, false);
                    ImageView image=(ImageView) ImageLayout.findViewById(R.id.image);

                    Bitmap bitmap = BitmapFactory.decodeFile(arrayImagenes.getString(i));
                    if (bitmap != null) {
                        image.setImageBitmap(Bitmap.createScaledBitmap(bitmap,200,200,false));
                    }
                    layoutImagenes.addView(ImageLayout);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        keys = rutasFirmas.keySet();
        for (int id : keys) {
            imageView = (ImageView) findViewById(id);
            Bitmap bitmap = BitmapFactory.decodeFile(rutasFirmas.get(id));
            if (bitmap != null) {
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth(), ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight() / 3, false));
            }
        }

    }

    /**
     * Controla la imagen que se obtiene desde la galeria
     *
     * @param data información del intent
     */
    private void enCamaraResult(Intent data) {
        try {
            Bitmap thumbnail = BitmapFactory.decodeFile(f.getAbsolutePath());
            if (thumbnail != null) {
                if(findViewById(idImage) instanceof ImageView){
                    ImageView imageView = (ImageView) findViewById(idImage);
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(thumbnail, imageView.getWidth(), imageView.getHeight(), false));
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                    Date date = new Date();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
                    File destination = new File(imageFolder + File.separator + "image-tcglobal" + timeStamp + ".jpg");
                    rutas.put(idImage, destination.getAbsolutePath());
                    FileOutputStream fo;

                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                    f.delete();
                }else {
                    LinearLayout layoutImagenes=(LinearLayout) findViewById(idImage);
                    LayoutInflater inflater = LayoutInflater.from(this);
                    View ImageLayout= inflater.inflate(R.layout.item_image_grid, null, false);

                    ImageView image=(ImageView) ImageLayout.findViewById(R.id.image);
                    image.setImageBitmap(Bitmap.createScaledBitmap(thumbnail, 200, 200, false));


                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                    Date date = new Date();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
                    File destination = new File(imageFolder + File.separator + "image-tcglobal" + timeStamp + ".jpg");

                    String Imagenes = rutasMulti.get(idImage);
                    JSONArray arregloImagenes = new JSONArray();
                    if(Imagenes!=null)
                        arregloImagenes= new JSONArray(Imagenes);

                    arregloImagenes.put(destination.getAbsolutePath());

                    rutasMulti.put(idImage, arregloImagenes.toString());
                    FileOutputStream fo;

                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                    f.delete();

                    layoutImagenes.addView(ImageLayout);
                }
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Controla la imagen que se obtiene desde la camara
     *
     * @param data informacion del intent
     */
    private void enGaleriaResult(Intent data) {
        if (data != null) {
            String path = getRealPathFromURI(this, data.getData());
            ImageView imageView = (ImageView) findViewById(idImage);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, imageView.getWidth(), imageView.getHeight(), false));
                rutas.put(idImage, path);
            }

        }
    }

    /**
     * Obtiene el real path desde un Uri
     *
     * @param context    el contexto de la aplicacion
     * @param contentUri la uri
     * @return La ruta del Uri
     */
    public String getRealPathFromURI(Context context, Uri contentUri) {
        if (Build.VERSION.SDK_INT >= 21) {
            // Will return "image:x*"
            String wholeID = DocumentsContract.getDocumentId(contentUri);

// Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};

// where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);

            String filePath = "";

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }

            cursor.close();
            return filePath;
        } else {
            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("rutasImagenes", rutas);
        savedInstanceState.putSerializable("rutasImagenesMulti", rutasMulti);
        savedInstanceState.putSerializable("rutasFirmas", rutasFirmas);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void enFirmaSeleccionado(int idFirma) {
        this.idFirma = idFirma;
        // Dialog Function
        dialog = new Dialog(FormularioActivity.this);
        // Removing the features of Normal Dialogs
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_signature);
        dialog.setCancelable(true);
        dialog_action();
    }

    /**
     * Actualiza la imagen de la firma
     *
     * @param pathImage la ruta de la firma
     * @param idFirma   el identificador del image view
     */
    private void actualizarFirma(String pathImage, int idFirma) {
        ImageView imageView = (ImageView) findViewById(idFirma);
        Bitmap bitmap = BitmapFactory.decodeFile(pathImage);
        if (bitmap != null) {
            imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, imageView.getWidth(), imageView.getHeight(), false));
            if (rutasFirmas.get(idFirma) != null) {
                File file = new File(rutasFirmas.get(idFirma));
                file.delete();
            }
            rutasFirmas.put(idFirma, pathImage);
        }
    }

    public JSONObject obtenerCampos(Formulario formulario, Gestion gestion) throws ValorRequeridoException {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1;
        try {
            jsonObject.put("gestion", gestion.getIdgestion());
            jsonObject.put("usuario", usuario.getId());
            jsonObject.put("latitud", gestion.getLatitud());
            jsonObject.put("longitud", gestion.getLongitud());
            jsonObject.put("fecha", gestion.getFecha());
            jsonObject1 = new JSONObject();
            ArrayList<Vista> vistas = new ArrayList<>();
            Object object;
            for (Contenedor c : formulario.getContenedores()) {
                vistas = c.getVistas();
                for (Vista v : vistas) {
                    v.actualizarValores();
                    try {
                        object = v.getValor();
                        if (object != null) {
                            jsonObject1.put(v.getNombreVariable(), object);
                        }
                    } catch (NoSoportaValorException e) {
                        continue;
                    }
                }
            }
            jsonObject.put("campos", jsonObject1);
        } catch (JSONException ex) {

        }
        return jsonObject;
    }

    /**
     * Funcion que inicializa el dialogo de la firma
     */
    public void dialog_action() {

        mContent = (LinearLayout) dialog.findViewById(R.id.linearLayout);
        mSignature = new signature(getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mClear = (Button) dialog.findViewById(R.id.clear);
        mGetSign = (Button) dialog.findViewById(R.id.getsign);
        mGetSign.setEnabled(false);
        mCancel = (Button) dialog.findViewById(R.id.cancel);
        view = mContent;

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });
        mGetSign.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                view.setDrawingCacheEnabled(true);
                Date date = new Date();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
                String StoredPath = imageFolder + File.separator + "firma-tcglobal" + timeStamp + ".jpg";
                mSignature.save(view, StoredPath);
                dialog.dismiss();
                actualizarFirma(StoredPath, idFirma);
                //recreate();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                // Calling the same class
                recreate();
            }
        });
        dialog.show();
    }

    /**
     * En caso de que el mensaje si se haya podido enviar
     */
    @Override
    public void enMensajeEnviado() {
        cargaSMS.dismiss();
        mensajeInformacion = new AlertDialog.Builder(this);
        mensajeInformacion.setTitle("Información");
        mensajeInformacion.setMessage("La gestión fue enviada");
        mensajeInformacion.setCancelable(false);
        recursosBaseDatos.actualizarEstadoGestion(gestion.getIdgestion(), Gestion.SINENVIARIMAGENES);
        unregisterReceiver(mensajeEnviadoIntent);
        unregisterReceiver(mensajeEntregadoIntent);
        mensajeInformacion.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(FormularioActivity.this, Gestiones.class);
                intent.putExtra("tipoGestion", clonarTipoGestion());
                startActivity(intent);
                finish();
            }
        });
        mensajeInformacion.show();
    }

    /**
     * En caso de que el mensaje no se haya podido enviar
     *
     * @param mensaje el mensaje de la razon
     */
    @Override
    public void enMensajeNoEnviado(String mensaje) {
        cargaSMS.dismiss();
        mensajeInformacion = new AlertDialog.Builder(this);
        mensajeInformacion.setTitle("Error");
        mensajeInformacion.setMessage(mensaje);
        mensajeInformacion.setCancelable(false);
        recursosBaseDatos.actualizarEstadoGestion(gestion.getId(), Gestion.SINENVIAR);
        unregisterReceiver(mensajeEnviadoIntent);
        unregisterReceiver(mensajeEntregadoIntent);
        mensajeInformacion.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(FormularioActivity.this, Gestiones.class);
                intent.putExtra("tipoGestion", clonarTipoGestion());
                startActivity(intent);
                finish();
            }
        });
        mensajeInformacion.show();
    }

    /**
     * Clona el tipo de gestion al que pertenece la gestion actual, esto se hace con el fin de
     * volver a iniciar la activity de gestiones, pero sin contar la gestion que ya se lleno
     *
     * @return el tipo de gestion
     */
    private TipoGestion clonarTipoGestion() {
        TipoGestion tipoGestion = this.tipoGestion.clone();
        int i = 0;
        ArrayList<Gestion> ges1 = (ArrayList<Gestion>) tipoGestion.getGestiones().clone();
        Gestion g = ges1.get(i);
        while (g.getIdgestion() != gestion.getIdgestion()) {
            i++;
            g = ges1.get(i);
        }
        tipoGestion.getGestiones().remove(i);
        return tipoGestion;
    }

    /**
     * En caso de que la sincronización haya sido completada
     */
    @Override
    public void enSincronizacionCompletada(String mensaje, int idGestion) {
        cargaSMS.dismiss();
        mensajeInformacion = new AlertDialog.Builder(this);
        mensajeInformacion.setTitle("Información");
        mensajeInformacion.setMessage(mensaje);
        mensajeInformacion.setCancelable(false);
        recursosBaseDatos.actualizarEstadoGestion(idGestion, Gestion.ENVIADO);
        mensajeInformacion.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(FormularioActivity.this, Gestiones.class);
                intent.putExtra("tipoGestion", clonarTipoGestion());
                startActivity(intent);
                finish();
            }
        });
        mensajeInformacion.show();
    }

    /**
     * En caso de que la sincronización fuera sido fallida
     */
    @Override
    public void enSincronizacionFallida(String mensaje, int idGestion) {
        cargaSMS.dismiss();
        mensajeInformacion = new AlertDialog.Builder(this);
        mensajeInformacion.setTitle("Información");
        mensajeInformacion.setMessage(mensaje);
        mensajeInformacion.setCancelable(false);
        recursosBaseDatos.actualizarEstadoGestion(idGestion, Gestion.SINENVIAR);
        mensajeInformacion.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(FormularioActivity.this, Gestiones.class);
                intent.putExtra("tipoGestion", clonarTipoGestion());
                startActivity(intent);
                finish();
            }
        });
        mensajeInformacion.show();

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitudActual = location.getLatitude();
            longitudActual = location.getLongitude();
        }
    }

    @Override
    public void onStatusChanged(String provider, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            latitudActual = location.getLatitude();
            longitudActual = location.getLongitude();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        //TODO
    }

    /**
     * Determina si el gps esta activado o no
     *
     * @return true si esta activado, false si no
     */
    private boolean tieneGpsActivado() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Construye un error estandar para informar que no se encuentra el GPS activado
     */
    private void construirErrorGps() {
        /**
        mensajeError = new AlertDialog.Builder(this);
        mensajeError.setTitle("Error");
        mensajeError.setMessage("El GPS no se encuentra activado, por favor activarlo, para continuar");
        mensajeError.setCancelable(false);
        mensajeError.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        mensajeError.show();*/
        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        Toast.makeText(this,"Por favor habilite el GPS",Toast.LENGTH_LONG).show();

    }



    /**
     * Clase que maneja el dibujado de la firma
     */
    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private final RectF dirtyRect = new RectF();
        private Paint paint = new Paint();
        private Path path = new Path();
        private float lastTouchX;
        private float lastTouchY;

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v, String StoredPath) {
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);
                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();
            } catch (Exception e) {
            }
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;
                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}
