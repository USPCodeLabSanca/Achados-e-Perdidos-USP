import Header from "../components/layout/header"
import Footer from "../components/layout/footer"
import { Outlet } from "react-router"

/* Layout das páginas "principais" (com header e footer padrão) */

export default function MainLayout() {
  return (
    <div className="flex min-h-dvh flex-col">
      <Header />
      <main className="flex flex-1 flex-col p-2 typeset">
        <Outlet />
      </main>
      <Footer />
    </div>
  )
}