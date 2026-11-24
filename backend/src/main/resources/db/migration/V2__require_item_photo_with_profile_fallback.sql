-- Complete old item records before enforcing the required photo column.
UPDATE public.item item
SET foto = COALESCE(
    NULLIF(BTRIM(item.foto), ''),
    NULLIF(BTRIM(usuario.foto), ''),
    'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/github/github-original.svg'
)
FROM public.usuario usuario
WHERE usuario.id = item.usuario_id
  AND (item.foto IS NULL OR BTRIM(item.foto) = '');

ALTER TABLE public.item ALTER COLUMN foto SET NOT NULL;
