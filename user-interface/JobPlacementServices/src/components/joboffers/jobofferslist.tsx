import { useEffect, useState } from 'react';
import {columns, JobOffer} from './columns';
import { DataTable } from './data-table';

async function getData(): Promise<JobOffer[]> {
  const response = await fetch('http://localhost:8083/crm2/API/joboffers/', { credentials: 'include' });
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  return await response.json();
}

export default function JobOffersList() {
  const [data, setData] = useState<JobOffer[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getData().then((data) => {
      setData(data);
      setLoading(false);
    }).catch((error) => {
      console.error('Error fetching data:', error);
      setLoading(false);
    });
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
      <div className="container mx-auto py-10">
        <DataTable<JobOffer, never> columns={columns} data={data} />
      </div>
  );
}