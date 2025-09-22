-- Insert seed data for testing

-- Insert sample products
INSERT INTO products (slug, title, type, description, status) VALUES
('dragon-well-green-tea', 'Dragon Well Green Tea', 'Green Tea', 'A classic Chinese green tea with a delicate, sweet flavor and beautiful flat leaves. Perfect for daily drinking.', 'ACTIVE'),
('earl-grey-black-tea', 'Earl Grey Black Tea', 'Black Tea', 'Traditional Earl Grey blend with bergamot oil, creating a distinctive citrusy aroma and flavor.', 'ACTIVE'),
('pu-erh-aged-tea', 'Aged Pu-erh Tea', 'Pu-erh Tea', 'A well-aged dark tea from Yunnan province with rich, earthy flavors that develop over time.', 'ACTIVE'),
('jasmine-phoenix-pearls', 'Jasmine Phoenix Pearls', 'Green Tea', 'Hand-rolled green tea pearls scented with fresh jasmine flowers, offering a delicate floral aroma.', 'ACTIVE'),
('himalayan-black-tea', 'Himalayan Black Tea', 'Black Tea', 'High-altitude black tea from the Himalayas with robust flavor and golden liquor.', 'ACTIVE'),
('white-peony-tea', 'White Peony Tea', 'White Tea', 'Gentle white tea with subtle sweetness and light, refreshing character.', 'ACTIVE'),
('ceramic-gaiwan', 'Traditional Ceramic Gaiwan', 'Teaware', 'Classic white porcelain gaiwan perfect for brewing and tasting tea.', 'ACTIVE'),
('bamboo-tea-tray', 'Bamboo Tea Tray', 'Teaware', 'Natural bamboo tea tray with drainage system for traditional tea service.', 'ACTIVE');

-- Insert sample lots
INSERT INTO lots (product_id, harvest_year, season, storage_type, press_date) VALUES
(1, 2024, 'SPRING', 'DRY', '2024-04-15'),
(2, 2023, 'SUMMER', 'DRY', NULL),
(3, 2020, 'AUTUMN', 'TRADITIONAL', '2020-10-20'),
(3, 2018, 'AUTUMN', 'TRADITIONAL', '2018-10-15'),
(4, 2024, 'SPRING', 'DRY', '2024-05-01'),
(5, 2024, 'SUMMER', 'DRY', NULL),
(6, 2024, 'SPRING', 'NATURAL', NULL);

-- Insert sample variants
INSERT INTO variants (product_id, sku, title, price, weight, shipping_weight, stock_qty, reserved_qty, lot_id) VALUES
-- Dragon Well Green Tea variants
(1, 'DW-25G-2024', 'Dragon Well Green Tea - 25g', 12.99, 0.025, 0.050, 50, 0, 1),
(1, 'DW-100G-2024', 'Dragon Well Green Tea - 100g', 39.99, 0.100, 0.150, 25, 2, 1),
(1, 'DW-250G-2024', 'Dragon Well Green Tea - 250g', 89.99, 0.250, 0.300, 15, 1, 1),

-- Earl Grey Black Tea variants
(2, 'EG-50G-2023', 'Earl Grey Black Tea - 50g', 15.99, 0.050, 0.075, 40, 0, 2),
(2, 'EG-200G-2023', 'Earl Grey Black Tea - 200g', 49.99, 0.200, 0.250, 20, 1, 2),

-- Aged Pu-erh Tea variants (2020 vintage)
(3, 'PU-357G-2020', 'Aged Pu-erh Tea Cake - 357g (2020)', 129.99, 0.357, 0.400, 8, 0, 3),
(3, 'PU-100G-2020', 'Aged Pu-erh Tea - 100g (2020)', 45.99, 0.100, 0.150, 12, 0, 3),

-- Aged Pu-erh Tea variants (2018 vintage)
(3, 'PU-357G-2018', 'Aged Pu-erh Tea Cake - 357g (2018)', 189.99, 0.357, 0.400, 5, 1, 4),
(3, 'PU-100G-2018', 'Aged Pu-erh Tea - 100g (2018)', 69.99, 0.100, 0.150, 8, 0, 4),

-- Jasmine Phoenix Pearls variants
(4, 'JP-30G-2024', 'Jasmine Phoenix Pearls - 30g', 24.99, 0.030, 0.060, 30, 0, 5),
(4, 'JP-100G-2024', 'Jasmine Phoenix Pearls - 100g', 74.99, 0.100, 0.150, 15, 1, 5),

-- Himalayan Black Tea variants
(5, 'HB-50G-2024', 'Himalayan Black Tea - 50g', 18.99, 0.050, 0.075, 35, 0, 6),
(5, 'HB-200G-2024', 'Himalayan Black Tea - 200g', 64.99, 0.200, 0.250, 18, 0, 6),

-- White Peony Tea variants
(6, 'WP-25G-2024', 'White Peony Tea - 25g', 19.99, 0.025, 0.050, 25, 0, 7),
(6, 'WP-100G-2024', 'White Peony Tea - 100g', 69.99, 0.100, 0.150, 12, 1, 7),

-- Teaware variants (no lots)
(7, 'GAIWAN-150ML', 'Traditional Ceramic Gaiwan - 150ml', 29.99, 0.200, 0.300, 20, 0, NULL),
(8, 'TRAY-BAMBOO-L', 'Bamboo Tea Tray - Large', 79.99, 1.200, 1.500, 10, 0, NULL),
(8, 'TRAY-BAMBOO-M', 'Bamboo Tea Tray - Medium', 59.99, 0.800, 1.000, 15, 1, NULL);

-- Update some products to have low stock scenarios for testing
UPDATE variants SET stock_qty = 2, reserved_qty = 0 WHERE sku = 'PU-357G-2018'; -- Low stock
UPDATE variants SET stock_qty = 1, reserved_qty = 1 WHERE sku = 'JP-100G-2024'; -- Out of stock (all reserved)
UPDATE variants SET stock_qty = 3, reserved_qty = 1 WHERE sku = 'WP-100G-2024'; -- Low stock with reservation