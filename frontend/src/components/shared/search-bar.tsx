import { SearchIcon } from "lucide-react"
import {
    InputGroup,
    InputGroupAddon,
    InputGroupInput,
} from "@/components/ui/input-group"

export default function SearchBar() {
    return (
        <InputGroup className="bg-secondary rounded-full py-4 px-2 border-none max-w-xs">
            <InputGroupInput placeholder="Buscar..." />
            <InputGroupAddon>
                <SearchIcon />
            </InputGroupAddon>
        </InputGroup>
    )
}