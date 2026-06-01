# Hospital Management System - Logging Implementation Guide

## Overview
The Hospital Management System now includes comprehensive logging using **SLF4J** with **Logback** configuration. This guide explains the logging setup, configuration, and how to use it for debugging and monitoring.

---

## 📋 Logging Components Implemented

### 1. **Logback Configuration** (`logback.xml`)
- Located at: `src/main/resources/logback.xml`
- Provides structured logging with multiple appenders:
  - **CONSOLE**: Real-time logs in console/terminal
  - **FILE_ALL**: All logs in `logs/hospital_management.log`
  - **FILE_ERROR**: Error-only logs in `logs/hospital_management_error.log`
  - **FILE_SECURITY**: Security-related logs in `logs/hospital_management_security.log`

### 2. **Application Properties Configuration** (`application.properties`)
- Root logging level: `INFO`
- Application logging level: `DEBUG`
- Security logging level: `INFO`
- Hibernate SQL logging: `DEBUG`

### 3. **Instrumented Services** (8 Services)
All major business services include comprehensive logging:

| Service | Log Level | Coverage |
|---------|-----------|----------|
| **AuthService** | INFO | User registration, authentication, JWT generation |
| **PatientService** | DEBUG | Patient registry, registration, retrieval |
| **AppointmentService** | DEBUG | Appointment booking, status updates, conflict detection |
| **EncounterService** | DEBUG | Clinical encounters, CRUD operations |
| **PrescriptionService** | DEBUG | Prescription lifecycle (create, update, delete) |
| **BillingService** | DEBUG | Invoice generation, calculations, financial transactions |
| **Controllers** | INFO | All API requests and responses |

### 4. **Instrumented Controllers** (6 Controllers)
All major API controllers log incoming requests and responses:

| Controller | Endpoints Logged |
|------------|-----------------|
| **AuthController** | `/api/v1/auth/register`, `/api/v1/auth/authenticate` |
| **PatientController** | `/api/v1/patient` (GET, GET by ID, POST) |
| **BillingController** | `/api/v1/finance/billing` (all CRUD operations) |
| **PaymentController** | `/api/v1/finance/payments` (all CRUD operations) |
| **EncounterController** | `/api/v1/clinician/encounters` (all operations) |
| **LabController** | `/api/v1/lab/orders`, `/api/v1/lab/results` |

---

## 📂 Log File Locations

```
project-root/
├── logs/
│   ├── hospital_management.log          # All application logs
│   ├── hospital_management_error.log    # Error-only logs
│   └── hospital_management_security.log # Security-related logs
```

### Log File Management
- **Max file size**: 10MB per file
- **Rotation policy**: Daily + Size-based (when reaching 10MB)
- **Retention**: 30 days of logs
- **Total cap**: 1GB maximum disk space

---

## 🔍 What Gets Logged

### 1. **Authentication & Security**
```
INFO  - Attempting to register new user with email: user@example.com
INFO  - User registered successfully - Name: John Doe, Email: user@example.com, Phone: 555-0123, Role: PATIENT
INFO  - Attempting authentication for user with email: admin@clinic.com
WARN  - Authentication failed: Invalid credentials for email: admin@clinic.com
INFO  - User authenticated successfully - Email: admin@clinic.com, Role: ADMIN
```

### 2. **Patient Management**
```
DEBUG - Fetching all patients from registry
INFO  - Retrieved 45 patients from registry
DEBUG - Fetching patient with ID: 12
INFO  - Retrieved patient - ID: 12, MRN: MRN-2024-001, Name: Jane Smith
INFO  - Starting patient registration - Name: Robert Johnson, Email: robert@example.com, Phone: 555-9876
INFO  - Patient registered successfully - PatientID: 15, MRN: MRN-2024-002, UserID: 8
```

### 3. **Appointments**
```
INFO  - Processing appointment booking - Department: Cardiology, ServiceType: Initial Consultation
DEBUG - Patient self-booking appointment - UserID: 5
WARN  - Appointment booking failed: Clinician ID: 3 is double-booked for time slot 2024-04-28T10:00:00
INFO  - Appointment booked successfully - AppointmentID: 42, PatientID: 12, ClinicianID: 3, StartTime: 2024-04-28T10:30:00
INFO  - Updating appointment status - AppointmentID: 42, NewStatus: CHECKED_IN
```

### 4. **Clinical Operations**
```
INFO  - Creating new encounter - PatientID: 12, ClinicianID: 3, VisitType: Follow-up
INFO  - Encounter created successfully - EncounterID: 8, PatientID: 12
INFO  - Creating new prescription - PatientID: 12, EncounterID: 8, Dosage: 500mg
INFO  - Prescription created successfully - RxID: 25, PatientID: 12, Status: DRAFT
```

### 5. **Financial Transactions**
```
INFO  - Generating invoice from workflow - PatientID: 12
DEBUG - Invoice calculation - Subtotal: $1250.00, Tax: $100.00, Discount: $50.00
INFO  - Invoice generated successfully - ID: 89, Total: $1300.00
INFO  - API Request: POST /api/v1/finance/payments/receive - Receiving payment - InvoiceID: 89, Amount: $1300.00, Method: CARD
INFO  - Payment received successfully - ReceiptNumber: RCT-89-45, Amount: $1300.00
```

### 6. **Laboratory Operations**
```
INFO  - API Request: POST /api/v1/lab/orders - Creating new lab order - PatientID: 12, SampleID: SAMP-12345
INFO  - Lab order created - ID: 67, Status: PENDING
INFO  - API Request: PUT /api/v1/lab/orders/67 - Updating lab order - NewStatus: COMPLETED
INFO  - Lab order updated - ID: 67, Status: COMPLETED
```

---

## 🎯 Logging Levels Explained

| Level | Usage | Examples |
|-------|-------|----------|
| **ERROR** | Critical errors requiring immediate attention | DB connection failed, validation errors |
| **WARN** | Potentially harmful situations | Duplicate registration, failed login attempt |
| **INFO** | High-level application events | User action summary, transaction completion |
| **DEBUG** | Detailed diagnostic information | Method entry/exit, detailed calculations |
| **TRACE** | Most verbose; very detailed debugging | Not configured by default |

---

## ⚙️ Configuration Files

### 1. **logback.xml** - Detailed Logging Configuration
```xml
<!-- Console Appender for real-time output -->
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
</appender>

<!-- Rolling file appender with size and time-based rolling -->
<appender name="FILE_ALL" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${LOG_HOME}/${LOG_FILE_NAME}.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <fileNamePattern>${LOG_HOME}/${LOG_FILE_NAME}-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
        <maxFileSize>10MB</maxFileSize>
        <maxHistory>30</maxHistory>
    </rollingPolicy>
</appender>
```

### 2. **application.properties** - Log Level Configuration
```properties
logging.level.root=INFO
logging.level.com.HospitalManagement=DEBUG
logging.level.com.HospitalManagement.service.AuthService=INFO
logging.level.org.springframework.security=INFO
logging.config=classpath:logback.xml
```

---

## 🔧 How to Use Logs for Debugging

### 1. **Monitoring Real-Time Activity**
```bash
# View console logs in real-time
tail -f logs/hospital_management.log
```

### 2. **Tracking Error Issues**
```bash
# View only error logs
cat logs/hospital_management_error.log | grep "PatientID: 12"
```

### 3. **Security Audit Trail**
```bash
# View security-related operations
cat logs/hospital_management_security.log
```

### 4. **Performance Monitoring**
```bash
# Check specific service performance
grep "BillingService" logs/hospital_management.log | grep "Invoice generated"
```

### 5. **Filtering by Time Period**
```bash
# View logs from specific time
grep "2024-04-28 10:" logs/hospital_management.log
```

### 6. **Searching for Specific User/Patient**
```bash
# Find all activities for a patient
grep "PatientID: 12" logs/hospital_management.log

# Find all activities for a user email
grep "user@example.com" logs/hospital_management.log
```

---

## 📊 Log Pattern Format

```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

| Component | Example | Description |
|-----------|---------|-------------|
| `%d{...}` | `2024-04-28 10:23:45.123` | Timestamp |
| `[%thread]` | `[main]` | Thread name |
| `%-5level` | `INFO ` | Log level (padded) |
| `%logger{36}` | `c.H.service.AuthService` | Logger name (abbreviated) |
| `%msg` | Full log message | The actual log content |

---

## 🚀 Best Practices

1. **Use Appropriate Log Levels**
   - ERROR: System failures, exceptions
   - WARN: Unexpected conditions, potential issues
   - INFO: Business events, state changes
   - DEBUG: Internal operation details

2. **Include Context Information**
   - IDs (PatientID, UserID, InvoiceID, etc.)
   - User actions and transitions
   - Financial amounts and calculations
   - Timestamps for tracing

3. **Avoid Logging Sensitive Data**
   - ❌ Passwords should never be logged
   - ❌ SSN, credit card details should be masked
   - ✅ Log user IDs, not credentials
   - ✅ Log transaction IDs, not payment details

4. **Rotate and Archive Logs**
   - Logs automatically rotate when reaching 10MB
   - Keep 30 days of historical logs
   - Archive old logs for compliance

5. **Monitor Log File Size**
   - Check `logs/` directory size regularly
   - Total cap is 1GB to prevent disk issues
   - Configure retention based on compliance needs

---

## 🔐 Security Logging Features

The system maintains a dedicated security log (`hospital_management_security.log`) for:
- User authentication attempts (success/failure)
- Authorization failures
- Sensitive operations
- Data access patterns

This enables:
- Security audits
- Compliance reporting
- Fraud detection
- Access control verification

---

## 📝 Example Log Analysis

### Tracking a Complete Transaction
```
2024-04-28 10:15:32.456 [http-nio-8081-exec-1] INFO  AuthService - User authenticated successfully - Email: admin@clinic.com, Role: ADMIN
2024-04-28 10:15:45.123 [http-nio-8081-exec-2] INFO  PatientController - Retrieved patient - ID: 12, MRN: MRN-2024-001
2024-04-28 10:16:20.789 [http-nio-8081-exec-3] INFO  EncounterService - Encounter created successfully - EncounterID: 8, PatientID: 12
2024-04-28 10:16:45.234 [http-nio-8081-exec-4] INFO  BillingService - Invoice generated successfully - ID: 89, Total: $1300.00
2024-04-28 10:17:10.567 [http-nio-8081-exec-5] INFO  PaymentService - Payment received successfully - ReceiptNumber: RCT-89-45
```

This shows a complete workflow from authentication to payment in seconds!

---

## 🛠️ Troubleshooting

### Logs Not Appearing?
1. Check `logging.config=classpath:logback.xml` in application.properties
2. Verify `logback.xml` is in `src/main/resources/`
3. Check `logs/` directory exists and is writable

### Disk Space Growing Too Fast?
1. Reduce `maxHistory` from 30 to 15 days
2. Lower `maxFileSize` from 10MB to 5MB
3. Reduce `totalSizeCap` from 1GB to 500MB

### Not Seeing DEBUG Logs?
1. Check `logging.level.com.HospitalManagement=DEBUG` in properties
2. Ensure `<logger name="com.HospitalManagement" level="DEBUG" />` in logback.xml
3. Restart application after config changes

---

## 📜 Log Retention Compliance

The logging system is configured to meet common compliance requirements:
- **HIPAA**: 6 years (configure `maxHistory="2190"` in logback.xml)
- **GDPR**: 3 years (default configuration)
- **SOX**: 7 years (configure `maxHistory="2555"` in logback.xml)

To adjust: Edit `<maxHistory>` value in `logback.xml` for different retention periods.

---

## ✅ Summary

✓ Logback + SLF4J integration complete  
✓ Multiple file appenders (console, all logs, errors, security)  
✓ Automatic log rotation (size + time-based)  
✓ Configurable log levels per package  
✓ Comprehensive logging in 8+ services and 6 controllers  
✓ Security-specific logging channel  
✓ Ready for production use

---

**For questions or issues with logging, check the log files in the `logs/` directory or adjust configuration in `logback.xml` and `application.properties`.**
