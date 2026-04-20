-- INSERT INTO users (user_id, name, role, email, phone, status, created_at, updated_at) VALUES
-- (1, 'Dr. Asha Mehta', 'CLINICIAN', 'asha.mehta@clinicflow.com', '9876500001', 'ACTIVE', '2026-04-01 09:00:00', '2026-04-01 09:00:00'),
-- (2, 'Rahul Sharma', 'ADMIN', 'rahul.sharma@clinicflow.com', '9876500002', 'ACTIVE', '2026-04-01 09:15:00', '2026-04-01 09:15:00');

-- INSERT INTO patients (patient_id, mrn, name, dob, gender, contact_info_json, address_json, primary_contact, insurance_id, status, created_at) VALUES
-- (1, 'MRN1001', 'Priya Nair', '1992-06-15', 'FEMALE', '{"phone":"9000000001","email":"priya.nair@email.com"}', '{"line1":"12 Lake View","city":"Bengaluru","state":"KA","zip":"560001"}', 'Anil Nair', 'INS1001', 'ACTIVE', '2026-04-01 10:00:00'),
-- (2, 'MRN1002', 'Arjun Verma', '1988-11-20', 'MALE', '{"phone":"9000000002","email":"arjun.verma@email.com"}', '{"line1":"45 Green Park","city":"Hyderabad","state":"TS","zip":"500001"}', 'Sneha Verma', 'INS1002', 'ACTIVE', '2026-04-01 10:05:00');

-- INSERT INTO medication_master (med_id, code, name, formulation, strength, atc_code, controlled_flag, status) VALUES
-- (1, 'MED001', 'Paracetamol', 'Tablet', '500mg', 'N02BE01', 0, 'ACTIVE'),
-- (2, 'MED002', 'Amoxicillin', 'Capsule', '250mg', 'J01CA04', 0, 'ACTIVE');

-- INSERT INTO service_items (service_id, code, description, price, department, billing_category, status) VALUES
-- (1, 'SRV001', 'General Consultation', 500.00, 'OPD', 'CONSULTATION', 'ACTIVE'),
-- (2, 'SRV002', 'Complete Blood Count', 350.00, 'LAB', 'DIAGNOSTICS', 'ACTIVE');

-- INSERT INTO kpis (kpi_id, name, definition, target, current_value, reporting_period) VALUES
-- (1, 'Daily OPD Count', 'Number of OPD patients per day', 100.00, 86.00, 'DAILY'),
-- (2, 'Collection Efficiency', 'Percentage of invoice collection', 95.00, 91.50, 'MONTHLY');

-- INSERT INTO adapters (adapter_id, type, config_json, credentials_encrypted, last_sync_at, status) VALUES
-- (1, 'LAB_SYSTEM', '{"baseUrl":"https://lab.example.com/api","timeout":30}', 'enc_lab_token_001', '2026-04-16 18:00:00', 'ACTIVE'),
-- (2, 'INSURANCE_GATEWAY', '{"baseUrl":"https://payer.example.com/api","retry":3}', 'enc_payer_token_002', '2026-04-16 18:05:00', 'ACTIVE');

-- INSERT INTO audit_packages (package_id, period_start, period_end, contents_json, generated_at, package_uri) VALUES
-- (1, '2026-03-01', '2026-03-15', '{"tables":["audit_logs","payments"]}', '2026-03-16 08:00:00', '/audit/packages/pkg-2026-03a.zip'),
-- (2, '2026-03-16', '2026-03-31', '{"tables":["audit_logs","claims"]}', '2026-04-01 08:00:00', '/audit/packages/pkg-2026-03b.zip');

-- INSERT INTO encounters (encounter_id, patient_id, clinician_id, visit_type, chief_complaint, vitals_json, notes_json, diagnoses_json, orders_json, prescriptions_json, start_at, end_at, status, signed_by, signed_at) VALUES
-- (1, 1, 1, 'OPD', 'Fever and body pain', '{"temp":"101F","bp":"110/70","pulse":"92"}', '{"soap":"Patient reports fever for 2 days"}', '{"primary":"Viral fever"}', '{"lab":["CBC"]}', '{"planned":["Paracetamol"]}', '2026-04-10 09:00:00', '2026-04-10 09:20:00', 'COMPLETED', 1, '2026-04-10 09:25:00'),
-- (2, 2, 1, 'FOLLOW_UP', 'Cough and sore throat', '{"temp":"99F","bp":"120/80","pulse":"88"}', '{"soap":"Dry cough for 5 days"}', '{"primary":"Upper respiratory infection"}', '{"lab":["XRay Chest"]}', '{"planned":["Amoxicillin"]}', '2026-04-11 11:00:00', '2026-04-11 11:25:00', 'COMPLETED', 1, '2026-04-11 11:30:00');

-- INSERT INTO appointments (appt_id, patient_id, clinician_id, department, service_type, start_at, end_at, status, created_by, created_at) VALUES
-- (1, 1, 1, 'General Medicine', 'Consultation', '2026-04-10 09:00:00', '2026-04-10 09:30:00', 'COMPLETED', 2, '2026-04-09 16:00:00'),
-- (2, 2, 1, 'General Medicine', 'Follow Up', '2026-04-11 11:00:00', '2026-04-11 11:30:00', 'COMPLETED', 2, '2026-04-10 17:00:00');

-- INSERT INTO prescriptions (rx_id, encounter_id, patient_id, clinician_id, medication_id, dosage, frequency, duration_days, quantity, repeats, route, notes, status, issued_at) VALUES
-- (1, 1, 1, 1, 1, '1 tablet', 'TID', 5, 15, 0, 'ORAL', 'After food', 'ISSUED', '2026-04-10 09:15:00'),
-- (2, 2, 2, 1, 2, '1 capsule', 'BID', 7, 14, 0, 'ORAL', 'Complete the full course', 'ISSUED', '2026-04-11 11:15:00');

-- INSERT INTO problem_list (problem_id, patient_id, code, description, onset_date, status, created_at) VALUES
-- (1, 1, 'R50.9', 'Fever, unspecified', '2026-04-08', 'ACTIVE', '2026-04-10 09:10:00'),
-- (2, 2, 'J06.9', 'Acute upper respiratory infection', '2026-04-06', 'ACTIVE', '2026-04-11 11:10:00');

-- INSERT INTO lab_orders (lab_order_id, encounter_id, patient_id, ordered_by, tests_json, sample_id, collected_at, status, result_uri) VALUES
-- (1, 1, 1, 1, '{"tests":["CBC","CRP"]}', 'SMP1001', '2026-04-10 09:35:00', 'COLLECTED', '/lab/orders/1/results'),
-- (2, 2, 2, 1, '{"tests":["Chest X-Ray"]}', 'SMP1002', '2026-04-11 11:40:00', 'PROCESSING', '/lab/orders/2/results');

-- INSERT INTO lab_results (result_id, lab_order_id, test_code, value, units, reference_range_json, flag, reported_at, reported_by) VALUES
-- (1, 1, 'CBC-WBC', '8600', 'cells/uL', '{"min":"4000","max":"11000"}', 'NORMAL', '2026-04-10 14:00:00', 1),
-- (2, 2, 'XR-CHEST', 'Mild bronchitic changes', 'TEXT', '{"type":"narrative"}', 'ABNORMAL', '2026-04-11 16:30:00', 1);

-- INSERT INTO inventory_items (inventory_id, medication_id, batch_number, quantity, unit, expiry_date, location, cost_price, status) VALUES
-- (1, 1, 'PCM-250401', 500, 'TABLET', '2027-03-31', 'Pharmacy Rack A1', 1.20, 'IN_STOCK'),
-- (2, 2, 'AMX-250402', 300, 'CAPSULE', '2027-06-30', 'Pharmacy Rack B2', 2.80, 'IN_STOCK');

-- INSERT INTO invoices (invoice_id, patient_id, encounter_id, line_items_json, subtotal, taxes, discounts, total_amount, issued_at, due_date, status) VALUES
-- (1, 1, 1, '[{"code":"SRV001","amount":500.00},{"code":"SRV002","amount":350.00}]', 850.00, 45.00, 50.00, 845.00, '2026-04-10 10:00:00', '2026-04-15', 'PARTIALLY_PAID'),
-- (2, 2, 2, '[{"code":"SRV001","amount":500.00}]', 500.00, 25.00, 0.00, 525.00, '2026-04-11 12:00:00', '2026-04-16', 'PAID');

-- INSERT INTO payments (payment_id, invoice_id, patient_id, amount, method, paid_at, status) VALUES
-- (1, 1, 1, 400.00, 'CARD', '2026-04-10 10:30:00', 'SUCCESS'),
-- (2, 2, 2, 525.00, 'UPI', '2026-04-11 12:15:00', 'SUCCESS');

-- INSERT INTO insurance_claims (claim_id, invoice_id, payer_id, claim_payload_json, submitted_at, status, response_json) VALUES
-- (1, 1, 'PAYER001', '{"invoiceId":1,"memberId":"INS1001","amount":445.00}', '2026-04-10 11:00:00', 'SUBMITTED', '{"ack":"received"}'),
-- (2, 2, 'PAYER002', '{"invoiceId":2,"memberId":"INS1002","amount":525.00}', '2026-04-11 13:00:00', 'APPROVED', '{"approvedAmount":525.00}');

-- INSERT INTO dispense_records (dispense_id, prescription_id, inventory_item_id, patient_id, dispensed_by, quantity, dispensed_at, notes, status) VALUES
-- (1, 1, 1, 1, 1, 15, '2026-04-10 10:45:00', 'Issued full quantity', 'DISPENSED'),
-- (2, 2, 2, 2, 1, 14, '2026-04-11 12:30:00', 'Issued full quantity', 'DISPENSED');

-- INSERT INTO notifications (notification_id, user_id, patient_id, entity_id, message, category, channel, severity, created_at, read_at, status) VALUES
-- (1, 1, 1, 'LAB_ORDER_1', 'Lab sample collected for Priya Nair', 'LAB', 'IN_APP', 'MEDIUM', '2026-04-10 09:40:00', NULL, 'UNREAD'),
-- (2, 2, 2, 'INVOICE_2', 'Invoice paid successfully for Arjun Verma', 'BILLING', 'EMAIL', 'LOW', '2026-04-11 12:20:00', '2026-04-11 12:25:00', 'READ');

-- INSERT INTO tasks (task_id, assigned_to, related_entity_id, description, due_date, priority, created_at, completed_at, status) VALUES
-- (1, 2, 'CLAIM_1', 'Review insurance claim submission for invoice 1', '2026-04-12', 'HIGH', '2026-04-10 11:05:00', NULL, 'OPEN'),
-- (2, 1, 'LAB_ORDER_2', 'Review chest X-ray result and update notes', '2026-04-12', 'MEDIUM', '2026-04-11 16:35:00', NULL, 'OPEN');

-- INSERT INTO reports (report_id, scope, parameters_json, metrics_json, generated_by, generated_at, report_uri) VALUES
-- (1, 'DAILY_OPD', '{"date":"2026-04-10"}', '{"count":42,"avgWaitMins":18}', 2, '2026-04-10 18:00:00', '/reports/daily-opd-2026-04-10.pdf'),
-- (2, 'REVENUE_SUMMARY', '{"from":"2026-04-01","to":"2026-04-11"}', '{"gross":1370.00,"net":1245.00}', 2, '2026-04-11 18:30:00', '/reports/revenue-summary-apr.pdf');

-- INSERT INTO audit_logs (audit_id, user_id, action, resource_type, resource_id, details_json, timestamp) VALUES
-- (1, 2, 'CREATE', 'PATIENT', '1', '{"field":"patient","source":"reception"}', '2026-04-01 10:00:30'),
-- (2, 1, 'UPDATE', 'ENCOUNTER', '2', '{"field":"diagnosesJson","source":"clinician"}', '2026-04-11 11:31:00');


