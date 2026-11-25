
import { MapPin, PackageSearch } from "lucide-react"
import { useState } from "react"
import { Link } from "react-router"
import dayjs from "dayjs"
import type { Item } from "@/types/item"

type ItemCardProps = {
  item: Item
}

const statusLabels: Record<Item["status"], string> = {
  perdido: "Perdido",
  encontrado: "Achado",
  recuperado: "Recuperado",
}

const statusStyles: Record<Item["status"], string> = {
  perdido: "bg-rose-100 text-rose-800",
  encontrado: "bg-emerald-100 text-emerald-800",
  recuperado: "bg-blue-100 text-blue-800",
}

export default function ItemCard({ item }: ItemCardProps) {
  const [imagemComErro, setImagemComErro] = useState(false)

  return (
    <Link
      to={`/item/${item.idItem}`}
      state={{ item }}
      className="block h-full text-inherit no-underline"
    >
      <article className="flex h-full flex-col overflow-hidden rounded-2xl transition-shadow hover:shadow-md">

        {/* Foto do item */}
        <div className="relative flex aspect-square items-center justify-center overflow-hidden bg-muted">
          {item.foto && !imagemComErro ? (
            <img
              src={item.foto}
              alt={item.descricao || item.nome}
              className="h-full w-full object-cover"
              loading="lazy"
              onError={() => setImagemComErro(true)}
            />
          ) : (
            <div
              className="flex flex-col items-center gap-2 text-muted-foreground"
              aria-label="Sem foto disponível"
            >
              <PackageSearch className="size-10" />
              <span className="text-sm">Sem foto</span>
            </div>
          )}

          {/* Status */}
          <span
            className={`absolute left-3 top-3 rounded-full px-3 py-1 text-xs font-semibold ${
              statusStyles[item.status]
            }`}
          >
            {statusLabels[item.status] ?? "Item"}
          </span>
        </div>

        {/* Informações do item */}
        <div className="flex flex-1 flex-col gap-5 bg-linear-to-b from-[#FFDD87] to-[#FFCA46] px-4 py-6">

          <div className="flex flex-1 flex-col gap-2">

            {/* Nome e tipo */}
            <div className="flex items-start justify-between gap-3">
              <h2
                className="line-clamp-1 text-xl font-semibold"
                title={item.nome}
              >
                {item.nome}
              </h2>

              <span className="shrink-0 rounded-full bg-white/50 px-2.5 py-1 text-xs font-medium">
                {item.tipo}
              </span>
            </div>

            {/* Descrição */}
            <p className="line-clamp-3 text-sm text-neutral-700">
              {item.descricao?.trim() || "Sem descrição."}
            </p>

            {/* Local */}
            <div className="flex items-center gap-2 text-sm text-neutral-700">
              <MapPin className="size-4 shrink-0" />
              <span className="line-clamp-1">
                {item.local?.trim() || "Local não informada"}
              </span>
            </div>

            {/* Data */}
            <span
              className="text-sm font-medium text-muted-foreground"
              title={item.dataOcorrencia || ""}
            >
              {item.dataOcorrencia
                ? dayjs(item.dataOcorrencia).fromNow()
                : "Data não informada"}
            </span>
          </div>

          {/* Criador */}
          <div className="flex items-center gap-2">
            {item.criadorFoto ? (
              <img
                src={item.criadorFoto}
                alt=""
                className="size-6 rounded-full object-cover"
              />
            ) : (
              <div className="size-6 rounded-full bg-white/60" />
            )}

            <span className="text-xs font-medium">
              {item.criadorNome}
            </span>
          </div>

        </div>
      </article>
    </Link>
  )
}
