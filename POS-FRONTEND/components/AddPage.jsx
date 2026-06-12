'use client';

import PropTypes from "prop-types";

const CommonAdd = ({
  data,
  handleChange,
  showIdentifier = true,
  showName = true
}) => {
  return (
    <div className="grid md:grid-cols-2 gap-5">

      {showIdentifier && (
        <div>
          <label 
          htmlFor="identifier"
          className="block mb-1 text-sm font-medium text-slate-600">
            Identifier
          </label>

          <input
            type="text"
            name="identifier"
            value={data.identifier}
            onChange={handleChange}
            placeholder="Enter identifier"
            className="w-full px-4 py-2.5 border border-slate-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition"
          />
        </div>
      )}

      {showName && (
        <div>
          <label 
          htmlFor="name"
          className="block mb-1 text-sm font-medium text-slate-600">
            Name
          </label>

          <input
            type="text"
            name="name"
            value={data.name}
            onChange={handleChange}
            placeholder="Enter name"
            className="w-full px-4 py-2.5 border border-slate-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition"
          />
        </div>
      )}

    </div>
  );
};

CommonAdd.propTypes = {
  data: PropTypes.shape({
    identifier: PropTypes.string,
    name: PropTypes.string,
  }).isRequired,
  handleChange: PropTypes.func.isRequired,
  showIdentifier: PropTypes.bool,
  showName: PropTypes.bool,
};


export default CommonAdd;