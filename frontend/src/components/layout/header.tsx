import AccountButton from "../shared/account-button";
import SearchBar from "../shared/search-bar";

export default function Header() {
    return(
        <header className="sticky right-0 top-4 left-0 m-4 rounded-4xl flex items-center justify-between py-4 px-8 bg-primary drop-shadow-md">
            <div className="flex gap-4 items-center">
                <h1>USPerdi</h1>
                <SearchBar />
            </div>
            <AccountButton/>
        </header>
    )
}