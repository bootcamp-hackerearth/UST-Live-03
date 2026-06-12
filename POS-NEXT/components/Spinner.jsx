import { Loader2 } from 'lucide-react';
import PropTypes from 'prop-types';

const Spinner = ({ size = 8 }) => (

  <div className="min-h-screen flex items-center justify-center bg-[#F4F5FB]">
    <Loader2
      size={size}
      className=" text-gray-700"/>
  </div>

);

Spinner.propTypes = {
  size: PropTypes.number,
};

export default Spinner;
