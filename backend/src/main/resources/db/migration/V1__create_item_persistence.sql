-- Base required by item: account and its PostgreSQL enum types.
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type t JOIN pg_namespace n ON n.oid = t.typnamespace
                   WHERE n.nspname = 'public' AND t.typname = 'campus_enum') THEN
        CREATE TYPE public.campus_enum AS ENUM (
            'sp-butanta', 'sp-centro', 'sp-quadrilatero', 'sp-leste',
            'piracicaba', 'sao-carlos', 'ribeirao-preto', 'lorena',
            'bauru', 'pirassununga', 'santos', 'outro'
        );
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_type t JOIN pg_namespace n ON n.oid = t.typnamespace
                   WHERE n.nspname = 'public' AND t.typname = 'status_conta_enum') THEN
        CREATE TYPE public.status_conta_enum AS ENUM ('ativa', 'banida', 'suspensa', 'inativa');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_type t JOIN pg_namespace n ON n.oid = t.typnamespace
                   WHERE n.nspname = 'public' AND t.typname = 'status_item_enum') THEN
        CREATE TYPE public.status_item_enum AS ENUM ('perdido', 'encontrado', 'recuperado');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_type t JOIN pg_namespace n ON n.oid = t.typnamespace
                   WHERE n.nspname = 'public' AND t.typname = 'status_denuncia_enum') THEN
        CREATE TYPE public.status_denuncia_enum AS ENUM ('pendente', 'aceita', 'rejeitada');
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.usuario (
    id bigint GENERATED ALWAYS AS IDENTITY,
    nome character varying(100) NOT NULL,
    email_usp character varying(100) NOT NULL,
    telefone character varying(20),
    campus public.campus_enum,
    status_da_conta public.status_conta_enum DEFAULT 'ativa',
    foto text,
    data_cadastro timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT usuario_email_usp_key UNIQUE (email_usp)
);

CREATE TABLE IF NOT EXISTS public.item (
    id bigint GENERATED ALWAYS AS IDENTITY,
    nome character varying(100) NOT NULL,
    foto text,
    local character varying(150),
    status public.status_item_enum NOT NULL,
    descricao text,
    tipo character varying(50) NOT NULL,
    data_registro timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    data_ocorrencia date,
    usuario_id bigint NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (id)
);

-- Existing installations may have the older NOT NULL constraint on photo.
ALTER TABLE public.item ALTER COLUMN foto DROP NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fk_item_usuario_id' AND conrelid = 'public.item'::regclass
    ) THEN
        ALTER TABLE public.item
            ADD CONSTRAINT fk_item_usuario_id
            FOREIGN KEY (usuario_id) REFERENCES public.usuario(id)
            ON UPDATE CASCADE ON DELETE CASCADE;
    END IF;
END $$;
