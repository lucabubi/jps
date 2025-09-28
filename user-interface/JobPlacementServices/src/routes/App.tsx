import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import Landing from "@/routes/Landing";
import Home from '@/routes/dashboard/Home';
import { ProtectedLayout } from "@/hooks/useAuth";
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