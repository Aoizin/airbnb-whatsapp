/* Flyway migration: create schema for Hospede, Hospedagem, SessaoWhatsapp */
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS hospede (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    nome varchar(255) NOT NULL,
    email varchar(255),
    telefone varchar(50) NOT NULL,
    documento varchar(100),
    criado_em timestamptz NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_hospede_email ON hospede(email) WHERE email IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_hospede_telefone ON hospede(telefone);

CREATE TABLE IF NOT EXISTS sessao_whatsapp (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    external_id varchar(255) UNIQUE,
    phone_number varchar(50) NOT NULL,
    state varchar(50) NOT NULL,
    started_at timestamptz NOT NULL DEFAULT now(),
    last_activity_at timestamptz,
    hospede_id uuid NULL,
    CONSTRAINT fk_sessao_hospede FOREIGN KEY (hospede_id) REFERENCES hospede(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_sessao_phone ON sessao_whatsapp(phone_number);
CREATE INDEX IF NOT EXISTS idx_sessao_external ON sessao_whatsapp(external_id);

CREATE TABLE IF NOT EXISTS hospedagem (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    hospede_id uuid NOT NULL,
    checkin_date date NOT NULL,
    checkout_date date NOT NULL,
    status varchar(50) NOT NULL,
    origem varchar(50) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz,
    sessao_whatsapp_id uuid UNIQUE,
    CONSTRAINT fk_hospedagem_hospede FOREIGN KEY (hospede_id) REFERENCES hospede(id) ON DELETE RESTRICT,
    CONSTRAINT fk_hospedagem_sessao FOREIGN KEY (sessao_whatsapp_id) REFERENCES sessao_whatsapp(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_hospedagem_status ON hospedagem(status);
CREATE INDEX IF NOT EXISTS idx_hospedagem_hospede ON hospedagem(hospede_id);

