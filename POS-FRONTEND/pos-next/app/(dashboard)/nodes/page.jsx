import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import axios from "axios";

export default async function NodesPage() {
  
  const cookieStore = await cookies();
  const token = cookieStore.get("token")?.value;

  if (!token) {redirect("/login");}

  let nodes = [];

  try {
    const response = await axios.post("http://localhost:8080/api/node/list",
      {
        page: 0,
        sizePerPage: 20,
        sortDirection: "ASC",
        sortField: "id",
      },
      {headers: {Authorization: `Bearer ${token}`}}
    );
    
    nodes = response.data.dtoList || [];

  } catch (error) {
    console.log(error);
    nodes = [];
  }

  return (
    <div className="bg-white rounded-2xl shadow-sm border border-slate-200 p-6">
      <div className="flex items-center justify-between mb-5">
        <h1 className="text-2xl font-bold text-slate-800">
          Nodes List
        </h1>

      </div>

      {nodes.length > 0 ? (
        <div className="overflow-x-auto">
          <table className="w-full border-collapse">
            <thead>
              <tr className="border-b border-slate-200 bg-slate-50">
                <th className="text-left px-4 py-3 text-sm font-semibold text-slate-600">
                  Identifier
                </th>
                <th className="text-left px-4 py-3 text-sm font-semibold text-slate-600">
                  Path
                </th>
                <th className="text-left px-4 py-3 text-sm font-semibold text-slate-600">
                  Status
                </th>
              </tr>
            </thead>

            <tbody>

              {nodes.map((node) => {
                const rowKey = node.id ?? node.identifier ?? node.path;

                return (
                  <tr
                    key={rowKey}
                    className="border-b border-slate-100 hover:bg-slate-50"
                  >
                    <td className="px-4 py-3 text-sm text-slate-700">
                      {node.id}
                    </td>
                    <td className="px-4 py-3 text-sm text-slate-700">
                      {node.identifier}
                    </td>
                    <td className="px-4 py-3 text-sm text-slate-700">
                      {node.path}
                    </td>

                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      ) : (

        <div className="text-center py-10 text-slate-500">
          No nodes found
        </div>

      )}

    </div>
  );
}