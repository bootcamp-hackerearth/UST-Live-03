'use client';

import { useRouter } from 'next/navigation';
import { FileQuestion, ArrowLeft } from 'lucide-react';

export default function ResourceNotFound() {
  const router = useRouter();

  return (
    <div className="fixed inset-0 z-50 flex flex-col items-center justify-center bg-gray-50 px-4 text-center dark:bg-gray-900">
      <div className="max-w-md animate-fade-in">
        
        <div className="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-indigo-100 text-indigo-600 dark:bg-indigo-900/30 dark:text-indigo-400">
          <FileQuestion className="h-10 w-10" strokeWidth={1.5} />
        </div>

        <h1 className="mb-2 text-3xl font-bold tracking-tight text-gray-900 dark:text-white sm:text-4xl">
          Resource Not Found
        </h1>
        <p className="mb-8 text-base text-gray-600 dark:text-gray-400">
          The page or asset you are looking for doesn't exist or has been moved.
        </p>

        <button
          onClick={() => router.push("/")}
          className="inline-flex items-center gap-2 rounded-lg bg-indigo-600 px-5 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600 transition-colors duration-200"
        >
          <ArrowLeft className="h-4 w-4" strokeWidth={2} />
          Go to Home
        </button>
        
      </div>
    </div>
  );
}