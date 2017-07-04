package com.valuarte.dtracking.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.support.annotation.Nullable;
import android.util.Log;

import com.valuarte.dtracking.BaseDatos.RecursosBaseDatos;
import com.valuarte.dtracking.ElementosGraficos.Contenedor;
import com.valuarte.dtracking.ElementosGraficos.Formulario;
import com.valuarte.dtracking.ElementosGraficos.Gestion;
import com.valuarte.dtracking.ElementosGraficos.Vista;
import com.valuarte.dtracking.Excepciones.NoSoportaValorException;
import com.valuarte.dtracking.Excepciones.ValorRequeridoException;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Jose Williams Garcia on 2/5/2017.
 */

public  class Utilidades {
    private static final float maxHeight = 580.0f;
    private static final float maxWidth = 580.0f;
    //public static final String URL_HOST = "http://192.168.0.38:8000";
    public static final String URL_HOST = "http://www.valuarte.com.ni";
    private static final String TAG = "Utilidades";


    public static boolean savecompressImage(String imagePath){
        try {
            byte[] imagencompress= compressImage(imagePath);
            FileOutputStream out = new FileOutputStream(imagePath);
            out.write(imagencompress);
            out.close();
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

        return true;
    }
    public static byte[] compressImage(String imagePath) {
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(imagePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
        return out.toByteArray();
    }
    public static String cargarImagen_Gestion(int idGestion, String variable, String rutaImagen,
                                              Context context, @Nullable Integer index){
        RestClient restClient=new RestClient_(context);
        MyRestErrorHandler myRestErrorHandler=MyRestErrorHandler_.getInstance_(context);
        restClient.setRestErrorHandler(myRestErrorHandler);
        String respuesta =null;
        try {
            File fimage = new File(rutaImagen);
            if (fimage.exists()){
                if(index!=null){
                    copyFile(fimage.getAbsolutePath(),fimage.getParent() + "/imagen" + String.valueOf(index) + ".jpg");
                    rutaImagen= fimage.getParent() + "/imagen" + String.valueOf(index) + ".jpg";
                }else {
                    copyFile(fimage.getAbsolutePath(),fimage.getParent() + "/imagen.jpg");
                    rutaImagen= fimage.getParent() + "/imagen.jpg";
                }
                FileSystemResource image = new FileSystemResource(rutaImagen);

                MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
                data.set("gestion", String.valueOf(idGestion));
                data.set("variable", variable);
                data.set("imagen", image);

                restClient.setHeader("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);
                respuesta = restClient.cargar_media(data);
            }
        }catch (Exception ex){

        }
        return  respuesta;
    }

    public static void copyFile(String orignePaht, String destinationPath){
        File sourceLocation = new File (orignePaht);
        File targetLocation = new File (destinationPath);
        if(sourceLocation.exists()){
            try {
                InputStream in = new FileInputStream(sourceLocation);
                OutputStream out = new FileOutputStream(targetLocation);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Log.v(TAG, "Copy file successful.");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Log.v(TAG, "Copy file failed. Source file missing.");
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static JSONObject obtenerCampos(Formulario formulario, Gestion gestion, Context context) throws ValorRequeridoException {
        RecursosBaseDatos recursosBaseDatos=new RecursosBaseDatos(context);
        Usuario usuario = recursosBaseDatos.getUsuario();
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
            ex.printStackTrace();
        }
        return jsonObject;
    }
}
