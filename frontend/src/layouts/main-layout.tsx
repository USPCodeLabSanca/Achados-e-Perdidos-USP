import Header from "../components/layout/header"
import Footer from "../components/layout/footer"
import { Outlet } from "react-router"

/* Layout das páginas "principais" (com header e footer padrão) */

export default function MainLayout() {
  return (
    <>
      <Header />
      <Outlet />
      <Footer />
    </>
  )
}