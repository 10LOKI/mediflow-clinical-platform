package com.mediflow.filter;

import com.mediflow.model.Role;
import com.mediflow.model.Utilisateur;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

/** Ordre explicite : encodage, session/rôle, puis CSRF pour chaque POST. */
public class SecurityFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        res.setHeader("X-Content-Type-Options", "nosniff");
        res.setHeader("X-Frame-Options", "DENY");
        res.setHeader("Content-Security-Policy", "default-src 'self'; style-src 'self'; form-action 'self'; frame-ancestors 'none'; base-uri 'self'");
        res.setHeader("Cache-Control", "no-store");
        String path = req.getServletPath();
        if (path.startsWith("/assets/") && (req.getMethod().equals("GET") || req.getMethod().equals("HEAD"))) {
            chain.doFilter(req, res);
            return;
        }
        HttpSession session = req.getSession(true);
        Utilisateur user = (Utilisateur) session.getAttribute("utilisateur");
        if (!path.equals("/login")) {
            if (user == null) {
                res.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            if ((path.startsWith("/infirmier/") && user.getRole() != Role.INFIRMIER)
                    || (path.startsWith("/generaliste/") && user.getRole() != Role.GENERALISTE)) {
                res.sendError(403);
                return;
            }
        }
        if (session.getAttribute("_csrf") == null) session.setAttribute("_csrf", UUID.randomUUID().toString());
        if (req.getMethod().equals("POST")) {
            String actual = req.getParameter("_csrf");
            String expected = (String) session.getAttribute("_csrf");
            if (actual == null || !MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8))) {
                res.sendError(403);
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
