package com.mediflow.service;

import java.util.Map;

public class ValidationException extends RuntimeException {
    private final Map<String, String> erreurs;
    public ValidationException(Map<String, String> erreurs) {
        super("Les données saisies sont invalides");
        this.erreurs = Map.copyOf(erreurs);
    }
    public Map<String, String> getErreurs() { return erreurs; }
}
