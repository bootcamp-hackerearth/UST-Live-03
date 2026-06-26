'use client';

import PropTypes from 'prop-types';

const CommonAdd = ({
  data,
  handleChange,
  showIdentifier = true,
  showName = true,
  errors
}) => {

  return (
    <div className="grid md:grid-cols-2 gap-5">
 
      {showIdentifier && (
        <div>
          <label 
            htmlFor="identifier"
            className="block mb-1 text-sm font-medium text-slate-700">
            Identifier
          </label>
 
          <input
            id="identifier"
            type="text"
            name="identifier"
            value={data.identifier}
            onChange={handleChange}
            placeholder="Enter identifier"
            className="w-full px-4 py-2.5 bg-slate-50 border border-slate-300 rounded-xl focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500 outline-none transition"
          />
          {errors?.identifier && (
            <p className="text-red-500 text-sm mt-1">
              {errors.identifier}
            </p>
          )}  
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
            id="name"
            type="text"
            name="name"
            value={data.name}
            onChange={handleChange}
            placeholder="Enter name"
            className="w-full px-4 py-2.5 bg-slate-50 border border-slate-300 rounded-xl focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500 outline-none transition"
          />

          {errors?.name && (
            <p className="text-red-500 text-sm mt-1">
              {errors.name}
            </p>
          )}
        </div>
      )}
    </div>
  );
};

CommonAdd.propTypes = {
  data: PropTypes.shape({
    identifier: PropTypes.string,
    name: PropTypes.string
  }).isRequired,

  handleChange: PropTypes.func.isRequired,
  showIdentifier: PropTypes.bool,
  showName: PropTypes.bool,
  errors: PropTypes.shape({
    identifier: PropTypes.string,
    name: PropTypes.string
  })
};

export default CommonAdd;