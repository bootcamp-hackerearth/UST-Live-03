// app/pos/brands/edit/[identifier]/page.jsx

"use client";

import { useState, useEffect, useRef } from "react";
import { useRouter, useParams } from "next/navigation";
import axios from "axios";
import api from "@/app/api/axios";
import { Tag } from "lucide-react";
import { inputClass, labelClass, BrandPageHeader, ErrorBanner, SuccessBanner, IconUploadBlock, AuditTrail, FormFooter, }
    from "@/components/brand/BrandShared";
import { handleSubmitResponse } from "@/components/shared/FormShared";


export default function BrandEditPage() {
    const router = useRouter();
    const params = useParams();
    const identifier = decodeURIComponent(params?.identifier || "");

    const [description, setDescription] = useState("");
    const [iconFile, setIconFile] = useState(null);
    const [iconPreview, setIconPreview] = useState(null);
    const [existingIcon, setExistingIcon] = useState(null);
    const [audit, setAudit] = useState({ createdBy: null, createdAt: null, modifiedBy: null, modifiedAt: null });
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const fileRef = useRef(null);

    useEffect(() => {
        if (!identifier) return;
        (async () => {
            try {
                setLoading(true);
                const res = await api.get("/brand/get", { params: { identifier } });
                const d = res.data;
                setDescription(d.description || "");
                setExistingIcon(d.iconPath || null);
                setIconPreview(d.iconPath ? `http://localhost:8080/uploads/${d.iconPath}` : null);
                setAudit({
                    createdBy: d.createdBy ?? null,
                    createdAt: d.createdAt ?? null,
                    modifiedBy: d.modifiedBy ?? null,
                    modifiedAt: d.modifiedAt ?? null,
                });
            } catch (err) {
                setError(err.response?.data?.message || "Failed to load brand data.");
            } finally {
                setLoading(false);
            }
        })();
    }, [identifier]);

    const handleFile = (e) => {
        const file = e.target.files?.[0];
        if (!file) return;
        setIconFile(file);
        setIconPreview(URL.createObjectURL(file));
    };

    const clearIcon = () => {
        setIconFile(null);
        setIconPreview(existingIcon ? `http://localhost:8080/uploads/${existingIcon}` : null);
        if (fileRef.current) fileRef.current.value = "";
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setSuccess("");
        setSubmitting(true);
        try {
            const token = localStorage.getItem("token");
            const formData = new FormData();
            formData.append("identifier", identifier);
            formData.append("description", description.trim());
            if (iconFile) formData.append("icon", iconFile);

            const res = await axios.put("http://localhost:8080/api/brand/update", formData, {
                headers: { "Content-Type": "multipart/form-data", ...(token && { Authorization: `Bearer ${token}` }) }
            });
            handleSubmitResponse({
                res,
                successMessage: "Brand updated successfully! Redirecting...",
                setError,
                setSuccess,
                setAudit,
                router,
            });
        } catch (err) {
            setError(err.response?.data?.message || "Unable to connect to server.");
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-slate-50 p-6">
                <div className="animate-pulse space-y-4">
                    <div className="h-8 w-48 bg-gray-200 rounded-lg" />
                    <div className="bg-white rounded-xl border border-gray-100 p-6 space-y-4">
                        <div className="h-10 bg-gray-100 rounded-lg w-1/2" />
                        <div className="h-10 bg-gray-100 rounded-lg w-full" />
                        <div className="h-16 w-16 bg-gray-100 rounded-xl" />
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-slate-50 p-6">
            <BrandPageHeader
                title="Edit Brand"
                subtitle={identifier}
                icon={Tag}
            />

            <ErrorBanner message={error} />
            <SuccessBanner message={success} />

            <form onSubmit={handleSubmit} className="space-y-6">
                <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-6">
                    <p className="text-xs font-bold text-gray-400 uppercase tracking-widest mb-5">Brand Details</p>

                    <div className="mb-5">
                        <label htmlFor="brandIdentifier" className={labelClass}>Brand ID</label>
                        <input
                            id="brandIdentifier"
                            type="text"
                            value={identifier}
                            disabled
                            className="w-full px-3 py-2.5 border border-gray-100 rounded-lg text-sm bg-gray-50 text-gray-400 cursor-not-allowed"
                        />
                    </div>

                    <div className="mb-5">
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

                    <IconUploadBlock
                        iconFile={iconFile}
                        iconPreview={iconPreview}
                        fileRef={fileRef}
                        onFileChange={handleFile}
                        onClear={clearIcon}
                        isEdit={true}
                    />
                </div>

                <AuditTrail audit={audit} />

                <FormFooter
                    onCancel={() => router.back()}
                    loading={submitting}
                    submitLabel="Update Brand"
                />
            </form>
        </div>
    );
}