import type { Item } from "@/types/item"
import dayjs from 'dayjs';


export default function ItemCard({ item }: { item: Item }) {

    return (
        <article className="flex flex-col rounded-2xl overflow-hidden">
            <img className="w-full rounded-none mt-0 aspect-square object-cover" src={item.foto || ""} alt={item.descricao || ""} />
            <div className="flex flex-col gap-8 bg-linear-to-b from-[#FFDD87] to-[#FFCA46] px-4 py-6">
                <div className="flex flex-col gap-0">
                    <h2 className="font-medium text-2xl mt-0">{item.nome}</h2>
                    <span className="text-sm font-medium text-muted-foreground mt-0">{item.local}</span>
                    <span className="text-sm font-medium text-muted-foreground first-letter:uppercase" title={item.dataOcorrencia || ""}>
                        {dayjs(item.dataOcorrencia).fromNow()}
                    </span>
                </div>
                <div className="flex gap-1 items-center">
                    <img src={item.criadorFoto || ""} className="w-4 h-4 mt-0 rounded-full" />
                    <span className="text-xs font-medium">{item.criadorNome}</span>
                </div>
            </div>
        </article>
    )
}
