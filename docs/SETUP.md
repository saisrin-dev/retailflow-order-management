# Local setup — Windows and IntelliJ

## 1. Install prerequisites

- JDK 17 or 21 (https://adoptium.net/). Java 17 is this project's compilation target.
- IntelliJ IDEA with Java support (https://www.jetbrains.com/idea/).
- Node.js 22 LTS, at least 22.12 (https://nodejs.org/).
- Apache Maven 3.9 (https://maven.apache.org/install.html). Add its `bin` folder to PATH.
- Git for Windows (https://git-scm.com/downloads/win).
- Optional: Docker Desktop (https://www.docker.com/products/docker-desktop/).

Restart terminals after installation. In PowerShell, confirm:
```powershell
java -version
mvn -v
node -v
npm.cmd -v
git --version
```
If Maven is not on PATH, IntelliJ can run the backend using its bundled Maven: open `backend/pom.xml` as a project, choose JDK 17, let Maven import, and run `RetailFlowApplication.main()`.

## 2. Extract the project

Use a location such as `C:\Projects\retailflow-order-management`. Do not extract into your portfolio repository. The ZIP's top-level folder contains `backend`, `frontend`, and `README.md`.

## 3. Start Java backend

Open `backend/pom.xml` in IntelliJ, set Project SDK to Java 17, and wait for dependency import. Run `RetailFlowApplication`. Or use PowerShell:
```powershell
cd C:\Projects\retailflow-order-management\backend
mvn spring-boot:run
```
The startup log should say the server started on port 8080. Check http://localhost:8080/actuator/health; it should return `{"status":"UP"}`.

## 4. Start Angular UI

Open a second PowerShell window:
```powershell
cd C:\Projects\retailflow-order-management\frontend
npm.cmd ci
npm.cmd start
```
Open http://localhost:4200. If PowerShell blocks `npm.ps1`, use `npm.cmd` as shown; you do not need to loosen execution policy.

## 5. Verify core behavior

Place an order for two units, verify stock decreased by two, cancel it, and verify stock returned. Confirm a separate order, ship it, deliver it, and check the timestamped history.

Run backend tests:
```powershell
cd ..\backend
mvn verify
```
Build UI:
```powershell
cd ..\frontend
npm.cmd run build
```

## 6. One-command Docker + PostgreSQL path

From the project root:
```powershell
Copy-Item .env.example .env
```
Edit `.env`: replace the example value with your own local database password. `.env` is ignored by Git. Start Docker Desktop, then:
```powershell
docker compose up --build
```
Open http://localhost:8080. Check `docker compose logs app` for startup progress. First build can take several minutes. Stop with Ctrl+C, then `docker compose down`. Data remains in the named volume. Do not run `down -v` unless you intend to delete the local database.

## Troubleshooting

| Problem | Fix |
| --- | --- |
| UI says it cannot reach API | Start Java on 8080; open health endpoint; restart Angular proxy if needed. |
| Port 8080/4200 already used | Stop the earlier process or app. If changing backend port, also change `frontend/proxy.conf.json`. |
| Unsupported Java class version | Ensure `java -version`, `mvn -v`, and IntelliJ project SDK use JDK 17+. |
| Angular install/build fails | Use Node 22.12+; run `npm.cmd ci` from `frontend`; check internet access to npm. |
| Maven cannot resolve dependencies | Check Maven Central access and your corporate proxy configuration. |
| Empty orders after restart | Default H2 database is in-memory. Use Docker/PostgreSQL for persistence. |
| Root localhost:8080 is 404 without Docker | Expected for backend-only development: use Angular on 4200. The Docker build packages UI assets into Java. |
