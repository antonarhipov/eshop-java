-- Create initial schema for eshop application

-- Products table
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    slug VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on products for common queries
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_type ON products(type);
CREATE INDEX idx_products_type_status ON products(type, status);

-- Lots table
CREATE TABLE lots (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    harvest_year INTEGER NOT NULL,
    season VARCHAR(20) NOT NULL,
    storage_type VARCHAR(20) NOT NULL,
    press_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lots_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Create indexes on lots for common queries
CREATE INDEX idx_lots_product_id ON lots(product_id);
CREATE INDEX idx_lots_harvest_year ON lots(harvest_year);
CREATE INDEX idx_lots_season ON lots(season);
CREATE INDEX idx_lots_product_harvest_season ON lots(product_id, harvest_year, season);

-- Variants table
CREATE TABLE variants (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    weight DECIMAL(8,3) NOT NULL,
    shipping_weight DECIMAL(8,3) NOT NULL,
    stock_qty INTEGER NOT NULL DEFAULT 0,
    reserved_qty INTEGER NOT NULL DEFAULT 0,
    lot_id BIGINT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_variants_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_variants_lot FOREIGN KEY (lot_id) REFERENCES lots(id) ON DELETE SET NULL,
    CONSTRAINT chk_variants_stock_positive CHECK (stock_qty >= 0),
    CONSTRAINT chk_variants_reserved_positive CHECK (reserved_qty >= 0),
    CONSTRAINT chk_variants_reserved_not_exceed_stock CHECK (reserved_qty <= stock_qty)
);

-- Create indexes on variants for common queries
CREATE INDEX idx_variants_product_id ON variants(product_id);
CREATE INDEX idx_variants_lot_id ON variants(lot_id);
CREATE INDEX idx_variants_stock_availability ON variants(stock_qty, reserved_qty);
CREATE INDEX idx_variants_in_stock ON variants(stock_qty) WHERE stock_qty > reserved_qty;

-- Carts table
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    vat_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    shipping_cost DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index for cart cleanup queries
CREATE INDEX idx_carts_created_at ON carts(created_at);
CREATE INDEX idx_carts_updated_at ON carts(updated_at);

-- Cart items table
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    variant_id BIGINT NOT NULL,
    qty INTEGER NOT NULL,
    price_snapshot DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_variant FOREIGN KEY (variant_id) REFERENCES variants(id) ON DELETE CASCADE,
    CONSTRAINT chk_cart_items_qty_positive CHECK (qty > 0),
    CONSTRAINT uk_cart_items_cart_variant UNIQUE (cart_id, variant_id)
);

-- Create indexes on cart_items for common queries
CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_cart_items_variant_id ON cart_items(variant_id);

-- Orders table
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    tax DECIMAL(10,2) NOT NULL,
    shipping DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    fulfillment_status VARCHAR(30) NOT NULL DEFAULT 'UNFULFILLED',
    tracking_url VARCHAR(500),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes on orders for common queries
CREATE INDEX idx_orders_email ON orders(email);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_payment_status ON orders(payment_status);
CREATE INDEX idx_orders_fulfillment_status ON orders(fulfillment_status);
CREATE INDEX idx_orders_email_status ON orders(email, status);
CREATE INDEX idx_orders_payment_created ON orders(payment_status, created_at);
CREATE INDEX idx_orders_paid_unfulfilled ON orders(payment_status, fulfillment_status) WHERE payment_status = 'PAID' AND fulfillment_status = 'UNFULFILLED';

-- Order items table
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    variant_id BIGINT NOT NULL,
    title_snapshot VARCHAR(255) NOT NULL,
    qty INTEGER NOT NULL,
    price_snapshot DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_variant FOREIGN KEY (variant_id) REFERENCES variants(id) ON DELETE RESTRICT,
    CONSTRAINT chk_order_items_qty_positive CHECK (qty > 0)
);

-- Create indexes on order_items for common queries
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_variant_id ON order_items(variant_id);

-- Add constraints for enum values
ALTER TABLE products ADD CONSTRAINT chk_products_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'DRAFT'));
ALTER TABLE lots ADD CONSTRAINT chk_lots_season CHECK (season IN ('SPRING', 'SUMMER', 'AUTUMN', 'WINTER'));
ALTER TABLE lots ADD CONSTRAINT chk_lots_storage_type CHECK (storage_type IN ('DRY', 'WET', 'TRADITIONAL', 'NATURAL'));
ALTER TABLE orders ADD CONSTRAINT chk_orders_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED'));
ALTER TABLE orders ADD CONSTRAINT chk_orders_payment_status CHECK (payment_status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED'));
ALTER TABLE orders ADD CONSTRAINT chk_orders_fulfillment_status CHECK (fulfillment_status IN ('UNFULFILLED', 'FULFILLED', 'PARTIALLY_FULFILLED'));