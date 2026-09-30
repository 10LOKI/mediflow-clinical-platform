<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Nouveau patient · MediFlow</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head>
<body>
<%@ include file="fragments/header.jspf" %>
<main class="container narrow">
    <a class="back" href="${pageContext.request.contextPath}/infirmier/patients">← Patients du jour</a>
    <p class="eyebrow">ACCUEIL INFIRMIER</p><h1>Enregistrer un patient</h1>
    <p class="muted">Renseignez l'identité et les signes vitaux. Tous les champs sont obligatoires.</p>
    <c:if test="${not empty erreurs}">
        <div class="alert error" role="alert"><strong>Vérifiez les informations saisies.</strong><ul>
            <c:forEach var="erreur" items="${erreurs}"><li><c:out value="${erreur.value}"/></li></c:forEach>
        </ul></div>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/infirmier/patients" class="panel">
        <input type="hidden" name="_csrf" value="${sessionScope._csrf}">
        <fieldset><legend>01 · Identité du patient</legend><div class="form-grid">
            <div><label for="nom">Nom</label><input id="nom" name="nom" required maxlength="100" value="<c:out value='${valeurs.nom}'/>"></div>
            <div><label for="prenom">Prénom</label><input id="prenom" name="prenom" required maxlength="100" value="<c:out value='${valeurs.prenom}'/>"></div>
            <div><label for="dateNaissance">Date de naissance</label><input id="dateNaissance" name="dateNaissance" type="date" required value="<c:out value='${valeurs.dateNaissance}'/>"></div>
            <div><label for="numeroSecuriteSociale">Numéro de sécurité sociale</label><input id="numeroSecuriteSociale" name="numeroSecuriteSociale" required maxlength="30" value="<c:out value='${valeurs.numeroSecuriteSociale}'/>"></div>
        </div></fieldset>
        <fieldset><legend>02 · Signes vitaux</legend><div class="form-grid">
            <div><label for="tensionArterielle">Tension artérielle (mmHg)</label><input id="tensionArterielle" name="tensionArterielle" required maxlength="7" placeholder="120/80" pattern="[0-9]{2,3}/[0-9]{2,3}" value="<c:out value='${valeurs.tensionArterielle}'/>"></div>
            <div><label for="frequenceCardiaque">Fréquence cardiaque (batt./min)</label><input id="frequenceCardiaque" name="frequenceCardiaque" type="number" required min="20" max="250" value="<c:out value='${valeurs.frequenceCardiaque}'/>"></div>
            <div><label for="temperature">Température (°C)</label><input id="temperature" name="temperature" type="number" required min="30" max="45" step="0.1" value="<c:out value='${valeurs.temperature}'/>"></div>
            <div><label for="frequenceRespiratoire">Fréquence respiratoire (cycles/min)</label><input id="frequenceRespiratoire" name="frequenceRespiratoire" type="number" required min="5" max="80" value="<c:out value='${valeurs.frequenceRespiratoire}'/>"></div>
        </div></fieldset>
        <div class="form-footer"><span class="muted">L'heure d'arrivée est enregistrée automatiquement.</span><button type="submit">Enregistrer le patient</button></div>
    </form>
</main></body></html>
