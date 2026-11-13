import { Route, Routes } from "react-router"
import MainLayout from "./layouts/main-layout"
import Home from "./pages/home"
import Login from "./pages/login"
import FoundItem from "./pages/found-item"
import LostItem from "./pages/lost-item"

export default function App() {
  return (
    <Routes>
      <Route element={<MainLayout />}>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/cadastro/encontrado" element={<FoundItem />} />
        <Route path="/cadastro/perdido" element={<LostItem />} />
      </Route>
    </Routes>
  )
}