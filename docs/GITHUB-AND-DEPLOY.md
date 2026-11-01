# Push to GitHub and publish

Your portfolio remains at `https://saisrin-dev.github.io/`. Create a separate repository for this app: **retailflow-order-management**.

## A. Create the repository

1. Sign into https://github.com/new as `saisrin-dev`.
2. Name: `retailflow-order-management`.
3. Select Public.
4. For the command-line route, do NOT add a README, `.gitignore`, or license on GitHub: they already exist locally (no license is included).
5. Click Create repository.

## B. Push with Git (recommended)

Extract the ZIP. Open PowerShell in the folder containing `backend`, `frontend`, and `README.md`.

```powershell
git init
git branch -M main
git add .
git status
```
Before committing, confirm `node_modules`, `target`, `.env`, and database files are NOT staged. The provided `.gitignore` excludes them.

```powershell
git commit -m "Build RetailFlow order and inventory management app"
git remote add origin https://github.com/saisrin-dev/retailflow-order-management.git
git push -u origin main
```
Git for Windows may open your browser for GitHub sign-in. Follow that browser flow. Do not paste passwords or access tokens into commands or chat. If you accidentally initialized GitHub with a README, clone that repository into a fresh folder and copy these project files into it; do not force-push to solve the mismatch.

Check the repository's Actions tab for the `Build and test` workflow. It runs Java tests, Angular build, and the multi-stage Docker build. Resolve any red failure before sharing the project.

## C. Publish on Render (one URL for UI + Java API)

GitHub Pages hosts static files; it cannot run this Spring Boot backend. Use a Docker-capable service. This guide uses Render's web service workflow: https://render.com/docs/docker and https://render.com/docs/your-first-deploy.

1. Create/sign in to a Render account at https://render.com. Complete account terms yourself.
2. Choose **New → Web Service**.
3. Connect GitHub and grant access to `retailflow-order-management` only, or choose Render's public Git repository option if available.
4. Select branch `main`.
5. Runtime/Language: **Docker**.
6. Root directory: leave empty. Dockerfile path: `./Dockerfile`.
7. Choose a service name such as `retailflow-saisrin` if available. Render assigns the actual `.onrender.com` URL; example names here are not reserved.
8. Choose the Free instance if available for your account. Check the displayed terms/limits before selecting anything paid.
9. Health check path: `/actuator/health`.
10. Leave the start/Docker command blank; the Dockerfile supplies it. No secrets or database variables are required for the default H2 demo.
11. Click **Deploy Web Service** and follow build logs. The Docker build compiles Angular and runs Java tests before creating the runtime image.
12. When the service is Live, open its actual assigned URL. Test catalog loading, order placement, cancellation, and the resume portfolio backlink.

The app reads Render's `PORT` environment variable automatically. UI and API are same-origin. The free demo uses in-memory H2; restarts erase changes and reseed products. Free services may sleep when idle, so the first visitor can see a slow startup. See current limits: https://render.com/docs/free.

### Optional persistent PostgreSQL

Do this after the in-memory demo works:

- Create a PostgreSQL instance and use its provider-supplied host/database/credentials.
- Add Render environment variables:
  - `SPRING_PROFILES_ACTIVE=postgres`
  - `DB_URL=jdbc:postgresql://HOST:5432/DATABASE?sslmode=require`
  - `DB_USERNAME=YOUR_DATABASE_USER`
  - `DB_PASSWORD=YOUR_DATABASE_PASSWORD` (secret)
- Use the actual connection details from your provider. Do not paste a `postgres://` URL directly into `DB_URL`; JDBC expects `jdbc:postgresql://`.
- Redeploy and verify an order survives a service restart.
- Render's free PostgreSQL instances currently expire after 30 days; use a suitable persistent plan/provider for a long-lived demo. Recheck pricing and retention terms before committing.

## D. Add it to your portfolio

After deployment succeeds, add two clearly labeled links to your portfolio:

- **Live demo** → the exact URL Render returns.
- **Source code** → `https://github.com/saisrin-dev/retailflow-order-management`.

Share both with recruiters. Keep the portfolio at `saisrin-dev.github.io`; this project is separate.

## E. Later changes

Edit files locally, run `mvn verify` and `npm.cmd run build`, then:
```powershell
git add .
git commit -m "Describe the specific improvement"
git push
```
With Render auto-deploy enabled, it redeploys your main branch after the push. Confirm the deployment is Live and test the changed flow before sharing it.

## Before sharing

- Use only fictitious retailer data. This demo has no authentication or tenant isolation.
- Confirm the GitHub Actions build is green.
- Check the actual live demo from a private/incognito window.
- Do not advertise PostgreSQL, Kafka, Redis, security roles, or production metrics unless you actually enabled/implemented and verified them.
