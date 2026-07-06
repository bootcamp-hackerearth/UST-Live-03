import PropTypes from "prop-types";
import axios from "axios";
import Link from "next/link";
import {cookies} from "next/headers";
import {redirect} from "next/navigation";
import GenericTable from "../components/GenericTable";
import {ENTITY_CONFIG} from "../lib/entityConfig";
import AccessDenied from "../components/AccessDenied";
import NotFound from "../components/NotFound";

export default async function EntityPage({
  params,
  searchParams,
}) {

  const {entity} = await params;
  const resolvedSearch = await searchParams;
  const page = Number(resolvedSearch?.page || 0);
  const sizePerPage = Number(resolvedSearch?.size || 5);
  const keyword = resolvedSearch?.keyword || "";
  const config = ENTITY_CONFIG[entity];

  if (!config) {
    return <NotFound />}

  const cookieStore = await cookies();
  const token = cookieStore.get("token")?.value;

  if (!token) {redirect("/login");}

  let rows = [];
  let totalPages = 1;
  let totalRecords = 0;

  try {
    const response = await axios.post(`http://localhost:8080${config.endpoint}`,
        {
          page,
          sizePerPage,
          sortField: "identifier",
          sortDirection: "ASC",
          keyword
        },
        {headers: {Authorization:`Bearer ${token}`}}
      );

    const data = response.data;
    rows = data.dtoList || [];
    totalPages = data.totalPages || 1;
    totalRecords = data.totalRecords || 0;

  } catch (error) {

  if (error.response) {
    const status = error.response.status;
    if (status === 403) {
      return <AccessDenied />;
    }
    if (status === 404) {
      return <NotFound />;
    }
    if (status === 401) {
      redirect("/login");
    }
  }
  return (
    <div className="p-4 text-red-600">
      Failed to load data
    </div>
  );
}

  return (
    <div>
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-3xl font-semibold capitalize text-slate-800">
          {entity} Listing
        </h1>

        <Link
          href={`/${entity}/add`}
          className="rounded-lg bg-blue-600 px-5 py-2.5 text-white font-medium hover:bg-blue-700 transition"
          >
          Add New
        </Link>
      </div>

      <GenericTable
        entity={entity}
        columns={config.columns}
        rows={rows}
        config={config}
        toggleEndpoint={config.toggleEndpoint}
        currentPage={page}
        pageSize={sizePerPage}
        totalPages={totalPages}
        totalRecords={totalRecords}
        showActions={config.entityName !== "Order"}
      />
    </div>
  );
}

EntityPage.propTypes = {
  params: PropTypes.shape({
    entity: PropTypes.string.isRequired,
  }).isRequired,
  searchParams: PropTypes.object,
};
