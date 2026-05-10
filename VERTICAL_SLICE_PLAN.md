Vertical Slice Refactor Plan

Date: 2026-05-10
Branch: refactor/vertical-slice

Objective
- Reorganize backend (and minimal mobile/web) into Vertical Slice Architecture organized by feature modules.

Detected features (from codebase):
- auth
- users (user profile)
- books (catalog)
- book-copy
- queue
- borrowing
- reviews
- payments
- wishlist
- notifications
- audit-log
- system-config
- reading-stats
- book-verification
- search
- role-dashboard

Proposed project structure (backend)

backend/kwikq/src/main/java/edu/cit/mahinay/kwikq/features/
  auth/
    AuthController.java
    AuthService.java
    AuthRepository.java
    AuthModels.java (dto)
    AuthTests/
  users/
    UserController.java
    UserService.java
    UserRepository.java
    UserEntity.java
    UserProfileEntity.java
    UserTests/
  books/
    BookController.java
    BookService.java
    BookRepository.java
    BookEntity.java
    BookCopyEntity.java
    BookTests/
  borrowing/
    BorrowingController.java
    BorrowingService.java
    BorrowingRepository.java
    BorrowingEntity.java
    BorrowingTests/
  queue/
    QueueController.java
    QueueService.java
    QueueRepository.java
    BookQueueEntity.java
  reviews/
    ReviewController.java
    ReviewService.java
    ReviewRepository.java
    ReviewEntity.java
  payments/
    PaymentController.java
    PaymentService.java
    PaymentRepository.java
    PaymentEntity.java
  wishlist/
    WishlistController.java
    WishlistService.java
    WishlistRepository.java
    WishlistEntity.java
  notifications/
    NotificationController.java
    NotificationService.java
    NotificationRepository.java
    NotificationEntity.java
  admin/
    AuditLogController.java
    AuditLogService.java
    SystemConfigController.java
    RoleDashboardController.java

Notes & Rules
- Keep package root `edu.cit.mahinay.kwikq.features.<feature>` for each feature.
- Each feature contains its own controller, service, repository, entities, DTOs, and tests.
- Shared utilities (security, config, common DTOs) remain in `edu.cit.mahinay.kwikq.shared`.
- Do NOT change public API endpoints (paths) unless necessary; keep compatibility.
- Add module-level unit tests during the move.

Mobile app
- Only refactor `auth` slice for now (already migrated to Room). Place mobile auth files under `mobile/KwikQ/app/src/main/java/com/example/kwikq/features/auth/`.

Web static
- Group static HTML templates by feature under `web/kwikq/src/main/resources/static/features/<feature>/` and update backend resource mappings if required.

Migration Steps (high level)
1. Create feature folders under `backend/kwikq/src/main/java/edu/cit/mahinay/kwikq/features/`.
2. Move controller/service/repository/entity files into their feature folder, updating `package` declarations.
3. Update imports across the codebase to the new packages (IDE refactor or scripted `sed`).
4. Run `mvn -DskipTests package` and fix compile errors.
5. Add/adjust unit tests and run them.
6. Repeat iteratively per feature (start with `auth`, `users`, `books`).

Next actions I will take after your confirmation
- Scaffold `features/auth`, `features/users`, `features/books` folders and move a small number of files for `auth` as a proof-of-concept, run build, and run unit tests.
- Provide a detailed list of moved files and any code changes.

Please confirm to proceed with scaffolding and moving the `auth` feature as the first slice.