# Implementation Plan — Tea Shop Prototype (Java + Spring)

Last updated: 2025-10-07

This plan operationalizes the requirements in docs/requirements.md and the functional specification in docs/spec.md. It describes the architecture, phased delivery, rationale, and a traceable mapping from plan items to requirements (R1–R20). Priorities are assigned as P0 (must-have for prototype readiness), P1 (important, near-term), and P2 (nice-to-have/stretch).

---

## 1. Goals and Scope

- Deliver an end-to-end prototype: browse → PDP → cart → mock checkout → order created → confirmation event logged (no emails); admin can mark Paid/Shipped; inventory reservation/release; basic analytics; responsive pages. [R19, Spec §13] [P0]
- Public APIs for catalog, product, cart, checkout, and order lookup. [R12, Spec §7 Public] [P0]
- Admin APIs for catalog and order management behind Spring Security. [R8, R9, R13, Spec §7 Admin] [P0]
- Single VAT rate extraction and table-based shipping calculation. [R11, Spec §9] [P0]
- Accessibility and mobile-ready baseline for core pages. [R17, Spec §8] [P1]

Rationale: These align directly with prototype “Done Criteria” and enable meaningful demos and tests.

---

## 2. Architecture Overview

- Runtime:
  - Spring Boot (Java), REST controllers (JSON) for public/admin APIs. [R12, R13] [P0]
  - Server-rendered minimal UI using Spring MVC + Thymeleaf for core pages (Catalog, PDP, Cart, Checkout, Order Confirmation, Admin screens). Keeps stack simple; no SPA build complexity. [R14] [P0]
- Persistence:
  - JPA/Hibernate with Flyway migrations. Start with PostgreSQL (or H2 for local/dev) as per compose.yaml. Entities per domain model. [R18] [P0]
- Security:
  - Spring Security, form login for Admin, role-based access (ROLE_ADMIN), CSRF enabled for state-changing routes. [R13, R15] [P0]
- Notifications:
  - Log-based order lifecycle event logging (Order Received, Payment Received, Shipped); no SMTP in prototype. [R6, Spec §10] [P0]
- Configuration:
  - application.yml properties for VAT rate, shipping tables (zone + weight brackets), synonym list. Externalized for easy tuning. [R2, R11] [P0]
- Analytics:
  - Lightweight event logger abstraction with adapters: server log emission and simple frontend JS hooks. [R16] [P1]

Rationale: Prioritizes rapid implementation with minimal moving parts, favoring maintainability and testability.

---

## 3. Data Model (Persistence Layer)

Implement entities exactly as specified; add basic auditing fields where useful (createdAt, updatedAt). [R18]
- Product(id, slug, title, type, description, status)
- Variant(id, productId, sku, title, price, weight, shippingWeight, stockQty, reservedQty, lotId)
- Lot(id, productId, harvestYear, season, storageType, pressDate)
- Cart(id, items[], totals)
- CartItem(id, variantId, qty, priceSnapshot)
- Order(id, number, email, address, items[], totals, tax, shipping, status, paymentStatus, fulfillmentStatus, trackingUrl)
- OrderItem(id, orderId, variantId, titleSnapshot, qty, priceSnapshot)

Constraints: Referential integrity; optimistic locking (@Version) on Variant and Order to prevent stock races. [R10, R18] [P0]

Rationale: Matches requirement-level fields; versioning mitigates concurrency issues in prototype scale.

---

## 4. Functional Modules and APIs

4.1 Catalog & Search [R1, R2, R12]
- GET /api/products?filters=…: type, region, harvestYear, price range, inStock; URL-driven filters reflected in server-rendered Catalog page. Combine via AND; ignore invalid params. [P0]
- Search with configurable synonyms (e.g., "puer" ↔ "pu-erh"). Implement via simple token expansion pre-query. [P1]

4.2 Product Detail Page (PDP) [R3, R12]
- GET /api/products/{slug}: includes variants, prices, stock; UI variant switch updates price/SKU/stock via AJAX/HTMX. Disable add-to-cart if OOS. [P0]

4.3 Cart [R4, R12]
- POST /api/cart: create cart; PATCH /api/cart/{id}: idempotent add/update/remove. Persist via DB and maintain cartId cookie for session continuity. Prevent negative qty; cap by available stock. [P0]

4.4 Checkout & Orders [R5, R6, R7, R10, R11, R12]
- POST /api/checkout/{cartId}/submit: validate email/address, non-empty cart; create Order with paymentStatus=PENDING; reserve inventory (reservedQty += qty). Write "Order received" event to application log. [P0]
- GET /api/orders/{orderNumber}: public order summary sans sensitive data; 404 on unknown. [P0]

4.5 Admin [R8, R9, R13, R20]
- Catalog CRUD: POST /api/admin/products, /variants, /lots (and PATCH/DELETE as needed). Enforce referential integrity and validation. [P0]
- Inventory overview UI: server-rendered /admin/products page that fetches GET /api/admin/products and lists all products with ID, Title, Slug, Type, Status, Variant Count, Lot Count. [P0]
- Orders: PATCH mark-paid, ship, cancel with state checks; on Paid: stockQty -= qty; reservedQty -= qty; write "Payment received" event to log. On Ship: set fulfillmentStatus, trackingUrl; write "Order shipped" event to log. On Cancel (pre-payment): release reservations. [P0]

4.6 Pricing, VAT, Shipping [R11]
- Prices stored VAT-inclusive. Totals calculator extracts VAT at configured rate; shipping computed via zone × weight brackets (sum of item shippingWeight). Validate required destination fields before calculation. [P0]

4.7 Security & Privacy [R13, R15]
- Admin endpoints behind ROLE_ADMIN; CSRF enabled; minimal PII stored in orders; cookie banner and Privacy/Terms static pages. [P0 for auth/CSRF, P1 for banner/static pages]

4.8 Analytics [R16]
- Emit server-side logs for pageviews/add-to-cart/checkout; optional frontend event hooks. Respect opt-out flag. [P1]

4.9 Accessibility & Mobile [R17]
- Responsive layouts (simple CSS / Bootstrap), labeled controls, keyboard navigable forms. [P1]

---

## 5. Phased Delivery Plan (with Priorities)

Phase 0 — Project Skeleton & Config [P0]
- Wire Spring Boot modules, basic dependencies, profiles, Flyway, database container. [R12, R13]

Phase 1 — Data Model & Repositories [P0]
- Implement entities, repositories, migrations; add optimistic locking; seed minimal data. [R18, R10]

Phase 2 — Pricing & Shipping Calculators [P0]
- Implement VAT extractor and shipping table calculator; unit tests. [R11]

Phase 3 — Catalog & PDP [P0]
- Public endpoints, filtering, PDP with variant switch; Catalog and PDP server templates; basic stock indicators. [R1, R3, R12, R14]

Phase 4 — Cart [P0]
- Cart create/update/remove, session cookie, totals recalculation; error handling. [R4, R12, R14]

Phase 5 — Checkout & Order Creation [P0]
- Validation, order number generation, reservation updates, confirmation event logged; Order Confirmation page. [R5, R6, R7, R10, R14]

Phase 6 — Admin APIs (Catalog + Orders) [P0]
- Auth, CRUD, mark-paid/ship/cancel flows with state validation and event logs. [R8, R9, R13]

Phase 7 — Security & Privacy Hardening [P0/P1]
- Enforce CSRF; restrict admin; log audit info. Add Privacy/Terms and cookie banner. [R13, R15]

Phase 8 — Search Synonyms [P1]
- Config file for synonyms; query expansion; tests ("puer"/"pu-erh"). [R2]

Phase 9 — Analytics [P1]
- Event publisher + server logs + optional JS hooks; respect opt-out. [R16]

Phase 10 — Accessibility & Mobile Polish [P1]
- Responsive styles, ARIA labels, keyboard navigation checks. [R17]

Phase 11 — Robustness & Concurrency [P2]
- Add structured event logging and log enrichment; additional integration tests for concurrent orders. [R10]

---

## 6. Validation, Errors, and Observability

- Validation: Spring Validation annotations + custom validators; consistent 4xx with structured error payloads. [R12, R8] [P0]
- Global error handling: @ControllerAdvice to map exceptions to responses, use correlation ID in 5xx. [R12] [P0]
- Logging & auditing: who/when for admin operations; state transitions logged. [R13] [P0]

Rationale: Clear, predictable error semantics and traceability speed up dev and QA.

---

## 7. Order Event Logging & Formats [R6, R9, Spec §10]

- Events: Order Received (PENDING), Payment Received, Shipped (with tracking URL). [P0]
- Delivery: Structured application logs; no SMTP in prototype. [P0]

---

## 8. Configuration & Environment

- application.yml keys:
  - shop.vatRate: decimal (e.g., 0.20) [R11]
  - shop.shipping.zones: { Domestic/EU/ROW → weight brackets } [R11]
  - shop.search.synonyms: map/list of terms [R2]
  - logging.level.*, logging.file.name, logging.pattern.* [R19]
- Profiles: dev (H2 or local Postgres), prod-ready (Postgres); both configured to write logs to console and logs/eshop.log. [P0]

---

## 9. Testing Strategy

- Unit tests: pricing/VAT, shipping calculator, order number generator, validators. [P0]
- Integration tests: cart updates, checkout → order creation (reservedQty), mark-paid/ship/cancel flows with stock adjustments; public order lookup 404/200; admin auth. [P0]
- Concurrency: optimistic locking scenarios on Variant updates. [P2]
- UI smoke tests: render pages, variant switch behavior, OOS disabled states. [P1]

Traceability: Tests named and tagged per requirement IDs for coverage mapping.

---

## 10. Risks and Mitigations

- Stock race conditions under concurrent checkouts: use @Version + transactional updates; cap by available stock; return 409 on conflicts. [R10] [P0]
- Logging visibility: ensure order lifecycle events are structured and discoverable; do not block order creation on logging errors. [R6] [P0]
- Over-scoping UI: keep server-rendered, minimal CSS to meet accessibility baseline. [R14, R17] [P1]

---

## 11. Deliverables

- Working API + server-rendered pages achieving Done Criteria. [R19] [P0]
- Configuration for VAT, shipping, synonyms. [R2, R11] [P0/P1]
- Order event logging setup and formats. [R6] [P0]
- Test suite covering core flows. [R19] [P0]
- Documentation: this plan, API README, and environment setup notes. [P0]

---

## 12. Requirements Traceability Matrix (Plan → Requirement)

- Catalog filters & listing (Phase 3) → R1 [P0]
- Search synonyms (Phase 8) → R2 [P1]
- PDP details & variant switch (Phase 3) → R3 [P0]
- Cart operations (Phase 4) → R4 [P0]
- Checkout order creation (Phase 5) → R5 [P0]
- Order confirmation event log (Phase 5, §7) → R6 [P0]
- Public order lookup (Phase 5) → R7 [P0]
- Admin catalog CRUD (Phase 6) → R8 [P0]
- Admin order management (Phase 6) → R9 [P0]
- Inventory workflow/reservations (Phases 5–6) → R10 [P0]
- VAT & shipping calc (Phase 2) → R11 [P0]
- Public APIs (Phases 3–5) → R12 [P0]
- Admin auth/CSRF/audit (Phases 6–7) → R13 [P0]
- UI pages (Phases 3–5, 6) → R14 [P0]
- Security & privacy (Phases 6–7) → R15 [P0/P1]
- Analytics events (Phase 9) → R16 [P1]
- Accessibility & mobile (Phase 10) → R17 [P1]
- Data model conformance (Phase 1) → R18 [P0]
- Prototype readiness (All) → R19 [P0]
- Admin inventory overview UI (Phase 6) → R20 [P0]

---

## 13. Milestones & Priorities Summary

- M1: Core Data + Pricing/Shipping + Catalog/PDP + Cart + Checkout + Event logging + Admin flows (Paid/Ship) — Prototype E2E. [P0]
- M2: Security polish (cookie banner, static legal), synonyms search, analytics, accessibility/mobile polish. [P1]
- M3: Reliability polish (logging/observability) and concurrency test suite. [P2]

---

## 14. Rationale Highlights

- Server-rendered UI: fastest path to usable prototype with fewer moving parts; aligns with Spring stack and simplifies CSRF/session handling. [R14, R15]
- Optimistic locking over heavy queuing: acceptable for prototype load, simpler to demonstrate stock correctness. [R10]
- Config-driven VAT/shipping/synonyms: minimizes code changes for business tweaks; supports future admin manageability. [R2, R11]

End of plan.