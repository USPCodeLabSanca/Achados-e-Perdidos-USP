
import { Link } from "react-router"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"

export default function LostItem() {
  return (
    <div className="flex flex-1 flex-col bg-[#FFF0D8]">
      {/* Banner */}
      <section className="flex min-h-48 flex-col items-center justify-center gap-4 rounded-b-2xl bg-sky-500 px-4 text-white">
        <h1 className="text-center text-3xl font-medium sm:text-4xl">
          Achados e Perdidos
        </h1>

        <div className="flex gap-4">
            <Link
            to="/cadastro/encontrado"
            className="inline-flex h-9 w-24 items-center justify-center rounded-lg bg-primary px-2.5 text-sm font-medium text-primary-foreground no-underline hover:bg-primary/80"
            >
                Achei
            </Link>

            <Button
            size="lg"
            className="w-24 bg-violet-700 text-white hover:bg-violet-800"
            >
                Perdi
            </Button>
        </div>
      </section>

      {/* Formulário */}
      <section className="mx-auto w-full max-w-6xl px-4 py-8">
        <h2 className="mb-3 font-semibold">
          Relatar um Item Perdido:
        </h2>

        <form className="grid gap-10 rounded-md bg-white p-6 md:grid-cols-2 md:p-10">
          {/* Campos */}
          <div className="flex flex-col gap-6">
            <div className="flex flex-col gap-2">
              <label htmlFor="item" className="text-sm">
                Item:
              </label>
              <Input
                id="item"
                name="item"
                placeholder="Nome do item"
              />
            </div>

            <div className="flex flex-col gap-2">
              <label htmlFor="location" className="text-sm">
                Último local onde foi visto:
              </label>
              <Input
                id="location"
                name="location"
                placeholder="Local"
              />
            </div>

            <div className="flex flex-col gap-2">
              <label htmlFor="date" className="text-sm">
                Data em que perdeu:
              </label>
              <Input
                id="date"
                name="date"
                type="date"
              />
            </div>

            <div className="flex flex-col gap-2">
              <label htmlFor="description" className="text-sm">
                Descrição / Observações:
              </label>
              <Textarea
                id="description"
                name="description"
                placeholder="Descreva o item e outras informações importantes..."
                className="min-h-32 resize-y"
              />
            </div>
          </div>

          {/* Foto e botões */}
          <div className="flex flex-col gap-6">
            <div className="flex flex-col gap-2">
              <label htmlFor="photo" className="text-sm">
                Foto:
              </label>

              <label
                htmlFor="photo"
                className="flex min-h-64 cursor-pointer flex-col items-center justify-center gap-2 rounded-md bg-neutral-200 text-center transition hover:bg-neutral-300"
              >
                <span className="text-3xl text-amber-400">★</span>
                <span className="text-sm text-neutral-800">
                  Adicionar imagem
                </span>
                <input
                  id="photo"
                  name="photo"
                  type="file"
                  accept="image/*"
                  className="sr-only"
                />
              </label>
            </div>

            <div className="mt-auto flex justify-end gap-3">
              <Link
                to="/"
                className="inline-flex h-9 w-24 items-center justify-center rounded-lg border border-neutral-300 text-sm no-underline hover:bg-neutral-100"
              >
                Cancelar
              </Link>

              <Button
                type="button"
                className="w-24 bg-violet-700 text-white hover:bg-violet-800"
              >
                Postar
              </Button>
            </div>
          </div>
        </form>
      </section>
    </div>
  )
}