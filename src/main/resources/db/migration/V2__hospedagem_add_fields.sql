/* Add additional fields to hospedagem for conversation data */
ALTER TABLE hospedagem
  ADD COLUMN IF NOT EXISTS apartamento varchar(100),
  ADD COLUMN IF NOT EXISTS responsavel varchar(255),
  ADD COLUMN IF NOT EXISTS qtd_hospedes integer,
  ADD COLUMN IF NOT EXISTS placa varchar(50),
  ADD COLUMN IF NOT EXISTS observacao text;

CREATE INDEX IF NOT EXISTS idx_hospedagem_apartamento ON hospedagem(apartamento);
CREATE INDEX IF NOT EXISTS idx_hospedagem_qtd ON hospedagem(qtd_hospedes);

