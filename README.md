# Library Management and Digital Lending System

Java 17 + Jakarta Servlet/JSP + MySQL academic demo. Includes member CRUD, book catalogue, checkout/return/renewal, reservations, overdue fines, digital licences, and staff administration.

## Requirements
- JDK 17+
- Apache Maven 3.9+
- MySQL 8+
- Apache Tomcat 10.1+ (Tomcat 10.1 uses Jakarta Servlet 6)
- Internet access on first build to download Maven dependencies

## 1. Create the database
1. Start MySQL.
2. Run `database.sql` in MySQL Workbench, or:
   `mysql -u root -p < database.sql`
3. By default, the app connects to `jdbc:mysql://localhost:3306/library_db`, user `root`, empty password. Set environment variables if your local setup differs:
   - `LIBRARY_DB_URL`
   - `LIBRARY_DB_USER`
   - `LIBRARY_DB_PASSWORD`

Windows PowerShell example for the current terminal:
```powershell
$env:LIBRARY_DB_USER="root"
$env:LIBRARY_DB_PASSWORD="your_mysql_password"
$env:LIBRARY_DB_URL="jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
```

## 2. Create the first admin account
The app stores BCrypt password hashes. Do not store a plaintext password in the database. Build the project, then generate a hash with this one-time helper:
```powershell
mvn -q dependency:copy-dependencies
jshell --class-path "target/dependency/jbcrypt-0.4.jar"
```
At the jshell prompt:
```java
import org.mindrot.jbcrypt.BCrypt;
BCrypt.hashpw("ChangeThisPassword123!", BCrypt.gensalt(12));
/exit
```
Copy the printed hash, then run in MySQL Workbench:
```sql
USE library_db;
INSERT INTO admins(username,password_hash,role)
VALUES ('admin', 'PASTE_BCRYPT_HASH_HERE', 'ADMIN');
```
Use username `admin` and the password you chose to sign in. Change the example password to your own strong password.

## 3. Build and deploy
From the project root:
```bash
mvn clean package
```
This produces `target/library.war`.
- Copy `target/library.war` into Tomcat's `webapps/` folder.
- Start Tomcat (`bin\startup.bat` on Windows).
- Open `http://localhost:8080/library/login`.

If Tomcat is using a different port, use that port in the URL.

## Features
- Dashboard counts for members, titles, available copies, active loans, reservations, unpaid fines.
- Members: create/list/delete; email uniqueness enforced by database.
- Books: create/list/delete; copy availability maintained by checkout/return.
- Loans: 14-day checkout, renew for 14 days if not overdue, return.
- Fines: ₹5 per overdue day, created when a late loan is returned; staff can mark paid (no real payment is processed).
- Reservations: create and cancel; shown oldest first. This demo records the queue but does not automatically fulfil reservations.
- Digital licences: register a format, URL, concurrent limit, active count; this demo stores metadata and links, it does not host digital content or enforce live concurrent sessions.
- Admin: create staff accounts; passwords are BCrypt hashed.

## Quick test plan
1. Sign in using the admin account you created in the previous step.
2. Add a member with a unique email.
3. Add a book with 1 total copy and 1 available copy.
4. Check out the book to that member. Dashboard should show one active loan and zero available copies for that title.
5. Try to check out the same book again; it should fail because no copies are available.
6. Return the loan. Available copies should increase back to 1.
7. Place and cancel a reservation.
8. Add a digital licence for a book and confirm it appears in Digital Licences.
9. Create a librarian account in Admin, then sign out and test the new account.
10. To test a fine without waiting two weeks, in a local test database run:
```sql
UPDATE loans SET due_date = DATE_SUB(CURDATE(), INTERVAL 3 DAY) WHERE status='BORROWED';
```
Then return that loan. The fine should be about ₹15. Do this only in test data.

## Important limitations and security notes
- This is an academic starter project, not production-ready. Use only test data.
- Add CSRF protection, server-side role authorization, stronger validation, pagination, audit logging, and email/SMS provider configuration before public deployment.
- The current app checks for a signed-in staff session; it does not yet restrict every action by ADMIN vs LIBRARIAN role.
- Never expose the MySQL port to the public internet. Use HTTPS for deployment.
- Reservation FIFO is displayed in order, but automatic reservation fulfilment is not implemented.
- Digital licence concurrent limits are stored, but acquire/release session tracking is not implemented.
