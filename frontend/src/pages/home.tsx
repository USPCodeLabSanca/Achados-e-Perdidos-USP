export default function Home() {
  return (
    <main className="min-h-svh bg-[#FFF0D8] text-neutral-900">
      <section className="flex min-h-48 flex-col items-center justify-center gap-4 bg-sky-500 px-4 text-white">
        <h1 className="text-center text-3xl font-semibold sm:text-4xl">
          Achados e Perdidos
        </h1>

        <div className="flex gap-4">
          <button className="min-w-24 rounded-md bg-[#FFC943] px-5 py-2 text-sm">
            Achei
          </button>

          <button className="min-w-24 rounded-md bg-violet-700 px-5 py-2 text-sm">
            Perdi
          </button>
        </div>
      </section>

      <section className="mx-auto max-w-6xl px-4 py-8">
        {/* Dados futuros */}
      </section>

      <section className="mx-auto max-w-6xl px-4 pb-12">
        <h2 className="mb-4 text-xl font-bold">Recentes</h2>
        {/* Dados futuros */}
      </section>
    </main>
  )
}