package com.mediflow;

import com.mediflow.model.*;
import com.mediflow.repository.jdbc.*;
import com.mediflow.service.*;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.*;
import java.nio.file.*;
import java.sql.*;
import java.time.*;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class PatientWorkflowTest {
    private JdbcDataSource dataSource;
    private JdbcPatientDAO dao;
    private PatientService service;
    private final Clock clock = Clock.fixed(Instant.parse("2026-09-30T09:00:00Z"), ZoneId.of("Africa/Casablanca"));

    @BeforeEach void setup() throws Exception {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        try (Connection connection = dataSource.getConnection(); var reader = Files.newBufferedReader(Path.of("database/schema.sql"))) {
            RunScript.execute(connection, reader);
        }
        dao = new JdbcPatientDAO(dataSource);
        service = new PatientService(dao, clock);
    }

    private Patient patient(String nom) {
        Patient p = new Patient();
        p.setNom(nom); p.setPrenom("Sara"); p.setDateNaissance(LocalDate.of(1995, 2, 10));
        p.setNumeroSecuriteSociale("00123456"); p.setTensionArterielle("120/80");
        p.setTemperature(36.8); p.setFrequenceCardiaque(75); p.setFrequenceRespiratoire(16);
        return p;
    }

    @Test void enregistrementConserveLesChampsEtImposeLaDateServeur() {
        Patient p = patient("  O'Neil  ");
        p.setDateArrivee(LocalDateTime.of(2000, 1, 1, 0, 0));
        service.enregistrer(p);
        Patient stored = dao.findById(p.getId()).orElseThrow();
        assertAll(() -> assertEquals("O'Neil", stored.getNom()),
                () -> assertEquals("Sara", stored.getPrenom()),
                () -> assertEquals("00123456", stored.getNumeroSecuriteSociale()),
                () -> assertEquals(LocalDate.of(1995, 2, 10), stored.getDateNaissance()),
                () -> assertEquals("120/80", stored.getTensionArterielle()),
                () -> assertEquals(36.8, stored.getTemperature()),
                () -> assertEquals(75, stored.getFrequenceCardiaque()),
                () -> assertEquals(16, stored.getFrequenceRespiratoire()),
                () -> assertEquals(LocalDateTime.now(clock), stored.getDateArrivee()),
                () -> assertNull(stored.getConsultation()),
                () -> assertTrue(dao.findById(-1).isEmpty()));
    }

    @Test void rejetteLesChampsInvalidesSansEcrire() {
        Patient p = patient(" ");
        p.setPrenom("x".repeat(101)); p.setNumeroSecuriteSociale(null);
        p.setDateNaissance(LocalDate.now(clock).plusDays(1)); p.setTensionArterielle("80/120");
        p.setTemperature(Double.NaN); p.setFrequenceCardiaque(0); p.setFrequenceRespiratoire(100);
        assertEquals(8, assertThrows(ValidationException.class, () -> service.enregistrer(p)).getErreurs().size());
        assertTrue(dao.findAll().isEmpty());
    }

    @Test void filtreLaDateLocaleTrieEtExclutLesConsultationsExistantes() throws Exception {
        Patient late = patient("Dernier"); late.setDateArrivee(LocalDateTime.of(2026, 9, 30, 11, 0)); dao.save(late);
        Patient early = patient("Premier"); early.setDateArrivee(LocalDateTime.of(2026, 9, 30, 0, 0)); dao.save(early);
        Patient old = patient("Hier"); old.setDateArrivee(LocalDateTime.of(2026, 9, 29, 23, 59)); dao.save(old);
        Patient tomorrow = patient("Demain"); tomorrow.setDateArrivee(LocalDateTime.of(2026, 10, 1, 0, 0)); dao.save(tomorrow);
        assertEquals(java.util.List.of(early.getId(), late.getId()), service.patientsDuJour().stream().map(Patient::getId).toList());
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO utilisateurs (nom,email,mot_de_passe_hash,role) VALUES ('Docteur','doc@example.test','hash','GENERALISTE')");
            statement.executeUpdate("INSERT INTO consultations (patient_id,medecin_id,motif,observations,diagnostic,traitement,date_consultation) VALUES ("
                    + early.getId() + ",1,'Motif','Observations','Diagnostic','Traitement',CURRENT_TIMESTAMP)");
        }
        assertEquals(java.util.List.of(late.getId()), service.patientsEnAttente().stream().map(Patient::getId).toList());
        assertEquals("TERMINEE", new JdbcConsultationDAO(dataSource).findByPatientId(early.getId()).orElseThrow().getStatut());
        assertTrue(new JdbcConsultationDAO(dataSource).findByPatientId(late.getId()).isEmpty());
        assertEquals(2, service.patientsDuJour().size());
    }

    @Test void authentifieLesComptesDuScriptSqlEtRejetteLesAutres() throws Exception {
        try (Connection connection = dataSource.getConnection(); var reader = Files.newBufferedReader(Path.of("database/seed.sql"))) {
            RunScript.execute(connection, reader);
        }
        AuthService auth = new AuthService(new JdbcUserDAO(dataSource));
        assertEquals(Role.INFIRMIER, auth.authentifier(" INFIRMIER@MEDIFLOW.LOCAL ", "Infirmier123!").orElseThrow().getRole());
        assertEquals(Role.GENERALISTE, auth.authentifier("generaliste@mediflow.local", "Generaliste123!").orElseThrow().getRole());
        assertTrue(auth.authentifier("infirmier@mediflow.local", "incorrect").isEmpty());
        assertTrue(auth.authentifier("inconnu@mediflow.local", "Infirmier123!").isEmpty());
        assertTrue(auth.authentifier("' OR 1=1 --", "Infirmier123!").isEmpty());
        assertTrue(auth.authentifier(null, null).isEmpty());
    }
}
