/* Add foreign key to responsavel table and drop old responsavel column */
ALTER TABLE hospedagem ADD COLUMN IF NOT EXISTS responsavel_id uuid UNIQUE;
ALTER TABLE hospedagem ADD CONSTRAINT fk_hospedagem_responsavel FOREIGN KEY (responsavel_id) REFERENCES responsavel(id) ON DELETE SET NULL;
ALTER TABLE hospedagem DROP COLUMN IF EXISTS responsavel;
