import { Route, Routes } from "react-router"
import MainLayout from "./layouts/main-layout"
import Home from "./pages/home"
import Login from "./pages/login"
import FoundItem from "./pages/found-item"
import LostItem from "./pages/lost-item"
import { useAuth } from "@/contexts/auth-context"
import RegistrationModal from "@/components/shared/registration-modal"

export default function App() {
  const { userNotFound, loading } = useAuth()
  console.log(userNotFound, loading)
  return (
    <>
      <Routes>
        <Route element={<MainLayout />}>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/cadastro/encontrado" element={<FoundItem />} />
          <Route path="/cadastro/perdido" element={<LostItem />} />
        </Route>
      </Routes>
      {!loading && userNotFound &&
        <RegistrationModal open={true} />
      }
    </>
  )
}
