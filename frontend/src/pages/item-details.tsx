
import {
  ArrowLeft,
  CalendarDays,
  Mail,
  MapPin,
  PackageSearch,
  Phone,
  UserRound,
} from "lucide-react"
import { Link, useLocation } from "react-router"
import type { Item } from "@/types/item"

const statusLabels: Record<Item["status"], string> = {
  perdido: "Perdido",
  encontrado: "Achado",
  recuperado: "Recuperado",
}

const statusStyles: Record<Item["status"], string> = {
  perdido: "bg-rose-100 text-rose-800",
  encontrado: "bg-emerald-100 text-emerald-800",
  recuperado: "bg-slate-100 text-slate-700",
}

function formatarData(data: string | null) {
  if (!data) return "Data não informada"

  const dataFormatada = new Date(
    data.length === 10 ? `${data}T00:00:00` : data
  )

  if (Number.isNaN(dataFormatada.getTime())) return data

  return dataFormatada.toLocaleDateString("pt-BR")
}

export default function ItemDetails() {
  const location = useLocation()
  const item = (location.state as { item?: Item } | null)?.item

  // Caso a página seja aberta diretamente ou atualizada,
  // os dados não estarão disponíveis no state.
  if (!item) {
    return (
      <main className="min-h-screen bg-[#FFF0D8] px-4 py-12">
        <div className="mx-auto max-w-2xl rounded-2xl bg-white p-8 text-center shadow-sm">
          <PackageSearch className="mx-auto mb-4 size-12 text-muted-foreground" />

          <h1 className="mb-2 text-2xl font-bold">
            Item não encontrado
          </h1>

          <p className="mb-6 text-muted-foreground">
            Não foi possível carregar as informações deste item.
            Volte à página inicial e tente novamente.
          </p>

          <Link
            to="/"
            className="inline-flex items-center gap-2 rounded-lg bg-violet-600 px-5 py-3 font-semibold text-white no-underline transition-colors hover:bg-violet-700"
          >
            <ArrowLeft className="size-4" />
            Voltar para a página inicial
          </Link>
        </div>
      </main>
    )
  }

  return (
    <main className="min-h-screen bg-[#FFF0D8] px-4 py-8 md:px-8">
      <div className="mx-auto max-w-6xl">

        <Link
          to="/"
          className="mb-6 inline-flex items-center gap-2 font-medium text-violet-700 no-underline hover:text-violet-900"
        >
          <ArrowLeft className="size-5" />
          Voltar para a página inicial
        </Link>

        <div className="grid gap-6 lg:grid-cols-[1.2fr_0.8fr]">

          {/* Foto do item */}
          <section className="overflow-hidden rounded-2xl bg-white shadow-sm">
            <div className="relative flex min-h-72 items-center justify-center bg-muted md:min-h-[480px]">
              {item.foto ? (
                <img
                  src={item.foto}
                  alt={item.nome}
                  className="h-full max-h-[600px] w-full object-contain"
                />
              ) : (
                <div className="flex flex-col items-center gap-3 text-muted-foreground">
                  <PackageSearch className="size-16" />
                  <span>Sem foto disponível</span>
                </div>
              )}
            </div>
          </section>

          {/* Informações do item */}
          <div className="flex flex-col gap-6">

            <section className="rounded-2xl bg-white p-6 shadow-sm">
              <div className="mb-4 flex flex-wrap items-center gap-2">
                <span className={`rounded-full px-3 py-1 text-sm font-semibold ${statusStyles[item.status]}`}>
                  {statusLabels[item.status]}
                </span>

                <span className="rounded-full bg-violet-100 px-3 py-1 text-sm font-medium text-violet-800">
                  {item.tipo}
                </span>
              </div>

              <h1 className="mb-5 text-3xl font-bold text-neutral-900">
                {item.nome}
              </h1>

              <div className="mb-5 space-y-3 text-sm text-muted-foreground">
                <div className="flex items-center gap-3">
                  <MapPin className="size-5 shrink-0 text-violet-600" />
                  <span>{item.local?.trim() || "Local não informado"}</span>
                </div>

                <div className="flex items-center gap-3">
                  <CalendarDays className="size-5 shrink-0 text-violet-600" />
                  <span>{formatarData(item.dataOcorrencia)}</span>
                </div>
              </div>

              <div className="border-t pt-5">
                <h2 className="mb-3 text-lg font-semibold text-neutral-900">
                  Descrição
                </h2>

                <p className="whitespace-pre-wrap break-words text-sm leading-relaxed text-muted-foreground">
                  {item.descricao?.trim() || "Nenhuma descrição foi informada."}
                </p>
              </div>
            </section>

            {/* Informações de quem cadastrou */}
            <section className="rounded-2xl bg-white p-6 shadow-sm">
              <h2 className="mb-5 text-lg font-semibold text-neutral-900">
                Cadastrado por
              </h2>

              <div className="mb-5 flex items-center gap-3">
                {item.criadorFoto ? (
                  <img
                    src={item.criadorFoto}
                    alt={item.criadorNome}
                    className="size-12 rounded-full object-cover"
                  />
                ) : (
                  <div className="flex size-12 items-center justify-center rounded-full bg-violet-100 text-violet-700">
                    <UserRound className="size-6" />
                  </div>
                )}

                <span className="font-semibold text-neutral-900">
                  {item.criadorNome}
                </span>
              </div>

              <div className="space-y-4 text-sm">
                <a
                  href={`mailto:${item.criadorEmail}`}
                  className="flex items-center gap-3 break-all text-violet-700 no-underline hover:underline"
                >
                  <Mail className="size-5 shrink-0" />
                  {item.criadorEmail}
                </a>

                {item.criadorTelefone && (
                  <a
                    href={`tel:${item.criadorTelefone}`}
                    className="flex items-center gap-3 text-violet-700 no-underline hover:underline"
                  >
                    <Phone className="size-5 shrink-0" />
                    {item.criadorTelefone}
                  </a>
                )}
              </div>
            </section>

          </div>
        </div>
      </div>
    </main>
  )
}
