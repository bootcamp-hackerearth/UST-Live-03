import Layout from '@/components/Layout';
 
export default function Home() {
 
  return (
 
    <Layout>
 
      <div className="bg-white rounded-2xl p-10 shadow-lg border-t-4 border-cyan-500">
        <div className="inline-block px-4 py-1 bg-cyan-100 text-cyan-700 rounded-full text-sm font-medium mb-6">
          Dashboard
        </div>
        <h1 className="text-4xl font-bold text-slate-800 mb-4">
          Welcome
        </h1>
 
        <p className="text-slate-600 text-lg">
          Select a module from the sidebar to continue managing your application.
        </p>
 
      </div>
 
    </Layout>
  );
}
