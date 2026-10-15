import { Route, Routes } from "react-router"
import MainLayout from "./layouts/main-layout"
import Home from "./pages/home"
import Login from "./pages/login"

export default function App() {
  return (
    <Routes>
      <Route element={<MainLayout />}>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
      </Route>
    </Routes>
  )
}