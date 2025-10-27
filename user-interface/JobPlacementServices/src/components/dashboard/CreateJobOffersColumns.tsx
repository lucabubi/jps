import {Customer, JobOffer, Professional} from "@/lib/API.tsx";
import {Badge} from "@/components/ui/badge.tsx";
import { ColumnDef } from "@tanstack/react-table"
import { ListPlus, UserSearch, Users, UserCheck, FileCheck2, FileX2 } from "lucide-react";


type OnDirty = () => void;

function JobOfferStateBadge(state: string) {
    switch (state) {
        case 'CREATED':
            return <Badge className='rounded-md border-none bg-green-600/10 text-green-600 dark:bg-green-400/10 dark:text-green-400 dark:focus-visible:ring-green-400/40 hover:bg-green-600/10 dark:hover:bg-green-400/10'>
                <ListPlus className='size-3 mr-1' />
                Created
            </Badge>;
        case 'SELECTION_PHASE':
            return <Badge className='rounded-md border-none bg-amber-600/10 text-amber-600 dark:bg-amber-400/10 dark:text-amber-400 dark:focus-visible:ring-amber-400/40 hover:bg-amber-600/10 dark:hover:bg-amber-400/10'>
                <UserSearch className='size-3 mr-1' />
                Selection Phase
            </Badge>;
        case 'CANDIDATE_PROPOSAL':
            return <Badge className='rounded-md border-none bg-amber-600/10 text-amber-600 dark:bg-amber-400/10 dark:text-amber-400 dark:focus-visible:ring-amber-400/40 hover:bg-amber-600/10 dark:hover:bg-amber-400/10'>
                <Users className='size-3 mr-1' />
                Candidate Proposal
            </Badge>;
        case 'CONSOLIDATED':
            return <Badge className='rounded-md border-none bg-green-600/10 text-green-600 dark:bg-green-400/10 dark:text-green-400 dark:focus-visible:ring-green-400/40 hover:bg-green-600/10 dark:hover:bg-green-400/10'>
                <UserCheck className='size-3 mr-1' />
                Consolidated
            </Badge>;
        case 'DONE':
            return <Badge className='rounded-md border-none bg-green-600/10 text-green-600 dark:bg-green-400/10 dark:text-green-400 dark:focus-visible:ring-green-400/40 hover:bg-green-600/10 dark:hover:bg-green-400/10'>
                <FileCheck2 className='size-3 mr-1' />
                Done
            </Badge>;
        case 'ABORTED':
            return <Badge className='rounded-md border-none bg-red-600/10 text-red-600 dark:bg-red-400/10 dark:text-red-400 dark:focus-visible:ring-red-400/40 hover:bg-red-600/10 dark:hover:bg-red-400/10'>
                <FileX2 className='size-3 mr-1' />
                Aborted
            </Badge>;
        default:
            return "Unable to render the badge.";
    }
}

export const createJobOffersColumns = (_onDirty: OnDirty): ColumnDef<JobOffer>[] => [
    { accessorKey: "id", header: "ID" },
    { accessorKey: "description", header: "Description" },
    {
        accessorKey: "status",
        header: "Status",
        cell: ({getValue}) => {
            const status = getValue<string>();
            return JobOfferStateBadge(status);
        }
    },
    { accessorKey: "duration", header: "Duration" },
    {
        accessorKey: "customer",
        header: "Customer",
        cell: ({row}) => {
            const customer = row.getValue("customer") as Customer;
            const customerName = `${customer.contact.name} ${customer.contact.surname}`;


            return <span>{customerName || '...'}</span>;
        }
    },
    {
        accessorKey: "value",
        header: "Value",
        cell: ({ row }) => {
            const value = row.getValue("value") as number | null | undefined;
            if (value == null) {
                return <span>None</span>;
            }
            return <span>€{value.toFixed(2)}</span>;
        }
    },
    {
        accessorKey: "professional",
        header: "Professional",
        cell: ({ row }: { row: any }) => {
            const prof = row.getValue("professional") as Professional | null | undefined;
            console.log("Professional value:", prof);
            if (prof == null) {
                return <span>None</span>;
            }
            const professionalName = `${prof.contact.name} ${prof.contact.surname}`;
            return <span>{professionalName}</span>;
        }
    },
];
