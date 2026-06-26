"use client";
import {useEffect,useState} from "react";
import PropTypes from "prop-types";
import {fetchActiveProducts} from "@/services/api";
import ProductCard from "./ProductCard";

export default function ProductGrid({onSelect}){
  const [products,setProducts]=useState([]);
  const [loading,setLoading]=useState(true);
  const [search,setSearch]=useState("");

  useEffect(()=>{loadProducts();},[]);

  const loadProducts=async()=>{
    try{
      const response=await fetchActiveProducts();
      setProducts(response||[]);
    }catch(error){
      console.log(error);
    }finally{
      setLoading(false);
    }
  };

  const filtered=products.filter(p=>
    p.identifier?.toLowerCase().includes(search.toLowerCase())||
    p.brand?.toLowerCase().includes(search.toLowerCase())||
    p.model?.toLowerCase().includes(search.toLowerCase())
  );

  if(loading){
    return(
      <div className="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-3">
        {[
          "s1","s2","s3","s4","s5","s6","s7","s8"
        ].map((k)=>(
          <div key={k} className="h-36 rounded-2xl bg-[#e3e8ef] animate-pulse"/>
        ))}
      </div>
    );
  }

  return(
    <div>

      <div className="mb-4">
        <div className="relative">
          <span className="absolute left-3 top-1/2 -translate-y-1/2 text-[#6b7a99] text-sm">🔍</span>
          <input
            type="text"
            placeholder="Search products..."
            value={search}
            onChange={e=>setSearch(e.target.value)}
            className="w-full pl-9 pr-4 py-2.5 rounded-xl border border-[#e3e8ef] bg-white text-sm text-[#101828] placeholder:text-[#a0aab8] focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
      </div>
      {filtered.length===0?(
        <div className="flex flex-col items-center justify-center py-20 text-[#a0aab8]">
          <span className="text-4xl mb-3">📦</span>
          <p className="text-sm">No products found</p>
        </div>
      ):(
        <div className="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-3">
          {filtered.map((product)=>(
            <ProductCard key={product.id || product.identifier} product={product} onClick={onSelect}/>
          ))}
        </div>
      )}
    </div>
  );
}

ProductGrid.propTypes = {
  onSelect: PropTypes.func,
};