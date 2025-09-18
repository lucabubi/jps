import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { AppSidebar } from "@/components/app-sidebar";
import { TopBar } from "@/components/topbar.tsx";
import {MeInterface} from "@/App.tsx";

export default function Layout({children, me}: { children: React.ReactNode, me?: MeInterface | null }) {
    return (
        <div className="relative flex flex-col h-screen w-screen overflow-hidden">
            <TopBar me = {me}/>
            { me && me.principal &&
            <div className="flex flex-1 overflow-hidden">
                <SidebarProvider>
                    <AppSidebar/>
                    <main className="flex-1 overflow-auto">
                        <SidebarTrigger />
                        {children}
                    </main>
                </SidebarProvider>
            </div>
            }
        </div>
    );
}


