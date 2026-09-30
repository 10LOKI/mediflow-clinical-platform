<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Connexion · MediFlow</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head>
<body class="login-page">
<main class="login-card">
    <div class="brand"><span class="brand-icon">+</span> MediFlow</div>
    <p class="eyebrow">ESPACE CLINIQUE</p>
    <h1>Bienvenue</h1><p class="muted">Connectez-vous pour accéder à votre espace de travail.</p>
    <c:if test="${not empty erreur}"><p class="alert error" role="alert"><c:out value="${erreur}"/></p></c:if>
    <form method="post" action="${pageContext.request.contextPath}/login" class="stack">
        <input type="hidden" name="_csrf" value="${sessionScope._csrf}">
        <label for="email">Adresse email</label>
        <input id="email" name="email" type="email" required maxlength="254" autocomplete="username" value="<c:out value='${param.email}'/>">
        <label for="motDePasse">Mot de passe</label>
        <input id="motDePasse" name="motDePasse" type="password" required maxlength="72" autocomplete="current-password">
        <button type="submit">Se connecter</button>
    </form>
    <p class="login-note">Accès réservé aux infirmiers et médecins généralistes.</p>
</main>
</body></html>
