Requirements Document

Introduction
The Tea Shop Prototype is a Kotlin + Spring application that enables users to browse a catalog of teas and basic teaware, view detailed product information, manage a shopping cart, and complete a mock checkout process that simulates payment. The system creates orders, manages inventory reservations, applies VAT and shipping rules, and writes transactional events to the application log (no emails in the prototype). An admin interface supports product/catalog management and order lifecycle updates (Paid, Shipped). Security and privacy controls ensure safe guest checkout and protected admin operations. Basic analytics capture key user actions.

Requirements

1. Catalog Browsing and Faceted Filtering
- User Story: As a visitor, I want to browse the tea catalog and refine results by attributes so that I can quickly find products that match my preferences.
- Acceptance Criteria:
  - WHEN a visitor applies combinations of filters (Type, Region, Harvest Year, Price range, In Stock) on the catalog page THEN the system SHALL combine filters (logical AND) and update the result set accordingly without full page reload.
  - WHEN filters are applied or cleared THEN the system SHALL reflect the current filter state in the URL (query string) so that it can be shared and reloaded consistently.
  - WHEN filter combinations produce no matching products THEN the system SHALL show an empty-state message and SHALL provide a clear option to reset filters.
  - WHEN invalid or unsupported filter parameters are present in the URL THEN the system SHALL ignore them and SHALL return a valid response without erroring the page.
  - WHEN listing results THEN the system SHALL indicate stock status per product (e.g., In Stock, Low Stock, Out of Stock) if available.

2. Search with Synonyms
- User Story: As a visitor, I want to search by keywords and tags so that I can quickly find relevant teas even with alternative spellings.
- Acceptance Criteria:
  - WHEN a visitor searches for a keyword such as "puer" or "pu-erh" THEN the system SHALL match Pu-erh products using a configurable synonyms list.
  - WHEN the search query is empty THEN the system SHALL return the default catalog listing without error.
  - WHEN no products match the search query THEN the system SHALL display a no-results message with a prompt to modify the query or clear it.

3. Product Detail Page (PDP) with Variant Selection
- User Story: As a visitor, I want to view detailed product information and switch between variants so that I can choose the right size/option.
- Acceptance Criteria:
  - WHEN the PDP is loaded THEN the system SHALL display title, images, price, available variants (e.g., 25g, 200g), harvest year, storage type, basic brewing information, and current stock status for the selected variant.
  - WHEN a visitor switches the selected variant THEN the system SHALL update price, SKU, and stock status instantly without a full page reload.
  - WHEN a variant is out of stock THEN the system SHALL disable add-to-cart for that variant and SHALL indicate the status to the user.
  - WHEN product data is missing or unavailable THEN the system SHALL show a graceful error or fallback state instead of breaking the page.

4. Cart Management
- User Story: As a customer, I want to add items to a cart and adjust quantities so that I can prepare my purchase before checkout.
- Acceptance Criteria:
  - WHEN a customer adds a variant to the cart THEN the system SHALL create a cart (if none exists) and SHALL persist it for the session.
  - WHEN quantities are increased or decreased THEN the system SHALL update line totals and cart totals immediately and prevent negative quantities.
  - WHEN a customer attempts to add a variant with insufficient stock THEN the system SHALL limit the quantity to available stock and SHALL display a clear message.
  - WHEN a customer removes an item THEN the system SHALL update the cart totals and remove the line item from the cart.

5. Checkout with Mock Payment
- User Story: As a customer, I want to complete checkout using a mock payment so that I can place an order without real payment processing.
- Acceptance Criteria:
  - WHEN the customer submits checkout for a valid cart THEN the system SHALL create an Order with paymentStatus=PENDING and SHALL display the order number on the confirmation page.
  - WHEN the order is created THEN the system SHALL reserve inventory by increasing reservedQty for each ordered variant by the ordered quantity.
  - WHEN the checkout submission fails validation (e.g., missing email/address, empty cart) THEN the system SHALL not create an order and SHALL display validation errors indicating required fields.
  - WHEN mock payment is simulated THEN the system SHALL not interact with real payment gateways and SHALL not store any payment credentials.
  - WHEN the order is created THEN the system SHALL write an “Order received – awaiting payment” confirmation event to the application log including the order number.

6. Order Confirmation Event Log Content
- User Story: As a customer, I want the system to log a confirmation event with my order details so that there is a record of my purchase in the application logs.
- Acceptance Criteria:
  - WHEN an order is created THEN the system SHALL write a log entry containing order number, item list (titles, quantities), totals, VAT amount, and shipping cost breakdown.
  - WHEN logging fails transiently THEN the system SHALL not block order creation; the failure SHALL be visible in server diagnostics.

7. Public Order Lookup
- User Story: As a customer, I want to look up my order by order number so that I can review details and status.
- Acceptance Criteria:
  - WHEN a GET request is made to GET /api/orders/{orderNumber} with a valid order number THEN the system SHALL return order summary details excluding sensitive information (no PII beyond order data).
  - WHEN an invalid or unknown order number is provided THEN the system SHALL return 404 Not Found without revealing whether other orders exist.

8. Admin Catalog Management (Products, Variants, Lots)
- User Story: As an admin, I want to create, edit, and delete products, variants, and lots so that I can maintain the catalog.
- Acceptance Criteria:
  - WHEN an authenticated admin calls POST /api/admin/products, /variants, or /lots with valid payloads THEN the system SHALL create the respective records and SHALL enforce referential integrity (e.g., variant requires productId).
  - WHEN editing or deleting catalog entities THEN the system SHALL validate constraints (e.g., cannot delete a product if active variants exist unless cascading rules are satisfied).
  - WHEN invalid data is submitted THEN the system SHALL respond with 4xx status and detailed validation errors.

9. Admin Order Management (Mark Paid, Ship, Cancel)
- User Story: As an admin, I want to update order statuses so that I can progress fulfillment and communicate with customers.
- Acceptance Criteria:
  - WHEN an admin marks an order as Paid via PATCH /api/admin/orders/{id}/mark-paid THEN the system SHALL decrement stockQty and SHALL decrement reservedQty by the ordered quantities and set paymentStatus=PAID; the system SHALL write a “Payment received” event to the application log.
  - WHEN an admin marks an order as Shipped via PATCH /api/admin/orders/{id}/ship THEN the system SHALL set fulfillmentStatus=FULFILLED, attach a tracking URL when provided, and write a “Your order has shipped” event to the application log.
  - WHEN an admin cancels an order before payment via PATCH /api/admin/orders/{id}/cancel THEN the system SHALL release reservations by reducing reservedQty accordingly and update order status.
  - WHEN actions are invalid for the current state (e.g., marking an already shipped order as Paid) THEN the system SHALL reject with a 409 Conflict or appropriate 4xx response.

10. Inventory Reservation and Release Workflow
- User Story: As an operator, I want inventory to be reserved at checkout and adjusted on payment or cancellation so that stock accuracy is maintained.
- Acceptance Criteria:
  - WHEN an order is created (PENDING) THEN the system SHALL apply reservedQty += qty for each variant in the order.
  - WHEN an order is marked Paid THEN the system SHALL apply stockQty -= qty and reservedQty -= qty for each variant.
  - WHEN an order is canceled before payment THEN the system SHALL release the reservation by reducing reservedQty by the ordered quantity.
  - WHEN concurrent orders or updates occur THEN the system SHALL enforce consistency (e.g., via transactions or optimistic locking) to prevent negative stock.

11. Pricing, VAT, and Shipping Calculation
- User Story: As a customer, I want accurate totals including VAT and shipping so that I know the final price before purchase.
- Acceptance Criteria:
  - WHEN prices are displayed or totals are calculated THEN the system SHALL treat prices as VAT-inclusive and SHALL extract VAT at a single configurable rate (e.g., 20%).
  - WHEN calculating shipping THEN the system SHALL use a configurable table by zone (Domestic/EU/ROW) and weight brackets based on shippingWeight of items in the cart.
  - WHEN destination or weight information is missing THEN the system SHALL prompt for required fields and SHALL not calculate shipping until sufficient data is provided.
  - WHEN duties/taxes beyond VAT are applicable THEN the system SHALL display a disclaimer that duties are out of scope and not included.

12. Public APIs (Catalog, Product, Cart, Checkout, Order Lookup)
- User Story: As a client application, I want REST endpoints to browse products and manage carts/orders so that I can build the storefront experience.
- Acceptance Criteria:
  - WHEN clients call GET /api/products with filters THEN the system SHALL return a filtered list according to Requirement 1.
  - WHEN clients call GET /api/products/{slug} THEN the system SHALL return the product including available variants and key attributes.
  - WHEN clients call POST /api/cart THEN the system SHALL create and return a cart object; PATCH /api/cart/{id} SHALL support item add/update/remove operations idempotently with validation.
  - WHEN clients call POST /api/checkout/{cartId}/submit THEN the system SHALL create an order with paymentStatus=PENDING and follow the workflow in Requirements 5 and 10.
  - WHEN any request has invalid input THEN the system SHALL respond with 4xx and structured error payloads; server errors SHALL return 5xx with correlation IDs for diagnostics.

13. Admin APIs (Authentication Required)
- User Story: As a back-office tool, I want secure admin endpoints for catalog and order operations so that only authorized admins can manage data.
- Acceptance Criteria:
  - WHEN accessing any /api/admin/* endpoint THEN the system SHALL require authentication with role ADMIN via Spring Security.
  - WHEN CSRF protection is enabled THEN the system SHALL validate CSRF tokens on state-changing requests and reject missing/invalid tokens.
  - WHEN audit-relevant operations occur (create/update/delete, state transitions) THEN the system SHALL log who performed the action and when.

14. UI Pages and Navigation
- User Story: As a user, I want clear pages and navigation so that I can complete shopping and checkout smoothly.
- Acceptance Criteria:
  - WHEN visiting the site THEN the system SHALL provide the following pages: Home (featured products), Catalog (filters, search), PDP (variant selector), Cart, Checkout (mock payment form), Order Confirmation, and Admin pages (Login, Dashboard, Products, Variants, Lots, Orders).
  - WHEN navigating between pages THEN the system SHALL preserve cart state across the session.
  - WHEN a page fails to load data THEN the system SHALL show an error state with retry options and without exposing sensitive details.

15. Security and Privacy
- User Story: As a stakeholder, I want minimal risk and privacy safeguards so that user data is protected.
- Acceptance Criteria:
  - WHEN customers checkout as guests THEN the system SHALL require email and address fields sufficient for order processing and SHALL store no payment credentials.
  - WHEN accessing admin features THEN the system SHALL restrict access to authenticated users with ADMIN role only.
  - WHEN handling requests THEN the system SHALL have CSRF protection enabled for state-changing operations.
  - WHEN displaying legal notices THEN the system SHALL provide a cookie banner and static Privacy/Terms pages.
  - WHEN storing order data THEN the system SHALL store only necessary PII for order processing and SHALL avoid retaining extraneous sensitive data.

16. Analytics Events
- User Story: As a product owner, I want basic analytics events so that I can measure funnel performance.
- Acceptance Criteria:
  - WHEN users view pages, add items to cart, or complete a purchase THEN the system SHALL emit corresponding analytics events (pageview, add-to-cart, purchase) using the chosen analytics mechanism.
  - WHEN users decline tracking (if applicable) THEN the system SHALL respect the setting and limit analytics as configured.

17. Accessibility and Mobile Readiness
- User Story: As a user, I want the storefront to be accessible and usable on mobile devices so that I can shop comfortably.
- Acceptance Criteria:
  - WHEN using the storefront on mobile devices THEN the system SHALL present responsive layouts for all main pages.
  - WHEN navigating via keyboard or assistive technologies THEN the system SHALL provide accessible controls and labels for filters, variant selectors, cart operations, and checkout forms.

18. Data Model Conformance
- User Story: As an engineer, I want entities to conform to the defined domain model so that features operate consistently.
- Acceptance Criteria:
  - WHEN implementing persistence THEN the system SHALL provide entities with at least the following fields: Product (id, slug, title, type, description, status); Variant (id, productId, sku, title, price, weight, shippingWeight, stockQty, reservedQty, lotId); Lot (id, productId, harvestYear, season, storageType, pressDate); Cart (id, items[], totals); CartItem (id, variantId, qty, priceSnapshot); Order (id, number, email, address, items[], totals, tax, shipping, status, paymentStatus, fulfillmentStatus, trackingUrl); OrderItem (id, orderId, variantId, titleSnapshot, qty, priceSnapshot).
  - WHEN loading or saving these entities THEN the system SHALL maintain referential integrity and prevent orphaned records where applicable.

19. Non-Functional Done Criteria (Prototype Readiness)
- User Story: As a project sponsor, I want clear prototype readiness criteria so that we know when the scope is complete.
- Acceptance Criteria:
  - WHEN testing end-to-end THEN the system SHALL support browse → PDP → cart → mock checkout → order created → confirmation event logged.
  - WHEN operating the admin interface THEN the system SHALL allow marking orders as Paid and Shipped.
  - WHEN creating, paying, and canceling orders THEN the system SHALL correctly reserve and release inventory.
  - WHEN tracking events THEN the system SHALL capture basic analytics for the main funnel.
  - WHEN using the application on common devices THEN the system SHALL present accessible and mobile-friendly pages.

20. Admin Inventory Overview Page
- User Story: As a system administrator, I want an inventory overview page so that I can observe the whole tea shop catalog at a glance.
- Acceptance Criteria:
  - WHEN an authenticated admin visits GET /admin/products THEN the system SHALL render a Products management page listing all products with columns: ID, Title, Slug, Type, Status, Variant Count, Lot Count.
  - WHEN the page loads THEN the client SHALL fetch data via GET /api/admin/products and display the results in a table; an empty state SHALL be shown when no products exist.
  - WHEN an unauthenticated user attempts to access /admin/products THEN the system SHALL redirect to /admin/login.
  - WHEN an authenticated non-admin attempts to access admin pages or /api/admin endpoints THEN the system SHALL deny access (403) or redirect to login as configured.
  - WHEN the API fails temporarily THEN the page SHALL show an empty state and SHALL not expose stack traces or sensitive details.
