import {Message} from "@/lib/API.tsx";
import {Badge} from "@/components/ui/badge.tsx";
import { ColumnDef } from "@tanstack/react-table"
import { PhoneCall, Mail, MessageCircleMore, MailOpen } from "lucide-react";


type OnDirty = () => void;


export const createMessagesColumns = (_onDirty: OnDirty): ColumnDef<Message>[] => [
    { accessorKey: "id", header: "ID" },
    { accessorKey: "sender", header: "Sender" },
    { accessorKey: "subject", header: "Subject" },
    { accessorKey: "body", header: "Body" },
    {
        accessorKey: "channel",
        header: "Channel",
        cell: ({ row }) => {
            const channel = row.original.channel;
            // channel enum values are uppercase (e.g. "PHONE_CALL", "TEXT_MESSAGE", "EMAIL")
            // map them to icons and human-friendly labels
            // Use 1em for icon size so the SVG scales with the surrounding font size
            let icon = <MessageCircleMore className="w-[1em] h-[1em] inline align-middle" />;
            let label = String(channel ?? "Unknown");

            if (channel === "PHONE_CALL") {
                icon = <PhoneCall className="w-[1em] h-[1em] inline align-middle" />;
                label = "Phone Call";
            } else if (channel === "TEXT_MESSAGE") {
                icon = <MessageCircleMore className="w-[1em] h-[1em] inline align-middle" />;
                label = "Text Message";
            } else if (channel === "EMAIL") {
                // 'RECEIVED' is the enum value that represents an unread message in this codebase
                if (row.original.state === "RECEIVED")
                    icon = <Mail className="w-[1em] h-[1em] inline align-middle" />;
                else
                    icon = <MailOpen className="w-[1em] h-[1em] inline align-middle" />;
                label = "Email";
            }

            return (
                <Badge variant="outline">
                    <span className="inline-flex items-center gap-1">{icon}<span className="ml-1">{label}</span></span>
                </Badge>
            );
        }
    },
    {
        accessorKey: "priority",
        header: "Priority",
        cell: ({ row }) => {
            const priority = row.original.priority;
            // priority enum values are uppercase (e.g. "HIGH", "MEDIUM", "LOW")
            // map them to Badge variants and human-friendly labels
            let variant: "default" | "outline" | "destructive" | "secondary" | null | undefined = "default";
            let label = String(priority ?? "Unknown");

            if (priority === "HIGH") {
                variant = "destructive";
                label = "High";
            } else if (priority === "MEDIUM") {
                variant = "secondary";
                label = "Medium";
            } else if (priority === "LOW") {
                variant = "outline";
                label = "Low";
            }

            return <Badge variant={variant}>{label}</Badge>;
        },
    },
    {
        accessorKey: "state",
        header: "State",
        cell: ({ row }) => {
            const state = row.original.state;
            // state enum values are uppercase (e.g. "RECEIVED", "READ", "DISCARDED", "PROCESSING", "DONE", "FAILED")
            // map them to Badge variants and user-friendly labels
            let variant: "default" | "outline" | "destructive" | "secondary" | null | undefined = "default";
            let label = String(state ?? "Unknown");

            if (state === "RECEIVED") {
                variant = "default";
                label = "Received";
            } else if (state === "READ") {
                variant = "secondary";
                label = "Read";
            } else if (state === "DISCARDED") {
                variant = "outline";
                label = "Discarded";
            } else if (state === "PROCESSING") {
                variant = "secondary";
                label = "Processing";
            } else if (state === "DONE") {
                variant = "secondary";
                label = "Done";
            } else if (state === "FAILED") {
                variant = "destructive";
                label = "Failed";
            }

            return <Badge variant={variant}>{label}</Badge>;
        },
    },
    {
        accessorKey: "date",
        header: "Date",
        cell: ({ row }) => {
            const dateStr = row.original.date;
            const date = new Date(dateStr);
            return date.toLocaleString();
        }
    },
];
