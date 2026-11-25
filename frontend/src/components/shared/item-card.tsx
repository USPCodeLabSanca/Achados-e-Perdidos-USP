
import { MapPin, PackageSearch } from "lucide-react"
import { useState } from "react"
import { Link } from "react-router"
import type { Item } from "@/types/item"

type ItemCardProps = {
  item: Item
}

const statusLabels: Record<Item["status"], string> = {
  perdido: "Perdido",
  encontrado: "Achado",
  recuperado: "Recuperado",
}

export default function ItemCard({ item }: ItemCardProps) {
  const [imagemComErro, setImagemComErro] = useState(false)

  const statusStyle = item.status === "perdido"
    ? "bg-rose-100 text-rose-800"
    : "bg-emerald-100 text-emerald-800"

  return (
    <Link
      to={`/item/${item.idItem}`}
      state={{ item }}
      className="block h-full text-inherit no-underline"
    >
      <article className="h-full overflow-hidden rounded-2xl border bg-card text-card-foreground shadow-sm transition-shadow hover:shadow-md">
        <div className="relative flex aspect-[4/3] items-center justify-center bg-muted">
          {item.foto && !imagemComErro ? (
            <img
              src={item.foto}
              alt={item.nome}
              className="h-full w-full object-cover"
              loading="lazy"
              onError={() => setImagemComErro(true)}
            />
          ) : (
            <div className="flex flex-col items-center gap-2 text-muted-foreground" aria-label="Sem foto disponível">
              <PackageSearch aria-hidden="true" className="size-10" />
              <span className="text-sm">Sem foto</span>
            </div>
          )}

          <span className={`absolute left-3 top-3 rounded-full px-3 py-1 text-xs font-semibold ${statusStyle}`}>
            {statusLabels[item.status] ?? "Item"}
          </span>
        </div>

        <div className="flex min-h-48 flex-col p-4">
          <div className="mb-2 flex items-start justify-between gap-3">
            <h2 className="line-clamp-1 text-lg font-semibold" title={item.nome}>
              {item.nome}
            </h2>

            <span className="shrink-0 rounded-full bg-muted px-2.5 py-1 text-xs text-muted-foreground">
              {item.tipo}
            </span>
          </div>

          <p className="mb-4 line-clamp-3 flex-1 text-sm text-muted-foreground">
            {item.descricao?.trim() || "Sem descrição."}
          </p>

          <div className="flex items-center gap-2 text-sm text-muted-foreground">
            <MapPin aria-hidden="true" className="size-4 shrink-0" />
            <span className="line-clamp-1">
              {item.local?.trim() || "Local não informado"}
            </span>
          </div>
        </div>
      </article>
    </Link>
  )
}
