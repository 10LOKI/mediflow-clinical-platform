package com.mediflow.servlet;

import com.mediflow.model.Role;
import com.mediflow.model.Utilisateur;
import com.mediflow.service.AuthService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private AuthService authService;
    @Override public void init() { authService = (AuthService) getServletContext().getAttribute("authService"); }

    public static String accueil(Utilisateur user) {
        return user.getRole() == Role.INFIRMIER ? "/infirmier/patients" : "/generaliste/patients";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Utilisateur user = (Utilisateur) req.getSession().getAttribute("utilisateur");
        if (user != null) res.sendRedirect(req.getContextPath() + accueil(user));
        else req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Optional<Utilisateur> user = authService.authentifier(req.getParameter("email"), req.getParameter("motDePasse"));
        if (user.isEmpty()) {
            req.setAttribute("erreur", "Email ou mot de passe incorrect.");
            res.setStatus(401);
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, res);
            return;
        }
        req.getSession().invalidate();
        HttpSession session = req.getSession(true);
        Utilisateur u = user.get();
        // Le hash reste dans la couche d'authentification, pas dans la session HTTP.
        session.setAttribute("utilisateur", new Utilisateur(u.getId(), u.getNom(), u.getEmail(), null, u.getRole()));
        session.setAttribute("_csrf", UUID.randomUUID().toString());
        res.sendRedirect(req.getContextPath() + accueil(u));
    }
}
