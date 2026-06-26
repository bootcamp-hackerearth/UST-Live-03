'use client';

import { useEffect, useState } from 'react';
import api from '@/app/services/api';
import CommonDropdown from '@/components/Dropdown';
import PropTypes from 'prop-types';

const AddStock = ({
  closeModal,
  refreshData
}) => {

  const [shelves, setShelves] = useState([]);
  const [racks, setRacks] = useState([]);

  const [products, setProducts] = useState([]);
  const [warehouses, setWarehouses] = useState([]);
  const [stock, setStock] = useState({
    identifier: '',
    product: '',
    warehouse: '',
    quantity: '',
    expiryDate: '',
    shelf: [],
    rack: [],
    status: true
  });
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');

  useEffect(() => {
  fetchShelves();
  fetchRacks();
  fetchProducts();
  fetchWarehouses();
}, []);

  const fetchShelves = async () => {
    try {
      const response = await api.get('/shelf/shelfactive');
      setShelves(response.data || []);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchRacks = async () => {
    try {
      const response = await api.get('/rack/rackstatus');
      setRacks(response.data || []);
    } catch (err) {
      console.error(err);
    }
  };
  const fetchProducts = async () => {
  try {
    const response = await api.post('/product/list', {
      page: 0,
      sizePerPage: 100,
      sortDirection: 'ASC',
      sortField: 'name'
    });

    setProducts(response.data.dtoList || []);
  } catch (err) {
    console.error(err);
  }
};

const fetchWarehouses = async () => {
  try {
    const response = await api.post('/warehouse/list', {
      page: 0,
      sizePerPage: 100,
      sortDirection: 'ASC',
      sortField: 'identifier'
    });

    console.log('Warehouse Response:', response.data);

    setWarehouses(response.data.dtoList || []);
  } catch (err) {
    console.error(err);
  }
};

  const handleChange = (e) => {

    const { name, value } = e.target;

    setStock({
      ...stock,
      [name]: value
    });

  };

  const handleShelfChange = (e) => {

    const { value, checked } = e.target;

    if (checked) {
      setStock({
        ...stock,
        shelf: [...stock.shelf, value]
      });
    } else {
      setStock({
        ...stock,
        shelf: stock.shelf.filter(
          shelf => shelf !== value
        )
      });
    }
  };

  const handleRackChange = (e) => {

    const { value, checked } = e.target;

    if (checked) {
      setStock({
        ...stock,
        rack: [...stock.rack, value]
      });
    } else {
      setStock({
        ...stock,
        rack: stock.rack.filter(
          rack => rack !== value
        )
      });
    }
  };

  const validate = () => {

  if (!stock.identifier.trim()) {
    setMessageType('error');
    setMessage('Identifier is required');
    return false;
  }

  if (!stock.product.trim()) {
    setMessageType('error');
    setMessage('Product is required');
    return false;
  }

  if (!stock.warehouse.trim()) {
    setMessageType('error');
    setMessage('Warehouse is required');
    return false;
  }

  if (!stock.quantity.toString().trim()) {
    setMessageType('error');
    setMessage('Quantity is required');
    return false;
  }

  if (Number(stock.quantity) <= 0) {
    setMessageType('error');
    setMessage('Quantity must be greater than 0');
    return false;
  }

  if (!stock.expiryDate) {
    setMessageType('error');
    setMessage('Expiry Date is required');
    return false;
  }

  if (stock.shelf.length === 0) {
    setMessageType('error');
    setMessage('Please select at least one Shelf');
    return false;
  }

  if (stock.rack.length === 0) {
    setMessageType('error');
    setMessage('Please select at least one Rack');
    return false;
  }

  return true;
};

  const handleSubmit = async (e) => {

  e.preventDefault();

  setMessage('');
  setMessageType('');

  if (!validate()) {
    return;
  }

  try {

    const response = await api.post(
      '/stock/add',
      {
        ...stock,
        quantity: Number(stock.quantity)
      }
    );

    const data = response.data;

    if (data.success) {

      setMessageType('success');
      setMessage(
        data.message || 'Stock Added Successfully'
      );

      refreshData?.();

      setTimeout(() => {
        closeModal?.();
      }, 1000);

    } else {

      setMessageType('error');
      setMessage(
        data.message || 'Failed to add stock'
      );
    }

  } catch (err) {

    console.error(err);

    setMessageType('error');
    setMessage('Failed to add stock');
  }
};

  return (

    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">

      <div className="bg-white w-full max-w-4xl rounded-xl shadow-lg max-h-[90vh] flex flex-col">

        <div className="p-5 border-b">
          <h2 className="text-xl font-semibold">
            Add Stock
          </h2>

          {message && (
            <div
              className={`mt-4 rounded-lg px-4 py-3 text-sm border ${
                messageType === 'error'
                  ? 'border-red-200 bg-red-50 text-red-600'
                  : 'border-green-200 bg-green-50 text-green-600'
              }`}
            >
              {message}
            </div>
          )}
        </div>

        <form
          onSubmit={handleSubmit}
          className="p-6 overflow-y-auto space-y-5"
        >

          <div className="grid md:grid-cols-2 gap-4">

            <div>
              <label 
              htmlFor='identifier'
              className="text-sm text-gray-600">
                Identifier
              </label>

              <input
                type="text"
                name="identifier"
                value={stock.identifier}
                onChange={handleChange}
                className="w-full border p-2.5 rounded-lg"
                required
              />
            </div>

            <CommonDropdown
                label="Product"
                name="product"
                value={stock.product}
                options={products}
                onChange={handleChange}
                required
                placeholder="Select Product"
                />

            <div>
              <label 
              htmlFor='warehouse'
              className="block mb-2 text-sm font-semibold text-slate-700">
                Warehouse
              </label>

              <select
                name="warehouse"
                value={stock.warehouse}
                onChange={handleChange}
                required
                className="
                  w-full
                  p-3
                  border
                  border-slate-300
                  rounded-lg
                  focus:outline-none
                  focus:ring-2
                  focus:ring-slate-400
                "
              >
                <option value="">
                  Select Warehouse
                </option>

                {warehouses.map((warehouse, index) => (
                  <option
                    key={warehouse.id || index}
                    value={warehouse.identifier}
                  >
                    {warehouse.identifier}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label 
              htmlFor='quantity'
              className="text-sm text-gray-600">
                Quantity
              </label>

              <input
                type="number"
                name="quantity"
                value={stock.quantity}
                onChange={handleChange}
                className="w-full border p-2.5 rounded-lg"
                required
              />
            </div>

            <div>
              <label 
              htmlFor='expiryDate'
              className="text-sm text-gray-600">
                Expiry Date
              </label>

              <input
                type="datetime-local"
                name="expiryDate"
                value={stock.expiryDate}
                onChange={handleChange}
                className="w-full border p-2.5 rounded-lg"
              />
            </div>

          </div>

          <div className="border rounded-xl p-4 bg-slate-50">

            <h3 className="font-semibold mb-3">
              Select Shelves
            </h3>

            <div className="grid grid-cols-2 gap-2 max-h-32 overflow-y-auto">

              {shelves.map((shelf) => (

                <label
                  key={shelf.identifier}
                  className="flex items-center gap-2 bg-white border rounded-lg px-3 py-2"
                >

                  <input
                    type="checkbox"
                    value={shelf.identifier}
                    checked={stock.shelf.includes(
                      shelf.identifier
                    )}
                    onChange={handleShelfChange}
                  />

                  {shelf.name}

                </label>

              ))}

            </div>

          </div>

          <div className="border rounded-xl p-4 bg-slate-50">

            <h3 className="font-semibold mb-3">
              Select Racks
            </h3>

            <div className="grid grid-cols-2 gap-2 max-h-32 overflow-y-auto">

              {racks.map((rack) => (

                <label
                  key={rack.identifier}
                  className="flex items-center gap-2 bg-white border rounded-lg px-3 py-2"
                >

                  <input
                    type="checkbox"
                    value={rack.identifier}
                    checked={stock.rack.includes(
                      rack.identifier
                    )}
                    onChange={handleRackChange}
                  />

                  {rack.name}

                </label>

              ))}

            </div>

          </div>

        </form>

        <div className="p-4 border-t flex justify-end gap-3">

          <button
            type="button"
            onClick={closeModal}
            className="px-5 py-2 rounded-lg border hover:bg-gray-100"
          >
            Cancel
          </button>

          <button
            onClick={handleSubmit}
            className="px-5 py-2 rounded-lg bg-slate-800 text-white hover:bg-slate-700"
          >
            Save Stock
          </button>

        </div>

      </div>

    </div>

  );

};

AddStock.propTypes = {
  closeModal: PropTypes.func.isRequired,
  refreshData: PropTypes.func.isRequired
};

export default AddStock;