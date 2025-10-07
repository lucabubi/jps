import { useEffect, useState } from "react";
import { fetchProfessionals } from "@/lib/API";

export default function Professionals() {

    const [,setProfessionals] = useState<[]>([]);

    useEffect(() => {
        const getProfessionals = async () => {
            const data = await fetchProfessionals();
            setProfessionals(data);
        }
        getProfessionals();
    }, []);

    return (
        <></>
    )
}