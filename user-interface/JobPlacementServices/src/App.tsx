import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { Homepage } from '@/Homepage.tsx';
import Layout from '@/layout.tsx';
import { useEffect, useState } from 'react';

export interface MeInterface {
    name: string,
    fullName: string,
    loginUrl: string,
    logoutUrl: string,
    principal: never | null,
    xsrfToken: string
}

function App() {
    const [me, setMe] = useState<MeInterface | null>(null);

    useEffect(() => {
        const backendUrl = "http://localhost:8083";
        const fetchMe = async () => {
            try {
                const res = await fetch(`${backendUrl}/me`, { credentials: 'include' });

                if (res.ok) {
                    const meData = await res.json();
                    console.log("User data:", meData);
                    setMe(meData);
                } else {
                    console.log("User not authenticated or error", res.status);
                    setMe(null);
                }
            } catch (e) {
                console.error("Error fetching user data:", e);
                setMe(null);
            }
        };

        fetchMe().then(() => console.log("User data fetched"));
    }, []);

    return (
        <Router>
            <Layout me={me}>
                <Routes>
                    <Route path="/" element={<Homepage />} />
                </Routes>
            </Layout>
        </Router>
    );
}

export default App;