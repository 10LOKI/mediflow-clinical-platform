package com.mediflow.config;

import com.mediflow.repository.jdbc.JdbcPatientDAO;
import com.mediflow.repository.jdbc.JdbcUserDAO;
import com.mediflow.service.AuthService;
import com.mediflow.service.PatientService;
import jakarta.servlet.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.time.Clock;
import java.time.ZoneId;

public class AppListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            DataSource dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MediflowDB");
            ServletContext context = event.getServletContext();
            context.setAttribute("authService", new AuthService(new JdbcUserDAO(dataSource)));
            context.setAttribute("patientService", new PatientService(new JdbcPatientDAO(dataSource),
                    Clock.system(ZoneId.of("Africa/Casablanca"))));
        } catch (NamingException e) {
            throw new IllegalStateException("Configurez le DataSource JNDI jdbc/MediflowDB dans Tomcat.", e);
        }
    }
}
