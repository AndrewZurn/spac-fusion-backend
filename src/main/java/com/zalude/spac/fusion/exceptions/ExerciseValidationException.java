package com.zalude.spac.fusion.exceptions;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
public class ExerciseValidationException extends Exception {
    public ExerciseValidationException() {
    }

    public ExerciseValidationException(String message) {
        super(message);
    }

    public ExerciseValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExerciseValidationException(Throwable cause) {
        super(cause);
    }

    public ExerciseValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
