-- ================================
-- Level Up Game Merch Store - Seed Products
-- How to use on a blank SQL tab:
-- 1) Make sure you're connected, then run:  USE levelup_db;
-- 2) Paste this whole INSERT and click the lightning bolt (Execute).
-- 3) Run: SELECT * FROM products;  to see the new rows in the grid.
-- ================================
-- Note: columns align with app/model:
--   - price  (was cost)
--   - partNumber (was part_number)
--   - category added (kept manufacturer)

USE levelup_db;

INSERT INTO products
(name, description, manufacturer, category, partNumber, quantity, price)
VALUES
-- Apparel
('Test Tee', 'LevelUp logo shirt', 'LevelUp Co', 'Apparel', 'LU-TS-001', 25, 19.99),
('Retro Pixel Tee', 'Soft cotton tee with retro pixel art logo', 'LevelUp Co', 'Apparel', 'LU-TS-002', 40, 21.99),
('Boss Fight Hoodie', 'Fleece-lined black hoodie with boss fight graphic', 'LevelUp Co', 'Apparel', 'LU-HD-101', 30, 44.99),
('Speedrunner Cap', 'Adjustable baseball cap with stitched speedrunner badge', 'LevelUp Co', 'Apparel', 'LU-CP-210', 35, 18.99),

-- Accessories
('XP Gain Mug', 'Ceramic coffee mug with “+50 XP” print', 'LevelUp Co', 'Accessories', 'LU-MG-310', 50, 12.99),
('Critical Hit Mousepad', 'Large anti-slip mousepad (900x400mm)', 'LevelUp Co', 'Accessories', 'LU-MP-405', 28, 24.99),
('Quest Log Notebook', 'A5 dotted notebook with elastic band and ribbon', 'LevelUp Co', 'Accessories', 'LU-NB-501', 60, 9.99),
('Mana Potion Water Bottle', 'BPA-free 24oz bottle, potion label design', 'LevelUp Co', 'Accessories', 'LU-WB-520', 32, 16.99),

-- Desk & Decor
('Overworld Desk Mat', 'XL desk mat—map-style print, stitched edges', 'LevelUp Co', 'Desk & Decor', 'LU-DM-610', 22, 29.99),
('8-Bit LED Sign', 'USB-powered LED sign with 8-bit glow effect', 'LevelUp Co', 'Desk & Decor', 'LU-LED-701', 18, 34.99),
('Achievement Poster', '18x24 premium print—“Achievement Unlocked”', 'LevelUp Co', 'Desk & Decor', 'LU-PT-730', 45, 14.99),

-- Collectibles
('Chibi Hero Figurine', 'Hand-painted 10cm PVC figure', 'LevelUp Co', 'Collectibles', 'LU-FG-801', 26, 27.99),
('Enamel Pin: Health Bar', 'Hard enamel pin with rubber clutch', 'LevelUp Co', 'Collectibles', 'LU-PN-812', 80, 7.99),
('Keycap: WASD Set', 'PBT keycap kit (W/A/S/D) with arrows', 'LevelUp Co', 'Collectibles', 'LU-KC-825', 34, 19.49),

-- Gear
('Co-Op Backpack', 'Padded laptop pocket and controller sleeve', 'LevelUp Co', 'Gear', 'LU-BP-910', 15, 49.99),
('Controller Skin - Night Ops', 'Anti-slip silicone skin for popular controllers', 'LevelUp Co', 'Gear', 'LU-CS-940', 36, 12.49),
('Lanyard: Party Finder', 'Black lanyard with white party icons', 'LevelUp Co', 'Gear', 'LU-LY-955', 70, 5.99),

-- Stickers (fun extras)
('Sticker Pack: Loot Drop', '10 glossy vinyl stickers—loot icons', 'LevelUp Co', 'Stickers', 'LU-ST-980', 100, 6.99);

-- Optional dev helper:
-- SELECT * FROM products ORDER BY id;  -- IDs auto-start at 1 by default in MySQL
