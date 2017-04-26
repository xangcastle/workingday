package com.valuarte.dtracking.Excepciones;

/**
 * Moldea el hecho de que una Vista no genere un valor
 * @version 1.0
 */
public class NoSoportaValorException extends Exception {
    public NoSoportaValorException(String message)
    {
        super(message);
    }
}
