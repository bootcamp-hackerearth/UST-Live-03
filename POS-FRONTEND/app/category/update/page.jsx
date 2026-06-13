'use client';
 
import { useEffect, useState } from 'react';
 
import api from '@/app/services/api';
import CommonUpdate from '@/components/UpdatePage';
import Dropdown from '@/components/Dropdown';
 
const UpdateCategory = ({
  data,
  closeModal,
  refreshData
}) => {
 
  const [message, setMessage] = useState('');
 
  const [category, setCategory] = useState({
    identifier: '',
    name: '',
    superCategory: ''
  });
 
  const [categories, setCategories] = useState([]);
 
  useEffect(() => {
    fetchCategories();
  }, []);
 
  useEffect(() => {
 
    if (data) {

      setCategory({
        identifier: data.identifier || '',
        name: data.name || '',
        superCategory: data.superCategory || ''
      });
      
 
    }
 
  }, [data]);
 
  const fetchCategories = async () => {
 
    try {
 
      const response = await api.post(
        '/category/list',
        {
          page: 0,
          sizePerPage: 100
        }
      );
 
      const categoryList =
        response.data.dtoList ||
        response.data ||
        [];
 
      setCategories(
        categoryList.filter(
          cat => cat.identifier !== data?.identifier
        )
      );
 
    } catch (error) {
 
      console.error(error);
 
    }
 
  };
 
  const handleChange = (e) => {
 
    const { name, value } = e.target;
 
    setCategory(prev => ({
      ...prev,
      [name]: value
    }));
 
  };
 
  const handleSubmit = async (e) => {
 
    e.preventDefault();

    console.log("Sending Category:", category);
 
    try {
 
      await api.post(
        '/category/update',
        category,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
          }
        }
      );
 
      setMessage('Category updated successfully');
 
      refreshData?.();
 
      setTimeout(() => {
        closeModal?.();
      }, 500);
 
    } catch (error) {
 
      console.error(error);
 
      setMessage('Failed to update category');
 
    }
 
  };
 
  return (
  <div className="flex flex-col bg-white rounded-2xl overflow-hidden max-h-[85vh] border-t-4 border-cyan-500 shadow-lg">
 
    <div className="px-8 pt-6 pb-5 bg-slate-50 border-b border-slate-200 flex-shrink-0">
      <h2 className="text-2xl font-bold text-slate-800">
        Update Category
      </h2>
 
      <p className="text-sm text-slate-500 mt-1">
        Modify category details and save changes.
      </p>
    </div>
 
    <form
      id="update-category-form"
      onSubmit={handleSubmit}
      className="flex-1 overflow-y-auto px-8 py-6 space-y-8"
    >
 
      {message && (
        <div
          className={`
            rounded-xl px-4 py-3 text-sm
            ${
              message.toLowerCase().includes('failed')
                ? 'bg-red-50 border border-red-200 text-red-600'
                : 'bg-cyan-50 border border-cyan-200 text-cyan-600'
            }
          `}
        >
          {message}
        </div>
      )}
 
      <CommonUpdate
        data={category}
        handleChange={handleChange}
        showIdentifier={true}
        showName={true}
        identifierReadOnly={true}
      />
 
      <Dropdown
        label="Super Category"
        name="superCategory"
        value={category.superCategory}
        options={categories}
        onChange={handleChange}
        placeholder="Select Super Category"
      />
 
    </form>
 
    <div className="px-8 py-5 border-t border-slate-200 flex justify-end gap-3 flex-shrink-0">
 
      <button
        type="button"
        onClick={closeModal}
        className="
          px-6 py-2.5
          rounded-xl
          border border-slate-300
          text-slate-700
          font-medium
          hover:bg-slate-100
          transition
        "
      >
        Cancel
      </button>
 
      <button
        type="submit"
        form="update-category-form"
        className="
          px-6 py-2.5
          rounded-xl
          bg-cyan-500
          text-white
          hover:bg-cyan-600
          transition
          shadow-md
        "
      >
        Update Category
      </button>
 
    </div>
 
  </div>
);
};
 
export default UpdateCategory;