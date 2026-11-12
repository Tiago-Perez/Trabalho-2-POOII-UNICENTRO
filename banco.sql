CREATE TABLE IF NOT EXISTS users (
    userid SERIAL PRIMARY KEY,
    nome   VARCHAR(255) NOT NULL,
    senha  VARCHAR(255) NOT NULL,
    CONSTRAINT usuario_nome_unico UNIQUE (nome)
);

CREATE TABLE IF NOT EXISTS notas (
    notaid SERIAL PRIMARY KEY,
    userid INTEGER NOT NULL,
    nome   VARCHAR(255) NOT NULL,
    texto  TEXT,
    fixado BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT notas_userid_fkey FOREIGN KEY (userid)
        REFERENCES users (userid)
        ON DELETE CASCADE
);
