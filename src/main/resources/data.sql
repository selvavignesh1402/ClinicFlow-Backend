-- Default password for seeded users: password
INSERT INTO users (user_id, name, email, password, phone, role, status, created_at, updated_at) VALUES
(1, 'Dr. Asha Mehta', 'asha.mehta@clinicflow.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoO.Hm8z5G0siJmq9hw3rroWAt4EvsC0Bp', '9876500001', 'CLINICIAN', 'ACTIVE', '2026-04-01 09:00:00', '2026-04-01 09:00:00'),
(2, 'Rahul Sharma', 'rahul.sharma@clinicflow.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoO.Hm8z5G0siJmq9hw3rroWAt4EvsC0Bp', '9876500002', 'ADMIN', 'ACTIVE', '2026-04-01 09:15:00', '2026-04-01 09:15:00');

INSERT INTO patients (patient_id, mrn, name, dob, gender, contact_info_json, address_json, primary_contact, insurance_id, status, created_at, updated_at, version) VALUES
(1, 'MRN000001', 'Priya Nair', '1992-06-15', 'FEMALE', '{"phone":"9000000001","email":"priya.nair@email.com"}', '{"line1":"12 Lake View","city":"Bengaluru","state":"KA","zip":"560001"}', 'Anil Nair', 'INS1001', 'ACTIVE', '2026-04-01 10:00:00', '2026-04-01 10:00:00', 0),
(2, 'MRN000002', 'Arjun Verma', '1988-11-20', 'MALE', '{"phone":"9000000002","email":"arjun.verma@email.com"}', '{"line1":"45 Green Park","city":"Hyderabad","state":"TS","zip":"500001"}', 'Sneha Verma', 'INS1002', 'ACTIVE', '2026-04-01 10:05:00', '2026-04-01 10:05:00', 0);

INSERT INTO medication_master (med_id, code, name, formulation, strength, atc_code, controlled_flag, status) VALUES
(1, 'MED001', 'Paracetamol', 'Tablet', '500mg', 'N02BE01', 0, 'ACTIVE'),
(2, 'MED002', 'Amoxicillin', 'Capsule', '250mg', 'J01CA04', 0, 'ACTIVE');

INSERT INTO inventory_item (inventory_id, med_id, batch_number, quantity, unit, expiry_date, location, cost_price, status) VALUES
(1, 1, 'BATCH001', 500, 'Tablet', '2028-12-31', 'Pharmacy Rack A', 1.50, 'IN_STOCK'),
(2, 2, 'BATCH002', 200, 'Capsule', '2027-06-30', 'Pharmacy Rack B', 3.00, 'IN_STOCK');

INSERT INTO appointments (appt_id, patient_id, clinician_id, department, service_type, start_at, end_at, status, created_by, created_at) VALUES
(1, 1, 1, 'General Medicine', 'Consultation', '2026-04-10 09:00:00', '2026-04-10 09:30:00', 'COMPLETED', 2, '2026-04-09 16:00:00'),
(2, 2, 1, 'General Medicine', 'Follow Up', '2026-04-11 11:00:00', '2026-04-11 11:30:00', 'COMPLETED', 2, '2026-04-10 17:00:00');

INSERT INTO encounters (encounter_id, patient_id, clinician_id, visit_type, chief_complaint, vitals_json, notes_json, diagnoses_json, orders_json, start_at, end_at, status, signed_by, signed_at) VALUES
(1, 1, 1, 'OPD', 'Fever and body pain', '{"temp":"101F","bp":"110/70","pulse":"92"}', '{"soap":"Patient reports fever for 2 days"}', '{"primary":"Viral fever"}', '{"lab":["CBC"]}', '2026-04-10 09:00:00', '2026-04-10 09:20:00', 'COMPLETED', 1, '2026-04-10 09:25:00'),
(2, 2, 1, 'FOLLOW_UP', 'Cough and sore throat', '{"temp":"99F","bp":"120/80","pulse":"88"}', '{"soap":"Dry cough for 5 days"}', '{"primary":"Upper respiratory infection"}', '{"lab":["XRay Chest"]}', '2026-04-11 11:00:00', '2026-04-11 11:25:00', 'COMPLETED', 1, '2026-04-11 11:30:00');

INSERT INTO prescriptions (rx_id, encounter_id, patient_id, clinician_id, med_id, dosage, frequency, duration_days, quantity, repeats, route, notes, status, issued_at) VALUES
(1, 1, 1, 1, 1, '1 tablet', 'TID', 5, 15, 0, 'ORAL', 'After food', 'ISSUED', '2026-04-10 09:15:00'),
(2, 2, 2, 1, 2, '1 capsule', 'BID', 7, 14, 0, 'ORAL', 'Complete the course', 'ISSUED', '2026-04-11 11:15:00');

INSERT INTO dispense_record (dispense_id, rx_id, inventory_id, patient_id, dispensed_by_fk, quantity, dispensed_at, notes, status) VALUES
(1, 1, 1, 1, 1, 15, '2026-04-10 10:00:00', 'Dispensed standard dosage', 'DISPENSED'),
(2, 2, 2, 2, 1, 14, '2026-04-11 12:00:00', 'Dispensed complete course', 'DISPENSED');

INSERT INTO lab_order (lab_order_id, encounter_id, patient_id, ordered_by_fk, tests_json, sample_id, collected_at, status, result_uri) VALUES
(1, 1, 1, 1, '{"tests":["CBC","CRP"]}', 'SMP1001', '2026-04-10 09:35:00', 'COLLECTED', '/lab/orders/1/results'),
(2, 2, 2, 1, '{"tests":["Chest X-Ray"]}', 'SMP1002', '2026-04-11 11:40:00', 'RESULTS_REPORTED', '/lab/orders/2/results');

INSERT INTO lab_result (result_id, lab_order_id, test_code, value, units, reference_range_json, flag, reported_at, reported_by) VALUES
(1, 1, 'CBC-WBC', '8600', 'cells/uL', '{"min":"4000","max":"11000"}', 'NORMAL', '2026-04-10 14:00:00', 1),
(2, 2, 'XR-CHEST', 'Mild bronchitic changes', 'TEXT', '{"type":"narrative"}', 'NORMAL', '2026-04-11 16:30:00', 1);

INSERT INTO invoice (invoice_id, patient_id, encounter_id, line_items_json, subtotal, taxes, discounts, total_amount, issued_at, due_date, status) VALUES
(1, 1, 1, '[{"code":"SRV001","amount":500},{"code":"SRV002","amount":350}]', 850.00, 45.00, 50.00, 845.00, '2026-04-10 10:00:00', '2026-04-15 00:00:00', 'PARTIALLY_PAID'),
(2, 2, 2, '[{"code":"SRV001","amount":500}]', 500.00, 25.00, 0.00, 525.00, '2026-04-11 12:00:00', '2026-04-16 00:00:00', 'PAID');

INSERT INTO payment (payment_id, invoice_id, patient_id, amount, method, paid_at, status) VALUES
(1, 1, 1, 400.00, 'CARD', '2026-04-10 10:30:00', 'SUCCESS'),
(2, 2, 2, 525.00, 'UPI', '2026-04-11 12:15:00', 'SUCCESS');

INSERT INTO problem_list (problem_id, patient_id, code, description, onset_date, status, created_at) VALUES
(1, 1, 'J11', 'Viral fever', '2026-04-08', 'ACTIVE', '2026-04-10 09:15:00'),
(2, 2, 'J06.9', 'Upper respiratory infection', '2026-04-06', 'ACTIVE', '2026-04-11 11:15:00');

INSERT INTO report (report_id, scope, parameters_json, metrics_json, generated_by_fk, generated_at, report_uri) VALUES
(1, 'CLINIC', '{"startDate":"2026-03-01","endDate":"2026-03-15"}', '{"totalEncounters":42}', 2, '2026-03-16 08:00:00', '/reports/clinic-2026-03a.pdf'),
(2, 'FINANCE', '{"startDate":"2026-03-16","endDate":"2026-03-31"}', '{"totalRevenue":125000}', 2, '2026-04-01 08:00:00', '/reports/finance-2026-03b.pdf');

INSERT INTO task (task_id, assigned_to_fk, related_entity_id, description, due_date, priority, created_at, completed_at, status) VALUES
(1, 1, 'patient-1', 'Follow up on lab result for Priya Nair', '2026-04-12 18:00:00', 'HIGH', '2026-04-10 14:05:00', NULL, 'PENDING'),
(2, 2, 'invoice-2', 'Verify insurance claim for Arjun Verma', '2026-04-15 17:00:00', 'MEDIUM', '2026-04-11 12:30:00', '2026-04-12 10:00:00', 'COMPLETED');