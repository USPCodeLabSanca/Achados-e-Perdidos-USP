import { UserRound } from "lucide-react";
import { Button } from "../ui/button";

export default function AccountButton(){
    return(
        <Button variant="secondary" className={"rounded-full"} size="icon-lg">
            <UserRound />
        </Button>
    )
}