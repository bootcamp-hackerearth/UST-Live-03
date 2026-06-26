'use client';

import PropTypes from 'prop-types';

const ToggleSwitch = ({
  checked,
  onChange
}) => {

  return (
    <button
      type="button"
      onClick={onChange}
      className={`
        relative
        w-11
        h-6
        rounded-full
        transition-all
        ${checked
          ? 'bg-green-500'
          : 'bg-gray-300'
        }
      `}
    >
      <span
        className={`
          absolute
          top-1
          h-4
          w-4
          rounded-full
          bg-white
          transition-all
          ${checked
            ? 'left-6'
            : 'left-1'
          }
        `}
      />
    </button>
  );
};

ToggleSwitch.propTypes = {
  checked: PropTypes.bool,
  onChange: PropTypes.func
};

export default ToggleSwitch;