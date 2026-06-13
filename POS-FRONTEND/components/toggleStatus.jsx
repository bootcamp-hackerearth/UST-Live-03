'use client';
 
const ToggleSwitch = ({
  checked,
  onChange
}) => {
 
  return (
 
    <label className="relative inline-flex items-center cursor-pointer">
 
      <input
        type="checkbox"
        className="sr-only peer"
        checked={checked}
        onChange={onChange}
      />
 
      <div
        className="
          w-11
          h-6
          bg-gray-300
          rounded-full
          transition-colors
          peer-checked:bg-green-500
        "
      />
 
      <div
        className="
          absolute
          left-1
          top-1
          w-4
          h-4
          bg-white
          rounded-full
          transition-transform
          peer-checked:translate-x-5
        "
      />
 
    </label>
 
  );
};
 
export default ToggleSwitch;