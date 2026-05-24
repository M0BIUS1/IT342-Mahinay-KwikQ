Title: Add mobile retry analytics, tests, and analytics endpoint

Summary:
- Adds a backend controller to accept mobile retry analytics at `POST /api/analytics/retry`.
- Adds a test hook to `RetryAnalytics` in the mobile app and Robolectric tests for sampling behavior.
- Adds a Robolectric test for `NetworkUtils` retry/backoff behavior.
- Updates security to allow anonymous POSTs to `/api/analytics/**` so mobile clients can report analytics.

Files changed (high level):
- backend: `AnalyticsController.java`, `AnalyticsControllerTests.java`, `SecurityConfig.java`
- mobile: `RetryAnalytics.kt` (test hook), `RetryAnalyticsRobolectricTest.kt`, `NetworkUtilsRobolectricTest.kt`

Notes for reviewer:
- The analytics endpoint currently logs events; no persistence is added (intentionally minimal).
- Security change permits `/api/analytics/**` without authentication; ensure this aligns with your threat model.
- Mobile tests rely on Robolectric and should run in CI. If you want server-side ingestion, I can add persistence and validation.

Suggested PR body:
```
This PR adds a lightweight analytics ingestion endpoint and mobile test coverage for retry/sampling.

Why:
- Improve observability of mobile retry/backoff behavior and allow sampled events to be reported for analysis.

What changed:
- Backend: add `AnalyticsController` to accept `POST /api/analytics/retry` and permit anonymous access for analytics paths.
- Mobile: add a test hook to `RetryAnalytics` and Robolectric tests for sampling and NetworkUtils retries.

Notes:
- Backend test suite was run locally (`mvn test`) and all tests passed.
- Mobile JVM tests (Robolectric) will run in CI; runner must have Android SDK configured (existing CI workflow handles this).

Follow-ups (optional):
- Implement server-side ingestion/persistence (DB table + validation).
- Add rate-limiting or HMAC signature if analytics must be authenticated.

```
