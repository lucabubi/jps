import { useState, createContext, useContext, useCallback, useEffect, useMemo, ReactNode } from 'react';
import { fetchUser } from "@/lib/API";
import { toast } from "sonner";

// Interface wrapper for user data
interface UserOuterInterface {
    loginUrl: string,
    logoutUrl: string,
    userData: UserInnerInterface | null
}

// Interface representing the user data structure
interface UserInnerInterface {
    name: string,
    fullName: string,
    principal: any,
    xsrfToken: string,
    roles: string[],
}

// Interface representing the user context
interface AuthContextInterface {
    isAuthenticated: boolean;
    isCustomer: boolean;
    isProfessional: boolean;
    isOperator: boolean;
    userData: UserInnerInterface | null;
    handleLogin: () => Promise<void>;
    handleLogout: () => Promise<void>;
}

// Context for handling user info and user-related functions
const AuthContext = createContext<AuthContextInterface>({
    isAuthenticated: false,
    isCustomer: false,
    isProfessional: false,
    isOperator: false,
    userData: null,
    handleLogin: async () => {},
    handleLogout: async () => {}
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
    const [user, setUser] = useState<UserOuterInterface | null>(null);
    const [userData, setUserData] = useState<UserInnerInterface | null>(null);

    // need rewrite
    useEffect(() => {
        const checkAuth = async () => {
            try {
                const fetchedUser: UserOuterInterface | null = await fetchUser();
                if (fetchedUser != null) {
                    setUser(fetchedUser);
                    if (fetchedUser.userData) {
                        setIsAuthenticated(true);
                        setUserData(fetchedUser.userData);
                        setIsCustomer(fetchedUser.userData.roles.includes("customer"));
                        setIsProfessional(fetchedUser.userData.roles.includes("professional"));
                        setIsOperator(fetchedUser.userData.roles.includes("operator"));
                    } else {
                        setIsCustomer(false);
                        setIsProfessional(false);
                        setIsOperator(false);
                        setIsAuthenticated(false);
                    }
                } else throw new Error("Failed to fetch user data.");
            } catch {
                setIsCustomer(false);
                setIsProfessional(false);
                setIsOperator(false);
                setIsAuthenticated(false);
                setUser(null);
                setUserData(null);
                toast.error("Authentication-related functionalities are not available.");
            }
        };
        checkAuth();
    }, []);

    const handleLogin = useCallback(async () => {
        const url = user?.loginUrl ? 'http://localhost:8083' + user.loginUrl : null;
        if (!url) {
            toast.error("Login URL not available.");
            return;
        }
        window.location.href = url;
    }, [user]);

    const handleLogout = useCallback(async () => {
        const url = user?.logoutUrl ? 'http://localhost:8083' + user.logoutUrl : null;
        if (!url) {
            toast.error("Logout URL not available.");
            return;
        }
        window.location.href = url;
    }, [user]);

    // Memorize the context value
    const contextValue: AuthContextInterface = useMemo(() => ({
        isAuthenticated,
        isCustomer,
        isProfessional,
        isOperator,
        userData,
        handleLogin,
        handleLogout
    }), [isAuthenticated, userData, isCustomer, isProfessional, isOperator, handleLogin, handleLogout]);

    return (
        <AuthContext.Provider value={contextValue}>
            {children}
        </AuthContext.Provider>
    );
}

export type { UserOuterInterface, UserInnerInterface };
export { AuthProvider, useAuth };
