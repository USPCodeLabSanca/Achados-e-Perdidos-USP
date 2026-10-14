
export default function Footer() {
  return (
    <footer className="bg-[#FFC943] px-6 py-8">
      <div className="mx-auto flex max-w-6xl flex-col items-center justify-between gap-6 sm:flex-row">
        <a href="/" className="text-2xl font-bold text-white">
          USPerdi
        </a>

        <div className="flex flex-col items-center gap-2 sm:items-end">
          <h2 className="font-semibold text-neutral-900">
            Mapa do site
          </h2>

          <nav className="flex gap-4 text-sm text-neutral-800">
            <a href="/" className="hover:underline">
              Início
            </a>
            <a href="#recentes" className="hover:underline">
              Recentes
            </a>
          </nav>
        </div>
      </div>

      <p className="mt-6 text-center text-xs text-neutral-700">
        © 2026 USPerdi — Achados e Perdidos
      </p>
    </footer>
  )
}