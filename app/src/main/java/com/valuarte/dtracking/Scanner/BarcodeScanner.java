package com.valuarte.dtracking.Scanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;


import com.valuarte.dtracking.ElementosGraficos.Gestion;
import com.valuarte.dtracking.ElementosGraficos.TipoGestion;
import com.valuarte.dtracking.FormularioActivity;
import com.valuarte.dtracking.R;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.util.ArrayList;


/**
 * Representa el lector de codigo de barras
 */
public class BarcodeScanner extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    private Button scanButton;
    private ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    private TipoGestion tipoGestion;

    static {
        System.loadLibrary("iconv");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            tipoGestion = (TipoGestion) getIntent().getSerializableExtra("tipoGestion");
            setContentView(R.layout.activity_barcode_scanner);
            setToolbar((Toolbar) findViewById(R.id.toolbar));
            initControls();
        }
        catch (Exception e){ }
    }

    /**
     * Incia los controles para la gestion del escaner
     */
    private void initControls() {
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            autoFocusHandler = new Handler();
            mCamera = getCameraInstance();

            // Instance barcode scanner
            scanner = new ImageScanner();
            scanner.setConfig(0, Config.X_DENSITY, 3);
            scanner.setConfig(0, Config.Y_DENSITY, 3);

            mPreview = new CameraPreview(BarcodeScanner.this, mCamera, previewCb,
                    autoFocusCB);
            FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
            preview.addView(mPreview);
        }
        catch (Exception e){}

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            releaseCamera();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * Obtiene una instancia de la camara
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
        }
        return c;
    }

    /**
     * Resetea la camara
     */
    private void resetearCamara() {
        try {
            barcodeScanned = false;
            mCamera.setPreviewCallback(previewCb);
            mCamera.startPreview();
            previewing = true;
            mCamera.autoFocus(autoFocusCB);
        }
        catch (Exception e){}
    }

    /**
     * deja la conexion con la camara
     */
    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * Hilo para autofocar el escaner
     */
    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };
    /**
     * Callback de la camara
     */
    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            try {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();

                    for (Symbol sym : syms) {
                        String scanResult = sym.getData().trim();
                        Log.e("resulkt", scanResult);
                        Gestion g = obtenerGestion(scanResult);
                        if (g != null) {
                            Intent intent = new Intent(BarcodeScanner.this, FormularioActivity.class);
                            intent.putExtra("gestion", g);
                            intent.putExtra("tipoGestion", tipoGestion);
                            barcodeScanned = true;
                            startActivity(intent);
                            releaseCamera();
                            finish();
                        } else {
                            crearError();
                        }

                        break;
                    }
                }
            }
            catch (Exception ex)
            {

            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    /**
     * Dialogo que muestra el codigo leido
     *
     */
    private void crearError() {
        AlertDialog.Builder error=new AlertDialog.Builder(this);
        error.setTitle("Error");
        error.setCancelable(false);
        error.setMessage("No se encontro la gestión");
        error.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resetearCamara();
            }
        });
        error.show();
    }

    /**
     * Setea el toolbar
     *
     * @param toolbar
     */
    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releaseCamera();
                onBackPressed();
                finish();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Obtiene la gestion a partir del codigo de barras
     *
     * @param codigoBarras el codigo de barras que le pertenece a la gestion
     * @return una gestion asociada al codigo de barras, o null si no la encuentra
     */
    private Gestion obtenerGestion(String codigoBarras) {
        if (tipoGestion != null) {
            ArrayList<Gestion> gestions = tipoGestion.getGestiones();
            for (Gestion g : gestions) {
                if (g.getCodigoBarras().trim().equals(codigoBarras)) {
                    return g;
                }
            }
        }
        return null;
    }
}