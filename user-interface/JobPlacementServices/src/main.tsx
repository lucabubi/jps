import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from "@/routes/App"
import { AuthProvider } from "@/hooks/useAuth";
import './index.css'

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <div className="relative flex flex-col h-screen w-screen overflow-hidden">
            <AuthProvider>
                <App />
            </AuthProvider>
        </div>
    </StrictMode>,
)
