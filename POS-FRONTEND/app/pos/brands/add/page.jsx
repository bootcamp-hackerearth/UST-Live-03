// app/pos/brands/add/page.jsx

"use client";

import { useState, useRef } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import { Tag } from "lucide-react";
import { inputClass, labelClass, BrandPageHeader, ErrorBanner, SuccessBanner, IconUploadBlock, FormFooter, }
    from "@/components/brand/BrandShared";

export default function BrandAddPage() {
    const router = useRouter();

    const [identifier, setIdentifier] = useState("");
    const [description, setDescription] = useState("");
    const [iconFile, setIconFile] = useState(null);
    const [iconPreview, setIconPreview] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const fileRef = useRef(null);

    const handleFile = (e) => {
        const file = e.target.files?.[0];
        if (!file) return;
        setIconFile(file);
        setIconPreview(URL.createObjectURL(file));
    };

    const clearIcon = () => {
        setIconFile(null);
        setIconPreview(null);
        if (fileRef.current) fileRef.current.value = "";
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setSuccess("");
        if (!identifier.trim()) { setError("Brand ID is required."); return; }
        setLoading(true);
        try {
            const token = localStorage.getItem("token");
            const formData = new FormData();
            formData.append("identifier", identifier.trim());
            formData.append("description", description.trim());
            if (iconFile) formData.append("icon", iconFile);

            const res = await axios.post(
                "http://localhost:8080/api/brand/add",
                formData,
                { headers: { "Content-Type": "multipart/form-data", ...(token && { Authorization: `Bearer ${token}` }) } }
            );

            if (res.data?.success === false) { setError(res.data.message || "Failed to add brand."); return; }
            setSuccess("Brand added successfully! Redirecting...");
            setTimeout(() => router.back(), 1500);
        } catch (err) {
            setError(err.response?.data?.message || "Unable to connect to server.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-slate-50 p-6">
            <BrandPageHeader
                title="Add Brand"
                subtitle="Create a new brand with icon and description"
                icon={Tag}
            />

            <ErrorBanner message={error} />
            <SuccessBanner message={success} />

            <form onSubmit={handleSubmit} className="space-y-6">
                <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-6">
                    <p className="text-xs font-bold text-gray-400 uppercase tracking-widest mb-5">Brand Details</p>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
                        <div>
                            <label htmlFor="brandIdentifier" className={labelClass}>Brand Identifier<span className="text-red-400">*</span></label>
                            <input
                                id="brandIdentifier"
                                type="text"
                                value={identifier}
                                onChange={(e) => setIdentifier(e.target.value)}
                                placeholder="e.g. APPLE, SAMSUNG"
                                className={inputClass}
                                required
                            />
                        </div>
                        <div>
                            <label htmlFor="brandDescription" className={labelClass}>Description</label>
                            <input
                                id="brandDescription"
                                type="text"
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                                placeholder="Short brand description"
                                className={inputClass}
                            />
                        </div>
                    </div>
                    <IconUploadBlock
                        iconFile={iconFile}
                        iconPreview={iconPreview}
                        fileRef={fileRef}
                        onFileChange={handleFile}
                        onClear={clearIcon}
                        isEdit={false}
                    />
                </div>

                <FormFooter
                    onCancel={() => router.back()}
                    loading={loading}
                    submitLabel="Add Brand"
                />
            </form>
        </div>
    );
}