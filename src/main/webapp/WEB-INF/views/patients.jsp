<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Patients du jour · MediFlow</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head>
<body>
<%@ include file="fragments/header.jspf" %>
<main class="container">
    <p class="eyebrow">ACCUEIL INFIRMIER</p>
    <div class="page-heading"><div><h1>Patients du jour <span class="count">${fn:length(patients)}</span></h1><p class="muted">Suivez les arrivées et les signes vitaux des patients.</p></div><a class="button" href="${pageContext.request.contextPath}/infirmier/patients/nouveau">+ Nouveau patient</a></div>
    <c:if test="${not empty succes}"><p class="alert success" role="status"><c:out value="${succes}"/></p></c:if>
    <section class="panel table-panel" aria-label="Liste des patients"><%@ include file="fragments/patient-table.jspf" %></section>
</main></body></html>
