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
                    <div className="w-full h-full antialiased max-w-8xl mx-auto">
                    <App />
                        <Toaster richColors position="bottom-right" />
                    </div>
                </AuthProvider>
            </ThemeProvider>
    </StrictMode>,
)
