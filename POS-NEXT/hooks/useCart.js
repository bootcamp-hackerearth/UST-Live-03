import { useState, useEffect } from "react";
import { FetchEntity } from "@/apicalls/fetch/FetchEntity";

export default function useCart(selectedCustomerId, baseUrl) {
    const [cartEntries, setCartEntries] = useState([]);
    const [selectedCart, setSelectedCart] = useState(null);
    const [loadingCart, setLoadingCart] = useState(false);
    const [addingProductId, setAddingProductId] = useState("");

    const refreshCartEntries = async (cartIdentifier) => {
        const activeCartIdentifier =
            cartIdentifier ||
            selectedCart?.identifier ||
            selectedCustomerId;

        if (!activeCartIdentifier) {
            setCartEntries([]);
            return;
        }

        const cartEntryRes = await FetchEntity(
            `${baseUrl}/cartentry/cart`,
            "POST",
            activeCartIdentifier,
            "text/plain"
        );

        setCartEntries(
            (cartEntryRes || []).filter(
                (entry) => entry?.cartId === activeCartIdentifier
            )
        );
    };

    const reloadCart = async () => {
        const updatedCart = await FetchEntity(
            `${baseUrl}/cart/get`,
            "POST",
            selectedCustomerId,
            "text/plain"
        );

        setSelectedCart(updatedCart || null);

        await refreshCartEntries(
            updatedCart?.identifier || selectedCustomerId
        );
        return updatedCart;
    };

    useEffect(() => {
        if (!selectedCustomerId) {
            setSelectedCart(null);
            setCartEntries([]);
            return;
        }

        const loadCart = async () => {

            setLoadingCart(true);
            try {
                await reloadCart();
            } catch (err) {
                console.error("Error loading cart", err);
                setSelectedCart(null);
                setCartEntries([]);
            } finally {
                setLoadingCart(false);
            }
        };

        loadCart();
    }, [selectedCustomerId]);

    const handleAddProduct = async (product) => {
        const cartId =
            selectedCart?.identifier ||
            selectedCustomerId;

        if (!cartId) return;

        const productIdentifier = product?.identifier;

        setAddingProductId(productIdentifier);

        try {
            await FetchEntity(
                `${baseUrl}/cartentry/add`,
                "POST",
                {
                    cartId,
                    product: productIdentifier,
                    quantity: 1,
                    customerIdentifier: selectedCustomerId,
                },
                "application/json"
            );

            await reloadCart();
        } catch (err) {
            console.error("Error adding product", err);
        } finally {
            setAddingProductId("");
        }
    };

    const handleUpdateQuantity = async (entry, delta) => {
        try {
            if (delta === 1) {
                await FetchEntity(
                    `${baseUrl}/cartentry/add`,
                    "POST",
                    {
                        cartId: entry.cartId,
                        product: entry.product,
                        quantity: 1,
                        customerIdentifier: selectedCustomerId,
                    },
                    "application/json"
                );
            } else {
                const newQty = Number(entry.quantity) - 1;
                
                if (newQty < 1) return;

                await FetchEntity(
                    `${baseUrl}/cartentry/updatequantity`,
                    "PUT",
                    {
                        ...entry,
                        quantity: newQty,
                    },
                    "application/json"
                );
            }
            await reloadCart();
        } catch (err) {
            console.error("Error updating quantity", err);
        }
    };

    const handleDelete = async (identifier) => {
        try {
            const response = await FetchEntity(
                `${baseUrl}/cartentry/delete`,
                "DELETE",
                identifier,
                "text/plain"
            );

            if (response) {
                await reloadCart();
            }
        } catch (err) {
            console.error("Error deleting item", err);
        }
    };

    const handleClearCart = async () => {
        try {
            const response = await FetchEntity(
                `${baseUrl}/cartentry/clearCart`,
                "DELETE",
                selectedCustomerId,
                "text/plain"
            );
            if (response) {
                await reloadCart();
            }
        } catch (err) {
            console.error("Error clearing cart", err);
        }
    };

    return {
        cartEntries,
        selectedCart,
        loadingCart,
        addingProductId,
        handleAddProduct,
        handleUpdateQuantity,
        handleDelete,
        handleClearCart,
        reloadCart,
    };
}