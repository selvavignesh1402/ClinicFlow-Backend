-- Default password for seeded users: password
INSERT IGNORE INTO users (user_id, name, email, password, phone, role, status, created_at, updated_at) VALUES
(1, 'Dr. Asha Mehta', 'asha.mehta@clinicflow.com', '$2a$10$T9ZGTxoVcSvVTjfqqlyV3OSZsD7HBW/fIdKM3NMHmGkATkDJR3cra', '9876500001', 'CLINICIAN', 'ACTIVE', '2026-04-01 09:00:00', '2026-04-01 09:00:00'),
(2, 'Rahul Sharma', 'rahul.sharma@clinicflow.com', '$2a$10$T9ZGTxoVcSvVTjfqqlyV3OSZsD7HBW/fIdKM3NMHmGkATkDJR3cra', '9876500002', 'ADMIN', 'ACTIVE', '2026-04-01 09:15:00', '2026-04-01 09:15:00'),
(3, 'Dr. Sarah Mitchell', 'sarah.mitchell@clinicflow.com', '$2a$10$T9ZGTxoVcSvVTjfqqlyV3OSZsD7HBW/fIdKM3NMHmGkATkDJR3cra', '9876500003', 'CLINICIAN', 'ACTIVE', '2026-04-01 09:30:00', '2026-04-01 09:30:00'),
(4, 'Dr. James Carter', 'james.carter@clinicflow.com', '$2a$10$T9ZGTxoVcSvVTjfqqlyV3OSZsD7HBW/fIdKM3NMHmGkATkDJR3cra', '9876500004', 'CLINICIAN', 'ACTIVE', '2026-04-01 09:45:00', '2026-04-01 09:45:00'),
(5, 'Dr. Emily Watson', 'emily.watson@clinicflow.com', '$2a$10$T9ZGTxoVcSvVTjfqqlyV3OSZsD7HBW/fIdKM3NMHmGkATkDJR3cra', '9876500005', 'CLINICIAN', 'ACTIVE', '2026-04-01 10:00:00', '2026-04-01 10:00:00'),
(6, 'Vikram Malhotra', 'vikram.malhotra@clinicflow.com', '$2a$10$T9ZGTxoVcSvVTjfqqlyV3OSZsD7HBW/fIdKM3NMHmGkATkDJR3cra', '9876500006', 'LAB_TECHNICIAN', 'ACTIVE', '2026-04-01 10:15:00', '2026-04-01 10:15:00'),
(7, 'Meera Sen', 'meera.sen@clinicflow.com', '$2a$10$T9ZGTxoVcSvVTjfqqlyV3OSZsD7HBW/fIdKM3NMHmGkATkDJR3cra', '9876500007', 'PHARMACIST', 'ACTIVE', '2026-04-01 10:30:00', '2026-04-01 10:30:00'),
(8, 'Aditya Rao', 'aditya.rao@clinicflow.com', '$2a$10$T9ZGTxoVcSvVTjfqqlyV3OSZsD7HBW/fIdKM3NMHmGkATkDJR3cra', '9876500008', 'RECEPTION', 'ACTIVE', '2026-04-01 10:45:00', '2026-04-01 10:45:00'),
(9, 'Nisha Kapoor', 'nisha.kapoor@clinicflow.com', '$2a$10$T9ZGTxoVcSvVTjfqqlyV3OSZsD7HBW/fIdKM3NMHmGkATkDJR3cra', '9876500009', 'CLINIC_MANAGER', 'ACTIVE', '2026-04-01 11:00:00', '2026-04-01 11:00:00'),
(10, 'Sanjay Dutt', 'sanjay.dutt@clinicflow.com', '$2a$10$T9ZGTxoVcSvVTjfqqlyV3OSZsD7HBW/fIdKM3NMHmGkATkDJR3cra', '9876500010', 'FINANCE_OFFICER', 'ACTIVE', '2026-04-01 11:15:00', '2026-04-01 11:15:00');

INSERT IGNORE INTO patients (patient_id, mrn, name, dob, gender, contact_info_json, address_json, primary_contact, insurance_id, status, created_at, updated_at, version) VALUES
(1, 'MRN000001', 'Priya Nair', '1992-06-15', 'FEMALE', '{"phone":"9000000001","email":"priya.nair@email.com"}', '{"line1":"12 Lake View","city":"Bengaluru","state":"KA","zip":"560001"}', '9000000001', 'INS1001', 'ACTIVE', '2026-04-01 10:00:00', '2026-04-01 10:00:00', 0),
(2, 'MRN000002', 'Arjun Verma', '1988-11-20', 'MALE', '{"phone":"9000000002","email":"arjun.verma@email.com"}', '{"line1":"45 Green Park","city":"Hyderabad","state":"TS","zip":"500001"}', '9000000002', 'INS1002', 'ACTIVE', '2026-04-01 10:05:00', '2026-04-01 10:05:00', 0),
(3, 'MRN000003', 'Karan Johar', '1972-05-25', 'MALE', '{"phone":"9000000003","email":"karan.johar@email.com"}', '{"line1":"Film City Road","city":"Mumbai","state":"MH","zip":"400001"}', '9000000003', 'INS1003', 'ACTIVE', '2026-04-01 10:10:00', '2026-04-01 10:10:00', 0),
(4, 'MRN000004', 'Deepika Padukone', '1986-01-05', 'FEMALE', '{"phone":"9000000004","email":"deepika.p@email.com"}', '{"line1":"Prabhadevi Apt","city":"Mumbai","state":"MH","zip":"400025"}', '9000000004', 'INS1004', 'ACTIVE', '2026-04-01 10:15:00', '2026-04-01 10:15:00', 0),
(5, 'MRN000005', 'Amitabh Bachchan', '1942-10-11', 'MALE', '{"phone":"9000000005","email":"amitabh.b@email.com"}', '{"line1":"Jalsa Juhu","city":"Mumbai","state":"MH","zip":"400049"}', '9000000005', 'INS1005', 'ACTIVE', '2026-04-01 10:20:00', '2026-04-01 10:20:00', 0),
(6, 'MRN000006', 'Rohan Gupta', '1995-03-12', 'MALE', '{"phone":"9000000006","email":"rohan.gupta@email.com"}', '{"line1":"56 Outer Ring Road","city":"Bengaluru","state":"KA","zip":"560103"}', '9000000006', 'INS1006', 'ACTIVE', '2026-04-01 10:25:00', '2026-04-01 10:25:00', 0),
(7, 'MRN000007', 'Anjali Desai', '1990-09-08', 'FEMALE', '{"phone":"9000000007","email":"anjali.desai@email.com"}', '{"line1":"89 SG Road","city":"Ahmedabad","state":"GJ","zip":"380054"}', '9000000007', 'INS1007', 'ACTIVE', '2026-04-01 10:30:00', '2026-04-01 10:30:00', 0),
(8, 'MRN000008', 'Vijay Mallya', '1955-12-18', 'MALE', '{"phone":"9000000008","email":"vijay.mallya@email.com"}', '{"line1":"UB City Tower","city":"Bengaluru","state":"KA","zip":"560001"}', '9000000008', 'INS1008', 'ACTIVE', '2026-04-01 10:35:00', '2026-04-01 10:35:00', 0),
(9, 'MRN000009', 'Srinivas Murthy', '1968-07-22', 'MALE', '{"phone":"9000000009","email":"srinivas.m@email.com"}', '{"line1":"14 Jayanagar","city":"Bengaluru","state":"KA","zip":"560011"}', '9000000009', 'INS1009', 'ACTIVE', '2026-04-01 10:40:00', '2026-04-01 10:40:00', 0),
(10, 'MRN000010', 'Kavitha Ram', '1985-02-14', 'FEMALE', '{"phone":"9000000010","email":"kavitha.ram@email.com"}', '{"line1":"23 Mylapore","city":"Chennai","state":"TN","zip":"600004"}', '9000000010', 'INS1010', 'ACTIVE', '2026-04-01 10:45:00', '2026-04-01 10:45:00', 0);

INSERT IGNORE INTO medication_master (med_id, code, name, formulation, strength, atc_code, controlled_flag, status) VALUES
(1, 'MED001', 'Paracetamol', 'Tablet', '500mg', 'N02BE01', 0, 'ACTIVE'),
(2, 'MED002', 'Amoxicillin', 'Capsule', '250mg', 'J01CA04', 0, 'ACTIVE'),
(3, 'MED003', 'Ibuprofen', 'Tablet', '400mg', 'M01AE01', 0, 'ACTIVE'),
(4, 'MED004', 'Metformin', 'Tablet', '500mg', 'A10BA02', 0, 'ACTIVE'),
(5, 'MED005', 'Atorvastatin', 'Tablet', '10mg', 'C10AA05', 0, 'ACTIVE'),
(6, 'MED006', 'Amlodipine', 'Tablet', '5mg', 'C08CA01', 0, 'ACTIVE'),
(7, 'MED007', 'Omeprazole', 'Capsule', '20mg', 'A02BC01', 0, 'ACTIVE'),
(8, 'MED008', 'Azithromycin', 'Tablet', '500mg', 'J01FA10', 0, 'ACTIVE'),
(9, 'MED009', 'Pantoprazole', 'Tablet', '40mg', 'A02BC02', 0, 'ACTIVE'),
(10, 'MED010', 'Cetirizine', 'Tablet', '10mg', 'R06AE07', 0, 'ACTIVE');

INSERT IGNORE INTO inventory_item (inventory_id, med_id, batch_number, quantity, unit, expiry_date, location, cost_price, status) VALUES
(1, 1, 'BATCH001', 500, 'Tablet', '2028-12-31', 'Pharmacy Rack A', 1.50, 'IN_STOCK'),
(2, 2, 'BATCH002', 200, 'Capsule', '2027-06-30', 'Pharmacy Rack B', 3.00, 'IN_STOCK'),
(3, 3, 'BATCH003', 300, 'Tablet', '2028-04-15', 'Pharmacy Rack C', 2.00, 'IN_STOCK'),
(4, 4, 'BATCH004', 450, 'Tablet', '2028-09-22', 'Pharmacy Rack D', 1.20, 'IN_STOCK'),
(5, 5, 'BATCH005', 150, 'Tablet', '2027-11-30', 'Pharmacy Rack E', 4.50, 'IN_STOCK'),
(6, 6, 'BATCH006', 400, 'Tablet', '2028-02-28', 'Pharmacy Rack F', 1.80, 'IN_STOCK'),
(7, 7, 'BATCH007', 250, 'Capsule', '2027-08-31', 'Pharmacy Rack G', 2.50, 'IN_STOCK'),
(8, 8, 'BATCH008', 100, 'Tablet', '2027-05-15', 'Pharmacy Rack H', 6.00, 'IN_STOCK'),
(9, 9, 'BATCH009', 350, 'Tablet', '2028-10-10', 'Pharmacy Rack I', 2.80, 'IN_STOCK'),
(10, 10, 'BATCH010', 600, 'Tablet', '2029-01-20', 'Pharmacy Rack J', 0.90, 'IN_STOCK');

INSERT IGNORE INTO appointments (appt_id, patient_id, clinician_id, department, service_type, start_at, end_at, status, created_by, created_at) VALUES
(1, 1, 1, 'General Medicine', 'Consultation', '2026-04-10 09:00:00', '2026-04-10 09:30:00', 'COMPLETED', 2, '2026-04-09 16:00:00'),
(2, 2, 1, 'General Medicine', 'Follow Up', '2026-04-11 11:00:00', '2026-04-11 11:30:00', 'COMPLETED', 2, '2026-04-10 17:00:00'),
(3, 3, 3, 'Pediatrics', 'Consultation', '2026-04-12 10:00:00', '2026-04-12 10:30:00', 'SCHEDULED', 8, '2026-04-11 09:00:00'),
(4, 4, 3, 'Pediatrics', 'Routine Check', '2026-04-12 10:30:00', '2026-04-12 11:00:00', 'SCHEDULED', 8, '2026-04-11 09:10:00'),
(5, 5, 4, 'Cardiology', 'Consultation', '2026-04-13 14:00:00', '2026-04-13 14:30:00', 'SCHEDULED', 8, '2026-04-12 11:00:00'),
(6, 6, 4, 'Cardiology', 'Follow Up', '2026-04-13 14:30:00', '2026-04-13 15:00:00', 'SCHEDULED', 8, '2026-04-12 11:15:00'),
(7, 7, 5, 'Dermatology', 'Consultation', '2026-04-14 09:00:00', '2026-04-14 09:30:00', 'SCHEDULED', 8, '2026-04-13 10:00:00'),
(8, 8, 5, 'Dermatology', 'Routine Check', '2026-04-14 09:30:00', '2026-04-14 10:00:00', 'SCHEDULED', 8, '2026-04-13 10:15:00'),
(9, 9, 1, 'General Medicine', 'Consultation', '2026-04-15 11:00:00', '2026-04-15 11:30:00', 'SCHEDULED', 8, '2026-04-14 09:00:00'),
(10, 10, 3, 'Pediatrics', 'Follow Up', '2026-04-15 15:00:00', '2026-04-15 15:30:00', 'SCHEDULED', 8, '2026-04-14 09:30:00');

INSERT IGNORE INTO encounters (encounter_id, patient_id, clinician_id, visit_type, chief_complaint, vitals_json, notes_json, diagnoses_json, orders_json, start_at, end_at, status, signed_by, signed_at) VALUES
(1, 1, 1, 'OPD', 'Fever and body pain', '{"temp":"101F","bp":"110/70","pulse":"92"}', '{"soap":"Patient reports fever for 2 days"}', '["Viral fever"]', '["CBC"]', '2026-04-10 09:00:00', '2026-04-10 09:20:00', 'COMPLETED', 1, '2026-04-10 09:25:00'),
(2, 2, 1, 'FOLLOW_UP', 'Cough and sore throat', '{"temp":"99F","bp":"120/80","pulse":"88"}', '{"soap":"Dry cough for 5 days"}', '["Upper respiratory infection"]', '["XRay Chest"]', '2026-04-11 11:00:00', '2026-04-11 11:25:00', 'COMPLETED', 1, '2026-04-11 11:30:00'),
(3, 3, 3, 'OPD', 'Mild skin rash', '{"temp":"98.4F","bp":"115/75","pulse":"78"}', '{"soap":"Itching in forearm for 3 days"}', '["Contact Dermatitis"]', '[]', '2026-04-12 10:00:00', '2026-04-12 10:15:00', 'COMPLETED', 3, '2026-04-12 10:20:00'),
(4, 4, 3, 'OPD', 'High blood sugar check', '{"temp":"98.6F","bp":"130/85","pulse":"80"}', '{"soap":"Routine check for diabetic patient"}', '["Type 2 Diabetes"]', '["HbA1c"]', '2026-04-12 10:30:00', '2026-04-12 10:50:00', 'COMPLETED', 3, '2026-04-12 10:55:00'),
(5, 5, 4, 'OPD', 'Chest tightness', '{"temp":"98.2F","bp":"140/90","pulse":"88"}', '{"soap":"Occasional dyspnea on exertion"}', '["Essential Hypertension"]', '["Lipid Profile"]', '2026-04-13 14:00:00', '2026-04-13 14:25:00', 'COMPLETED', 4, '2026-04-13 14:30:00'),
(6, 6, 4, 'FOLLOW_UP', 'Ankle swelling', '{"temp":"98.4F","bp":"120/80","pulse":"76"}', '{"soap":"Ankle edema resolved partially"}', '["Edema"]', '["LFT"]', '2026-04-13 14:30:00', '2026-04-13 14:55:00', 'COMPLETED', 4, '2026-04-13 15:00:00'),
(7, 7, 5, 'OPD', 'Acne outbreak', '{"temp":"98.6F","bp":"110/70","pulse":"72"}', '{"soap":"Acne vulgaris on cheeks"}', '["Acne"]', '[]', '2026-04-14 09:00:00', '2026-04-14 09:20:00', 'COMPLETED', 5, '2026-04-14 09:25:00'),
(8, 8, 5, 'OPD', 'Scalp itching', '{"temp":"98.5F","bp":"118/74","pulse":"75"}', '{"soap":"Severe dandruff and flaking"}', '["Seborrheic Dermatitis"]', '[]', '2026-04-14 09:30:00', '2026-04-14 09:50:00', 'COMPLETED', 5, '2026-04-14 09:55:00'),
(9, 9, 1, 'OPD', 'Stomach ache', '{"temp":"98.8F","bp":"122/78","pulse":"84"}', '{"soap":"Epigastric pain after meals"}', '["Acidity/Gastritis"]', '[]', '2026-04-15 11:00:00', '2026-04-15 11:20:00', 'COMPLETED', 1, '2026-04-15 11:25:00'),
(10, 10, 3, 'OPD', 'Sneezing and runny nose', '{"temp":"99.0F","bp":"112/72","pulse":"82"}', '{"soap":"Allergic rhinitis symptoms"}', '["Allergic Rhinitis"]', '[]', '2026-04-15 15:00:00', '2026-04-15 15:20:00', 'COMPLETED', 3, '2026-04-15 15:25:00');

INSERT IGNORE INTO prescriptions (rx_id, encounter_id, patient_id, clinician_id, med_id, dosage, frequency, duration_days, quantity, repeats, route, notes, status, issued_at) VALUES
(1, 1, 1, 1, 1, '1 tablet', 'TID', 5, 15, 0, 'ORAL', 'After food', 'ISSUED', '2026-04-10 09:15:00'),
(2, 2, 2, 1, 2, '1 capsule', 'BID', 7, 14, 0, 'ORAL', 'Complete the course', 'ISSUED', '2026-04-11 11:15:00'),
(3, 3, 3, 3, 10, '1 tablet', 'OD', 10, 10, 1, 'ORAL', 'At bedtime', 'ISSUED', '2026-04-12 10:10:00'),
(4, 4, 4, 3, 4, '1 tablet', 'BID', 30, 60, 2, 'ORAL', 'With meals', 'ISSUED', '2026-04-12 10:45:00'),
(5, 5, 5, 4, 5, '1 tablet', 'OD', 30, 30, 3, 'ORAL', 'Evening', 'ISSUED', '2026-04-13 14:20:00'),
(6, 6, 6, 4, 6, '1 tablet', 'OD', 30, 30, 3, 'ORAL', 'Morning', 'ISSUED', '2026-04-13 14:50:00'),
(7, 7, 7, 5, 7, '1 capsule', 'OD', 14, 14, 0, 'ORAL', 'Empty stomach', 'ISSUED', '2026-04-14 09:15:00'),
(8, 8, 8, 5, 3, '1 tablet', 'BID', 5, 10, 0, 'ORAL', 'After meals', 'ISSUED', '2026-04-14 09:45:00'),
(9, 9, 9, 1, 9, '1 tablet', 'OD', 14, 14, 0, 'ORAL', 'Morning empty stomach', 'ISSUED', '2026-04-15 11:15:00'),
(10, 10, 10, 3, 10, '1 tablet', 'OD', 5, 5, 0, 'ORAL', 'Bedtime', 'ISSUED', '2026-04-15 15:15:00');

INSERT IGNORE INTO dispense_record (dispense_id, rx_id, inventory_id, patient_id, dispensed_by_fk, quantity, dispensed_at, notes, status) VALUES
(1, 1, 1, 1, 7, 15, '2026-04-10 10:00:00', 'Dispensed standard dosage', 'DISPENSED'),
(2, 2, 2, 2, 7, 14, '2026-04-11 12:00:00', 'Dispensed complete course', 'DISPENSED'),
(3, 3, 10, 3, 7, 10, '2026-04-12 11:00:00', 'Dispensed allergy medicine', 'DISPENSED'),
(4, 4, 4, 4, 7, 60, '2026-04-12 12:00:00', 'Dispensed 1 month supply', 'DISPENSED'),
(5, 5, 5, 5, 7, 30, '2026-04-13 15:30:00', 'Dispensed cholesterol medicine', 'DISPENSED'),
(6, 6, 6, 6, 7, 30, '2026-04-13 16:00:00', 'Dispensed hypertension medicine', 'DISPENSED'),
(7, 7, 7, 7, 7, 14, '2026-04-14 10:30:00', 'Dispensed antacid pills', 'DISPENSED'),
(8, 8, 3, 8, 7, 10, '2026-04-14 11:00:00', 'Dispensed painkillers', 'DISPENSED'),
(9, 9, 9, 9, 7, 14, '2026-04-15 12:00:00', 'Dispensed stomach medicine', 'DISPENSED'),
(10, 10, 10, 10, 7, 5, '2026-04-15 16:00:00', 'Dispensed antihistamines', 'DISPENSED');

INSERT IGNORE INTO lab_order (lab_order_id, encounter_id, patient_id, ordered_by_fk, tests_json, sample_id, collected_at, status, result_uri) VALUES
(1, 1, 1, 1, '["CBC","CRP"]', 'SMP1001', '2026-04-10 09:35:00', 'RESULTS_REPORTED', '/lab/orders/1/results'),
(2, 2, 2, 1, '["Chest X-Ray"]', 'SMP1002', '2026-04-11 11:40:00', 'RESULTS_REPORTED', '/lab/orders/2/results'),
(3, 4, 4, 3, '["HbA1c"]', 'SMP1003', '2026-04-12 11:15:00', 'RESULTS_REPORTED', '/lab/orders/3/results'),
(4, 5, 5, 4, '["Lipid Profile"]', 'SMP1004', '2026-04-13 14:45:00', 'RESULTS_REPORTED', '/lab/orders/4/results'),
(5, 6, 6, 4, '["LFT"]', 'SMP1005', '2026-04-13 15:15:00', 'RESULTS_REPORTED', '/lab/orders/5/results'),
(6, 1, 1, 1, '["Kidney Function Test"]', 'SMP1006', '2026-04-10 10:15:00', 'RESULTS_REPORTED', '/lab/orders/6/results'),
(7, 2, 2, 1, '["Thyroid Panel"]', 'SMP1007', '2026-04-11 12:30:00', 'RESULTS_REPORTED', '/lab/orders/7/results'),
(8, 4, 4, 3, '["Urine Routine"]', 'SMP1008', '2026-04-12 11:45:00', 'RESULTS_REPORTED', '/lab/orders/8/results'),
(9, 5, 5, 4, '["Complete Blood Count"]', 'SMP1009', '2026-04-13 15:30:00', 'RESULTS_REPORTED', '/lab/orders/9/results'),
(10, 6, 6, 4, '["Lipid Panel"]', 'SMP1010', '2026-04-13 16:00:00', 'RESULTS_REPORTED', '/lab/orders/10/results');

INSERT IGNORE INTO lab_result (result_id, lab_order_id, test_code, value, units, reference_range_json, flag, reported_at, reported_by) VALUES
(1, 1, 'CBC-WBC', '8600', 'cells/uL', '{"min":"4000","max":"11000"}', 'NORMAL', '2026-04-10 14:00:00', 6),
(2, 2, 'XR-CHEST', 'Mild bronchitic changes', 'TEXT', '{"type":"narrative"}', 'NORMAL', '2026-04-11 16:30:00', 6),
(3, 3, 'HBA1C', '6.2', '%', '{"min":"4.0","max":"5.6"}', 'HIGH', '2026-04-12 15:00:00', 6),
(4, 4, 'LIPID-CHOL', '245', 'mg/dL', '{"min":"100","max":"200"}', 'HIGH', '2026-04-13 17:00:00', 6),
(5, 5, 'LFT-ALT', '38', 'U/L', '{"min":"7","max":"56"}', 'NORMAL', '2026-04-13 18:00:00', 6),
(6, 6, 'KFT-CREAT', '1.1', 'mg/dL', '{"min":"0.6","max":"1.2"}', 'NORMAL', '2026-04-10 15:00:00', 6),
(7, 7, 'TSH', '2.5', 'uIU/mL', '{"min":"0.4","max":"4.0"}', 'NORMAL', '2026-04-11 17:30:00', 6),
(8, 8, 'URINE-GLU', 'Negative', 'TEXT', '{"type":"narrative"}', 'NORMAL', '2026-04-12 16:00:00', 6),
(9, 9, 'CBC-HB', '13.5', 'g/dL', '{"min":"12.0","max":"16.0"}', 'NORMAL', '2026-04-13 18:30:00', 6),
(10, 10, 'CHOL-LDL', '165', 'mg/dL', '{"min":"50","max":"100"}', 'HIGH', '2026-04-13 19:00:00', 6);

INSERT IGNORE INTO invoice (invoice_id, patient_id, encounter_id, line_items_json, subtotal, taxes, discounts, total_amount, issued_at, due_date, status) VALUES
(1, 1, 1, '[{"code":"SRV001","amount":500},{"code":"SRV002","amount":350}]', 850.00, 45.00, 50.00, 845.00, '2026-04-10 10:00:00', '2026-04-15 00:00:00', 'PARTIALLY_PAID'),
(2, 2, 2, '[{"code":"SRV001","amount":500}]', 500.00, 25.00, 0.00, 525.00, '2026-04-11 12:00:00', '2026-04-16 00:00:00', 'PAID'),
(3, 3, 3, '[{"code":"SRV001","amount":500},{"code":"SRV003","amount":150}]', 650.00, 32.50, 0.00, 682.50, '2026-04-12 11:00:00', '2026-04-17 00:00:00', 'UNPAID'),
(4, 4, 4, '[{"code":"SRV001","amount":500},{"code":"SRV004","amount":400}]', 900.00, 45.00, 50.00, 895.00, '2026-04-12 12:00:00', '2026-04-17 00:00:00', 'UNPAID'),
(5, 5, 5, '[{"code":"SRV001","amount":500},{"code":"SRV005","amount":600}]', 1100.00, 55.00, 100.00, 1055.00, '2026-04-13 15:30:00', '2026-04-18 00:00:00', 'UNPAID'),
(6, 6, 6, '[{"code":"SRV001","amount":500}]', 500.00, 25.00, 0.00, 525.00, '2026-04-13 16:00:00', '2026-04-18 00:00:00', 'UNPAID'),
(7, 7, 7, '[{"code":"SRV001","amount":500},{"code":"SRV006","amount":250}]', 750.00, 37.50, 0.00, 787.50, '2026-04-14 10:30:00', '2026-04-19 00:00:00', 'UNPAID'),
(8, 8, 8, '[{"code":"SRV001","amount":500}]', 500.00, 25.00, 0.00, 525.00, '2026-04-14 11:00:00', '2026-04-19 00:00:00', 'UNPAID'),
(9, 9, 9, '[{"code":"SRV001","amount":500},{"code":"SRV007","amount":300}]', 800.00, 40.00, 50.00, 790.00, '2026-04-15 12:00:00', '2026-04-20 00:00:00', 'UNPAID'),
(10, 10, 10, '[{"code":"SRV001","amount":500}]', 500.00, 25.00, 0.00, 525.00, '2026-04-15 16:00:00', '2026-04-20 00:00:00', 'UNPAID');

INSERT IGNORE INTO payment (payment_id, invoice_id, patient_id, amount, method, paid_at, status) VALUES
(1, 1, 1, 400.00, 'CARD', '2026-04-10 10:30:00', 'SUCCESS'),
(2, 2, 2, 525.00, 'UPI', '2026-04-11 12:15:00', 'SUCCESS'),
(3, 3, 3, 682.50, 'CASH', '2026-04-12 12:30:00', 'SUCCESS'),
(4, 4, 4, 895.00, 'CARD', '2026-04-12 13:00:00', 'SUCCESS'),
(5, 5, 5, 1055.00, 'UPI', '2026-04-13 16:30:00', 'SUCCESS'),
(6, 6, 6, 525.00, 'CARD', '2026-04-13 17:00:00', 'SUCCESS'),
(7, 7, 7, 787.50, 'CASH', '2026-04-14 11:30:00', 'SUCCESS'),
(8, 8, 8, 525.00, 'UPI', '2026-04-14 12:00:00', 'SUCCESS'),
(9, 9, 9, 790.00, 'CARD', '2026-04-15 13:00:00', 'SUCCESS'),
(10, 10, 10, 525.00, 'UPI', '2026-04-15 17:00:00', 'SUCCESS');

INSERT IGNORE INTO problem_list (problem_id, patient_id, code, description, onset_date, status, created_at) VALUES
(1, 1, 'J11', 'Viral fever', '2026-04-08', 'ACTIVE', '2026-04-10 09:15:00'),
(2, 2, 'J06.9', 'Upper respiratory infection', '2026-04-06', 'ACTIVE', '2026-04-11 11:15:00'),
(3, 3, 'L23.9', 'Contact Dermatitis', '2026-04-10', 'ACTIVE', '2026-04-12 10:10:00'),
(4, 4, 'E11.9', 'Type 2 Diabetes', '2022-05-15', 'ACTIVE', '2026-04-12 10:45:00'),
(5, 5, 'I10', 'Essential Hypertension', '2021-08-10', 'ACTIVE', '2026-04-13 14:20:00'),
(6, 6, 'R60.9', 'Edema', '2026-04-01', 'ACTIVE', '2026-04-13 14:50:00'),
(7, 7, 'L70.0', 'Acne vulgaris', '2025-12-10', 'ACTIVE', '2026-04-14 09:15:00'),
(8, 8, 'L21.9', 'Seborrheic Dermatitis', '2026-03-25', 'ACTIVE', '2026-04-14 09:45:00'),
(9, 9, 'K29.7', 'Gastritis', '2026-04-12', 'ACTIVE', '2026-04-15 11:15:00'),
(10, 10, 'J30.9', 'Allergic Rhinitis', '2024-03-10', 'ACTIVE', '2026-04-15 15:15:00');

INSERT IGNORE INTO report (report_id, scope, parameters_json, metrics_json, generated_by_fk, generated_at, report_uri) VALUES
(1, 'CLINIC', '{"startDate":"2026-03-01","endDate":"2026-03-15"}', '{"totalEncounters":42}', 2, '2026-03-16 08:00:00', '/reports/clinic-2026-03a.pdf'),
(2, 'FINANCE', '{"startDate":"2026-03-16","endDate":"2026-03-31"}', '{"totalRevenue":125000}', 2, '2026-04-01 08:00:00', '/reports/finance-2026-03b.pdf'),
(3, 'CLINIC', '{"startDate":"2026-03-16","endDate":"2026-03-31"}', '{"totalEncounters":56}', 2, '2026-04-01 08:30:00', '/reports/clinic-2026-03b.pdf'),
(4, 'FINANCE', '{"startDate":"2026-04-01","endDate":"2026-04-15"}', '{"totalRevenue":142000}', 2, '2026-04-16 08:00:00', '/reports/finance-2026-04a.pdf'),
(5, 'CLINIC', '{"startDate":"2026-04-01","endDate":"2026-04-15"}', '{"totalEncounters":68}', 2, '2026-04-16 08:30:00', '/reports/clinic-2026-04a.pdf'),
(6, 'FINANCE', '{"startDate":"2026-04-16","endDate":"2026-04-30"}', '{"totalRevenue":160000}', 2, '2026-05-01 08:00:00', '/reports/finance-2026-04b.pdf'),
(7, 'CLINIC', '{"startDate":"2026-04-16","endDate":"2026-04-30"}', '{"totalEncounters":72}', 2, '2026-05-01 08:30:00', '/reports/clinic-2026-04b.pdf'),
(8, 'FINANCE', '{"startDate":"2026-05-01","endDate":"2026-05-15"}', '{"totalRevenue":185000}', 2, '2026-05-16 08:00:00', '/reports/finance-2026-05a.pdf'),
(9, 'CLINIC', '{"startDate":"2026-05-01","endDate":"2026-05-15"}', '{"totalEncounters":88}', 2, '2026-05-16 08:30:00', '/reports/clinic-2026-05a.pdf'),
(10, 'FINANCE', '{"startDate":"2026-05-16","endDate":"2026-05-31"}', '{"totalRevenue":210000}', 2, '2026-06-01 08:00:00', '/reports/finance-2026-05b.pdf');

INSERT IGNORE INTO task (task_id, assigned_to_fk, related_entity_id, description, due_date, priority, created_at, completed_at, status) VALUES
(1, 1, 'patient-1', 'Follow up on lab result for Priya Nair', '2026-04-12 18:00:00', 'HIGH', '2026-04-10 14:05:00', NULL, 'PENDING'),
(2, 2, 'invoice-2', 'Verify insurance claim for Arjun Verma', '2026-04-15 17:00:00', 'MEDIUM', '2026-04-11 12:30:00', '2026-04-12 10:00:00', 'COMPLETED'),
(3, 3, 'patient-3', 'Review skin patch test results', '2026-04-15 12:00:00', 'MEDIUM', '2026-04-12 10:15:00', NULL, 'PENDING'),
(4, 4, 'patient-5', 'Schedule cardiac rehab session', '2026-04-18 15:00:00', 'HIGH', '2026-04-13 14:30:00', NULL, 'PENDING'),
(5, 5, 'patient-7', 'Check patient tolerance to acne cream', '2026-04-20 10:00:00', 'LOW', '2026-04-14 09:25:00', NULL, 'PENDING'),
(6, 6, 'lab-order-5', 'Process LFT tests sample SMP1005', '2026-04-14 18:00:00', 'HIGH', '2026-04-13 15:15:00', '2026-04-13 18:00:00', 'COMPLETED'),
(7, 7, 'prescription-7', 'Dispense Omeprazole stock verify', '2026-04-15 11:00:00', 'MEDIUM', '2026-04-14 09:15:00', '2026-04-14 10:30:00', 'COMPLETED'),
(8, 8, 'appointment-9', 'Reschedule conflicting slot', '2026-04-16 17:00:00', 'LOW', '2026-04-15 09:00:00', NULL, 'PENDING'),
(9, 9, 'inventory-8', 'Reorder Azithromycin stock', '2026-04-18 12:00:00', 'HIGH', '2026-04-15 12:00:00', NULL, 'PENDING'),
(10, 10, 'invoice-9', 'Verify billing code correction', '2026-04-20 17:00:00', 'MEDIUM', '2026-04-15 13:00:00', NULL, 'PENDING');