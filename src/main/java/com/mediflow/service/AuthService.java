package com.mediflow.service;

import com.mediflow.model.Utilisateur;
import com.mediflow.repository.UserDAO;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Locale;
import java.util.Optional;

public class AuthService {
    private final UserDAO userDAO;
    private final String dummyHash = BCrypt.hashpw("invalid-account", BCrypt.gensalt(10));

    public AuthService(UserDAO userDAO) { this.userDAO = userDAO; }

    public Optional<Utilisateur> authentifier(String email, String motDePasse) {
        if (email == null || motDePasse == null || email.length() > 254 || motDePasse.length() > 72) {
            return Optional.empty();
        }
        Optional<Utilisateur> utilisateur = userDAO.findByEmail(email.trim().toLowerCase(Locale.ROOT));
        boolean valide = BCrypt.checkpw(motDePasse, utilisateur.map(Utilisateur::getMotDePasseHash).orElse(dummyHash));
        return valide ? utilisateur : Optional.empty();
    }
}
