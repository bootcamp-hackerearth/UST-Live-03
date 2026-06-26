"use client";

import { useState, useRef } from "react";
import { useRouter } from "next/navigation";
import CommonAdd from "@/components/common/CommonAdd";
import { addItem } from "@/services/api";
import CustomerFields from "@/components/customer/CustomerFields";

export default function AddCustomer() {
  const router = useRouter();

  const [identifier, setIdentifier] = useState("");
  const [name, setName] = useState("");
  const [phoneNo, setPhoneNo] = useState("");
  const [partyType, setPartyType] = useState([]);
  const [balance, setBalance] = useState("");
  const [creditLimit, setCreditLimit] = useState("");
  const [billingAddress, setBillingAddress] = useState({ addressType: "billingAddress" });
  const [shippingAddress, setShippingAddress] = useState({ addressType: "shippingAddress" });
  const [sameAsShipping, setSameAsShipping] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const validateRef = useRef(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (validateRef.current && !validateRef.current()) return;

    try {
      setLoading(true);
      setError("");
      setSuccessMessage("");

      await addItem("customer", {
        identifier,
        name,
        phoneNo: Number(phoneNo),
        partyType,
        balance: balance ? Number(balance) : null,
        creditLimit: creditLimit ? Number(creditLimit) : null,
        billingAddress: { ...billingAddress, addressType: "billingAddress" },
        shippingAddress: sameAsShipping
          ? { ...billingAddress, addressType: "shippingAddress" }
          : { ...shippingAddress, addressType: "shippingAddress" },
      });

      setSuccessMessage("Customer added successfully");
      setTimeout(() => router.push("/customers"), 1000);
    } catch (err) {
      setError(err?.response?.data?.message || "Failed to add customer");
    } finally {
      setLoading(false);
    }
  };

  return (
    <CommonAdd
      title="Add Customer"
      subtitle="Create a new customer record"
      identifier={identifier}
      setIdentifier={setIdentifier}
      showDescription={false}
      loading={loading}
      error={error}
      successMessage={successMessage}
      onSubmit={handleSubmit}
      cancelPath="/customers"
    >
      <CustomerFields
        name={name} setName={setName}
        phoneNo={phoneNo} setPhoneNo={setPhoneNo}
        partyType={partyType} setPartyType={setPartyType}
        balance={balance} setBalance={setBalance}
        creditLimit={creditLimit} setCreditLimit={setCreditLimit}
        billingAddress={billingAddress} setBillingAddress={setBillingAddress}
        shippingAddress={shippingAddress} setShippingAddress={setShippingAddress}
        sameAsShipping={sameAsShipping} setSameAsShipping={setSameAsShipping}
        onValidate={(fn) => { validateRef.current = fn; }}
      />
    </CommonAdd>
  );
}