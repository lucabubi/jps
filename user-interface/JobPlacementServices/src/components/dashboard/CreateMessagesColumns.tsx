import {Message} from "@/lib/API.tsx";
import {Badge} from "@/components/ui/badge.tsx";
import { ColumnDef } from "@tanstack/react-table"


type OnDirty = () => void;


export const createMessagesColumns = (_onDirty: OnDirty): ColumnDef<Message>[] => [
    { accessorKey: "id", header: "ID" },
    { accessorKey: "subject", header: "Subject" },
    { accessorKey: "body", header: "Body" },
    { accessorKey: "sender", header: "Sender" },
    { accessorKey: "channel", header: "Channel" },
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
                variant = "destructive";
                label = "Unread";
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
    { accessorKey: "date", header: "Date" },
];
