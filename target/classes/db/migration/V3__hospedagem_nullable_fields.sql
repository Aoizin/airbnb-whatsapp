/* Make hospede_id, checkin_date, and checkout_date nullable for progressive saving during conversation */
ALTER TABLE hospedagem ALTER COLUMN hospede_id DROP NOT NULL;
ALTER TABLE hospedagem ALTER COLUMN checkin_date DROP NOT NULL;
ALTER TABLE hospedagem ALTER COLUMN checkout_date DROP NOT NULL;
