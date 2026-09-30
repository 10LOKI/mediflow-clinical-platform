package com.mediflow.servlet;

import com.mediflow.service.PatientService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/generaliste/patients")
public class AttenteServlet extends HttpServlet {
    private PatientService patientService;
    @Override public void init() { patientService = (PatientService) getServletContext().getAttribute("patientService"); }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("patients", patientService.patientsEnAttente());
        req.getRequestDispatcher("/WEB-INF/views/attente.jsp").forward(req, res);
    }
}
