import { GoogleLogin } from '@react-oauth/google';
import { useNavigate } from 'react-router';
import axios from 'axios';

export default function Login() {
    const navigate = useNavigate();

    const handleLogin = async (credRes: any) => {
        try {
            const response = await axios.post(`${import.meta.env.VITE_API_URL}/auth/google`, {
                token: credRes.credential,
            });

            localStorage.setItem('jwt', response.data);

            navigate('/');
        } catch (e) {
            console.error(e);
        }

    }
    return (
        <div className="flex w-full flex-1 items-center justify-center">
            <div className="w-full md:w-125 h-75 bg-primary flex justify-center items-center p-8 rounded-2xl">
                <div className="flex flex-col items-center justify-between h-full text-primary-foreground">
                    <h1 className='text-primary-foreground'>Entrar</h1>
                    <GoogleLogin onSuccess={handleLogin} onError={() => console.error('Login failed')} useOneTap/>
                    <div className='flex flex-col items-center'>
                        <a>Termos e condições</a>
                        <a>Política de privacidade</a>
                    </div>
                </div>
            </div>
        </div>
    )
}