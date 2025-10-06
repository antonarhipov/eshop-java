-- Baseline Schema (V0) — Consolidated DDL
--
-- Effective date: 2025-10-06
-- Purpose: Consolidate the full schema definition into the baseline so that
-- brand‑new databases can be created from a single migration. Versioned
-- migrations V1 (initial schema) and V3/V4 (order contact fields) have been
-- superseded by this file and are now no‑op placeholders. Existing databases
-- MUST run Flyway REPAIR once after pulling this change to accept new checksums.
--
-- Safety: All CREATE statements use IF NOT EXISTS and inline constraints so this
-- migration can safely execute out‑of‑order on existing databases without error.
--
-- =============================================================
-- Products
-- =============================================================
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    slug VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE','DRAFT')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_products_status ON products(status);
CREATE INDEX IF NOT EXISTS idx_products_type ON products(type);
CREATE INDEX IF NOT EXISTS idx_products_type_status ON products(type, status);

-- =============================================================
-- Lots
-- =============================================================
CREATE TABLE IF NOT EXISTS lots (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    harvest_year INTEGER NOT NULL,
    season VARCHAR(20) NOT NULL CHECK (season IN ('SPRING','SUMMER','AUTUMN','WINTER')),
    storage_type VARCHAR(20) NOT NULL CHECK (storage_type IN ('DRY','WET','TRADITIONAL','NATURAL')),
    press_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lots_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_lots_product_id ON lots(product_id);
CREATE INDEX IF NOT EXISTS idx_lots_harvest_year ON lots(harvest_year);
CREATE INDEX IF NOT EXISTS idx_lots_season ON lots(season);
CREATE INDEX IF NOT EXISTS idx_lots_product_harvest_season ON lots(product_id, harvest_year, season);

-- =============================================================
-- Variants
-- =============================================================
CREATE TABLE IF NOT EXISTS variants (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    weight DECIMAL(8,3) NOT NULL,
    shipping_weight DECIMAL(8,3) NOT NULL,
    stock_qty INTEGER NOT NULL DEFAULT 0 CHECK (stock_qty >= 0),
    reserved_qty INTEGER NOT NULL DEFAULT 0 CHECK (reserved_qty >= 0),
    lot_id BIGINT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_variants_reserved_not_exceed_stock CHECK (reserved_qty <= stock_qty),
    CONSTRAINT fk_variants_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_variants_lot FOREIGN KEY (lot_id) REFERENCES lots(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_variants_product_id ON variants(product_id);
CREATE INDEX IF NOT EXISTS idx_variants_lot_id ON variants(lot_id);
CREATE INDEX IF NOT EXISTS idx_variants_stock_availability ON variants(stock_qty, reserved_qty);
CREATE INDEX IF NOT EXISTS idx_variants_in_stock ON variants(stock_qty) WHERE stock_qty > reserved_qty;

-- =============================================================
-- Carts
-- =============================================================
CREATE TABLE IF NOT EXISTS carts (
    id BIGSERIAL PRIMARY KEY,
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    vat_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    shipping_cost DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_carts_created_at ON carts(created_at);
CREATE INDEX IF NOT EXISTS idx_carts_updated_at ON carts(updated_at);

-- =============================================================
-- Cart Items
-- =============================================================
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    variant_id BIGINT NOT NULL,
    qty INTEGER NOT NULL CHECK (qty > 0),
    price_snapshot DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_cart_items_cart_variant UNIQUE (cart_id, variant_id),
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_variant FOREIGN KEY (variant_id) REFERENCES variants(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_variant_id ON cart_items(variant_id);

-- =============================================================
-- Orders (includes consolidated contact/address fields)
-- =============================================================
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    full_name VARCHAR(255) NOT NULL DEFAULT '',
    phone VARCHAR(50),
    street1 VARCHAR(255) NOT NULL DEFAULT '',
    street2 VARCHAR(255),
    city VARCHAR(255) NOT NULL DEFAULT '',
    region VARCHAR(255) NOT NULL DEFAULT '',
    postal_code VARCHAR(32) NOT NULL DEFAULT '',
    country VARCHAR(255) NOT NULL DEFAULT '',
    subtotal DECIMAL(10,2) NOT NULL,
    tax DECIMAL(10,2) NOT NULL,
    shipping DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','CONFIRMED','CANCELLED')),
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (payment_status IN ('PENDING','PAID','FAILED','REFUNDED')),
    fulfillment_status VARCHAR(30) NOT NULL DEFAULT 'UNFULFILLED' CHECK (fulfillment_status IN ('UNFULFILLED','FULFILLED','PARTIALLY_FULFILLED')),
    tracking_url VARCHAR(500),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_orders_email ON orders(email);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_payment_status ON orders(payment_status);
CREATE INDEX IF NOT EXISTS idx_orders_fulfillment_status ON orders(fulfillment_status);
CREATE INDEX IF NOT EXISTS idx_orders_email_status ON orders(email, status);
CREATE INDEX IF NOT EXISTS idx_orders_payment_created ON orders(payment_status, created_at);
CREATE INDEX IF NOT EXISTS idx_orders_paid_unfulfilled ON orders(payment_status, fulfillment_status) WHERE payment_status = 'PAID' AND fulfillment_status = 'UNFULFILLED';

-- =============================================================
-- Order Items
-- =============================================================
CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    variant_id BIGINT NOT NULL,
    title_snapshot VARCHAR(255) NOT NULL,
    qty INTEGER NOT NULL CHECK (qty > 0),
    price_snapshot DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_variant FOREIGN KEY (variant_id) REFERENCES variants(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_variant_id ON order_items(variant_id);

-- End of consolidated baseline DDL
