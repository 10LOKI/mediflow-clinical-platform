<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>File d'attente · MediFlow</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head>
<body>
<%@ include file="fragments/header.jspf" %>
<main class="container">
    <p class="eyebrow">ESPACE GÉNÉRALISTE</p>
    <div class="page-heading"><div><h1>Patients en attente <span class="count">${fn:length(patients)}</span></h1><p class="muted">Les patients du jour qui n'ont pas encore de consultation.</p></div><a class="button secondary" href="${pageContext.request.contextPath}/generaliste/patients">Actualiser</a></div>
    <p class="alert info">Version partielle : la saisie et la clôture des consultations seront disponibles dans une prochaine étape.</p>
    <section class="panel table-panel" aria-label="File d'attente"><%@ include file="fragments/patient-table.jspf" %></section>
</main></body></html>
