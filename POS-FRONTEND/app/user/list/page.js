"use client";
import { useEffect, useState, useMemo } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import api from "../../components/Axios";

const thStyle = "text-left p-3.5 border-b-2 border-solid border-gray-200 text-[12px] text-gray-500 font-bold uppercase tracking-wider bg-gray-50 sticky top-0";
const headers = ["Username", "Name", "Phone No", "Roles", "Status", "Actions"];

function NavButton({ disabled, onClick, children }) {
  return (
    <button
      className={`min-w-9 h-9 px-3 rounded-lg border-[1.5px] border-solid bg-white font-bold text-lg flex items-center justify-center transition-all ${
        disabled
          ? "text-gray-300 border-gray-100 cursor-not-allowed"
          : "text-brand border-gray-200 cursor-pointer hover:bg-gray-50"
      }`}
      disabled={disabled}
      onClick={onClick}
    >
      {children}
    </button>
  );
}
NavButton.propTypes = {
  disabled: PropTypes.bool.isRequired,
  onClick: PropTypes.func.isRequired,
  children: PropTypes.node.isRequired,
};

function ActionButton({ onClick, className, children }) {
  return (
    <button
      className={`px-3 py-1.25 rounded-md text-white border-none cursor-pointer text-xs font-semibold transition-colors ${className}`}
      onClick={onClick}
    >
      {children}
    </button>
  );
}
ActionButton.propTypes = {
  onClick: PropTypes.func.isRequired,
  className: PropTypes.string.isRequired,
  children: PropTypes.node.isRequired,
};

function ModalButton({ onClick, className, children }) {
  return (
    <button
      className={`px-4.5 py-2.25 rounded-md border-none cursor-pointer text-xs font-semibold transition-colors ${className}`}
      onClick={onClick}
    >
      {children}
    </button>
  );
}
ModalButton.propTypes = {
  onClick: PropTypes.func.isRequired,
  className: PropTypes.string.isRequired,
  children: PropTypes.node.isRequired,
};

function DeleteModal({ target, onCancel, onConfirm }) {
  if (!target) return null;
  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 font-sans">
      <div className="bg-white p-6 rounded-xl shadow-[0_4px_20px_rgba(0,0,0,0.15)] w-full max-w-[400px] text-center">
        <h3 className="m-0 mb-2.5 text-lg font-bold text-gray-800">Confirm Deletion</h3>
        <p className="m-0 mb-5 text-sm text-gray-500 leading-relaxed">
          Are you sure you want to delete <strong>{target}</strong>? This action cannot be undone.
        </p>
        <div className="flex gap-3 justify-center">
          <ModalButton className="bg-gray-100 text-gray-600 hover:bg-gray-200" onClick={onCancel}>
            Cancel
          </ModalButton>
          <ModalButton className="bg-[#d62828] text-white hover:bg-[#ba2222]" onClick={onConfirm}>
            Delete
          </ModalButton>
        </div>
      </div>
    </div>
  );
}
DeleteModal.propTypes = {
  target: PropTypes.string,
  onCancel: PropTypes.func.isRequired,
  onConfirm: PropTypes.func.isRequired,
};
DeleteModal.defaultProps = {
  target: null,
};

export default function ListUser() {
  const [data, setData] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [pagination, setPagination] = useState({
    page: 0,
    sizePerPage: 4,
    sortDirection: "ASC",
    sortField: "id",
  });
  const router = useRouter();

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedSearch(searchTerm);
      setPagination((p) => ({ ...p, page: 0 }));
    }, 300);
    return () => clearTimeout(handler);
  }, [searchTerm]);

  useEffect(() => {
    const clean = (v) => (Array.isArray(v) ? v.join(" ") : String(v ?? ""));
    const matcher = (item, r, f) =>
      r.test(String(item.username ?? "")) ||
      f.some((k) => r.test(clean(item[k])));
    const load = async () => {
      const f = ["name", "phoneNo", "roles"];
      try {
        const res = await api.post("/user/list",
          debouncedSearch.trim()
            ? { ...pagination, sizePerPage: 1000 }
            : pagination
        );
        if (debouncedSearch.trim()) {
          const esc = debouncedSearch.replaceAll(/[.*+?^${}()|[\]\\]/g, String.raw`\$&`);
          const rx = new RegExp(String.raw`\b${esc}`, "i");
          const list = res.data.dtoList ?? [];
          setData(list.filter((i) => matcher(i, rx, f)));
          setTotalPages(1);
        } else {
          setData(res.data.dtoList ?? []);
          setTotalPages(res.data.totalPages ?? 1);
        }
      } catch {}
    };
    load();
  }, [pagination, debouncedSearch]);

  const refresh = () => setPagination((p) => ({ ...p }));
  const go = (p) => setPagination((prev) => ({ ...prev, page: p }));

  const confirmDelete = async () => {
    if (!deleteTarget) return;
    await api.get(`/user/delete?username=${encodeURIComponent(deleteTarget)}`);
    setDeleteTarget(null);
    refresh();
  };

  const toggle = async (u) => {
    await api.post(`/user/toggle-status?username=${encodeURIComponent(u)}`);
    refresh();
  };

  const page = pagination.page;

  const visible = useMemo(() => {
    let s = page - 1;
    if (s < 0) s = 0;
    if (s + 3 > totalPages) s = totalPages - 3;
    if (s < 0) s = 0;
    return Array.from({ length: Math.min(3, totalPages) }, (_, i) => s + i);
  }, [page, totalPages]);

  const renderRow = (r) => {
    const cells = [
      r.username,
      r.name,
      r.phoneNo,
      Array.isArray(r.roles) ? r.roles.join(", ") : r.roles,
      <button
        key="s"
        className={`w-12 py-1.25 text-center text-white rounded-full cursor-pointer text-[12px] font-semibold select-none border-none ${
          r.status ? "bg-brand" : "bg-gray-300"
        }`}
        onClick={() => toggle(r.username)}
      >
        {r.status ? "ON" : "OFF"}
      </button>,
      <div key="a">
        <ActionButton className="mr-2 bg-[#1e6091] hover:bg-[#1a527c]" onClick={() => router.push(`/user/edit/${r.username}`)}>
          Edit
        </ActionButton>
        <ActionButton className="bg-[#d62828] hover:bg-[#ba2222]" onClick={() => setDeleteTarget(r.username)}>
          Delete
        </ActionButton>
      </div>,
    ];
    return (
      <tr key={r.username} className="border-b border-solid border-gray-100 hover:bg-gray-50/50 transition-colors">
        {cells.map((c, i) => (
          <td key={`${r.username}-cell-${i}`} className="p-3.5 text-sm text-gray-800">
            {c}
          </td>
        ))}
      </tr>
    );
  };

  return (
    <div className="fixed top-[60px] left-[220px] right-0 bottom-0 bg-gray-50 font-sans flex flex-col overflow-hidden">
      <div className="flex-1 p-5 md:p-6 flex flex-col overflow-hidden">
        <div className="flex items-center mb-4 shrink-0 relative gap-3">
          <button
            className="px-4 py-2 bg-transparent text-brand border-[1.5px] border-solid border-brand rounded-lg text-xs font-semibold cursor-pointer shrink-0 transition-colors hover:bg-brand/5"
            onClick={() => router.push("/home")}
          >
            &larr; Home
          </button>
          <h2 className="absolute left-1/2 -translate-x-1/2 m-0 text-xl font-bold text-brand whitespace-nowrap">
            Users
          </h2>
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search..."
            className="ml-auto px-3.5 py-2 border-[1.5px] border-solid border-gray-300 rounded-lg text-xs outline-none bg-white w-[220px] focus:border-brand"
          />
          <button
            className="px-4.5 py-2.5 bg-brand text-white border-none rounded-lg cursor-pointer text-sm font-semibold shrink-0 transition-colors hover:bg-brand-hover"
            onClick={() => router.push("/user/add")}
          >
            + Add Users
          </button>
        </div>
        <div className="bg-white rounded-xl shadow-[0_2px_10px_rgba(0,0,0,0.06)] flex-1 overflow-hidden flex flex-col">
          <div className="overflow-y-auto flex-1">
            <table className="w-full border-collapse">
              <thead>
                <tr>
                  {headers.map((h) => (
                    <th key={h} className={thStyle}>{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {data.length === 0 ? (
                  <tr>
                    <td colSpan={6} className="text-center p-10 text-gray-400 text-sm">
                      No records found.
                    </td>
                  </tr>
                ) : (
                  data.map(renderRow)
                )}
              </tbody>
            </table>
          </div>
          {totalPages > 1 && (
            <div className="flex items-center justify-center gap-1.5 p-4 border-t border-solid border-gray-200 bg-white shrink-0">
              <NavButton disabled={page === 0} onClick={() => go(page - 1)}>&lsaquo;</NavButton>
              {visible.map((p) => (
                <button
                  key={p}
                  className={`min-w-9 h-9 px-2.5 rounded-lg border-[1.5px] border-solid text-xs font-semibold flex items-center justify-center cursor-pointer transition-all ${
                    page === p
                      ? "bg-brand border-brand text-white"
                      : "bg-white border-gray-200 text-gray-700 hover:bg-gray-50"
                  }`}
                  onClick={() => go(p)}
                >
                  {p + 1}
                </button>
              ))}
              <NavButton disabled={page === totalPages - 1} onClick={() => go(page + 1)}>&rsaquo;</NavButton>
              <span className="text-xs text-gray-400 px-2 whitespace-nowrap">
                Page {page + 1} of {totalPages}
              </span>
            </div>
          )}
        </div>
      </div>
      <DeleteModal
        target={deleteTarget}
        onCancel={() => setDeleteTarget(null)}
        onConfirm={confirmDelete}
      />
    </div>
  );
}