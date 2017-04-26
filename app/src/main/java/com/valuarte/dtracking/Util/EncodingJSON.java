package com.valuarte.dtracking.Util;

/**
 * Reemplaza caracteres especiales por constantes
 * @version 1.0
 */
public class EncodingJSON {
    /**
     * Cadena que remmplaza las llaves que abren
     */
    public static final String LLAVESRA="lll";
    /**
     * llave que abre
     */
    public static final String LLAVESNA="{";
    /**
     * cadena que reemplaza las llaves que cierran
     */
    public static final String LLAVESRC="mmm";
    /**
     * LLave que cierra
     */
    public static final String LLAVESNC="}";
    /**
     * Cadena que reemplaza los corchetes que abren
     */
    public static final String CORCHETERA="rrr";
    /**
     * corchete que abre
     */
    public static final String CORCHETENA="[";
    /**
     * cadena qu reemplaza los cochetes que cierran
     */
    public static final String CORCHETERC="sss";
    /**
     * corchete que cierra
     */
    public static final String CORCHETENC="]";

    /**
     * Convierte una cadena json, a una cadena que se va a enviar por SMS
     * @param mensaje el mensaje a convertir
     * @return cadena convertida
     */
    public static String convertirJSONParaEnviar(String mensaje)
    {
        String cadena="";
        if(mensaje!=null)
        {
            cadena=mensaje;
            cadena=cadena.replace(LLAVESNA,LLAVESRA);
            cadena=cadena.replace(LLAVESNC,LLAVESRC);
            cadena=cadena.replace(CORCHETENA,CORCHETERA);
            cadena=cadena.replace(CORCHETENC,CORCHETERC);
        }
        return cadena;
    }

    /**
     * Convierte una cadena que se recibio via SMS, a una cadena json
     * @param mensaje el mesnaje a convertir
     * @return la cadena convertida
     */
    public static String convertirJSONRecibido(String mensaje)
    {
        String cadena="";
        if(mensaje!=null)
        {
            cadena=mensaje;
            cadena=cadena.replace(LLAVESRA,LLAVESNA);
            cadena=cadena.replace(LLAVESRC,LLAVESNC);
            cadena=cadena.replace(CORCHETERA,CORCHETENA);
            cadena=cadena.replace(CORCHETERC,CORCHETENC);
        }
        return cadena;
    }

}
