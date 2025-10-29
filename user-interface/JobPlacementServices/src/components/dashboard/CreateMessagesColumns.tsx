import {Message} from "@/lib/API.tsx";
import {Badge} from "@/components/ui/badge.tsx";
import { ColumnDef } from "@tanstack/react-table"
import {
    PhoneCall,
    Mail,
    MailCheck,
    MailX,
    MailOpen,
    MailPlus,
    CircleAlert,
    CircleDashed,
    CircleEllipsis,
    Calendar,
    Settings,
    MessageCircleMore,
    Trash
} from "lucide-react";



type OnDirty = () => void;

function MessageStateBadge(state: string) {
    switch (state) {
        case 'RECEIVED':
            return <Badge className='rounded-md border-none bg-gray-600/10 text-gray-600 dark:bg-gray-400/10 dark:text-gray-400 dark:focus-visible:ring-gray-400/40 hover:bg-gray-600/10 dark:hover:bg-gray-400/10'>                <MailPlus className='size-3 mr-1' />
                Received
            </Badge>;
        case 'READ':
            return <Badge className='rounded-md border-none bg-purple-600/10 text-purple-600 dark:bg-purple-400/10 dark:text-purple-400 dark:focus-visible:ring-purple-400/40 hover:bg-purple-600/10 dark:hover:bg-purple-400/10'>
                <MailOpen className='size-3 mr-1' />
                Selection Phase
            </Badge>;
        case 'DISCARDED':
            return <Badge className='rounded-md border-none bg-gray-600/10 text-gray-600 dark:bg-gray-400/10 dark:text-gray-400 dark:focus-visible:ring-gray-400/40 hover:bg-gray-600/10 dark:hover:bg-gray-400/10'>
                <Trash className='size-3 mr-1' />
                Discarded
            </Badge>;
        case 'PROCESSING':
            return <Badge className='rounded-md border-none bg-purple-600/10 text-purple-600 dark:bg-purple-400/10 dark:text-purple-400 dark:focus-visible:ring-purple-400/40 hover:bg-purple-600/10 dark:hover:bg-purple-400/10'>
                <Settings className='size-3 mr-1' />
                Processing
            </Badge>;
        case 'DONE':
            return <Badge className='rounded-md border-none bg-green-600/10 text-green-600 dark:bg-green-400/10 dark:text-green-400 dark:focus-visible:ring-green-400/40 hover:bg-green-600/10 dark:hover:bg-green-400/10'>
                <MailCheck className='size-3 mr-1' />
                Done
            </Badge>;
        case 'FAILED':
            return <Badge className='rounded-md border-none bg-red-600/10 text-red-600 dark:bg-red-400/10 dark:text-red-400 dark:focus-visible:ring-red-400/40 hover:bg-red-600/10 dark:hover:bg-red-400/10'>
                <MailX className='size-3 mr-1' />
                Failed
            </Badge>;
        default:
            return "Unable to render the badge.";
    }
}

function MessagePriorityBadge(priority: string) {
    switch (priority) {
        case 'LOW':
            return <Badge className='rounded-md border-none bg-green-600/10 text-green-600 dark:bg-green-400/10 dark:text-green-400 dark:focus-visible:ring-green-400/40 hover:bg-green-600/10 dark:hover:bg-green-400/10'>
                <CircleDashed className='size-3 mr-1' />
                Low
            </Badge>;
        case 'MEDIUM':
            return <Badge className='rounded-md border-none bg-amber-600/10 text-amber-600 dark:bg-amber-400/10 dark:text-amber-400 dark:focus-visible:ring-amber-400/40 hover:bg-amber-600/10 dark:hover:bg-amber-400/10'>
                <CircleEllipsis className='size-3 mr-1' />
                Medium
            </Badge>;
        case 'HIGH':
            return <Badge className='rounded-md border-none bg-red-600/10 text-red-600 dark:bg-red-400/10 dark:text-red-400 dark:focus-visible:ring-red-400/40 hover:bg-red-600/10 dark:hover:bg-red-400/10'>
                <CircleAlert className='size-3 mr-1' />
                High
            </Badge>;
        default:
            return "Unable to render the badge.";
    }
}

function MessageChannelBadge(channel: string) {
    switch (channel) {
        case 'PHONE_CALL':
            return <Badge className='rounded-md border-none bg-purple-600/10 text-purple-600 dark:bg-purple-400/10 dark:text-purple-400 dark:focus-visible:ring-purple-400/40 hover:bg-purple-600/10 dark:hover:bg-purple-400/10'>
                <PhoneCall className='size-3 mr-1' />
                Phone Call
            </Badge>;
        case 'TEXT_MESSAGE':
            return <Badge className='rounded-md border-none bg-purple-600/10 text-purple-600 dark:bg-purple-400/10 dark:text-purple-400 dark:focus-visible:ring-purple-400/40 hover:bg-purple-600/10 dark:hover:bg-purple-400/10'>
                <MessageCircleMore className='size-3 mr-1' />
                Text Message
            </Badge>;
        case 'EMAIL':
            return <Badge className='rounded-md border-none bg-purple-600/10 text-purple-600 dark:bg-purple-400/10 dark:text-purple-400 dark:focus-visible:ring-purple-400/40 hover:bg-purple-600/10 dark:hover:bg-purple-400/10'>
                <Mail className='size-3 mr-1' />
                Email
            </Badge>;
        default:
            return "Unable to render the badge.";
    }
}

function MessageDateBadge(dateStr: string) {
    const date = new Date(dateStr);
    const formattedDate = date.getFullYear() + '-' +
        String(date.getMonth() + 1).padStart(2, '0') + '-' +
        String(date.getDate()).padStart(2, '0') + ', ' +
        String(date.getHours()).padStart(2, '0') + ':' +
        String(date.getMinutes()).padStart(2, '0');

    return <Badge
        variant="secondary"
        className="rounded-md"
        >
        <Calendar className='size-3 mr-1' />
        {formattedDate}
    </Badge>;
}

export const createMessagesColumns = (_onDirty: OnDirty): ColumnDef<Message>[] => [
    { accessorKey: "id", header: "ID" },
    { accessorKey: "sender", header: "Sender" },
    { accessorKey: "subject", header: "Subject" },
    { accessorKey: "body", header: "Body" },
    {
        accessorKey: "channel",
        header: "Channel",
        cell: ({ getValue }) => {
            const channel = getValue<string>();
            return MessageChannelBadge(channel);
        }
    },
    {
        accessorKey: "priority",
        header: "Priority",
        cell: ({ getValue }) => {
            const priority = getValue<string>();
            return MessagePriorityBadge(priority);
        },
    },
    {
        accessorKey: "state",
        header: "State",
        cell: ({ getValue }) => {
            const state = getValue<string>();
            return MessageStateBadge(state);
        },
    },
    {
        accessorKey: "date",
        header: "Date",
        cell: ({ getValue }) => {
            const dateStr = getValue<string>();
            return MessageDateBadge(dateStr);
        }
    },
];
