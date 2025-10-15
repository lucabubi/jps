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

async function fetchProfessionals(): Promise<[] | Professional[]> {
    let result:[] | Professional[] = [];

    try {
        toast.info("Fetching professionals from CRM...");
        const res = await fetch(`${gatewayBackendUrl}/API/professionals/?page=0&size=10`, { credentials: 'include' });
        if (res.ok) {
            result = await res.json();
            toast.success("Professionals retrieved successfully.");
        }
        else {
            toast.error("Error contacting CRM/Gateway server.");
            result = [];
        }
    } catch {
        toast.error("Error contacting CRM/Gateway server.");
        result = [];
    }
    return result;
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
            result = false;
        }
    }
    catch {
        toast.error("Error contacting CRM/Gateway server.");
        result = false;
    }
    return result;
}

export { fetchUser, fetchProfessionals, deleteContactById };