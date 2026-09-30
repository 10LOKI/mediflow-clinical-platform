package com.mediflow.servlet;

import com.mediflow.model.Patient;
import com.mediflow.service.PatientService;
import com.mediflow.service.ValidationException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@WebServlet({"/infirmier/patients", "/infirmier/patients/nouveau"})
public class PatientServlet extends HttpServlet {
    private PatientService patientService;
    @Override public void init() { patientService = (PatientService) getServletContext().getAttribute("patientService"); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (req.getServletPath().endsWith("/nouveau")) {
            req.getRequestDispatcher("/WEB-INF/views/patient-form.jsp").forward(req, res);
        } else {
            req.setAttribute("patients", patientService.patientsDuJour());
            req.setAttribute("succes", req.getSession().getAttribute("succes"));
            req.getSession().removeAttribute("succes");
            req.getRequestDispatcher("/WEB-INF/views/patients.jsp").forward(req, res);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (req.getServletPath().endsWith("/nouveau")) { res.sendError(405); return; }
        Map<String, String> valeurs = new LinkedHashMap<>();
        for (String champ : List.of("nom", "prenom", "dateNaissance", "numeroSecuriteSociale", "tensionArterielle",
                "frequenceCardiaque", "temperature", "frequenceRespiratoire")) {
            String valeur = req.getParameter(champ);
            valeurs.put(champ, valeur == null ? "" : valeur.trim());
        }
        Patient patient = new Patient();
        patient.setNom(valeurs.get("nom"));
        patient.setPrenom(valeurs.get("prenom"));
        patient.setNumeroSecuriteSociale(valeurs.get("numeroSecuriteSociale"));
        patient.setTensionArterielle(valeurs.get("tensionArterielle"));
        try { patient.setDateNaissance(LocalDate.parse(valeurs.get("dateNaissance"))); }
        catch (DateTimeParseException ignored) { /* La validation métier fournira le message. */ }
        patient.setFrequenceCardiaque(entier(valeurs.get("frequenceCardiaque")));
        patient.setFrequenceRespiratoire(entier(valeurs.get("frequenceRespiratoire")));
        try { patient.setTemperature(Double.parseDouble(valeurs.get("temperature"))); }
        catch (NumberFormatException ignored) { patient.setTemperature(Double.NaN); }
        try {
            patientService.enregistrer(patient);
            req.getSession().setAttribute("succes", "Patient enregistré et ajouté à la file d'attente.");
            res.sendRedirect(req.getContextPath() + "/infirmier/patients");
        } catch (ValidationException e) {
            req.setAttribute("valeurs", valeurs);
            req.setAttribute("erreurs", e.getErreurs());
            res.setStatus(400);
            req.getRequestDispatcher("/WEB-INF/views/patient-form.jsp").forward(req, res);
        }
    }

    private int entier(String valeur) {
        try { return Integer.parseInt(valeur); }
        catch (NumberFormatException e) { return 0; }
    }
}
