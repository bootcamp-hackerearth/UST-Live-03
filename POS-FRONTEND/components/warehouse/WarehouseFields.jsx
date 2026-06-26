"use client";
import PropTypes from "prop-types";

export default function WarehouseFields({
  country,
  setCountry,
  region,
  setRegion,
  address,
  setAddress,
  phoneNumber,
  setPhoneNumber,
}) {
  return (
    <>
      <div>
        <label htmlFor="country" className="block mb-2 text-sm font-medium text-[#344054]">
          Country
        </label>
        <input
          id="country"
          type="text"
          value={country}
          onChange={(e) => setCountry(e.target.value)}
          required
          className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
        />
      </div>
      <div>
        <label htmlFor="region" className="block mb-2 text-sm font-medium text-[#344054]">
          Region
        </label>
        <input
          id="region"
          type="text"
          value={region}
          onChange={(e) => setRegion(e.target.value)}
          required
          className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
        />
      </div>
      <div>
        <label htmlFor="address" className="block mb-2 text-sm font-medium text-[#344054]">
          Address
        </label>
        <input
          id="address"
          type="text"
          value={address}
          onChange={(e) => setAddress(e.target.value)}
          required
          className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
        />
      </div>
      <div>
        <label htmlFor="phoneNumber" className="block mb-2 text-sm font-medium text-[#344054]">
          Phone Number
        </label>
        <input
          id="phoneNumber"
          type="text"
          value={phoneNumber}
          onChange={(e) => setPhoneNumber(e.target.value.replaceAll(/\D/g,"").slice(0,10))}
          required
          maxLength={10}
          className="w-full h-14 px-5 rounded-2xl border border-[#d0d5dd] bg-white text-[#101828] outline-none transition-all focus:border-[#2563eb] focus:ring-4 focus:ring-blue-100"
        />
        <p className="mt-2 text-sm text-[#667085]">Must be exactly 10 digits if provided</p>
      </div>
    </>
  );
}

WarehouseFields.propTypes = {
  country: PropTypes.string,
  setCountry: PropTypes.func,
  region: PropTypes.string,
  setRegion: PropTypes.func,
  address: PropTypes.string,
  setAddress: PropTypes.func,
  phoneNumber: PropTypes.string,
  setPhoneNumber: PropTypes.func,
};