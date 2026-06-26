"use client";

import { useEffect, useState, useRef } from "react";
import { useRouter, useParams } from "next/navigation";
import CommonEdit from "@/components/common/CommonEdit";
import { getItem, updateItem } from "@/services/api";
import CustomerFields from "@/components/customer/CustomerFields";

export default function EditCustomer() {
  const router = useRouter();
  const params = useParams();
  const identifier = decodeURIComponent(params.identifier);

  const [name, setName] = useState("");
  const [phoneNo, setPhoneNo] = useState("");
  const [partyType, setPartyType] = useState([]);
  const [balance, setBalance] = useState("");
  const [creditLimit, setCreditLimit] = useState("");
  const [billingAddress, setBillingAddress] = useState({ addressType: "billingAddress" });
  const [shippingAddress, setShippingAddress] = useState({ addressType: "shippingAddress" });
  const [sameAsShipping, setSameAsShipping] = useState(false);
  const [loading, setLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [auditData, setAuditData] = useState({});
  const validateRef = useRef(null);

  useEffect(() => {
    loadCustomer();
  }, []);

  const loadCustomer = async () => {
    try {
      setPageLoading(true);
      const response = await getItem("customer", identifier);
      setName(response.name || "");
      setPhoneNo(response.phoneNo ? String(response.phoneNo) : "");
      setPartyType(response.partyType || []);
      setBalance(response.balance ?? "");
      setCreditLimit(response.creditLimit ?? "");
      setBillingAddress(response.billingAddress || { addressType: "billingAddress" });
      setShippingAddress(response.shippingAddress || { addressType: "shippingAddress" });
      setAuditData({
        createdBy: response.createdBy,
        createdOn: response.createdOn,
        modifiedBy: response.modifiedBy,
        modifiedOn: response.modifiedOn,
      });
    } catch (err) {
      console.log(err);
      setError("Failed to load customer");
    } finally {
      setPageLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (validateRef.current && !validateRef.current()) return;

    try {
      setLoading(true);
      setError("");
      setSuccessMessage("");

      await updateItem("customer", {
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

      setSuccessMessage("Customer updated successfully");
      setTimeout(() => router.push("/customers"), 1000);
    } catch (err) {
      console.log(err);
      setError(err?.response?.data?.message || "Failed to update customer");
    } finally {
      setLoading(false);
    }
  };

  if (pageLoading) {
    return (
      <div className="flex items-center justify-center py-20">
        <p className="text-[#667085]">Loading customer...</p>
      </div>
    );
  }

  return (
    <CommonEdit
      title="Edit Customer"
      subtitle="Update customer details"
      identifier={identifier}
      showDescription={false}
      loading={loading}
      error={error}
      successMessage={successMessage}
      onSubmit={handleSubmit}
      cancelPath="/customers"
      auditData={auditData}
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
        isEdit={true}
        onValidate={(fn) => { validateRef.current = fn; }}
        />
    </CommonEdit>
  );
}