-- Initial Data Seeding
INSERT INTO serviceable_locations (id, pincode, city, state) VALUES (gen_random_uuid(), '500001', 'Hyderabad', 'Telangana') ON CONFLICT (pincode) DO NOTHING;
INSERT INTO serviceable_locations (id, pincode, city, state) VALUES (gen_random_uuid(), '560001', 'Bengaluru', 'Karnataka') ON CONFLICT (pincode) DO NOTHING;

INSERT INTO vehicle_makes (vehicle_type, make_name) VALUES ('TWO_WHEELER', 'Hero') ON CONFLICT (vehicle_type, make_name) DO NOTHING;
INSERT INTO vehicle_makes (vehicle_type, make_name) VALUES ('FOUR_WHEELER', 'Maruti Suzuki') ON CONFLICT (vehicle_type, make_name) DO NOTHING;

INSERT INTO vehicle_models (make_id, model_name) 
SELECT id, 'Splendor Plus' FROM vehicle_makes WHERE make_name='Hero'
ON CONFLICT DO NOTHING;

INSERT INTO recharge_packages (id, name, price_in_inr, base_credits, bonus_credits, popular, active)
VALUES (gen_random_uuid(), 'Starter Pack', 1000, 100, 10, true, true)
ON CONFLICT DO NOTHING;
