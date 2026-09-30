package com.mediflow.repository.jdbc;

import com.mediflow.model.Patient;
import com.mediflow.repository.DataAccessException;
import com.mediflow.repository.PatientDAO;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcPatientDAO implements PatientDAO {
    private final DataSource dataSource;
    private static final String SELECT = """
            SELECT p.*, c.id AS consultation_id, c.patient_id, c.medecin_id, c.motif,
                   c.observations, c.diagnostic, c.traitement, c.cout, c.statut, c.date_consultation
            FROM patients p LEFT JOIN consultations c ON c.patient_id = p.id
            """;

    public JdbcPatientDAO(DataSource dataSource) { this.dataSource = dataSource; }

    @Override
    public Patient save(Patient patient) {
        String sql = """
                INSERT INTO patients (nom, prenom, date_naissance, numero_securite_sociale,
                    tension_arterielle, frequence_cardiaque, temperature, frequence_respiratoire, date_arrivee)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        // Un INSERT est atomique ; chaque connexion est rendue au pool par try-with-resources.
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, patient.getNom());
            statement.setString(2, patient.getPrenom());
            statement.setDate(3, Date.valueOf(patient.getDateNaissance()));
            statement.setString(4, patient.getNumeroSecuriteSociale());
            statement.setString(5, patient.getTensionArterielle());
            statement.setInt(6, patient.getFrequenceCardiaque());
            statement.setDouble(7, patient.getTemperature());
            statement.setInt(8, patient.getFrequenceRespiratoire());
            statement.setTimestamp(9, Timestamp.valueOf(patient.getDateArrivee()));
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Identifiant du patient absent");
                patient.setId(keys.getLong(1));
            }
            return patient;
        } catch (SQLException e) {
            throw new DataAccessException("Impossible d'enregistrer le patient", e);
        }
    }

    @Override
    public List<Patient> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT);
             ResultSet rs = statement.executeQuery()) {
            List<Patient> patients = new ArrayList<>();
            while (rs.next()) patients.add(map(rs));
            return patients;
        } catch (SQLException e) {
            throw new DataAccessException("Impossible de lister les patients", e);
        }
    }

    @Override
    public Optional<Patient> findById(long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT + " WHERE p.id = ?")) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Impossible de lire le patient", e);
        }
    }

    private Patient map(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setId(rs.getLong("id"));
        patient.setNom(rs.getString("nom"));
        patient.setPrenom(rs.getString("prenom"));
        patient.setDateNaissance(rs.getDate("date_naissance").toLocalDate());
        patient.setNumeroSecuriteSociale(rs.getString("numero_securite_sociale"));
        patient.setTensionArterielle(rs.getString("tension_arterielle"));
        patient.setFrequenceCardiaque(rs.getInt("frequence_cardiaque"));
        patient.setTemperature(rs.getDouble("temperature"));
        patient.setFrequenceRespiratoire(rs.getInt("frequence_respiratoire"));
        patient.setDateArrivee(rs.getTimestamp("date_arrivee").toLocalDateTime());
        if (rs.getObject("consultation_id") != null) patient.setConsultation(JdbcConsultationDAO.map(rs));
        return patient;
    }
}
