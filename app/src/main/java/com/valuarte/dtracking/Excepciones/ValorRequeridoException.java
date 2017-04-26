package com.valuarte.dtracking.Excepciones;

/**
 * Excepcion que se lanza cuando un campo no contiene un valor
 * @version 1.0
 */
public class ValorRequeridoException extends Exception{
    /**
     * El nombre de la variable a la que se asocia el valor
     */
    private String nombreVariable;
    /**
     * Valor del campo
     */
    private String titulo;
    /**
     * Posicion en x del campo
     */
    private float x;
    /**
     * Posicion en y del campo
     */
    private float y;
    public ValorRequeridoException(String detailMessage, String nombreVariable, String titulo,float x,float y) {
        super(detailMessage);
        this.nombreVariable = nombreVariable;
        this.titulo = titulo;
        this.x=x;
        this.y=y;
    }

    public String getMessage()
    {
        return super.getMessage();
    }


    /**
     * El nombre de la variable a la que se asocia el valor
     */
    public String getNombreVariable() {
        return nombreVariable;
    }

    /**
     * Valor del campo
     */
    public String getTitulo() {
        return titulo;
    }
}
