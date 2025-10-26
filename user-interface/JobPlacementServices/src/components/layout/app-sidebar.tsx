import * as React from "react"
import {
    // eslint-disable-next-line no-shadow-restricted-names
    Infinity,
    LifeBuoy,
    Map,
    PieChart,
    Star,
    LayoutDashboard,
    User,
    Building2,
    Mail
} from "lucide-react"

import { NavMain } from "@/components/layout/nav-main.tsx"
import { NavCustomer } from "@/components/layout/nav-customer.tsx"
import { NavProfessional } from "@/components/layout/nav-professional.tsx"
import { NavOperator } from "@/components/layout/nav-operator.tsx"
import { NavSecondary } from "@/components/layout/nav-secondary.tsx"
import { NavUser } from "@/components/layout/nav-user.tsx"
import {
  NavSidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/layout/nav-sidebar.tsx"
import { useAuth } from "@/hooks/useAuth.tsx";
import {Button} from "@/components/ui/button.tsx";

export const sidebarData = {
  navMain: [{
      title: "Home",
      url: "/dashboard",
      icon: LayoutDashboard,
      isActive: true,
  }],
    navCustomer: [{
        title: "Customer Only Page",
        url: "#",
        icon: PieChart,
    }],
  navProfessional: [
    {
      title: "Professional Only page",
      url: "#",
      icon: PieChart,
    },
    {
      title: "Professional Only page",
      url: "#",
      icon: Map,
    },
  ],
    navOperator: [{
        title: "Customers",
        url: "/dashboard/customers",
        icon: Building2,
    },
        {
            title: "Professionals",
            url: "/dashboard/professionals",
            icon: User,
        },
        {
            title: "Messages",
            url: "/dashboard/messages",
            icon: Mail,
        }],
  navSecondary: [
    {
      title: "Rate on Github.com",
      url: "https://github.com/lucabubi/jps",
      icon: Star,
    },
    {
      title: "FAQ & Support",
      url: "/dashboard/support",
      icon: LifeBuoy,
    },
  ]
}

export function buildTitleMap(data: Record<string, { title: string; url: string }[]>) {
    const map: Record<string, string> = {};

    Object.values(data).forEach((group) => {
        group.forEach((item) => {
            map[item.url] = item.title;
        });
    });

    return map;
}

export function AppSidebar({ ...props }: React.ComponentProps<typeof NavSidebar>) {

  const { isAuthenticated, isCustomer, isProfessional, isOperator, handleLogin } = useAuth()

  return (
    <NavSidebar variant="inset" {...props}>
      <SidebarHeader>
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton size="lg" asChild>
              <a href="#">
                <div className="flex aspect-square size-8 items-center justify-center rounded-lg bg-sidebar-primary text-sidebar-primary-foreground">
                  <Infinity className="size-4" />
                </div>
                <div className="grid flex-1 text-left text-sm leading-tight">
                  <span className="truncate font-semibold">JPS</span>
                  <span className="truncate text-xs">Job Placement Services</span>
                </div>
              </a>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarHeader>
        <SidebarContent>
            {isAuthenticated ? (
                <>
                    <NavMain items={sidebarData.navMain} />
                    {isCustomer && <NavCustomer items={sidebarData.navCustomer} />}
                    {isProfessional && <NavProfessional items={sidebarData.navProfessional} />}
                    {isOperator && <NavOperator items={sidebarData.navOperator} />}
                    <NavSecondary items={sidebarData.navSecondary} className="mt-auto" />
                </>
            ) : null}
        </SidebarContent>
      <SidebarFooter>
          { isAuthenticated ? <NavUser /> : <Button onClick={handleLogin}>Login</Button> }
      </SidebarFooter>
    </NavSidebar>
  )
}
