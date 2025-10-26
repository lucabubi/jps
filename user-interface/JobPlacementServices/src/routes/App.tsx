import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import Landing from "@/routes/Landing";
import Home from '@/routes/dashboard/Home';
import Support from '@/routes/dashboard/Support';
import Customers from "@/routes/dashboard/Customers.tsx";
import Professionals from "@/routes/dashboard/Professionals.tsx";
import { ProtectedLayout } from "@/hooks/useAuth";
import Messages from "@/routes/dashboard/Messages.tsx";
//import { toast } from 'sonner';

function App() {
    return (
        <>
            <Router>
                <Routes>
                    {/* Landing page always accessible */}
                    <Route path="/" element={<Landing />} />

                    {/* Protected routes */}
                    <Route path="/dashboard/*" element={<ProtectedLayout />}>
                        <Route index element={<Home />} />
                        <Route path="support" element={<Support />} />
                        <Route path="customers" element={<Customers />} />
                        <Route path="professionals" element={<Professionals />} />
                        <Route path="messages" element={<Messages />} />
                    </Route>

                    {/* All other not-found routes redirect to landing page with not found error message */}
                    <Route path="*" element={ <NotFound /> } />
                </Routes>
            </Router>
        </>
    );
}

function NotFound() {
    //toast.error(<>Requested resource was not found!<br />You have been redirected to the Landing page.</>);
    return <Navigate to="/" replace/>;
}


export default App;