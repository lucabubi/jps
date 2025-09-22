import { useState, createContext, useContext, useEffect, useMemo, ReactNode } from 'react';
import { fetchUser } from "@/lib/API";
import { toast } from "sonner";

// Interface representing the user data structure
interface UserInterface {
    name: string,
    fullName: string,
    loginUrl: string,
    logoutUrl: string,
    principal: never | null,
    xsrfToken: string
}

// Interface representing the user context
interface AuthContextInterface {
    isAuthenticated: boolean;
    isCustomer: boolean;
    isProfessional: boolean;
    isOperator: boolean;
    userData: UserInterface | null;
    handleLogin: () => Promise<void>;
    handleLogout: () => Promise<void>;
}

async function handleLogin() {
    window.location.href = "http://localhost:8083/secure";
}

async function handleLogout() {
    try {
        // Call the backend logout endpoint giving cookies
        await fetch(`http://localhost:8083/logout`, {
            method: 'POST',
            credentials: 'include',
        });
        window.location.href = 'http://localhost:8083/logout';
    } catch {
        toast.error("Error during logout process.");
    }
}

// Context for handling user info and user-related functions
const AuthContext = createContext<AuthContextInterface>({
    isAuthenticated: false,
    isCustomer: false,
    isProfessional: false,
    isOperator: false,
    userData: null,
    handleLogin,
    handleLogout,
});

function useAuth(): AuthContextInterface {
    return useContext(AuthContext);
}

function AuthProvider({ children }: { children: ReactNode }) {
    // Keep track in the client of user role and user data
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isCustomer, setIsCustomer] = useState(false);
    const [isProfessional, setIsProfessional] = useState(false);
    const [isOperator, setIsOperator] = useState(false);
    const [userData, setUserData] = useState<UserInterface | null>(null);

    useEffect(() => {
        const checkAuth = async () => {
            try {
                const user: UserInterface | null = await fetchUser();
                if (!user) {
                    setIsAuthenticated(false);
                    return;
                }
                // setIsCustomer(user.roles.includes("customer"));
                // setIsProfessional(user.roles.includes("professional"));
                // setIsOperator(user.roles.includes("operator"));
                setUserData(user);
                setIsAuthenticated(true);
            } catch {
                setIsAuthenticated(false);
                toast.error("Authentication-related functionalities are not available.");
            }
        };
        checkAuth();
    }, []);

    // Memorize the context value
    const contextValue: AuthContextInterface = useMemo(() => ({
        isAuthenticated,
        isCustomer,
        isProfessional,
        isOperator,
        userData,
        handleLogin,
        handleLogout
    }), [isAuthenticated, userData, isCustomer, isProfessional, isOperator]);

    return (
        <AuthContext.Provider value={contextValue}>
            {children}
        </AuthContext.Provider>
    );
}

export type { UserInterface };
export { AuthProvider, useAuth };
