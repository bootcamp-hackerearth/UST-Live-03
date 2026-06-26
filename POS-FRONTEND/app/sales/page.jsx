"use client"
import React, { useEffect, useState } from 'react'
import axios from "@/components/axiosConfig"
import { Trash2 } from "lucide-react"
import Modal from '@/components/Modal';
import CustomerAddModal from '@/components/CustomerAddModal';
import PaymentModal from '@/components/PaymentModal';
import BillModal from '@/components/BillModal';
import dynamic from "next/dynamic";
const Select = dynamic(() => import("react-select"), {
    ssr: false,
});


const SalesPage = () => {
    const [customerList, setCustomerList] = useState([])
    const [currentCustomer, setCurrentCustomer] = useState()
    const [cartEntryList, setCartEntryList] = useState([])
    const [newProductId, setNewProductId] = useState("")
    const [quantities, setQuantities] = useState({})
    const [cartInfo, setCartInfo] = useState({
        totalOriginalPrice: 0,
        totalPrice: 0,
        discount: 0,
        coupon: 0
    })
    const [isOpen, setIsOpen] = useState(false);
    const [isPaymentOpen, setIsPaymentOpen] = useState(false);
    const [isBillOpen, setIsBillOpen] = useState(false);
    const [isCharging, setIsCharging] = useState(false);
    const [currentOrderId, setCurrentOrderId] = useState(null);
    const [productList, setProductList] = useState([])
    const [filter, setFilter] = useState("")
    const pagination = { page: 0, sizePerPage: 100 }

    useEffect(() => {
        getCustomerList()
        getProductListInitial()
    }, [])

    async function getCustomerList() {
        const res = await axios.post("/customer/list", pagination)
        setCustomerList(res.data.dtoList)
    }

    async function getProductListInitial() {
        const res = await axios.post("/product/list", pagination)
        console.log(res.data.dtoList)
        setProductList(res.data.dtoList)
    }

    async function getProductList(keyword) {
        const res = await axios.post(`/product/searchByIdentifierOrName?keyword=${keyword}`, {
            page: 0,
            sizePerPage: 2
        })
        console.log(res.data.dtoList)
        setProductList(res.data.dtoList)
    }

    useEffect(() => {
        if (!currentCustomer) return
        const init = async () => {
            await getCustomerList()
            fetchCart()
            fetchCartInfo()
        }
        init()
    }, [currentCustomer])

    const fetchCart = async () => {
        const res = await axios.get(`/cartEntry?cartId=${currentCustomer}`)
        setCartEntryList(res.data)

        const qMap = {}
        res.data.forEach((entry) => {
            qMap[entry.identifier] = entry.quantity
        })
        setQuantities(qMap)
    }

    const fetchCartInfo = async () => {
        const res = await axios.get(`/cart/get?identifier=${currentCustomer}`)
        setCartInfo(res.data)
    }

    const selectCustomer = (selectedOption) => {
        setCurrentCustomer(selectedOption.value)
    }

    const changeQuantity = async (e, cartEntry) => {
        const newQty = e.target.value === "" ? 1 : e.target.value
        setQuantities((prev) => ({ ...prev, [cartEntry.identifier]: newQty }))
        await axios.post("/cartEntry/updateQuantity", {
            product: cartEntry.product,
            cartId: cartEntry.cartId,
            quantity: newQty,
        })
        fetchCart()
        fetchCartInfo()
    }

    const deleteCartEntry = async (e, cartEntry) => {
        await axios.delete(`/cartEntry/delete?cartId=${cartEntry.cartId}&product=${cartEntry.product}`)
        fetchCart()
        fetchCartInfo()
    }

    const addProduct = async (productId = newProductId) => {
        if (!productId.trim() || !currentCustomer) return
        try{
            const res = await axios.post("/cartEntry/add", {
            product: productId,
            cartId: currentCustomer,
            quantity: 1,
        })
            console.log(res)
            if (!res.data.success) {
                alert(res.data.message)
                return
            }
        }catch(error){
            alert(error.response.data.message)
        }
        
        setNewProductId("")
        fetchCart()
        fetchCartInfo()
    }

    const clearCart = async (e, cartId) => {
        await axios.get(`/cartEntry/clearCart?cartId=${cartId}`)
        fetchCart()
        fetchCartInfo()
    }

    const handleChargeConfirm = async (paymentType) => {
        if (!paymentType || !currentCustomer) return
        setIsCharging(true)
        try {
            const orderRes = await axios.post("/orderEntry/add", {
                paymentType,
                customerId: currentCustomer,
            })

            setCurrentOrderId(orderRes.data.identifier)

            await axios.get(`/cartEntry/clearCart?cartId=${currentCustomer}`)
            fetchCart()
            fetchCartInfo()

            setIsPaymentOpen(false)
            setIsBillOpen(true)
        } catch (err) {
            console.error("Order placement failed", err)
            alert("Failed to place order. Please try again.")
        } finally {
            setIsCharging(false)
        }
    }

    const handleBillClose = () => {
        setIsBillOpen(false)
        setCurrentOrderId(null)
    }

    const options = [
        { value: "", label: "Select the customer" },
        ...customerList.map(customer => ({
            value: customer.identifier,
            label: customer.name + ` (${customer.identifier})`
        }))
    ];

    const selectedOption = options.find(o => o.value === currentCustomer) || null;

    const productOptions = [
        { value: "", lable: "Select the product" },
        ...productList.map(product => ({
            value: product.identifier,
            label: product.name + ` (${product.identifier})`
        }))
    ]

    const handleInputChangeProduct = (inputValue) => {
        if (inputValue) {
            getProductList(inputValue);
        }
    };

    const productSelect = (selectedOption) => {
        addProduct(selectedOption.value)
    }

    const getProductName = (productId) => {
        const found = productList.find(p => p.identifier === productId)
        return found ? found.name : productId
    }

    const cartHasItems = cartEntryList.length > 0

    return (
        <div className="flex h-screen border border-zinc-200 rounded-xl overflow-hidden">

            <div className="flex flex-col flex-1 bg-white">

                <div className="flex items-center justify-between px-4 py-3 border-b border-zinc-100">
                    <div className="flex items-center gap-2 bg-zinc-50 border border-zinc-200 rounded-xl px-3 py-1.5 cursor-pointer min-w-50">
                        <div className="w-7 h-7 rounded-full bg-blue-50 text-blue-800 text-xs font-medium flex items-center justify-center">
                            {currentCustomer ? currentCustomer.slice(0, 2).toUpperCase() : '?'}
                        </div>
                        <Select options={options}
                            onChange={selectCustomer}
                            value={selectedOption}
                            isSearchable placeholder="Select customer…"
                            className="text-xs flex-1"
                        />
                        <button
                            onClick={() => setIsOpen(true)}
                            className="w-8 h-8 rounded-lg border border-zinc-200 bg-zinc-50 flex items-center justify-center text-zinc-500 hover:bg-zinc-100 cursor-pointer">
                            +
                        </button>
                    </div>
                    <div className="flex">
                        <button
                            onClick={(e) => clearCart(e, currentCustomer)}
                            className="text-[10px] w-20 h-8 rounded-lg border border-zinc-200 bg-zinc-50 flex justify-center text-zinc-500 hover:bg-red-50 hover:text-red-700 hover:border-red-200 cursor-pointer gap-1 items-center">
                            <Trash2 className="size-4 shrink-0" /> Clear Cart
                        </button>
                    </div>
                </div>

                <div className="flex items-center gap-2 px-4 py-2 bg-zinc-50 border-b border-zinc-100">
                    <span className="text-zinc-400 text-sm">⌕</span>
                    <input className="flex-1 text-xs bg-transparent border-none outline-none placeholder-zinc-400"
                        placeholder="Filter cart items…"
                        value={filter} onChange={e => setFilter(e.target.value)}
                    />
                </div>

                <div className="flex-1 overflow-y-auto p-3 flex flex-col gap-2">

                    {cartEntryList.length === 0 ? (
                        <div className="flex flex-col items-center justify-center p-8 border border-dashed border-zinc-200 rounded-xl bg-zinc-50/50 text-center">
                            <span className="text-lg mb-1"></span>
                            <div className="text-xs font-medium text-zinc-500">Cart is empty</div>
                            <p className="text-[10px] text-zinc-400 mt-0.5">Select a customer and add products to start</p>
                        </div>
                    ) : (
                        ""
                    )}

                    {cartEntryList.filter(e => e.product.toLowerCase().includes(filter.toLowerCase()))
                        .map((entry, i) => (
                            <div key={entry.identifier} className="grid border border-zinc-100 rounded-lg overflow-hidden hover:border-zinc-200 transition-colors" style={{ gridTemplateColumns: '32px 1fr auto' }}>

                                <div className="bg-zinc-50 flex items-center justify-center text-[11px] text-zinc-400 border-r border-zinc-100 font-mono">{i + 1}</div>

                                <div className="p-2.5 flex flex-col gap-1.5">
                                    <div className="flex items-center gap-2">
                                        <span className="text-[13px] font-medium">{getProductName(entry.product)}</span>
                                        <span className="text-[11px] font-mono text-zinc-400">{entry.product}</span>
                                    </div>

                                    <div className="flex gap-3">

                                        {[['Unit', `$${entry.unitPrice}`], ['Original', `$${entry.totalOriginalPrice}`, true], ['Discount', `-$${entry.discount}`, false, true], ['Subtotal', `$${entry.totalPrice}`, false, false, true]].map(([lbl, val, struck, green, bold]) => (
                                            <div key={lbl} className="flex flex-col gap-0.5">
                                                <span className="text-[10px] text-zinc-400 tracking-wide">{lbl}</span>
                                                <span className={`text-[12px] font-mono ${struck ? 'line-through text-zinc-400' : ''} ${green ? 'text-green-700' : ''} ${bold ? 'font-medium text-[13px]' : ''}`}>{val}</span>
                                            </div>
                                        ))}

                                    </div>

                                </div>
                                <div className="flex flex-col items-center justify-between p-2 border-l border-zinc-100 gap-1.5">
                                    <div className="flex flex-col items-center gap-1">

                                        <button
                                            onClick={() => changeQuantity({ target: { value: (quantities[entry.identifier] || entry.quantity) + 1 } }, entry)}
                                            className="w-5 h-5 rounded border border-zinc-200 bg-zinc-50 text-sm flex items-center justify-center hover:bg-zinc-100">
                                            +
                                        </button>

                                        <span className="text-[13px] font-mono font-medium">{quantities[entry.identifier] ?? entry.quantity}</span>

                                        <button
                                            onClick={() => changeQuantity({ target: { value: Math.max(1, (quantities[entry.identifier] || entry.quantity) - 1) } }, entry)}
                                            className="w-5 h-5 rounded border border-zinc-200 bg-zinc-50 text-sm flex items-center justify-center hover:bg-zinc-100">
                                            −
                                        </button>
                                    </div>

                                    <button
                                        onClick={(e) => deleteCartEntry(e, entry)}
                                        className="w-5 h-5 rounded text-zinc-300 hover:text-red-500 hover:bg-red-50 flex items-center justify-center text-sm">
                                        ✕
                                    </button>

                                </div>
                            </div>
                        ))}
                </div>
            </div>

            <div className="w-64 bg-zinc-50 border-l border-zinc-100 flex flex-col">
                <div className="p-4 border-b border-zinc-100">
                    <p className="text-[10px] font-medium text-zinc-400 uppercase tracking-widest mb-2.5">Add product</p>

                    <Select
                        options={productOptions}
                        onChange={productSelect}
                        onInputChange={handleInputChangeProduct}
                        isSearchable placeholder="Search by name…"
                        className="text-xs mb-2"
                    />

                    <div className="flex items-center gap-2 my-2"><div className="flex-1 h-px bg-zinc-200" />
                        <span className="text-[11px] text-zinc-400">or</span><div className="flex-1 h-px bg-zinc-200" />
                    </div>

                    <div className="flex gap-1.5">
                        <input
                            type="text"
                            className="flex-1 text-xs border border-zinc-200 rounded-lg px-2.5 py-1.5 bg-white font-mono outline-none focus:border-zinc-400"
                            placeholder="Product ID…"
                            value={newProductId} onChange={e => setNewProductId(e.target.value)} onKeyDown={e => e.key === 'Enter' && addProduct()} />

                        <button onClick={() => addProduct()} className="px-3 py-1.5 text-xs font-medium border border-zinc-200 rounded-lg bg-white hover:bg-zinc-100 cursor-pointer">Add</button>
                    </div>
                </div>

                <div className="flex-1 p-4">
                    <p className="text-[10px] font-medium text-zinc-400 uppercase tracking-widest mb-1">Order summary</p>
                    {[['Original', cartInfo.totalOriginalPrice, 'struck'], ['Discount', cartInfo.discount, 'green'], ['Coupon', cartInfo.coupon || 'N/A', ''], ['Tax (8%)', (cartInfo.totalPrice * 0.08).toFixed(2), '']].map(([lbl, val, style]) => (
                        <div key={lbl} className="flex justify-between items-center py-1.5 border-b border-zinc-100">
                            <span className="text-xs text-zinc-500">{lbl}</span>
                            <span className={`text-xs font-mono ${style === 'struck' ? 'line-through text-zinc-400' : ''} ${style === 'green' ? 'text-green-700 font-medium' : ''}`}>${val}</span>
                        </div>
                    ))}
                </div>

                <div className="p-4 border-t border-zinc-100">
                    <div className="flex justify-between items-baseline mb-3">
                        <span className="text-xs text-zinc-500">Total due</span>
                        <span className="text-xl font-medium font-mono">${cartInfo.totalPrice}</span>
                    </div>
                    <button
                        onClick={() => setIsPaymentOpen(true)}
                        disabled={!cartHasItems}
                        className="w-full bg-zinc-900 text-white text-sm font-medium py-2.5 rounded-xl hover:bg-zinc-700 disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer transition-colors"
                    >
                        Charge customer
                    </button>
                </div>
            </div>

            <Modal isOpen={isOpen} onClose={() => setIsOpen(false)}>
                <CustomerAddModal
                    setCurrentCustomer={setCurrentCustomer}
                    onClose={() => setIsOpen(false)}
                />
            </Modal>

            <Modal isOpen={isPaymentOpen} onClose={() => !isCharging && setIsPaymentOpen(false)}>
                <PaymentModal
                    cartInfo={cartInfo}
                    cartEntryList={cartEntryList}
                    onConfirm={handleChargeConfirm}
                    onCancel={() => setIsPaymentOpen(false)}
                    isLoading={isCharging}
                />
            </Modal>

            <Modal isOpen={isBillOpen} onClose={handleBillClose}>
                {currentOrderId && (
                    <BillModal
                        orderId={currentOrderId}
                        onClose={handleBillClose}
                    />
                )}
            </Modal>
        </div>
    )
}
export default SalesPage;