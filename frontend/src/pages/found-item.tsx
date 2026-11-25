
import { Link, useNavigate } from "react-router"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import React from "react"
import api from "@/lib/axios"
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"

const tipos = [
    { label: "Selecione o tipo", value: null },
    { label: "Objeto", value: "objeto" },
    { label: "Documento", value: "documento" },
    { label: "Roupa", value: "roupa" },
]

export default function FoundItem() {
    const navigate = useNavigate();
    const [nome, setNome] = React.useState("");
    const [local, setLocal] = React.useState("");
    const [data, setData] = React.useState("");
    const [descricao, setDescricao] = React.useState("");
    const [foto, setFoto] = React.useState<File | null>(null);
    const [tipo, setTipo] = React.useState<string | null>(null);

    const onSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();
        try {
            const formData = new FormData();

            formData.append(
                "dados",
                new Blob(
                    [JSON.stringify({
                        nome,
                        tipo,
                        local,
                        descricao,
                        dataOcorrencia: data || null,
                    })],
                    { type: "application/json" },
                ),
            );

            if (foto) formData.append("foto", foto);

            await api.post("/v1/items/found", formData);
            navigate("/");
        } catch (error) {
            console.error(error)
        }

    }

    return (
        <div className="flex flex-1 flex-col bg-[#FFF0D8]">
            {/* Formulário */}
            <section className="mx-auto w-full max-w-6xl px-4 py-8">
                <h2 className="mb-3 font-semibold">
                    Relatar um Item Encontrado:
                </h2>

                <form onSubmit={onSubmit} className="grid gap-10 rounded-md bg-white p-6 md:grid-cols-2 md:p-10">
                    {/* Campos de texto */}
                    <div className="flex flex-col gap-6">
                        <div className="flex flex-col gap-2">
                            <label htmlFor="item" className="text-sm">
                                Item:
                            </label>
                            <Input id="item" name="item" placeholder="Nome do item" value={nome} onChange={(e) => setNome(e.target.value)} />
                        </div>

                        <div className="flex flex-col gap-2">
                            <label htmlFor="location" className="text-sm">
                                Local onde encontrou:
                            </label>
                            <Input id="location" name="location" placeholder="Local" value={local} onChange={(e) => setLocal(e.target.value)} />
                        </div>

                        <div className="flex flex-col gap-2">
                            <label htmlFor="date" className="text-sm">
                                Data em que encontrou:
                            </label>
                            <Input id="date" name="date" type="date" value={data} onChange={(e) => setData(e.target.value)} />
                        </div>

                        <Select id="tipo-1" items={tipos} onValueChange={setTipo} value={tipo}>
                            <SelectTrigger className="w-full max-w-48">
                                <SelectValue />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectGroup>
                                    {tipos.map((tipo) => (
                                        <SelectItem key={tipo.value} value={tipo.value}>
                                            {tipo.label}
                                        </SelectItem>
                                    ))}
                                </SelectGroup>
                            </SelectContent>
                        </Select>

                        <div className="flex flex-col gap-2">
                            <label htmlFor="description" className="text-sm" >
                                Descrição:
                            </label>
                            <Textarea
                                id="description"
                                name="description"
                                value={descricao}
                                onChange={(e) => setDescricao(e.target.value)}

                                placeholder="Descreva o item..."
                                className="min-h-32 resize-y"
                            />
                        </div>
                    </div>

                    {/* Foto e botões */}
                    <div className="flex flex-col gap-6">
                        <div className="flex flex-col gap-2">
                            <label htmlFor="photo" className="text-sm">
                                Foto:
                            </label>

                            <label
                                htmlFor="photo"
                                className="flex min-h-64 cursor-pointer flex-col items-center justify-center gap-2 rounded-md bg-neutral-200 text-center transition hover:bg-neutral-300"
                            >
                                <span className="text-3xl text-amber-400">★</span>
                                <span className="text-sm text-neutral-800">
                                    Adicionar imagem
                                </span>
                                <input
                                    id="photo"
                                    name="photo"
                                    type="file"
                                    accept="image/*"
                                    className="sr-only"
                                    onChange={(e) => {
                                        if (e.target.files && e.target.files.length > 0) {
                                            setFoto(e.target.files[0]);
                                        }
                                    }}
                                />
                            </label>
                        </div>

                        <div className="mt-auto flex justify-end gap-3">
                            <Link to="/">
                                <Button variant="ghost">
                                    Cancelar
                                </Button>
                            </Link>

                            <Button type="submit">
                                Postar
                            </Button>
                        </div>
                    </div>
                </form>
            </section>
        </div>
    )
}