-- Keep PostgreSQL enum labels aligned with the Java Campus enum names.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_enum
        WHERE enumtypid = 'public.campus_enum'::regtype AND enumlabel = 'sp-butanta'
    ) AND NOT EXISTS (
        SELECT 1 FROM pg_enum
        WHERE enumtypid = 'public.campus_enum'::regtype AND enumlabel = 'sp_butanta'
    ) THEN
        ALTER TYPE public.campus_enum RENAME VALUE 'sp-butanta' TO 'sp_butanta';
    END IF;

    IF EXISTS (
        SELECT 1 FROM pg_enum
        WHERE enumtypid = 'public.campus_enum'::regtype AND enumlabel = 'sp-centro'
    ) AND NOT EXISTS (
        SELECT 1 FROM pg_enum
        WHERE enumtypid = 'public.campus_enum'::regtype AND enumlabel = 'sp_centro'
    ) THEN
        ALTER TYPE public.campus_enum RENAME VALUE 'sp-centro' TO 'sp_centro';
    END IF;

    IF EXISTS (
        SELECT 1 FROM pg_enum
        WHERE enumtypid = 'public.campus_enum'::regtype AND enumlabel = 'sp-quadrilatero'
    ) AND NOT EXISTS (
        SELECT 1 FROM pg_enum
        WHERE enumtypid = 'public.campus_enum'::regtype AND enumlabel = 'sp_quadrilatero'
    ) THEN
        ALTER TYPE public.campus_enum RENAME VALUE 'sp-quadrilatero' TO 'sp_quadrilatero';
    END IF;

    IF EXISTS (
        SELECT 1 FROM pg_enum
        WHERE enumtypid = 'public.campus_enum'::regtype AND enumlabel = 'sp-leste'
    ) AND NOT EXISTS (
        SELECT 1 FROM pg_enum
        WHERE enumtypid = 'public.campus_enum'::regtype AND enumlabel = 'sp_leste'
    ) THEN
        ALTER TYPE public.campus_enum RENAME VALUE 'sp-leste' TO 'sp_leste';
    END IF;

    IF EXISTS (
        SELECT 1 FROM pg_enum
        WHERE enumtypid = 'public.campus_enum'::regtype AND enumlabel = 'sao-carlos'
    ) AND NOT EXISTS (
        SELECT 1 FROM pg_enum
        WHERE enumtypid = 'public.campus_enum'::regtype AND enumlabel = 'sao_carlos'
    ) THEN
        ALTER TYPE public.campus_enum RENAME VALUE 'sao-carlos' TO 'sao_carlos';
    END IF;

    IF EXISTS (
        SELECT 1 FROM pg_enum
        WHERE enumtypid = 'public.campus_enum'::regtype AND enumlabel = 'ribeirao-preto'
    ) AND NOT EXISTS (
        SELECT 1 FROM pg_enum
        WHERE enumtypid = 'public.campus_enum'::regtype AND enumlabel = 'ribeirao_preto'
    ) THEN
        ALTER TYPE public.campus_enum RENAME VALUE 'ribeirao-preto' TO 'ribeirao_preto';
    END IF;
END $$;