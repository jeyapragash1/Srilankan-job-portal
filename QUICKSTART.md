# 🚀 Quick Start Guide

## For Developers - 5 Minute Setup

### 1️⃣ Prerequisites Check
```bash
java -version    # Should be 8+
mvn -version     # Should be 3.6+
mysql --version  # Should be 8.0+
```

### 2️⃣ Database Setup (2 minutes)
```bash
# Login to MySQL
mysql -u root -p

# Create database and import
CREATE DATABASE lk_job;
USE lk_job;
source database/sl_jobportal.sql
source database/migration_v2.0.sql
exit;
```

### 3️⃣ Configure (1 minute)
Edit `src/main/resources/application.properties`:
```properties
db.username=root
db.password=YOUR_PASSWORD_HERE
email.enabled=false  # Keep false for testing
```

### 4️⃣ Build & Run (2 minutes)
```bash
# Build
mvn clean package

# Deploy to Tomcat
cp target/job-portal.war $TOMCAT_HOME/webapps/

# Start Tomcat
$TOMCAT_HOME/bin/startup.sh  # or startup.bat on Windows
```

### 5️⃣ Access
Open: http://localhost:8080/job-portal/

**Login as Admin:**
- Email: admin1@jobportal.com
- Password: adminpass1

---

## Common Development Tasks

### Run Tests
```bash
mvn test
```

### Clean Build
```bash
mvn clean install
```

### View Logs
```bash
tail -f logs/job-portal.log
```

### Database Reset
```bash
mysql -u root -p lk_job < database/sl_jobportal.sql
```

---

## Troubleshooting Quick Fixes

**Build fails?**
```bash
mvn clean install -U
```

**Can't connect to database?**
- Check MySQL is running: `systemctl status mysql`
- Verify credentials in application.properties

**Port 8080 in use?**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

**File upload doesn't work?**
- Create directory: `mkdir -p uploads/resumes`
- Check permissions: `chmod 755 uploads`

---

## Development Workflow

1. **Create feature branch**
   ```bash
   git checkout -b feature/my-feature
   ```

2. **Make changes & test**
   ```bash
   mvn test
   ```

3. **Commit with meaningful message**
   ```bash
   git add .
   git commit -m "Add feature: description"
   ```

4. **Push and create PR**
   ```bash
   git push origin feature/my-feature
   ```

---

## Useful Maven Commands

```bash
# Compile only
mvn compile

# Run specific test
mvn test -Dtest=PasswordUtilTest

# Skip tests
mvn clean install -DskipTests

# Generate JavaDoc
mvn javadoc:javadoc

# Dependency tree
mvn dependency:tree

# Update dependencies
mvn versions:display-dependency-updates
```

---

## IDE Setup

### IntelliJ IDEA
1. File → Open → Select pom.xml
2. Import as Maven project
3. Configure Tomcat: Run → Edit Configurations → + → Tomcat Server

### Eclipse
1. File → Import → Maven → Existing Maven Projects
2. Select project folder
3. Window → Preferences → Server → Runtime Environments → Add Tomcat

### VS Code
1. Install extensions:
   - Java Extension Pack
   - Tomcat for Java
2. Open folder
3. Configure Tomcat in settings

---

## Production Deployment Checklist

- [ ] Change all default passwords
- [ ] Update application.properties with production values
- [ ] Enable HTTPS (set `secure=true` in web.xml)
- [ ] Configure email settings
- [ ] Set up database backups
- [ ] Enable application logging
- [ ] Configure firewall rules
- [ ] Set up monitoring
- [ ] Test all features
- [ ] Review security settings

---

**Need help?** Check the full [README.md](README.md) or create an issue!
