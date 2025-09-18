import {
    Menubar,
    MenubarContent,
    MenubarItem,
    MenubarMenu,
    //MenubarSeparator,
    //MenubarShortcut,
    MenubarTrigger,
} from "@/components/ui/menubar"

import {Button} from "@/components/ui/button.tsx";
import Link from "next/link"
import {Avatar, AvatarFallback, AvatarImage} from "@radix-ui/react-avatar";
import {LogOut} from "lucide-react";
import {User} from 'lucide-react';
import {CircleUserRound} from 'lucide-react';
import {MeInterface} from "@/App.tsx";


const handleLogout = async () => {
    try {
        // Esegui il logout con una richiesta POST, includendo i cookie
        await fetch(`http://localhost:8083/logout`, {
            method: 'POST',
            credentials: 'include',  // Invia i cookie (cookie di sessione) con la richiesta
        });

        // Dopo il logout, puoi reindirizzare l'utente
        window.location.href = 'http://localhost:8083/logout';  // Reindirizza alla UI (frontend)
    } catch (e) {
        console.error('Logout failed', e);
    }
};

function ButtonLogin() {
    return (
        <Link href="http://localhost:8083/secure">
        <Button className={"text-sm py-1 px-4 rounded-md h-8"}>
            <CircleUserRound/>
            Login
        </Button>
        </Link>
    );
}

interface TopBarProps {
    me?: MeInterface | null
}

export function TopBar({me}: TopBarProps) {


    return (
        <Menubar className="h-20">
            <MenubarMenu>
                <MenubarTrigger>
                    <Avatar>
                        <AvatarImage src=""/>
                        <AvatarFallback>JPS</AvatarFallback>
                    </Avatar>
                </MenubarTrigger>
                <MenubarTrigger>
                    Job Placement Services
                </MenubarTrigger>
                {me && me.principal &&
                    <>
                        <MenubarTrigger style={{marginLeft: 'auto'}}>
                            <User/>
                        </MenubarTrigger>
                        <MenubarContent>
                            <MenubarItem>
                                Username: {me?.name}
                            </MenubarItem>
                            <MenubarItem>
                                Fullname: {me?.fullName}
                            </MenubarItem>
                            <MenubarItem onClick={handleLogout}>
                                Logout <LogOut size={20} style={{marginLeft: "0.5rem"}}/>
                            </MenubarItem>
                        </MenubarContent>
                    </>
                }
                {me && me.principal == null && me.loginUrl &&
                    <MenubarTrigger style={{marginLeft: 'auto'}}>
                        <ButtonLogin/>
                    </MenubarTrigger>
                }

            </MenubarMenu>
        </Menubar>
    )
}

