import {useEffect, useState} from "react";
import { ColumnDef } from "@tanstack/react-table"
import {Customer, Email} from "@/lib/API";
import { Badge } from "@/components/ui/badge"
import { Copy, Mail, MoreHorizontal, Trash2, UserPen } from "lucide-react";
import { Button } from "@/components/ui/button"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel, DropdownMenuSeparator,
    //DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
    Drawer,
    DrawerClose,
    DrawerContent,
    DrawerDescription,
    DrawerFooter,
    DrawerHeader,
    DrawerTitle,
} from "@/components/ui/drawer"
import { deleteContactById } from "@/lib/API";
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

function ConfirmDeleteCustomerDrawer({ open, onOpenChange, onConfirm, idToConfirm }: ConfirmDeleteDrawerProps) {
    const [typedId, setTypedId] = useState("");

    useEffect(() => {
        if (open) setTypedId("");
    }, [open]);

    const expected = String(idToConfirm);
    const canConfirm = typedId === expected;

    return (
        <Drawer open={open} onOpenChange={onOpenChange}>
            <DrawerContent>
                <DrawerHeader>
                    <DrawerTitle>Attention! You're deleting a customer and the associated contact.</DrawerTitle>
                    <DrawerDescription>
                        This action is irreversible. The associated contact information will be deleted automatically. Paste actions are temporarily disabled. Confirm you want to delete the customer with ID: <Badge className="rounded-none bg-red-100 text-red-800 border border-red-200 dark:bg-red-900/30 dark:text-red-200 dark:border-red-800 hover:bg-red-100 hover:text-red-800 hover:border-red-200 transition-none">{expected}</Badge>
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
                            className="font-bold text-red-800 focus-visible:ring-red-700 focus:ring-red-700 selection:bg-red-200 selection:text-red-900 dark:selection:bg-red-900/40"
                            onPaste={(e) => {e.preventDefault(); toast.error("For security reasons, paste actions are disabled here.")}}
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

const handleDeleteCustomer = async (customerId: number, onDirty: OnDirty) => {
    if (!customerId) {
        toast.error("Unable to find the given customer.");
        return;
    }
    const result = await deleteContactById(customerId);
    if (result) {
        onDirty();
        toast.success("Customer deleted.");
    } else {
        toast.error("Delete failed.");
    }
};

function ActionsCell({ customer, onDirty }: { customer: Customer; onDirty: OnDirty }) {
    const [open, setOpen] = useState(false);

    const confirmAndDelete = async () => {
        await handleDeleteCustomer(customer.contact.id, onDirty);
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
                    <DropdownMenuItem onClick={() => {
                        try {
                            navigator.clipboard.writeText(String(customer.id))
                            toast.success("ID copied to clipboard.")
                        } catch {
                            toast.error("Unable to copy ID to clipboard.")
                        }
                    }}>
                        <Copy />
                        Copy ID
                    </DropdownMenuItem>
                    <DropdownMenuItem disabled={!customer.contact.ssn } onClick={() => {
                        try {
                            navigator.clipboard.writeText(String(customer.contact.ssn))
                            toast.success("SSN copied to clipboard.")
                        } catch {
                            toast.error("Unable to copy SSN to clipboard.")
                        }
                    }}>
                        <Copy />
                        Copy SSN
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem>
                        <UserPen />
                        View/Edit Profile
                    </DropdownMenuItem>
                    <DropdownMenuItem
                        onClick={() => setOpen(true)}
                        className="text-red-600 focus:text-red-600 data-[highlighted]:bg-destructive/20"
                    >
                        <Trash2 className="text-red-600" />
                        <span className="text-red-600">Delete Customer</span>
                    </DropdownMenuItem>
                </DropdownMenuContent>
            </DropdownMenu>

            <ConfirmDeleteCustomerDrawer
                open={open}
                onOpenChange={setOpen}
                onConfirm={confirmAndDelete}
                idToConfirm={customer.id}
            />
        </>
    );
}

export const createCustomersColumns = (onDirty: OnDirty): ColumnDef<Customer>[] => [
    { accessorKey: "id", header: "ID" },
    { accessorKey: "contact.name", header: "First Name" },
    { accessorKey: "contact.surname", header: "Last Name" },
    {
        id: "emails",
        header: "Email",
        accessorFn: (row) => {
            // emails may be stored as Email objects or plain strings; normalize to Email[]
            const list = Array.isArray(row.contact?.emails) ? row.contact.emails : [];
            return list
                .map((e, i) => {
                    if (e == null) return undefined;
                    if (typeof e === "string") {
                        // create a minimal Email-shaped object for rendering
                        return { id: `s-${i}`, email: e } as unknown as Email;
                    }
                    // assume it's already an Email-like object
                    return e as Email;
                })
                .filter(Boolean) as Email[];
        },
        cell: ({ getValue }) => {
            const emails = getValue<Email[]>();
            return (
                <div className="flex flex-wrap gap-1">
                    {emails.length ? (
                        emails.map((em, idx) => (
                            <Badge
                                key={String((em as any).id ?? `email-${idx}`)}
                                variant="secondary"
                                className="rounded-md"
                            >
                                <Mail className='size-3 mr-1' />
                                {em.email}
                            </Badge>
                        ))
                    ) : (
                        "No data."
                    )}
                </div>
            );
        },
    },
    { accessorKey: "contact.ssn", header: "SSN" },
    {
        id: "actions",
        header: "Actions",
        cell: ({ row }) => <ActionsCell customer={row.original} onDirty={onDirty} />,
    },
];
