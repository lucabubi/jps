import * as React from "react"
import { useState } from "react";
import { Link } from "react-router-dom";
import { type LucideIcon } from "lucide-react"
import { Calendar } from "@/components/ui/calendar";
import {
  SidebarGroup,
  SidebarGroupContent,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/layout/nav-sidebar.tsx"

export function NavSecondary({
  items,
  ...props
}: {
  items: {
    title: string
    url: string
    icon: LucideIcon
  }[]
} & React.ComponentPropsWithoutRef<typeof SidebarGroup>) {
  const [date, setDate] = useState<Date | undefined>(new Date())
  const [timeZone, setTimeZone] = React.useState<string | undefined>(undefined)

  React.useEffect(() => {
      setTimeZone(Intl.DateTimeFormat().resolvedOptions().timeZone)
  }, [])

  return (
    <SidebarGroup {...props}>
      <SidebarGroupContent>
          <div className="flex w-full items-center justify-center">
            <Calendar
                mode="single"
                selected={date}
                onSelect={setDate}
                className="rounded-md border shadow-sm scale-90 items-center justify-center"
                captionLayout="label"
                timeZone={timeZone}
            />
          </div>
          <SidebarMenu>
          {items.map((item) => (
            <SidebarMenuItem key={item.title}>
              <SidebarMenuButton asChild size="sm">
                  <Link to={item.url}>
                      <item.icon />
                      <span>{item.title}</span>
                  </Link>
              </SidebarMenuButton>
            </SidebarMenuItem>
          ))}
        </SidebarMenu>
      </SidebarGroupContent>
    </SidebarGroup>
  )
}
