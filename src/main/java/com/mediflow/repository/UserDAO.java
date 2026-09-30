package com.mediflow.repository;

import com.mediflow.model.Utilisateur;
import java.util.Optional;

public interface UserDAO {
    Optional<Utilisateur> findByEmail(String email);
}
