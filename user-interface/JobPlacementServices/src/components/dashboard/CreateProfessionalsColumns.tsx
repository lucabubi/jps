// file: 'user-interface/JobPlacementServices/src/components/dashboard/CreateProfessionalsColumns.tsx'
import {useEffect, useState} from "react";
import { ColumnDef } from "@tanstack/react-table"
import { Professional } from "@/lib/API";
import { Badge } from "@/components/ui/badge"
import { Mail, MoreHorizontal, Trash2, UserPen } from "lucide-react";
import { Button } from "@/components/ui/button"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    //DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
    Drawer, DrawerClose,
    DrawerContent,
    DrawerDescription, DrawerFooter,
    DrawerHeader,
    DrawerTitle,
} from "@/components/ui/drawer"
import { deleteProfessionalById } from "@/lib/API";
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { toast } from "sonner";

type OnDirty = () => void;

type ConfirmDeleteDrawerProps = {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    onConfirm: () => Promise<void> | void;
    idToConfirm: number | string;
};

function ConfirmDeleteDrawer({ open, onOpenChange, onConfirm, idToConfirm }: ConfirmDeleteDrawerProps) {
    const [typedId, setTypedId] = useState("");

    useEffect(() => {
        if (open) setTypedId("");
    }, [open]);

    const expected = String(idToConfirm);
    const canConfirm = typedId.trim() === expected;

    return (
        <Drawer open={open} onOpenChange={onOpenChange}>
            <DrawerContent>
                <DrawerHeader>
                    <DrawerTitle>Attention! You're deleting a professional.</DrawerTitle>
                    <DrawerDescription>
                        This action is irreversible. You are deleting the professional with ID: <Badge className="rounded-none bg-red-100 text-red-800 border border-red-200 dark:bg-red-900/30 dark:text-red-200 dark:border-red-800 hover:bg-red-100 hover:text-red-800 hover:border-red-200 transition-none">{expected}</Badge>
                    </DrawerDescription>
                    <div className="grid w-full max-w-sm items-center gap-2">
                        <Label htmlFor="delete-confirm">Type <Badge className="rounded-none bg-red-100 text-red-800 border border-red-200 dark:bg-red-900/30 dark:text-red-200 dark:border-red-800 hover:bg-red-100 hover:text-red-800 hover:border-red-200 transition-none">{expected}</Badge> in the box below to proceed:</Label>
                        <Input
                            id="delete-confirm"
                            type="text"
                            placeholder={expected}
                            value={typedId}
                            onChange={(e) => setTypedId(e.target.value)}
                            autoComplete="off"
                            className="font-bold text-red-800 focus-visible:ring-red-600 focus:ring-red-600 selection:bg-red-200 selection:text-red-900 dark:selection:bg-red-900/40"
                        />
                    </div>
                </DrawerHeader>

                <DrawerFooter>
                    <Button
                        onClick={onConfirm}
                        className="bg-red-600 hover:bg-red-700"
                        disabled={!canConfirm}
                    >
                        Confirm deletion
                    </Button>
                    <DrawerClose asChild>
                        <Button variant="outline">Cancel</Button>
                    </DrawerClose>
                </DrawerFooter>
            </DrawerContent>
        </Drawer>
    );
}

const handleDeleteProfessional = async (professionalId: number, onDirty: OnDirty) => {
    if (!professionalId) {
        toast.error("Unable to find the given professional.");
        return;
    }
    const result = await deleteProfessionalById(professionalId);
    if (result) {
        onDirty();
        toast.success("Professional deleted.");
    } else {
        toast.error("Delete failed.");
    }
};

function ActionsCell({ professional, onDirty }: { professional: Professional; onDirty: OnDirty }) {
    const [open, setOpen] = useState(false);

    const confirmAndDelete = async () => {
        await handleDeleteProfessional(professional.id, onDirty);
        setOpen(false);
    };

    return (
        <>
            <DropdownMenu>
                <DropdownMenuTrigger asChild>
                    <Button variant="ghost" className="h-8 w-8 p-0">
                        <span className="sr-only">Open Actions</span>
                        <MoreHorizontal className="h-4 w-4" />
                    </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                    <DropdownMenuLabel>Actions</DropdownMenuLabel>
                    <DropdownMenuItem>
                        <UserPen />
                        Edit Professional
                    </DropdownMenuItem>
                    <DropdownMenuItem
                        onClick={() => setOpen(true)}
                        className="text-red-600 focus:text-red-600 data-[highlighted]:bg-destructive/20"
                    >
                        <Trash2 className="text-red-600" />
                        <span className="text-red-600">Delete Professional</span>
                    </DropdownMenuItem>
                </DropdownMenuContent>
            </DropdownMenu>

            <ConfirmDeleteDrawer
                open={open}
                onOpenChange={setOpen}
                onConfirm={confirmAndDelete}
                idToConfirm={professional.id}
            />
        </>
    );
}

export const createProfessionalsColumns = (onDirty: OnDirty): ColumnDef<Professional>[] => [
    { accessorKey: "id", header: "ID" },
    { accessorKey: "contact.name", header: "First Name" },
    { accessorKey: "contact.surname", header: "Last Name" },
    {
        id: "emails",
        header: "Email",
        accessorFn: (row) => {
            const list = Array.isArray(row.contact?.emails) ? row.contact.emails : [];
            return list.map((e) => (typeof e === "string" ? e : e?.email)).filter(Boolean) as string[];
        },
        cell: ({ getValue }) => {
            const emails = getValue<string[]>();
            return (
                <div className="flex flex-wrap gap-1">
                    {emails.length
                        ? emails.map((em) => (
                            <Badge
                                key={em}
                                variant="secondary"
                                className="bg-purple-400 text-white dark:bg-purple-500"
                            >
                                <Mail size={14} className="mr-1" />
                                {em}
                            </Badge>
                        ))
                        : null}
                </div>
            );
        },
    },
    { accessorKey: "ssn", header: "SSN" },
    {
        accessorKey: "dailyRate",
        header: "Daily Rate",
        cell: ({ row }) => {
            const amount = parseFloat(row.getValue("dailyRate"));
            const formatted = new Intl.NumberFormat("en-US", {
                style: "currency",
                currency: "USD",
            }).format(amount);
            return <div className="text-left font-semibold">{formatted}</div>;
        },
    },
    { accessorKey: "employmentState", header: "Employment State" },
    { accessorKey: "skills", header: "Skills" },
    { accessorKey: "location", header: "Location" },
    {
        id: "actions",
        cell: ({ row }) => <ActionsCell professional={row.original} onDirty={onDirty} />,
    },
];
