'use client';

import PropTypes from 'prop-types';
 
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
 
          <label 
            htmlFor="identifier"
            className="block mb-2 text-sm font-semibold text-slate-700">
            Identifier
          </label>
 
          <input
            id="identifier"
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
 
          <label 
            htmlFor="name"
            className="block mb-2 text-sm font-semibold text-slate-700">
            Name
          </label>
 
          <input
            id="name"
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
 
CommonUpdate.propTypes = {
  data: PropTypes.shape({
    identifier: PropTypes.string,
    name: PropTypes.string
  }).isRequired,
  handleChange: PropTypes.func.isRequired,
  showIdentifier: PropTypes.bool,
  showName: PropTypes.bool,
  identifierReadOnly: PropTypes.bool
};

export default CommonUpdate;