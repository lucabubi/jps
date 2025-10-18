import { useState, createContext, useContext, useCallback, useEffect, useMemo, ReactNode } from 'react';
import { Navigate, Outlet } from "react-router-dom";
import { Spinner as SpinnerIO } from '@/components/ui/shadcn-io/spinner/spinner.tsx';
import Layout  from "@/components/layout/Layout.tsx";
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
    principal: object,
    xsrfToken: string,
    roles: string[],
}

// Interface representing the user context
interface AuthContextInterface {
    isLoading: boolean;
    isAuthenticated: boolean;
    isCustomer: boolean;
    isProfessional: boolean;
    isOperator: boolean;
    userData: UserInnerInterface | null;
    handleLogin: () => Promise<void>;
    handleLogout: () => Promise<void>;
}

// One-time toast keys (per session)
const INFO_TOAST_KEY = 'auth.infoToastShown';
const SUCCESS_TOAST_KEY = 'auth.successToastShown';

// Context for handling user info and user-related functions
const AuthContext = createContext<AuthContextInterface>({
    isLoading: true,
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
    const [isLoading, setIsLoading] = useState(true);
    // Keep track in the client of user role and user data
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isCustomer, setIsCustomer] = useState(false);
    const [isProfessional, setIsProfessional] = useState(false);
    const [isOperator, setIsOperator] = useState(false);
    const [user, setUser] = useState<UserOuterInterface | null>(null);
    const [userData, setUserData] = useState<UserInnerInterface | null>(null);

    useEffect(() => {
        const checkAuth = async () => {
            setIsLoading(true);
            // Show "checking" only once per session
            if (!sessionStorage.getItem(INFO_TOAST_KEY)) {
                toast.info("Checking for a valid authentication cookie just for you...");
                sessionStorage.setItem(INFO_TOAST_KEY, '1');
            }
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
                        // Show "success" only once per session
                        if (!sessionStorage.getItem(SUCCESS_TOAST_KEY)) {
                            toast.success("User authenticated successfully.");
                            sessionStorage.setItem(SUCCESS_TOAST_KEY, '1');
                        }
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
            } finally {
                setIsLoading(false);
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
        // Reset session toasts on new login flow
        sessionStorage.removeItem(INFO_TOAST_KEY);
        sessionStorage.removeItem(SUCCESS_TOAST_KEY);
        window.location.href = url;
    }, [user]);

    const handleLogout = useCallback(async () => {
        const url = user?.logoutUrl ? 'http://localhost:8083' + user.logoutUrl : null;
        if (!url) {
            toast.error("Logout URL not available.");
            return;
        }
        // Clear toasts so next session can show them again
        sessionStorage.removeItem(INFO_TOAST_KEY);
        sessionStorage.removeItem(SUCCESS_TOAST_KEY);
        window.location.href = url;
    }, [user]);

    // Memorize the context value
    const contextValue: AuthContextInterface = useMemo(() => ({
        isLoading,
        isAuthenticated,
        isCustomer,
        isProfessional,
        isOperator,
        userData,
        handleLogin,
        handleLogout
    }), [isLoading, isAuthenticated, userData, isCustomer, isProfessional, isOperator, handleLogin, handleLogout]);

    return (
        <AuthContext.Provider value={contextValue}>
            {children}
        </AuthContext.Provider>
    );
}

function Authenticated({ children }: { children: ReactNode }) {
    const { isAuthenticated, isLoading } = useAuth();
    if (isLoading) {
        return (
            <div className="flex h-screen w-full items-center justify-center bg-background">
                <SpinnerIO variant="infinite" />
            </div>
        )
    }
    if (!isAuthenticated) {
        toast.error(<>Requested resource needs authentication!<br/>You have been redirected to the Landing page.</>);
        return <Navigate to="/" replace />;
    }
    return <>{children}</>;
}

function ProtectedLayout() {
    return (
        <Authenticated>
            <div style={{ display: 'flex', minHeight: '100dvh' }}>
                <Layout>
                    <Outlet />
                </Layout>
            </div>
        </Authenticated>
    );
}

export type { UserOuterInterface, UserInnerInterface };
export { AuthProvider, useAuth, Authenticated, ProtectedLayout };
