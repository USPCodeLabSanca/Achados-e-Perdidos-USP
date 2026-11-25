import { Button } from "@/components/ui/button"
import {
    Popover,
    PopoverContent,
    PopoverDescription,
    PopoverHeader,
    PopoverTitle,
    PopoverTrigger,
} from "@/components/ui/popover"
import { useAuth } from "@/contexts/auth-context";
import { useNavigate } from "react-router";
import {campi} from "@/lib/utils";

export default function AccountButton() {
    const navigate = useNavigate();
    const { user } = useAuth();
    return (
        user ? (
            <Popover>
                <PopoverTrigger render={
                    <Button variant="secondary" className={"rounded-full"} size="icon-lg">
                        <img src={user.foto || ""} alt="Avatar" />
                    </Button>
                } />
                <PopoverContent align="start">
                    <PopoverHeader>
                        <PopoverTitle>{user.nome}</PopoverTitle>
                        <PopoverDescription>
                            {user.emailUsp} - {campi[user.campus]}
                        </PopoverDescription>
                    </PopoverHeader>
                </PopoverContent>
            </Popover>

        ) :
            <Button onClick={() => { navigate("/login") }} variant="secondary">
                Entrar
            </Button>
    )
}