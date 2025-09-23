import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { Toaster } from 'sonner';
import Landing from "@/routes/Landing";
import { Homepage } from '@/routes/dashboard/Homepage';

function App() {

    return (
        <>
        <Router>
                <Routes>
                    <Route path="/landing" element={ <Landing /> } />
                    { /*<Route path="/*" element={  } /> */}
                    <Route path="/" element={<Homepage /> } />
                    { /*<Route path="/settings" element={ } /> */}
                </Routes>
        </Router>
        <Toaster position="bottom-right"/>
        </>
    );
}

export default App;