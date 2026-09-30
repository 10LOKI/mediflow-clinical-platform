package com.mediflow;

import com.mediflow.filter.SecurityFilter;
import com.mediflow.model.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;

class SecurityFilterTest {
    private final SecurityFilter filter = new SecurityFilter();
    private HttpServletRequest req;
    private HttpServletResponse res;
    private HttpSession session;
    private FilterChain chain;

    @BeforeEach void setup() {
        req = mock(HttpServletRequest.class); res = mock(HttpServletResponse.class);
        session = mock(HttpSession.class); chain = mock(FilterChain.class);
        when(req.getSession(true)).thenReturn(session);
        when(req.getContextPath()).thenReturn("/mediflow");
        when(req.getMethod()).thenReturn("GET");
        when(req.getServletPath()).thenReturn("/infirmier/patients");
        when(session.getAttribute("_csrf")).thenReturn("valid-token");
    }

    @Test void redirigeUneSessionAbsente() throws Exception {
        filter.doFilter(req, res, chain);
        verify(res).sendRedirect("/mediflow/login"); verifyNoInteractions(chain);
    }

    @Test void interditLeMauvaisRole() throws Exception {
        when(session.getAttribute("utilisateur")).thenReturn(new Utilisateur(1,"Doc","doc@test",null,Role.GENERALISTE));
        filter.doFilter(req,res,chain);
        verify(res).sendError(403); verifyNoInteractions(chain);
    }

    @Test void interditAussiLesPagesGeneralisteALInfirmier() throws Exception {
        when(req.getServletPath()).thenReturn("/generaliste/patients");
        when(session.getAttribute("utilisateur")).thenReturn(new Utilisateur(1,"Inf","inf@test",null,Role.INFIRMIER));
        filter.doFilter(req,res,chain);
        verify(res).sendError(403); verifyNoInteractions(chain);
    }

    @Test void refuseUnPostLoginSansCsrf() throws Exception {
        when(req.getServletPath()).thenReturn("/login"); when(req.getMethod()).thenReturn("POST");
        filter.doFilter(req,res,chain);
        verify(res).sendError(403); verifyNoInteractions(chain);
    }

    @Test void refuseUnPostAvecUnMauvaisCsrf() throws Exception {
        when(req.getServletPath()).thenReturn("/logout"); when(req.getMethod()).thenReturn("POST");
        when(session.getAttribute("utilisateur")).thenReturn(new Utilisateur(1,"Inf","inf@test",null,Role.INFIRMIER));
        when(req.getParameter("_csrf")).thenReturn("wrong-token");
        filter.doFilter(req,res,chain);
        verify(res).sendError(403); verifyNoInteractions(chain);
    }

    @Test void autoriseUnPostDuBonRoleAvecCsrf() throws Exception {
        when(req.getMethod()).thenReturn("POST"); when(req.getParameter("_csrf")).thenReturn("valid-token");
        when(session.getAttribute("utilisateur")).thenReturn(new Utilisateur(1,"Inf","inf@test",null,Role.INFIRMIER));
        filter.doFilter(req,res,chain);
        verify(chain).doFilter(req,res); verify(res).setHeader("Cache-Control", "no-store");
    }
}
