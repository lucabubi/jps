-- ##################################################
-- ##             CONTATTI (CONTACT)             ##
-- ##################################################
-- ID espliciti necessari per le foreign key di email, address, customer, etc.

INSERT INTO contact (id, name, surname, ssn, category) VALUES
                                                           (1, 'Mario', 'Rossi', 'RSSMRA80A01H501U', 0),  -- Cliente
                                                           (2, 'Anna', 'Bianchi', 'BNCANNA75B41L219J', 0), -- Cliente
                                                           (3, 'Luigi', 'Verdi', 'VRDLGU85C15F205K', 1),  -- Professionista
                                                           (4, 'Sofia', 'Neri', 'NRISFO90D50H501X', 1),   -- Professionista
                                                           (5, 'Davide', 'Gialli', 'GLLDVD78E22A001Z', 0), -- Cliente
                                                           (6, 'Paolo', 'Bruno', null, 2);                -- Sconosciuto

-- ##################################################
-- ##         DETTAGLI CONTATTI (EMAIL,         ##
-- ##         ADDRESS, TELEPHONE)                ##
-- ##################################################
-- ID rimossi: saranno generati automaticamente.

-- Email
INSERT INTO email (email, contact_id) VALUES
                                          ('mario.rossi@example.com', 1),
                                          ('anna.bianchi@example.com', 2),
                                          ('a.bianchi@work.com', 2),
                                          ('luigi.verdi@gmail.com', 3),
                                          ('sofia.neri@outlook.com', 4),
                                          ('davide.gialli@startup.it', 5),
                                          ('paolo.bruno@info.com', 6);

-- Address (Indirizzi)
INSERT INTO address (address, contact_id) VALUES
                                              ('Via Roma 1, 10123 Torino', 1),
                                              ('Corso Francia 10, 10100 Torino', 2),
                                              ('Via Garibaldi 20, 20121 Milano', 3),
                                              ('Piazza Castello 5, 10122 Torino', 4),
                                              ('Lungo Po Antonelli 15, 10153 Torino', 5);

-- Telephone
INSERT INTO telephone (telephone, contact_id) VALUES
                                                  ('+39011123456', 1),
                                                  ('+393331234567', 2),
                                                  ('+393478901234', 3),
                                                  ('+393385556677', 4),
                                                  ('+39011987654', 5);


-- ##################################################
-- ##             CLIENTI (CUSTOMER)             ##
-- ##################################################
-- ID espliciti necessari per le foreign key di job_offer

INSERT INTO customer (id, contact_id) VALUES
                                          (1, 1), -- Mario Rossi
                                          (2, 2), -- Anna Bianchi
                                          (3, 5); -- Davide Gialli

-- Note per i Clienti (ElementCollection)
INSERT INTO customer_notes (customer_id, notes) VALUES
                                                    (1, 'Cliente storico, molto affidabile'),
                                                    (1, 'Preferisce contatto via email'),
                                                    (2, 'Nuovo cliente, acquisito tramite fiera'),
                                                    (3, 'Startup innovativa, settore IoT');


-- ##################################################
-- ##         PROFESSIONISTI (PROFESSIONAL)        ##
-- ##################################################
-- ID espliciti necessari per le foreign key di job_offer

INSERT INTO professional (id, daily_rate, employment_state, location, contact_id) VALUES
                                                                                      (1, 150.0, 1, 'Milano', 3), -- Luigi Verdi
                                                                                      (2, 120.0, 0, 'Torino', 4); -- Sofia Neri

-- Skills per Professionisti
INSERT INTO professional_skills (professional_id, skills) VALUES
                                                              (1, 'Kotlin'),
                                                              (1, 'Spring Boot'),
                                                              (1, 'JPA/Hibernate'),
                                                              (1, 'PostgreSQL'),
                                                              (2, 'React'),
                                                              (2, 'TypeScript'),
                                                              (2, 'Node.js'),
                                                              (2, 'UX/UI Design');

-- Note per Professionisti
INSERT INTO professional_notes (professional_id, notes) VALUES
                                                            (1, 'Senior developer, 10+ anni esperienza'),
                                                            (2, 'Front-end specialist'),
                                                            (2, 'Attualmente impiegata, ma valuta offerte');


-- ##################################################
-- ##           OFFERTE DI LAVORO (JOB OFFER)      ##
-- ##################################################
-- ID espliciti necessari per le foreign key di job_offer_notes e job_offer_required_skills

-- Offerta 1: Creata, nessun professionista assegnato (Cliente 1)
INSERT INTO job_offer (id, description, status, duration, value, customer_id, professional_id) VALUES
    (1, 'Sviluppo backend microservizi', 0, 120, null, 1, null);

INSERT INTO job_offer_notes (job_offer_id, notes) VALUES
    (1, 'Richiesta urgente');
INSERT INTO job_offer_required_skills (job_offer_id, required_skills) VALUES
                                                                          (1, 'Java'),
                                                                          (1, 'Spring Boot');


-- Offerta 2: Consolidata, assegnata (Cliente 2, Prof 2)
INSERT INTO job_offer (id, description, status, duration, value, customer_id, professional_id) VALUES
    (2, 'Refactoring interfaccia utente', 3, 90, 2160.0, 2, 2);

INSERT INTO job_offer_required_skills (job_offer_id, required_skills) VALUES
                                                                          (2, 'React'),
                                                                          (2, 'UX/UI Design');


-- Offerta 3: Completata (Cliente 1, Prof 1)
INSERT INTO job_offer (id, description, status, duration, value, customer_id, professional_id) VALUES
    (3, 'Migrazione database legacy', 4, 60, 1800.0, 1, 1);

INSERT INTO job_offer_required_skills (job_offer_id, required_skills) VALUES
                                                                          (3, 'PostgreSQL'),
                                                                          (3, 'Data Migration');


-- Offerta 4: In selezione (Cliente 3, nessun prof)
INSERT INTO job_offer (id, description, status, duration, value, customer_id, professional_id) VALUES
    (4, 'Sviluppo app mobile Android', 1, 180, null, 3, null);

INSERT INTO job_offer_notes (job_offer_id, notes) VALUES
    (4, 'Progetto a lungo termine');
INSERT INTO job_offer_required_skills (job_offer_id, required_skills) VALUES
                                                                          (4, 'Kotlin'),
                                                                          (4, 'Android SDK');


-- Offerta 5: Annullata (Cliente 2)
INSERT INTO job_offer (id, description, status, duration, value, customer_id, professional_id) VALUES
    (5, 'Consulenza sicurezza', 5, 30, null, 2, null);

INSERT INTO job_offer_notes (job_offer_id, notes) VALUES
    (5, 'Budget annullato dal cliente');
INSERT INTO job_offer_required_skills (job_offer_id, required_skills) VALUES
    (5, 'Security');


-- ##################################################
-- ##           MESSAGGI E STORICO (MESSAGE)     ##
-- ##################################################
-- ID espliciti per 'message' necessari per la foreign key di 'history'

-- Messaggio 1: Email urgente
INSERT INTO message (id, sender, date, subject, body, channel, state, priority) VALUES
    (1, 'mario.rossi@example.com', '2025-10-26 10:00:00', 'URGENTE: Problema server', 'Il server di produzione è down!', 2, 3, 2);

-- ID rimosso da 'history': sarà generato automaticamente
INSERT INTO history (date, state, comment, message_id) VALUES
                                                           ('2025-10-26 10:00:00', 0, 'Messaggio ricevuto e parcheggiato', 1),
                                                           ('2025-10-26 10:05:00', 1, 'Letto da operatore', 1),
                                                           ('2025-10-26 10:06:00', 3, 'Assegnato a tecnico senior', 1);


-- Messaggio 2: Telefonata
INSERT INTO message (id, sender, date, subject, body, channel, state, priority) VALUES
    (2, '+393331234567', '2025-10-27 09:30:00', 'Chiamata da Anna Bianchi', 'Chiede info su stato Job Offer #2', 0, 4, 1);

-- ID rimosso da 'history': sarà generato automaticamente
INSERT INTO history (date, state, comment, message_id) VALUES
                                                           ('2025-10-27 09:30:00', 0, 'Chiamata in ingresso', 2),
                                                           ('2025-10-27 09:35:00', 4, 'Richiesta evasa, fornite info su Sofia Neri.', 2);


-- Messaggio 3: SMS
INSERT INTO message (id, sender, date, subject, body, channel, state, priority) VALUES
    (3, '+393478901234', '2025-10-27 09:45:00', 'Disponibilità', 'Confermo disponibilità per nuovi progetti da settimana prossima. Saluti, Luigi Verdi', 1, 1, 0);

-- ID rimosso da 'history': sarà generato automaticamente
INSERT INTO history (date, state, comment, message_id) VALUES
                                                           ('2025-10-27 09:45:00', 0, 'SMS da Luigi Verdi', 3),
                                                           ('2025-10-27 09:50:00', 1, 'Letto e archiviato', 3);