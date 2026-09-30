-- Comptes de démonstration locale uniquement, mots de passe hachés avec bcrypt (coût 10).
-- Infirmier123! / Generaliste123! ; remplacer ces comptes avant tout usage réel.
INSERT INTO utilisateurs (nom, email, mot_de_passe_hash, role) VALUES
('Infirmier démo', 'infirmier@mediflow.local', '$2a$10$y1Z2N9bgMjnCYeGcKvXw5OT/uhnMJoMDBN3UHKUU3WW9IaxYu8Utu', 'INFIRMIER'),
('Généraliste démo', 'generaliste@mediflow.local', '$2a$10$ocHU7NkNgmFRi/Wz5Ioy8e4wWWqhiKKZW64aeh1ZbzJCxPXSBlbeS', 'GENERALISTE');
