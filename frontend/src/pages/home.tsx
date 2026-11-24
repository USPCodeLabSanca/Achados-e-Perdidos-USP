import axios from "axios"
import { useEffect, useState } from "react"
import { Button } from "@/components/ui/button"
import ItemCard from "@/components/shared/item-card"
import type { Item, PageResponse } from "@/types/item"

const apiUrl = import.meta.env.VITE_API_URL ?? "http://localhost:8080"

export default function Home() {
  const [items, setItems] = useState<Item[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [reloadCount, setReloadCount] = useState(0)

  useEffect(() => {
    const controller = new AbortController()
    const jwt = localStorage.getItem("jwt")

    async function carregarItens() {
      setIsLoading(true)
      setError(null)

      try {
        const response = await axios.get<PageResponse<Item>>(`${apiUrl}/v1/items/recentes`, {
          headers: jwt ? { Authorization: `Bearer ${jwt}` } : {},
          signal: controller.signal,
          params: { page: 0, size: 12 },
        })
        setItems(response.data.content)
      } catch (requestError) {
        if (controller.signal.aborted) return

        setError("Não foi possível carregar os itens. Verifique sua conexão e tente novamente.")
      } finally {
        if (!controller.signal.aborted) setIsLoading(false)
      }
    }

    void carregarItens()
    return () => controller.abort()
  }, [reloadCount])

  return (
    <div className="mx-auto w-full max-w-7xl px-4 pb-12">
      <section className="mb-10 flex min-h-64 flex-col items-center justify-center gap-4 rounded-3xl bg-sky-500 px-4 py-12 text-center text-white">
        <h1 className="text-4xl font-semibold sm:text-6xl">Achados e Perdidos</h1>
        <p className="max-w-2xl text-base sm:text-lg">
          Encontre objetos perdidos ou ajude alguém a recuperar o que encontrou.
        </p>
        <div className="flex gap-4">
          <Button size="lg" className="w-24">Achei</Button>
          <Button size="lg" className="w-24 bg-[#4905FF] text-white hover:bg-[#3d04d6]">Perdi</Button>
        </div>
      </section>

      <section aria-labelledby="itens-recentes-titulo">
        <div className="mb-5 flex items-end justify-between gap-4">
          <div>
            <h2 id="itens-recentes-titulo" className="text-2xl font-bold">Itens recentes</h2>
            <p className="mt-1 text-sm text-muted-foreground">Publicações que ainda estão ativas.</p>
          </div>
        </div>

        {isLoading ? (
          <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4" aria-label="Carregando itens">
            {Array.from({ length: 8 }, (_, index) => (
              <div key={index} className="overflow-hidden rounded-2xl border bg-card">
                <div className="aspect-[4/3] animate-pulse bg-muted" />
                <div className="space-y-3 p-4">
                  <div className="h-5 w-2/3 animate-pulse rounded bg-muted" />
                  <div className="h-12 animate-pulse rounded bg-muted" />
                  <div className="h-4 w-1/2 animate-pulse rounded bg-muted" />
                </div>
              </div>
            ))}
          </div>
        ) : error ? (
          <div role="alert" className="rounded-2xl border border-destructive/30 bg-destructive/5 p-6 text-center">
            <p className="mb-4 text-sm text-destructive">{error}</p>
            <Button onClick={() => setReloadCount((count) => count + 1)}>Tentar novamente</Button>
          </div>
        ) : items.length === 0 ? (
          <div className="rounded-2xl border border-dashed p-10 text-center">
            <h3 className="font-semibold">Nenhum item publicado ainda</h3>
            <p className="mt-2 text-sm text-muted-foreground">Quando houver novas publicações, elas aparecerão aqui.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {items.map((item) => <ItemCard key={item.idItem} item={item} />)}
          </div>
        )}
      </section>
    </div>
  )
}
