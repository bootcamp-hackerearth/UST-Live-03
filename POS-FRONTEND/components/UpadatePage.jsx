'use client';
import PropTypes from "prop-types";

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
            id="name"  
            type="text"
            name="identifier"
            value={data.identifier}
            onChange={handleChange}
            readOnly={identifierReadOnly}
            placeholder="Identifier"
            className={`w-full p-3 border border-slate-300 rounded-lg ${
              identifierReadOnly
                ? 'bg-slate-100 text-slate-500 cursor-not-allowed'
                : ''
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
            type="text"
            name="name"
            value={data.name}
            onChange={handleChange}
            required
            placeholder="Enter name"
            className="w-full p-3 border border-slate-300 rounded-lg"
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