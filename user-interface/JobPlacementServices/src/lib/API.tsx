import {toast} from 'sonner'
import {UserOuterInterface} from '@/hooks/useAuth';

export type Email = {
    id: number;
    email: string;
};

export type Telephone = {
    id: number;
    telephone: string;
};

export type Address = {
    id: number;
    address: string;
};

export type Contact = {
    id: number;
    name: string;
    surname: string;
    ssn: string | null;
    category: "CUSTOMER" | "PROFESSIONAL" | "UNKNOWN";
    emails: Email[];
    addresses: Address[];
    telephones: Telephone[];
};

export type Professional = {
    id: number;
    contact: Contact;
    notes: string[];
    skills: string[]; // Set server side -> JSON array
    dailyRate: number;
    employmentState: "EMPLOYED" | "AVAILABLE_FOR_WORK" | "NOT_AVAILABLE";
    location: string | null;
};

export type Message = {
    id: number;
    subject: string;
    body: string;
    sender: string;
    channel: "PHONE_CALL" | "TEXT_MESSAGE" | "EMAIL";
    date: string;
    priority: "LOW" | "MEDIUM" | "HIGH";
    state: "RECEIVED" | "READ" | "DISCARDED" | "PROCESSING" | "DONE" | "FAILED";
}

// URL of the backend server
const gatewayBackendUrl = "http://localhost:8083";

// Function to fetch user data from the backend
async function fetchUser(): Promise<UserOuterInterface | null> {
    let result: UserOuterInterface | null = null;

    try {
        const res = await fetch(`${gatewayBackendUrl}/me`, { credentials: 'include' });

        if (res.ok) {
            const parsed = await res.json();
            const x: UserOuterInterface = {
                loginUrl: parsed.loginUrl,
                logoutUrl: parsed.logoutUrl,
                userData: null
            };
            if (parsed.principal) {
                x.userData = {
                    name: parsed.name,
                    fullName: parsed.fullName,
                    principal: parsed.principal,
                    xsrfToken: parsed.xsrfToken,
                    roles: parsed.roles
                };
            }
            result = x;
        } else {
            toast.error("Error contacting authentication server.");
            result = null;
        }
    } catch {
        toast.error("Error contacting authentication server.");
        result = null;
    }

    return result;
}

async function fetchProfessionals(pageIndex: number = 0, pageSize: number = 10): Promise<{ total: number; data: Professional[] }> {
    let data: Professional[] = [];
    let total = 0;

    try {
        toast.info("Fetching professionals from CRM...");
        const res = await fetch(`${gatewayBackendUrl}/API/professionals/?page=${pageIndex}&size=${pageSize}`, { credentials: 'include' });

        if (res.ok) {
            const headerTotal = res.headers.get('X-Total-Count') ?? res.headers.get('x-total-count');
            const parsedTotal = headerTotal ? parseInt(headerTotal, 10) : 0;
            total = Number.isNaN(parsedTotal) ? 0 : parsedTotal;

            data = await res.json();
            toast.success("Professionals retrieved successfully.");
        } else {
            toast.error("Error contacting CRM/Gateway server.");
        }
    } catch {
        toast.error("Error contacting CRM/Gateway server.");
    }

    return { total, data };
}


async function deleteContactById(contactId: number): Promise<boolean> {
    let result = false;

    try {
        toast.info("Deleting data from CRM...");
        const res = await fetch(`${gatewayBackendUrl}/API/contacts/${contactId}`, {
            method: 'DELETE',
            credentials: 'include'
        });
        if (res.ok) {
            toast.success("Data deleted successfully.");
            result = true;
        }
        else {
            toast.error("Error contacting CRM/Gateway server.");
        }
    }
    catch {
        toast.error("Error contacting CRM/Gateway server.");
    }
    return result;
}

async function fetchMessages(pageIndex: number = 0, pageSize: number = 10): Promise<{ total: number; data: Message[] }> {
    let data: Message[] = [];
    let total = 0;

    try {
        toast.info("Fetching messages from CRM...");
        const res = await fetch(`${gatewayBackendUrl}/API/messages/?page=${pageIndex}&size=${pageSize}`, { credentials: 'include' });

        if (res.ok) {
            const headerTotal = res.headers.get('X-Total-Count') ?? res.headers.get('x-total-count');
            const parsedTotal = headerTotal ? parseInt(headerTotal, 10) : 0;
            total = Number.isNaN(parsedTotal) ? 0 : parsedTotal;

            data = await res.json();
            toast.success("Messages retrieved successfully.");
        } else {
            toast.error("Error contacting CRM/Gateway server.");
        }
    } catch {
        toast.error("Error contacting CRM/Gateway server.");
    }

    return { total, data };
}

export { fetchUser, fetchProfessionals, fetchMessages, deleteContactById };