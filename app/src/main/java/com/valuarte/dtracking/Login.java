package com.valuarte.dtracking;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.Util.ConexionAInternet;
import com.valuarte.dtracking.Util.SincronizadorBackground;
import com.valuarte.dtracking.Util.SincronizadorGestionesEliminadas;
import com.valuarte.dtracking.Util.SincronizadorPosicionActual;
import com.valuarte.dtracking.Util.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Representa el activity para el login en la aplicacion
 * @version 1.0
 */
public class Login extends AppCompatActivity {
    /**
     * Boton que se presiona para entrar a la plicacion
     */
    private Button entrar;
    /**
     * boton de cancelar login
     */
    private Button cancelar;
    /**
     * Dialogo de error con el login
     */
    private AlertDialog.Builder errorLogin;
    /**
     * Dialogo de progreso para el inicio de sesión
     */
    private ProgressDialog cargaLogin;
    /**
     * Nombre de usuario
     */
    private EditText username;
    /**
     * Contraseña del usuario
     */
    private EditText password;
    /**
     * Cola de peticiones al servidor
     */
    private RequestQueue requestQueue;
    /**
     * Acceso a la base de datos
     */
    private RecursosBaseDatos recursosBaseDatos;
    /**
     * Sirve para verificar la conexion a intenernet
     */
    private ConexionAInternet conexionAInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login2);
            verificarPermisos(this);
            conexionAInternet = new ConexionAInternet();
            recursosBaseDatos = new RecursosBaseDatos(this);
            if (estaLogueado()) {
                Intent i = new Intent(Login.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
            cancelar = (Button) findViewById(R.id.cancelar);
            entrar = (Button) findViewById(R.id.loguear);
            username = (EditText) findViewById(R.id.username);
            password = (EditText) findViewById(R.id.password);
            requestQueue = Volley.newRequestQueue(this);
            asignarEventosABotones();
        }
        catch (Exception e){}
    }

    /**
     * Actualiza los permisos para escribir en el almacenamiento externo del movil
     *
     */
    public void verificarPermisos(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> permisosArrayList = new ArrayList<>();
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permisosArrayList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permisosArrayList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                permisosArrayList.add(Manifest.permission.CAMERA);
            }
            if (checkSelfPermission(Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                permisosArrayList.add(Manifest.permission.INTERNET);
            }
            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                permisosArrayList.add(Manifest.permission.SEND_SMS);
            }
            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                permisosArrayList.add(Manifest.permission.RECEIVE_SMS);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                permisosArrayList.add(Manifest.permission.ACCESS_NETWORK_STATE);
            }
            if(checkSelfPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED)
                    != PackageManager.PERMISSION_GRANTED)
            {
                permisosArrayList.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            {
                permisosArrayList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            {
                permisosArrayList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.VIBRATE)!=PackageManager.PERMISSION_GRANTED)
            {
                permisosArrayList.add(Manifest.permission.VIBRATE);
            }
            if (permisosArrayList.size() > 0) {
                String[] permisos = new String[permisosArrayList.size()];
                for (int i = 0; i < permisosArrayList.size(); i++) {
                    permisos[i] = permisosArrayList.get(i);
                }
                ActivityCompat.requestPermissions(this, permisos, 1);
            }
        }
    }

    /**
     * Asigna los eventos a los botones de entrar y cancelar
     */
    private void asignarEventosABotones() {
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorLogin = new AlertDialog.Builder(Login.this);
                errorLogin.setTitle("Confirmación");
                errorLogin.setMessage("Realmente desea salir de la aplicacion");
                errorLogin.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                errorLogin.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                errorLogin.show();
            }
        });
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarYSolicitarLogin();
            }
        });
        password.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            validarYSolicitarLogin();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Valida y solicita el login al servidor
     */
    private void validarYSolicitarLogin() {
        String user = username.getText().toString();
        String pass = password.getText().toString();
        if (conexionAInternet.estaConectado(this)) {
            if (validarCampos(user, pass)) {
                cargaLogin = new ProgressDialog(Login.this);
                cargaLogin.setMessage("Solicitando Login...");
                cargaLogin.setCancelable(true);
                cargaLogin.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                cargaLogin.show();
                solicitarLogin(user, pass);
            } else {
                construirError("Hay campos en blanco");
            }
        } else {
            Usuario usuario = recursosBaseDatos.getUsuario();
            if (usuario != null) {
                if (usuario.getEstado() == usuario.LOGUEADO || usuario.getEstado() == usuario.DESLOGUEADO) {
                    if (validarCampos(user, pass)) {
                        if (usuario.getNommbreUsuario().equals(user) && usuario.getContrasenia().equals(pass)) {
                            recursosBaseDatos.actualizarEstadoUsuario(usuario.LOGUEADO);
                            construirBackground(usuario);
                            Intent i = new Intent(Login.this, MainActivity.class);
                            i.putExtra("inicioSesion", 1);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else {
                            construirError("Datos incorrectos");
                        }
                    } else {
                        construirError("Hay campos en blanco");
                    }
                }
            } else {
                construirError("No tiene conexion a internet");
            }
        }
    }

    /**
     * Valida que los campos esten llenos
     *
     * @param username el nombre de usuario
     * @param password password del usuario
     * @return true si esta completos, false si no
     */
    private boolean validarCampos(String username, String password) {
        return (!username.trim().equals("") && !password.trim().equals(""));
    }

    /**
     * Indica si hay un usuario logueado o no
     *
     * @return true si esta logueado, false si no
     */
    private boolean estaLogueado() {
        Usuario usuario = recursosBaseDatos.getUsuario();
        if (usuario != null) {
            return usuario.getEstado() == Usuario.LOGUEADO;
        } else {
            return false;
        }
    }

    /**
     * Solicita login al servidor
     *
     * @param userName nombre de usuario
     * @param password contraseña
     */
    private void solicitarLogin(final String userName, final String password) {
        String url = "http://192.168.0.38:8000/dtracking/movil/login/";
        /**
         * solicitud post al servidor
         */
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    /**
                     * obtiene la respuesta que envia el servior
                     * @param response
                     */
                    @Override
                    public void onResponse(String response) {
                        // response
                        cargaLogin.dismiss();
                        try {
                            //verificamos que si se haya podido loguear
                            if (!response.trim().equals("")) {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                JSONObject jsonObject1;
                                if (jsonObject.isNull("error")) {
                                    int intervalo=Usuario.INTERVALOSINCRONIZACIONGPS;
                                    recursosBaseDatos.eliminarUsuariosRegistrados();
                                    jsonObject1 = jsonObject.getJSONObject("perfil");
                                    if(!jsonObject1.isNull("intervalo"))
                                    {

                                        intervalo=jsonObject1.getInt("intervalo")*1000;
                                        if(intervalo<60000)
                                        {
                                            intervalo=60000;
                                        }
                                    }

                                    String numero="";
                                    if(!jsonObject1.isNull("sms_gateway"))
                                    {
                                        numero=jsonObject1.getString("sms_gateway");
                                    }
                                    Usuario usuario = new Usuario(jsonObject.getInt("id"), jsonObject.getString("username"), jsonObject.getString("name")
                                            , jsonObject1.getString("foto"), numero, password,
                                            jsonObject1.getString("server_conection"), intervalo,Usuario.LOGUEADO);
                                    recursosBaseDatos.guardarUsuario(usuario);
                                    construirBackground(usuario);
                                    Intent i = new Intent(Login.this, MainActivity.class);
                                    i.putExtra("inicioSesion", 2);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                } else {
                                    construirError(jsonObject.getString("error"));
                                }


                            } else {
                                construirError("Datos incorrectos");
                            }

                        } catch (JSONException e) {
                            Log.e("Error", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cargaLogin.dismiss();
                        errorLogin = new AlertDialog.Builder(Login.this);
                        errorLogin.setTitle("Error");
                        errorLogin.setMessage("Error con el servidor");
                        errorLogin.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        errorLogin.show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", userName);
                params.put("password", password);
                return params;
            }
        };
        requestQueue.add(postRequest);
    }

    /**
     * Construye las alarmas para la sincronizacion en background
     */
    private void construirBackground(Usuario usuario)
    {
        if(Build.VERSION.SDK_INT<19){
        Intent alarmIntent = new Intent(this, SincronizadorBackground.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        alarmIntent = new Intent(this, SincronizadorBackground.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis(), MainActivity.INTERVALOTIEMPOSINCRONIZACION, pendingIntent);
        alarmIntent = new Intent(this, SincronizadorPosicionActual.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        alarmIntent = new Intent(this, SincronizadorPosicionActual.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), usuario.getIntervaloSincroinizacionGPS(), pendingIntent);
        alarmIntent = new Intent(this, SincronizadorGestionesEliminadas.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        alarmIntent = new Intent(this, SincronizadorGestionesEliminadas.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,  SystemClock.elapsedRealtime(), MainActivity.INTERVALOGESTIONESELIMINADAS, pendingIntent);
        }
        else
        {
            Intent alarmIntent = new Intent(this, SincronizadorBackground.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pendingIntent);
            alarmIntent = new Intent(this, SincronizadorBackground.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
            manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
            alarmIntent = new Intent(this, SincronizadorPosicionActual.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
            manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pendingIntent);
            alarmIntent = new Intent(this, SincronizadorPosicionActual.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
            manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
            alarmIntent = new Intent(this, SincronizadorGestionesEliminadas.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
            manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pendingIntent);
            alarmIntent = new Intent(this, SincronizadorGestionesEliminadas.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
            manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        }
    }
    /**
     * Construye un error con el mesnaje que recibe por parametro
     *
     * @param mensaje mensaje a mostrar
     */
    private void construirError(String mensaje) {
        errorLogin = new AlertDialog.Builder(Login.this);
        errorLogin.setTitle("Error");
        errorLogin.setMessage(mensaje);
        errorLogin.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        errorLogin.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
