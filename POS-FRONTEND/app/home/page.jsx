import Layout from '@/components/Layout';

export default function Home() {

  return (

    <Layout>

      <div className="bg-white rounded-xl p-10 shadow-[0_8px_20px_rgba(0,0,0,0.08)]">

        <h1 className="text-4xl font-bold text-gray-800 mb-4">
          Welcome
        </h1>

        <p className="text-gray-600 text-lg">
          Select a module from the sidebar to continue managing your application.
        </p>

      </div>

    </Layout>
  );
}