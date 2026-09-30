package com.mediflow.repository.jdbc;

import com.mediflow.model.Role;
import com.mediflow.model.Utilisateur;
import com.mediflow.repository.DataAccessException;
import com.mediflow.repository.UserDAO;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class JdbcUserDAO implements UserDAO {
    private final DataSource dataSource;

    public JdbcUserDAO(DataSource dataSource) { this.dataSource = dataSource; }

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        String sql = "SELECT id, nom, email, mot_de_passe_hash, role FROM utilisateurs WHERE email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new Utilisateur(rs.getLong("id"), rs.getString("nom"),
                        rs.getString("email"), rs.getString("mot_de_passe_hash"), Role.valueOf(rs.getString("role"))));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Impossible de rechercher l'utilisateur", e);
        }
    }
}
