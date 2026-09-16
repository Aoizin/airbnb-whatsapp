/* Create responsavel table */
CREATE TABLE IF NOT EXISTS responsavel (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    nome varchar(255) NOT NULL,
    cpf varchar(14) NOT NULL,
    telefone varchar(50) NOT NULL,
    apartamentos varchar(255) NOT NULL,
    cadastro_aprovado boolean NOT NULL DEFAULT false
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_responsavel_cpf ON responsavel(cpf);
