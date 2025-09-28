import {ReactNode} from "react";
import { useLocation, Link } from "react-router-dom";
import { AppSidebar } from "@/components/layout/app-sidebar.tsx"
import {
    Breadcrumb,
    BreadcrumbItem,
    BreadcrumbLink,
    BreadcrumbList,
    BreadcrumbPage,
    BreadcrumbSeparator,
} from "@/components/ui/breadcrumb.tsx"
import { Separator } from "@/components/layout/separator.tsx"
import {
    SidebarInset,
    SidebarProvider,
    SidebarTrigger,
} from "@/components/layout/nav-sidebar.tsx"
import { sidebarData, buildTitleMap } from "@/components/layout/app-sidebar.tsx";

const titleMap = buildTitleMap(sidebarData);

export default function Layout({ children }: { children: ReactNode }) {
    const location = useLocation();
    const path_names = location.pathname.split("/").filter(Boolean);

    return (
        <SidebarProvider>
            <AppSidebar />
            <SidebarInset>
                <header className="flex h-16 shrink-0 items-center gap-2">
                    <div className="flex items-center gap-2 px-4">
                        <SidebarTrigger className="-ml-1" />
                        <Separator
                            orientation="vertical"
                            className="mr-2 data-[orientation=vertical]:h-4"
                        />
                        <Breadcrumb>
                            <BreadcrumbList>
                                <BreadcrumbItem>
                                    <BreadcrumbLink asChild>
                                        <Link to="/dashboard">Home</Link>
                                    </BreadcrumbLink>
                                </BreadcrumbItem>

                                {path_names.map((value, index) => {
                                    const to = `/${path_names.slice(0, index + 1).join("/")}`;
                                    const isLast = index === path_names.length - 1;

                                    const label = titleMap[to] ?? decodeURIComponent(value);

                                    // If on root or label is Home, skip
                                    if (to === "/dashboard" || label === "Home") return null;

                                    return (
                                        <div className="flex items-center" key={to}>
                                            <BreadcrumbSeparator />
                                            <BreadcrumbItem>
                                                {isLast ? (
                                                    <BreadcrumbPage>{label}</BreadcrumbPage>
                                                ) : (
                                                    <BreadcrumbLink asChild>
                                                        <Link to={to}>{label}</Link>
                                                    </BreadcrumbLink>
                                                )}
                                            </BreadcrumbItem>
                                        </div>
                                    );
                                })}
                            </BreadcrumbList>
                        </Breadcrumb>
                    </div>
                </header>

                <div className="flex flex-1 flex-col gap-4 p-6 pt-0">
                    {children}
                </div>
            </SidebarInset>
        </SidebarProvider>
    );
}