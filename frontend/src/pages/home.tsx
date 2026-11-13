import { Button } from "@/components/ui/button";
import { Link } from "react-router"

export default function Home() {
  	return (
    <>
		<section className="absolute top-0 left-0 right-0">
			<div className="flex flex-col items-center justify-center gap-4 px-4 h-96 bg-sky-500">
				<h1 className="text-center text-6xl m:text-4xl text-white font-medium">
					Achados e Perdidos
				</h1>

				<div className="flex gap-4">
					<Link
					to="/cadastro/encontrado"
					className="inline-flex h-9 w-24 items-center justify-center rounded-lg bg-primary px-2.5 text-sm font-medium text-primary-foreground no-underline transition-colors hover:bg-primary/80"
					>
						Achei
					</Link>

					<Link
					to="/cadastro/perdido"
					className="inline-flex h-9 w-24 items-center justify-center rounded-lg bg-violet-700 px-2.5 text-sm font-medium text-white no-underline transition-colors hover:bg-violet-800"
					>
						Perdi
					</Link>
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