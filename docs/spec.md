# Functional Requirements Specification — Tea Shop Prototype (Kotlin + Spring)

## 1. Purpose
This document specifies the functional requirements for the **Tea Shop Prototype**, built with Kotlin + Spring Framework.  
The prototype allows customers to browse teas, view product details, add items to cart, complete a mock checkout, and receive confirmation.  
An admin can manage catalog, inventory, and orders. Real payment integration is deferred; a **mock checkout** simulates payment.

---

## 2. Scope

### In-Scope (Prototype)
- Catalog (Teas + basic Teaware)
- Product Detail Page (PDP)
- Faceted search & filters
- Cart & checkout with **mock payment**
- Order confirmation email
- Admin CRUD for products, variants, lots, stock
- Order management (mark as Paid, Shipped)
- VAT (fixed rate) and shipping table (weight × zone)
- Basic analytics (page, add-to-cart, purchase events)

### Out-of-Scope (Future Phases)
- Real payment integration (Stripe, PayPal)
- Subscriptions, loyalty, discounts
- B2B/wholesale
- Multi-warehouse, OSS/IOSS VAT handling
- Reviews, blogs, advanced content

---

## 3. User Roles
- **Visitor** — browse catalog, search, view PDP
- **Customer** — checkout as guest, place order
- **Admin** — manage products, stock, orders

---

## 4. User Stories & Acceptance Criteria

### 4.1 Browsing & Discovery
- **US1:** As a visitor, I can list teas and filter by **Type**, **Region**, **Harvest Year**, **Price**, and **In Stock**.  
  - **AC:** Filters combine; URL reflects state; empty results handled gracefully.
- **US2:** As a visitor, I can search teas by title and tags.  
  - **AC:** “puer”, “pu-erh” both match Pu-erh products (synonyms list).

### 4.2 Product Detail Page (PDP)
- **US3:** As a visitor, I can view a product with title, images, price, variants (25g/200g), harvest year, storage type, brewing basics, and stock status.  
  - **AC:** Switching variant updates price, SKU, and stock without reload.

### 4.3 Cart & Checkout
- **US4:** As a customer, I can add products to a cart, adjust quantities, and proceed to checkout.  
  - **AC:** Cart persists during session; totals update live.
- **US5:** As a customer, I can complete checkout with a **mock payment method**.  
  - **AC:** Checkout creates an order with `paymentStatus=PENDING`; customer sees order number and receives confirmation email.

### 4.4 Orders
- **US6:** As a customer, I receive an order confirmation email after placing an order.  
  - **AC:** Email contains order number, items, totals, VAT, and shipping costs.

### 4.5 Admin
- **US7:** As an admin, I can create, edit, and delete products, variants, and lots.  
- **US8:** As an admin, I can view orders and mark them as **Paid** or **Shipped**.  
  - **AC:** Marking as Paid decrements stock; customer gets “Payment received” email.  
  - **AC:** Marking as Shipped adds tracking URL and sends “Shipped” email.

---

## 5. Domain Model

### Entities
- **Product**: id, slug, title, type, description, status
- **Variant**: id, productId, sku, title, price, weight, shippingWeight, stockQty, reservedQty, lotId
- **Lot**: id, productId, harvestYear, season, storageType, pressDate
- **Cart**: id, items[], totals
- **CartItem**: id, variantId, qty, priceSnapshot
- **Order**: id, number, email, address, items[], totals, tax, shipping, status, paymentStatus, fulfillmentStatus, trackingUrl
- **OrderItem**: id, orderId, variantId, titleSnapshot, qty, priceSnapshot

---

## 6. Order & Inventory Workflow

1. Customer submits checkout → Order created with `paymentStatus=PENDING`.
2. Variant stock: `reservedQty += qty`.
3. Admin marks order Paid → `stockQty -= qty; reservedQty -= qty`; paymentStatus=PAID.
4. Admin marks Shipped → fulfillmentStatus=FULFILLED; email sent.
5. If canceled before payment → release reservation.

---

## 7. APIs

### Public
- `GET /api/products?filters=` — list products
- `GET /api/products/{slug}` — get product
- `POST /api/cart` — create cart
- `PATCH /api/cart/{id}` — update cart
- `POST /api/checkout/{cartId}/submit` — create order (mock payment)
- `GET /api/orders/{orderNumber}` — public order lookup

### Admin
- `POST /api/admin/products`
- `POST /api/admin/variants`
- `POST /api/admin/lots`
- `GET /api/admin/orders`
- `PATCH /api/admin/orders/{id}/mark-paid`
- `PATCH /api/admin/orders/{id}/ship`
- `PATCH /api/admin/orders/{id}/cancel`

---

## 8. UI Pages

- Home (featured products)
- Catalog (filters, search)
- PDP (details, variant selector)
- Cart
- Checkout (mock payment form)
- Order confirmation page
- Admin: Login, Dashboard, Products, Variants, Lots, Orders

---

## 9. Pricing, VAT & Shipping

- Prices stored **VAT-inclusive**.
- VAT extracted at a single fixed rate (configurable, e.g., 20%).  
- Shipping: table by zone (Domestic/EU/ROW) × weight brackets.  
- Duties: out of scope (show disclaimer).

---

## 10. Emails

- **Order Received (PENDING)** — subject: “Order received – awaiting payment”
- **Order Paid** — subject: “Payment received – preparing your order”
- **Order Shipped** — subject: “Your order has shipped”

---

## 11. Security & Privacy

- Guest checkout only (email + address required).
- Admin secured with Spring Security login (role: ADMIN).
- CSRF protection enabled.
- No payment/PII stored beyond necessary order data.
- Cookie banner + Privacy/Terms static pages.

---
---

## 13. Done Criteria (Prototype)

- End-to-end flow: browse → PDP → cart → mock checkout → order created → confirmation email.  
- Admin can mark orders as Paid/Shipped.  
- Inventory reservation and release works.  
- Basic analytics captured.  
- Pages accessible and mobile-friendly.

