package com.mediflow.repository.jdbc;

import com.mediflow.model.Consultation;
import com.mediflow.repository.ConsultationDAO;
import com.mediflow.repository.DataAccessException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class JdbcConsultationDAO implements ConsultationDAO {
    private final DataSource dataSource;
    public JdbcConsultationDAO(DataSource dataSource) { this.dataSource = dataSource; }

    static Consultation map(ResultSet rs) throws SQLException {
        return new Consultation(rs.getLong("consultation_id"), rs.getLong("patient_id"),
                rs.getLong("medecin_id"), rs.getString("motif"), rs.getString("observations"),
                rs.getString("diagnostic"), rs.getString("traitement"), rs.getBigDecimal("cout"),
                rs.getString("statut"), rs.getTimestamp("date_consultation").toLocalDateTime());
    }

    @Override
    public Optional<Consultation> findByPatientId(long patientId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id AS consultation_id, c.* FROM consultations c WHERE patient_id = ?")) {
            statement.setLong(1, patientId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Impossible de lire la consultation", e);
        }
    }
}
