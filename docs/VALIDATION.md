# Validation performed

Verified in this workspace on October 31, 2026.

| Check | Result |
| --- | --- |
| Java compilation and `mvn verify` | Passed on Java 17 |
| Order API integration tests | 10 passed, 0 failures/errors |
| Concurrent last-unit order and transaction rollback tests | 2 passed, 0 failures/errors |
| Angular production build | Passed, strict TypeScript and templates enabled |
| npm audit, runtime dependencies only | 0 reported vulnerabilities at verification time |
| Executable JAR HTTP smoke test | Passed |

The HTTP smoke test starts the actual packaged application and checks the Angular root document, health endpoint, seeded catalog, order creation/replay, inventory decrement, persisted order serialization, cancellation/stock return, and dashboard value. After `mvn package`, run `python scripts/smoke.py` from the project root (Python 3 required). Port 18080 must be free. This uses a temporary in-memory database and stops its server afterward.

## Not verified here

- Docker image build and Compose/PostgreSQL runtime: Docker was unavailable in this workspace. Java and Angular build stages were verified independently.
- PostgreSQL-specific locking and SQL behavior: integration tests use H2. PostgreSQL/Testcontainers coverage remains a next improvement.
- Browser visual/interaction QA: the preview environment could not load the Java runtime's shared library. Angular's production build and the real HTTP backend were verified; inspect responsive layout and click through the UI locally before sharing.
- GitHub Actions execution in your account and public Render deployment: no authenticated account deployment was performed. Follow GITHUB-AND-DEPLOY.md and verify the returned live URL.

Dependency checks are a point-in-time result for runtime npm packages, not a security certification or a full Java/container/development-tool audit. The app is a public sandbox without authentication and uses only fictitious data.
