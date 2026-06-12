import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./AddProduct.css";

function AddProduct() {

    const navigate = useNavigate();

    const [product, setProduct] = useState({

        identifier: "",
        name: "",
        categories: [],
        brand: "",
        model: "",
        unit: "",
        status: true

    });

    const [categories, setCategories] = useState([]);
    const [brands, setBrands] = useState([]);
    const [models, setModels] = useState([]);
    const [units, setUnits] = useState([]);

    const [message, setMessage] = useState("");

    // FETCH DROPDOWN DATA

    useEffect(() => {

        fetchData();

    }, []);

    const fetchData = async () => {

        try {

            const token =
                localStorage.getItem("token");

            // CATEGORY

            const categoryRes = await axios.post(

                "http://localhost:8080/api/category/list",

                {
                    page: 0,
                    sizePerPage: 100,
                    sortDirection: "ASC",
                    sortField: "id"
                },

                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }

            );

            setCategories(categoryRes.data);

            // BRAND

            const brandRes = await axios.post(

                "http://localhost:8080/api/brand/list",

                {
                    page: 0,
                    sizePerPage: 100,
                    sortDirection: "ASC",
                    sortField: "id"
                },

                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }

            );

            setBrands(brandRes.data);

            // MODEL

            const modelRes = await axios.post(

                "http://localhost:8080/api/models/list",

                {
                    page: 0,
                    sizePerPage: 100,
                    sortDirection: "ASC",
                    sortField: "id"
                },

                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }

            );

            setModels(modelRes.data);

            // UNIT

            const unitRes = await axios.post(

                "http://localhost:8080/api/unit/list",

                {
                    page: 0,
                    sizePerPage: 100,
                    sortDirection: "ASC",
                    sortField: "id"
                },

                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }

            );

            setUnits(unitRes.data);

        }

        catch (error) {

            console.error(
                "Error loading data",
                error
            );

        }
    };

    // HANDLE INPUT

    const handleChange = (e) => {

        const { name, value } = e.target;

        setProduct({

            ...product,

            [name]: value

        });
    };

    // HANDLE MULTI SELECT

    const handleCategoryChange = (e) => {

        const values =
            Array.from(
                e.target.selectedOptions,
                option => option.value
            );

        setProduct({

            ...product,

            categories: values

        });
    };

    // SUBMIT

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            const token =
                localStorage.getItem("token");

            const response = await axios.post(

                "http://localhost:8080/api/product/add",

                product,

                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }

            );

            if (response.data.success === false) {

                setMessage(response.data.message);

                return;

            }

            alert("Product added successfully");

            navigate("/products");

        }

        catch (error) {

            console.error(
                "Add product failed",
                error
            );

            setMessage(
                "Failed to add product"
            );

        }
    };

    return (

        <div className="add-product-page">

            {/* TOP ACTIONS */}

            <div className="top-actions">

                <button
                    className="back-btn"
                    onClick={() => navigate("/products")}
                >

                    ← Back

                </button>

            </div>

            {/* CARD */}

            <div className="add-product-card">

                {/* HEADER */}

                <div className="card-header">

                    <h2>
                        Add Product
                    </h2>

                    <p>
                        Create and manage products in your POS system
                    </p>

                </div>

                {/* ERROR */}

                {

                    message && (

                        <div className="error-msg">

                            {message}

                        </div>

                    )

                }

                {/* FORM */}

                <form onSubmit={handleSubmit}>

                    {/* IDENTIFIER */}

                    <div className="form-group">

                        <label>
                            Identifier
                        </label>

                        <input
                            type="text"
                            name="identifier"
                            value={product.identifier}
                            onChange={handleChange}
                            placeholder="Enter identifier"
                            required
                        />

                    </div>

                    {/* NAME */}

                    <div className="form-group">

                        <label>
                            Product Name
                        </label>

                        <input
                            type="text"
                            name="name"
                            value={product.name}
                            onChange={handleChange}
                            placeholder="Enter product name"
                            required
                        />

                    </div>

                    {/* BRAND */}

                    <div className="form-group">

                        <label>
                            Brand
                        </label>

                        <select
                            name="brand"
                            value={product.brand}
                            onChange={handleChange}
                        >

                            <option value="">
                                -- Select Brand --
                            </option>
                            {
                                brands.map((brand) => (
                                    <option
                                        key={brand.id}
                                        value={brand.identifier}
                                    >
                                        {brand.identifier}
                                    </option>
                                ))
                            }
                        </select>
                    </div>
                    {/* MODEL */}
                    <div className="form-group">
                        <label>
                            Model
                        </label>
                        <select
                            name="model"
                            value={product.model}
                            onChange={handleChange}
                        >
                            <option value="">
                                -- Select Model --
                            </option>
                            {
                                models.map((model) => (
                                    <option
                                        key={model.id}
                                        value={model.identifier}
                                    >
                                        {model.identifier}
                                    </option>
                                ))
                            }
                        </select>
                    </div>
                    {/* UNIT */}

                    <div className="form-group">

                        <label>
                            Unit
                        </label>
                        <select
                            name="unit"
                            value={product.unit}
                            onChange={handleChange}
                        >
                            <option value="">
                                -- Select Unit --
                            </option>
                            {
                                units.map((unit) => (
                                    <option
                                        key={unit.id}
                                        value={unit.identifier}
                                    >
                                        {unit.identifier}
                                    </option>
                                ))
                            }
                        </select>
                    </div>
                    {/* STATUS */}
                    <div className="form-group">
                        <label>
                            Status
                        </label>
                        <select
                            name="status"
                            value={product.status}
                            onChange={handleChange}
                        >
                            <option value={true}>
                                Active
                            </option>
                            <option value={false}>
                                Inactive
                            </option>
                        </select>
                    </div>
                    {/* CATEGORY */}
                    <div className="form-group full-width">
                        <label>
                            Categories
                        </label>
                        <select
                            multiple
                            value={product.categories}
                            onChange={handleCategoryChange}
                        >
                            {
                                categories.map((cat) => (
                                    <option
                                        key={cat.id}
                                        value={cat.identifier}
                                    >
                                        {cat.identifier}
                                    </option>
                                ))
                            }
                        </select>
                        <small>
                            Hold Ctrl to select multiple categories
                        </small>
                    </div>
                    {/* SUBMIT */}
                    <button
                        type="submit"
                        className="submit-btn full-width"
                    >
                        Save Product
                    </button>
                </form>
            </div>
        </div>
    );
}
export default AddProduct;