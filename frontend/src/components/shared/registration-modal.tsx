import {
    Dialog,
    DialogContent,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import {
    Field,
    FieldGroup,
} from "@/components/ui/field"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { jwtDecode } from "jwt-decode";
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import { PhoneInput } from "@/components/shared/phone-input"
import api from "@/lib/axios"
import { useAuth } from "@/contexts/auth-context"
import React from "react"

const campi = [
    { label: "Selecione seu campus", value: null },
    { label: "São Paulo", value: "sp_butanta" },
    { label: "Piracicaba", value: "piracicaba" },
    { label: "São Carlos", value: "sao_carlos" },
    { label: "Pirassununga", value: "pirassununga" },
    { label: "Lorena", value: "lorena" },
    { label: "Ribeirão Preto", value: "ribeirao_preto" },
    { label: "Bauru", value: "bauru" },
]

export default function RegistrationModal({ open }: { open: boolean }) {
    const { refreshUser } = useAuth();

    const token = localStorage.getItem("jwt");
    const infos = token ? jwtDecode<{ name?: string, email?: string }>(token) : { name: "", email: "" };
    const { name, email } = infos;

    const [nome, setNome] = React.useState(name);
    const [emailUsp, setEmailUsp] = React.useState(email);
    const [campus, setCampus] = React.useState<string | null>(null);
    const [telefone, setTelefone] = React.useState("");

    const onSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();
        try {
            await api.post("/v1/usuarios", {
                nome,
                emailUsp,
                campus,
                telefone: telefone || null,
            });
            await refreshUser();
        } catch (error) {
            console.error(error)
        }
    }
    return (
        <Dialog modal={true} open={open}>
            <DialogTrigger render={<Button variant="outline">Open Dialog</Button>} />
            <DialogContent className="sm:max-w-sm">
                <form onSubmit={onSubmit}>
                    <DialogHeader>
                        <DialogTitle>Criar perfil</DialogTitle>
                    </DialogHeader>
                    <FieldGroup>
                        <Field>
                            <Label htmlFor="name-1">Nome</Label>
                            <Input id="name-1" name="name" value={nome} onChange={(e) => setNome(e.target.value)} defaultValue={name} disabled={!!name} />
                        </Field>
                        <Field>
                            <Label htmlFor="email-1">E-mail</Label>
                            <Input id="email-1" name="email" value={emailUsp} onChange={(e) => setEmailUsp(e.target.value)} defaultValue={email} disabled={!!name} />
                        </Field>
                        <Field>
                            <Label htmlFor="phone-1">Celular</Label>
                            <PhoneInput defaultCountry="BR" id="phone-1" name="phone" value={telefone} onChange={setTelefone} />
                        </Field>
                        <Field>
                            <Label htmlFor="campus-1">Campus</Label>
                            <Select id="campus-1" items={campi} onValueChange={setCampus} value={campus}>
                                <SelectTrigger className="w-full max-w-48">
                                    <SelectValue />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectGroup>
                                        {campi.map((campus) => (
                                            <SelectItem key={campus.value} value={campus.value}>
                                                {campus.label}
                                            </SelectItem>
                                        ))}
                                    </SelectGroup>
                                </SelectContent>
                            </Select>
                        </Field>
                    </FieldGroup>
                    <DialogFooter>
                        <Button type="submit">Criar</Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}