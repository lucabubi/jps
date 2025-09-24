import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import {Toaster} from "sonner";
import { ThemeProvider } from "@/hooks/useTheme"
import App from "@/routes/App"
import { AuthProvider } from "@/hooks/useAuth";
import './index.css'

createRoot(document.getElementById('root')!).render(
    <StrictMode>
            <ThemeProvider defaultTheme="system" storageKey="vite-ui-theme">
                <AuthProvider>
                    <div className="relative flex flex-col h-screen w-screen overflow-hidden">
                        <App />
                        <Toaster richColors position="bottom-right" />
                    </div>
                </AuthProvider>
            </ThemeProvider>
    </StrictMode>,
)
