import { useEffect, useRef, useState } from "react";
import { fetchProfessionals } from "@/lib/API";
import { Professional } from "@/lib/API";
//import { Spinner } from "@/components/ui/spinner";
import { createProfessionalsColumns } from "@/components/dashboard/CreateProfessionalsColumns.tsx";
import { DataTable } from "@/components/ui/data-table";
import { Spinner as SpinnerIO } from "@/components/ui/shadcn-io/spinner/spinner.tsx";
import { Button } from "@/components/ui/button.tsx";
import { Spinner } from "@/components/ui/spinner.tsx";
import CreateProfessionalButton from "@/components/dashboard/CreateProfessionalButton";
import { RefreshCcw } from "lucide-react";

export type paginationType = {
    pageIndex: number;
    pageSize: number;
    totalResults: number;
}

export default function Professionals() {
    const [isLoading, setIsLoading] = useState(true);
    const [professionals,setProfessionals] = useState<Professional[] | []>([]);
    const [pagination, setPagination] = useState<paginationType>({
        pageIndex: 0,       // default page index
        pageSize: 10,       // default page size
        totalResults: 0,    // default total results
    });

    // ensure default column visibility in localStorage if not present
    useEffect(() => {
        try {
            const COLUMN_VISIBILITY_STORAGE_KEY = "professional.columnVisibility";
            const saved = localStorage.getItem(COLUMN_VISIBILITY_STORAGE_KEY);
            if (!saved) {
                localStorage.setItem(COLUMN_VISIBILITY_STORAGE_KEY, JSON.stringify({"id":false,"contact_ssn":false,"dailyRate":false,"skills":false, "location":false}) );
            }
        } catch {
            // ignore storage errors
        }
    }, []);

    // isDirty indicates if we need to refresh the data (es. after insertion/pagination change...)
    const [isDirty, setIsDirty] = useState(true);

    // mark dirty when pagination changes, skipping the initial render
    const isFirstLoad = useRef(true);
    useEffect(() => {
        if (isFirstLoad.current) {
            isFirstLoad.current = false;
            return;
        }
        setIsDirty(true);
    }, [pagination.pageIndex, pagination.pageSize]);

    useEffect(() => {
        if (!isDirty) return;
        // Fetch professionals data
        const getProfessionals = async () => {
            setIsLoading(true);
            try {
                const data = await fetchProfessionals(pagination.pageIndex, pagination.pageSize);
                setProfessionals(data.data);
                setPagination(prev => ({ ...prev, totalResults: data.total }));
            } finally {
                setIsLoading(false);
                setIsDirty(false);
            }
        };
        getProfessionals();
    }, [isDirty, pagination.pageIndex, pagination.pageSize]);

    return (
        <>
            <div className="w-full flex items-center justify-between border-b">
                <h2 className="scroll-m-20 pb-2 text-3xl font-semibold tracking-tight first:mt-2">
                    Professionals
                </h2>
                <div className="flex items-center gap-2">
                    <Button variant="outline" size="sm" aria-label="Refresh" disabled={isLoading} onClick={() => setIsDirty(true)}>
                        { isLoading ? <Spinner className="text-violet-600" /> : <RefreshCcw /> }
                        <span className="hidden sm:inline">Refresh</span>
                    </Button>
                    <CreateProfessionalButton isLoading={isLoading} />
                </div>
            </div>
            { isLoading ?
                <div className="w-full flex justify-center items-center h-64">
                    <SpinnerIO variant="infinite" />
                </div>
            :
            <div className="w-full">
                <DataTable variant="professional" columns={createProfessionalsColumns(() => setIsDirty(true))} data={professionals} pagination={pagination} setPagination={setPagination}/>
            </div>
            }
        </>
    );
}