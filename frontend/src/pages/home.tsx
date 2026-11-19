import { Button } from "@/components/ui/button";

export default function Home() {
  return (
    <>
      <section className="absolute top-0 left-0 right-0">
        <div className="flex flex-col items-center justify-center gap-4 px-4 h-96 bg-sky-500">
          <h1 className="text-center text-6xl m:text-4xl text-white font-medium">
            Achados e Perdidos
          </h1>

          <div className="flex gap-4">
            <Button size={"lg"} className={"w-24"}>
              Achei
            </Button>

            <Button size={"lg"} className="bg-[#4905FF] text-white px-5 w-24">
              Perdi
            </Button>
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-6xl px-4 py-8">
        {/* Dados futuros */}
      </section>

      <section className="mx-auto max-w-6xl px-4 pb-12">
        <h2 className="mb-4 text-xl font-bold">Recentes</h2>
        {/* Dados futuros */}
      </section>
    </>
  )
}