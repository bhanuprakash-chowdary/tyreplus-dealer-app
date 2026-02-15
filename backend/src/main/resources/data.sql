-- Initial Data Seeding
INSERT INTO serviceable_locations (id, pincode, city, state) VALUES (gen_random_uuid(), '500001', 'Hyderabad', 'Telangana') ON CONFLICT (pincode) DO NOTHING;
INSERT INTO serviceable_locations (id, pincode, city, state) VALUES (gen_random_uuid(), '560001', 'Bengaluru', 'Karnataka') ON CONFLICT (pincode) DO NOTHING;
INSERT INTO serviceable_locations (id, pincode, city, state) VALUES (gen_random_uuid(), '400001', 'Mumbai', 'Maharashtra') ON CONFLICT (pincode) DO NOTHING;

-- Vehicle Makes
INSERT INTO vehicle_makes (vehicle_type, make_name, image_url) VALUES ('TWO_WHEELER', 'Hero', 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/6f/Hero_MotoCorp.svg/1200px-Hero_MotoCorp.svg.png') ON CONFLICT (vehicle_type, make_name) DO NOTHING;
INSERT INTO vehicle_makes (vehicle_type, make_name, image_url) VALUES ('TWO_WHEELER', 'Honda', 'https://upload.wikimedia.org/wikipedia/commons/thumb/7/7b/Honda_Logo.svg/1200px-Honda_Logo.svg.png') ON CONFLICT (vehicle_type, make_name) DO NOTHING;
INSERT INTO vehicle_makes (vehicle_type, make_name, image_url) VALUES ('FOUR_WHEELER', 'Maruti Suzuki', 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/1e/Maruti_Suzuki_logo.svg/1200px-Maruti_Suzuki_logo.svg.png') ON CONFLICT (vehicle_type, make_name) DO NOTHING;

-- Vehicle Models
INSERT INTO vehicle_models (make_id, model_name) SELECT id, 'Splendor Plus' FROM vehicle_makes WHERE make_name='Hero' ON CONFLICT DO NOTHING;
INSERT INTO vehicle_models (make_id, model_name) SELECT id, 'Activa 6G' FROM vehicle_makes WHERE make_name='Honda' ON CONFLICT DO NOTHING;
INSERT INTO vehicle_models (make_id, model_name) SELECT id, 'Swift' FROM vehicle_makes WHERE make_name='Maruti Suzuki' ON CONFLICT DO NOTHING;

-- Recharge Packages
INSERT INTO recharge_packages (id, name, price_in_inr, base_credits, bonus_credits, popular, active)
VALUES (gen_random_uuid(), 'Starter Pack', 1000, 100, 10, false, true)
ON CONFLICT DO NOTHING;

INSERT INTO recharge_packages (id, name, price_in_inr, base_credits, bonus_credits, popular, active)
VALUES (gen_random_uuid(), 'Pro Dealer Pack', 5000, 500, 100, true, true)
ON CONFLICT DO NOTHING;

-- Dummy Dealers
INSERT INTO dealers (id, business_name, owner_name, is_verified, email, phone_number, street, city, state, zip_code, country, opening_time, closing_time)
VALUES ('d1000000-0000-0000-0000-000000000001', 'Best Tyres Hyderabad', 'Ramesh Kumar', true, 'ramesh@besttyres.com', '9876543210', 'Road No 1, Banjara Hills', 'Hyderabad', 'Telangana', '500034', 'India', '09:00:00', '20:00:00')
ON CONFLICT (phone_number) DO NOTHING;

INSERT INTO dealers (id, business_name, owner_name, is_verified, email, phone_number, street, city, state, zip_code, country, opening_time, closing_time)
VALUES ('d1000000-0000-0000-0000-000000000002', 'Speedy Wheels Bangalore', 'Suresh Reddy', false, 'suresh@speedy.com', '9876543211', 'Indiranagar 100ft Road', 'Bengaluru', 'Karnataka', '560038', 'India', '10:00:00', '21:00:00')
ON CONFLICT (phone_number) DO NOTHING;

-- Wallets for Dealers
INSERT INTO wallets (id, dealer_id, purchased_credits, bonus_credits)
VALUES (gen_random_uuid(), 'd1000000-0000-0000-0000-000000000001', 500, 50)
ON CONFLICT (dealer_id) DO NOTHING;

INSERT INTO wallets (id, dealer_id, purchased_credits, bonus_credits)
VALUES (gen_random_uuid(), 'd1000000-0000-0000-0000-000000000002', 0, 0)
ON CONFLICT (dealer_id) DO NOTHING;

-- Dummy Leads
INSERT INTO leads (id, customer_name, customer_phone, customer_email, vehicle_model, vehicle_year, status, lead_cost, created_at)
VALUES (gen_random_uuid(), 'Amit Sharma', '9988776655', 'amit@gmail.com', 'Swift Dzire', '2019', 'NEW', 50, NOW() - INTERVAL '2 hours');

INSERT INTO leads (id, customer_name, customer_phone, customer_email, vehicle_model, vehicle_year, status, lead_cost, created_at)
VALUES (gen_random_uuid(), 'Priya Singh', '9988776644', 'priya@yahoo.com', 'Honda Activa', '2021', 'NEW', 25, NOW() - INTERVAL '5 hours');

INSERT INTO leads (id, customer_name, customer_phone, customer_email, vehicle_model, vehicle_year, status, lead_cost, created_at, purchased_by_dealer_id, purchased_at)
VALUES (gen_random_uuid(), 'Rahul Verma', '9988776633', 'rahul@outlook.com', 'Hyundai Creta', '2022', 'SOLD', 75, NOW() - INTERVAL '1 day', 'd1000000-0000-0000-0000-000000000001', NOW() - INTERVAL '20 hours');

-- Tyres Inventory
INSERT INTO tyres (id, brand, pattern, size, price, product_code, features, image_url, warranty_years)
VALUES (gen_random_uuid(), 'MRF', 'Zapper', '90/100-10', 1200.00, 'MRF-ZAP-001', 'Long lasting, Good grip', 'https://m.media-amazon.com/images/I/51p+fTzJcRL._AC_UF1000,1000_QL80_.jpg', 3);

INSERT INTO tyres (id, brand, pattern, size, price, product_code, features, image_url, warranty_years)
VALUES (gen_random_uuid(), 'CEAT', 'Milaze', '145/80 R12', 2500.00, 'CEAT-MIL-002', 'Fuel efficient, Durable', 'https://m.media-amazon.com/images/I/51-u7w-DqCL._AC_UF1000,1000_QL80_.jpg', 5);

INSERT INTO tyres (id, brand, pattern, size, price, product_code, features, image_url, warranty_years)
VALUES (gen_random_uuid(), 'Apollo', 'Amazer', '165/80 R14', 3200.00, 'APO-AMA-003', 'Comfortable ride, Low noise', 'https://m.media-amazon.com/images/I/61r-vj+jZPL._AC_UF1000,1000_QL80_.jpg', 5);

INSERT INTO tyres (id, brand, pattern, size, price, product_code, features, image_url, warranty_years)
VALUES (gen_random_uuid(), 'JK Tyre', 'Taxi Max', '4.00-8', 1500.00, 'JK-TAX-004', 'Heavy Load, Long Life', 'https://m.media-amazon.com/images/I/51+9+8+7+6L._AC_UF1000,1000_QL80_.jpg', 2);

INSERT INTO tyres (id, brand, pattern, size, price, product_code, features, image_url, warranty_years)
VALUES (gen_random_uuid(), 'Michelin', 'City Pro', '2.75-17', 1800.00, 'MICH-CIT-005', 'Puncture Resistant', 'https://m.media-amazon.com/images/I/71+9+8+7+6L._AC_UF1000,1000_QL80_.jpg', 4);

-- Flattened Vehicles Data (Required for VehicleController APIs)
-- 2W
INSERT INTO vehicles (id, type, make, model, variant, tyre_size) VALUES (gen_random_uuid(), '2W', 'Hero', 'Splendor Plus', 'Kick Start', '80/100-18');
INSERT INTO vehicles (id, type, make, model, variant, tyre_size) VALUES (gen_random_uuid(), '2W', 'Hero', 'HF Deluxe', 'Self Start', '2.75-18');
INSERT INTO vehicles (id, type, make, model, variant, tyre_size) VALUES (gen_random_uuid(), '2W', 'Honda', 'Activa 6G', 'Standard', '90/100-10');
INSERT INTO vehicles (id, type, make, model, variant, tyre_size) VALUES (gen_random_uuid(), '2W', 'Honda', 'Shine', 'Disc', '80/100-18');
INSERT INTO vehicles (id, type, make, model, variant, tyre_size) VALUES (gen_random_uuid(), '2W', 'Royal Enfield', 'Classic 350', 'Dual Channel ABS', '90/90-19');
INSERT INTO vehicles (id, type, make, model, variant, tyre_size) VALUES (gen_random_uuid(), '2W', 'TVS', 'Jupiter', 'Classic', '90/90-12');

-- 3W
INSERT INTO vehicles (id, type, make, model, variant, tyre_size) VALUES (gen_random_uuid(), '3W', 'Bajaj', 'RE Compact', 'CNG', '4.00-8');
INSERT INTO vehicles (id, type, make, model, variant, tyre_size) VALUES (gen_random_uuid(), '3W', 'Bajaj', 'Maxima', 'Diesel', '4.50-10');
INSERT INTO vehicles (id, type, make, model, variant, tyre_size) VALUES (gen_random_uuid(), '3W', 'Piaggio', 'Ape City', 'Petrol', '4.00-8');
INSERT INTO vehicles (id, type, make, model, variant, tyre_size) VALUES (gen_random_uuid(), '3W', 'Mahindra', 'Alfa', 'Passenger', '4.50-10');

-- 4W
INSERT INTO vehicles (id, type, make, model, variant, tyre_size) VALUES (gen_random_uuid(), '4W', 'Maruti Suzuki', 'Swift', 'LXu', '165/80 R14');
INSERT INTO vehicles (id, type, make, model, variant, tyre_size) VALUES (gen_random_uuid(), '4W', 'Maruti Suzuki', 'Baleno', 'Delta', '185/65 R15');
INSERT INTO vehicles (id, type, make, model, variant, tyre_size) VALUES (gen_random_uuid(), '4W', 'Hyundai', 'Creta', 'SX', '205/65 R16');
INSERT INTO vehicles (id, type, make, model, variant, tyre_size) VALUES (gen_random_uuid(), '4W', 'Hyundai', 'i20', 'Sportz', '195/55 R16');
INSERT INTO vehicles (id, type, make, model, variant, tyre_size) VALUES (gen_random_uuid(), '4W', 'Tata', 'Nexon', 'XZ', '215/60 R16');
INSERT INTO vehicles (id, type, make, model, variant, tyre_size) VALUES (gen_random_uuid(), '4W', 'Mahindra', 'XUV700', 'AX5', '235/65 R17');
