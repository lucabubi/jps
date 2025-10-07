import { useCallback } from "react";
import { useNavigate } from "react-router-dom";
import {
  //BadgeCheck,
  ChevronsUpDown,
  LogOut,
  Settings,
} from "lucide-react"
import {
  Avatar,
  AvatarFallback,
  //AvatarImage,
} from "@/components/ui/avatar.tsx"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuGroup,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuShortcut,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu.tsx"
import {
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from "@/components/layout/nav-sidebar.tsx"
import { useIsAppleDevice } from "@/hooks/useIsAppleDevice";
import { useAuth } from "@/hooks/useAuth";
import useHotkeys from "@/hooks/useHotkeys";

export function NavUser() {
  const { isMobile } = useSidebar()
  const { userData, handleLogout } = useAuth();
  const navigate = useNavigate();
  const isApple = useIsAppleDevice();

  const openSettings = useCallback(() => {
    navigate("/dashboard/settings");
  }, [navigate]);

  // Shortcuts registered
  useHotkeys(
    {
        s: openSettings,
        l: handleLogout,
    },
    { requireMod: true }
  );

  return (
    <SidebarMenu>
      <SidebarMenuItem>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <SidebarMenuButton
              size="lg"
              className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
            >
              <Avatar className="h-8 w-8 rounded-lg">
                  {/*<AvatarImage src={user.avatar} alt={userData.fullName} />*/}
                <AvatarFallback className="rounded-lg">JPS</AvatarFallback>
              </Avatar>
              <div className="grid flex-1 text-left text-sm leading-tight">
                <span className="truncate font-semibold">{userData?.fullName || "Unable to fetch name"}</span>
                  <span className="truncate text-xs">
  {userData?.roles?.[0]
      ? userData.roles[0].charAt(0).toUpperCase() + userData.roles[0].slice(1).toLowerCase()
      : "Unable to fetch role"}
</span>
              </div>
              <ChevronsUpDown className="ml-auto size-4" />
            </SidebarMenuButton>
          </DropdownMenuTrigger>
          <DropdownMenuContent
            className="w-[--radix-dropdown-menu-trigger-width] min-w-56 rounded-lg"
            side={isMobile ? "bottom" : "right"}
            align="end"
            sideOffset={4}
          >
            <DropdownMenuLabel className="p-0 font-normal">
              <div className="flex items-center gap-2 px-1 py-1.5 text-left text-sm">
                <Avatar className="h-8 w-8 rounded-lg">
                    {/*<AvatarImage src={user.avatar} alt={userData.fullName} />*/}
                  <AvatarFallback className="rounded-lg">JPS</AvatarFallback>
                </Avatar>
                <div className="grid flex-1 text-left text-sm leading-tight">
                  <span className="truncate font-semibold">{userData?.fullName || "Unable to fetch name"}</span>
                    <span className="truncate text-xs">
  {userData?.roles?.[0]
      ? userData.roles[0].charAt(0).toUpperCase() + userData.roles[0].slice(1).toLowerCase()
      : "Unable to fetch role"}
</span>
                </div>
              </div>
            </DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuGroup>
                {/*
              <DropdownMenuItem onClick={()=>{navigate("/dashboard/profile")}}>
                <BadgeCheck />
                Profile
                  <KbdGroup className="ms-auto">
                      {isApple ? (
                        <Kbd>⌘ + P</Kbd>
                      ) : (
                        <Kbd>Ctrl + P</Kbd>
                      )}
                  </KbdGroup>
              </DropdownMenuItem>
              */}
              <DropdownMenuItem onClick={()=>{navigate("/dashboard/settings")}}>
                <Settings />
                Settings
                      {isApple ? (
                          <>
                            <DropdownMenuShortcut>⌘</DropdownMenuShortcut>
                            <DropdownMenuShortcut>S</DropdownMenuShortcut>
                          </>
                      ) : (
                          <>
                            <DropdownMenuShortcut>Ctrl</DropdownMenuShortcut>
                            <DropdownMenuShortcut>S</DropdownMenuShortcut>
                          </>
                      )}
              </DropdownMenuItem>
            </DropdownMenuGroup>
              <DropdownMenuItem
                  onClick={handleLogout}
                  className="text-destructive focus:text-destructive data-[highlighted]:bg-destructive/20"
              >
                  <LogOut className="text-red-600" />
                  <span className="text-red-600">Logout</span>
                      {isApple ? (
                          <>
                            <DropdownMenuShortcut>⌘</DropdownMenuShortcut>
                            <DropdownMenuShortcut>L</DropdownMenuShortcut>
                          </>
                      ) : (
                          <>
                            <DropdownMenuShortcut>Ctrl</DropdownMenuShortcut>
                            <DropdownMenuShortcut>L</DropdownMenuShortcut>
                          </>
                      )}
              </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </SidebarMenuItem>
    </SidebarMenu>
  )
}
