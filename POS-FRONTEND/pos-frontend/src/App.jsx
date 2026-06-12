import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import "./App.css";
import Register from "./Register.jsx";
import Login from "./login.jsx";
import Profile from "./Profile.jsx";
import Home from "./Home.jsx";
import ProductList from "./Pages/Product/ProductList.jsx";
import AddProduct from "./AddProduct.jsx";
import UserList from "./Pages/User/UserList.jsx";
import NodeList from "./Pages/Node/NodeList.jsx";
import WarehouseList from "./Pages/Warehouse/Warehouse.jsx";
import CategoryList from "./Pages/Category/CategoryList.jsx";
import React from "react";
import RoleList from "./Pages/Role/RoleList.jsx";
import CustomerList from "./Pages/Customer/CustomerList.jsx";
import ShelfList from "./Pages/Shelf/ShelfList.jsx";
import UnitList from "./Pages/Unit/UnitList.jsx";
import BrandList from "./Pages/Brand/BrandList.jsx";
import RackList from "./Pages/Rack/RackList.jsx";
import PriceList from "./Pages/Price/PriceList.jsx";
import ModelList from "./Pages/Model/ModelList.jsx";
import StockList from "./Pages/Stock/StockList.jsx";
import ProductAdd from "./Pages/Product/ProductAdd.jsx";
import WarehouseAdd from "./Pages/Warehouse/WarehouseAdd.jsx";
import CustomerAdd from "./Pages/Customer/CustomerAdd.jsx";
import PriceAdd from "./Pages/Price/PriceAdd.jsx";
import StockAdd from "./Pages/Stock/StockAdd.jsx";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" />} />  {/* ✅ redirect root to login */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/profile" element={<Profile />}/>
        <Route path="/home" element={<Home />} />  {/* Add Home route */}
        <Route path="/products" element={<ProductList />} />  {/* Add ProductList route */}
        <Route path="/add-product" element={<ProductAdd />} />  {/* Add ProductAdd route */}
        <Route path="/users" element={<UserList />} />  {/* Add UserList route */}
        <Route path="/nodes" element={<NodeList />} />  {/* Add NodeList route */}
        <Route path="/warehouses" element={<WarehouseList />} />  {/* Add WarehouseList route */}
        <Route path="/warehouse/add" element={<WarehouseAdd />} />  {/* Add WarehouseAdd route */}
        <Route path="/categorys" element={<CategoryList />} />  {/* Add CategoryList route */}
        <Route path="/roles" element={<RoleList />} />  {/* Add RoleList route */}
        <Route path="/customers" element={<CustomerList />} />  {/* Add CustomerList route */}
        <Route path="/customer/add" element={<CustomerAdd />} />  {/* Add CustomerAdd route */}
        <Route path="/units" element={<UnitList />} />  {/* Add UnitList route */}
        <Route path="/brands" element={<BrandList />} />  {/* Add BrandList route */}
        <Route path="/shelfs" element={<ShelfList />} />  {/* Add ShelfList route */}
        <Route path="/racks" element={<RackList />} />  {/* Add RackList route */}
        <Route path="/prices" element={<PriceList />} />  {/* Add PriceList route */}
        <Route path="/price/add" element={<PriceAdd />} />  {/* Add PriceAdd route */}
        <Route path="/models" element={<ModelList />} />  {/* Add ModelList route */}
        <Route path="/stocks" element={<StockList />} />  {/* Add StockList route */}
        <Route path="/stock/add" element={<StockAdd />} />  {/* Add StockAdd route */}
      </Routes>
    </BrowserRouter>
  );
}
 
export default App;
 