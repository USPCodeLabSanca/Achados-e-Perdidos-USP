import api from '@/lib/axios';
import axios from 'axios';
import React from 'react';

type User = {
  idUsuario: number;
  nome: string;
  emailUsp: string;
  foto: string | null;
  telefone: string | null;
  campus: string;
  statusConta: string;
  dataRegistro: string;
};

type AuthContextType = {
  user: User | null;
  userNotFound: boolean;
  loading: boolean;
  error: string | null;
  refreshUser: () => Promise<void>;
};

const AuthContext = React.createContext<AuthContextType | null>(null);

export default function AuthProvider({children}: {children: React.ReactNode}){
    const [user, setUser] = React.useState<User | null>(null)
    const [loading, setLoading] = React.useState(false)
    const [userNotFound, setUserNotFound] = React.useState(false)
    const [error, setError] = React.useState<string | null>(null)

    const refreshUser = React.useCallback(async () => {
        const jwt = localStorage.getItem("jwt")

        if(!jwt){
            setUser(null)
            setError(null)
            setLoading(false)
            setUserNotFound(false)
            return
        }

        setLoading(true)
        setError(null)
        setUserNotFound(false)

        try{
            const {data} = await api.get<User>("v1/usuarios/eu")
            setUser(data)
        }catch(error){
            setUser(null)
            if(axios.isAxiosError(error) && error.response?.status == 404){
                setUserNotFound(true)
            } else if (axios.isAxiosError(error) && error.response?.status == 401){
                localStorage.removeItem("jwt")
                setUserNotFound(false)
            } else {
                setUserNotFound(false)
                setError("Erro ao carregar usuário")
            }
        }finally{
            setLoading(false)
        }
    }, [])

    React.useEffect(() => {
        refreshUser()
    }, [refreshUser])

    return(
        <AuthContext.Provider  value={{user, userNotFound, loading, error, refreshUser}}>
            {children}
        </AuthContext.Provider>
    )
}

export function useAuth(){
    const context = React.useContext(AuthContext);

    if (!context) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
}