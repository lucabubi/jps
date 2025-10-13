import { useEffect, useState } from "react";
import { fetchProfessionals } from "@/lib/API";
import { Professional } from "@/lib/API";
//import { Spinner } from "@/components/ui/spinner";
import { createProfessionalsColumns } from "@/components/dashboard/CreateProfessionalsColumns.tsx";
import { DataTable } from "@/components/ui/data-table";
import { Spinner } from "@/components/ui/spinner.tsx";

export default function Professionals() {
    const [isLoading, setIsLoading] = useState(true);
    const [professionals,setProfessionals] = useState<Professional[] | []>([]);
    // isDirty indicates if we need to refresh the data (es. after insertion/pagination change...)
    const [isDirty, setIsDirty] = useState(true);

    useEffect(() => {
        if (!isDirty) return;
        // Fetch professionals data
        const getProfessionals = async () => {
            setIsLoading(true);
            try {
                const data = await fetchProfessionals();
                setProfessionals(data);
            } finally {
                setIsLoading(false);
                setIsDirty(false);
            }
        };
        getProfessionals();
    }, [isDirty]);

    if (isLoading) {
        return <Spinner />
    }

    return (
        <div className="w-full">
            <DataTable columns={createProfessionalsColumns(() => setIsDirty(true))} data={professionals}/>
        </div>
    );
}