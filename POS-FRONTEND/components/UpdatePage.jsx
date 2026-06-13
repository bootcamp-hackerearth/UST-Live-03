'use client';
 
const CommonUpdate = ({
  data,
  handleChange,
  showIdentifier = true,
  showName = true,
  identifierReadOnly = true
}) => {
 
  return (
    <>
 
      {showIdentifier && (
 
        <div>
 
          <label className="block mb-2 text-sm font-semibold text-slate-700">
            Identifier
          </label>
 
          <input
            type="text"
            name="identifier"
            value={data.identifier}
            onChange={handleChange}
            readOnly={identifierReadOnly}
            placeholder="Identifier"
            className={`w-full px-4 py-2.5 border border-slate-300 rounded-xl transition ${identifierReadOnly
                ? 'bg-slate-100 text-slate-500 cursor-not-allowed'
                : 'bg-slate-50 focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500 outline-none'
              }`}
          />
 
        </div>
 
      )}
 
      {showName && (
 
        <div>
 
          <label className="block mb-2 text-sm font-semibold text-slate-700">
            Name
          </label>
 
          <input
            type="text"
            name="name"
            value={data.name}
            onChange={handleChange}
            required
            placeholder="Enter name"
            className="w-full 
                       px-4 
                       py-2.5 
                       border 
                       border-slate-300 
                       rounded-xl 
                       focus:ring-2 
                       focus:ring-cyan-500 
                       focus:border-cyan-500 
                       outline-none 
                       trandition
                      "
          />
 
        </div>
 
      )}
 
    </>
  );
};
 
export default CommonUpdate;