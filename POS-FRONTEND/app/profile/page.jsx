"use client"
import React, { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import { useRouter } from 'next/navigation'

const ProfilePage = () => {
    const [roles, setRoles] = useState([]);
    const [isEditing, setIsEditing] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const router = useRouter()
    const [email,setEmail] = useState("");

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors, isSubmitting }
    } = useForm();

    const paginationDto = {
        "page": 0,
        "sizePerPage": 50
    };

    useEffect(() => {
        const user = localStorage.getItem("username");

        if (!user) {
            console.error("Authentication data missing");
            setIsLoading(false);
            return;
        }

        async function initPage() {
            try {
                setIsLoading(true);
                await Promise.all([
                    fetchUserProfile(user),
                    getRoles()
                ]);
            } catch (error) {
                console.error("Error loading profile data:", error);
            } finally {
                setIsLoading(false);
            }
        }

        initPage();
    }, []);

    async function fetchUserProfile(user) {
        const res = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/user/get?username=${user}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
            credentials:"include",
        });
        if (res.ok) {
            const response = await res.json();
            reset(response);
            setEmail(response.username)
        }
    }

    async function getRoles() {
        const res = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/role/list`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(paginationDto)
        });
        if (res.ok) {
            const response = await res.json();
            setRoles(response.dtoList || []);
        }
    }

    const onSubmit = async (data) => {
        try {
            const res = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/user/update`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                },
                credentials:"include",
                body: JSON.stringify(data)
            });

            if (res.ok) {
                setIsEditing(false);
            }
            if( data.username !== email){
                localStorage.removeItem("token")
                localStorage.removeItem("username")
                router.push("/login")
                alert(`login with your new email`)
            }
        } catch (err) {
            console.error("Failed to update profile", err);
        }
    };

    if (isLoading) {
        return (
            <div className="min-h-screen flex justify-center items-center bg-gray-50 text-gray-500 font-medium text-sm">
                Loading POS Operator Profile...
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 py-10 px-4 sm:px-6 lg:px-8 flex justify-center items-start">
            <div className="w-full max-w-2xl bg-white shadow-sm border border-gray-200 rounded-xl overflow-hidden">
                <div className="border-b border-gray-200 bg-white px-6 py-5 sm:px-8 flex justify-between items-center">
                    <div>
                        <h1 className="text-xl font-bold text-gray-900 tracking-tight">User Profile</h1>
                    </div>
                </div>

                <form onSubmit={handleSubmit(onSubmit)} className="p-6 sm:p-8 space-y-6">
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                        
                        <div className="sm:col-span-2">
                            <label htmlFor="username" className="block text-xs font-semibold uppercase tracking-wider text-gray-500 mb-2">
                                Email / Username
                            </label>
                            <input 
                                id="username"
                                {...register("username")} 
                                type="text" 
                                disabled={true}
                               className={`w-full border px-3 py-2.5 rounded-lg text-sm transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500
                                ${isEditing ? 'bg-white text-gray-900 border-gray-300' : 'bg-gray-50 text-gray-600 border-gray-200'}`}
                            />
                        </div>

                        <div>
                            <label htmlFor="name" className="block text-xs font-semibold uppercase tracking-wider text-gray-500 mb-2">
                                Full Name
                            </label>
                            <input 
                                id="name"
                                {...register("name", { required: "Name is required" })} 
                                type="text" 
                                disabled={!isEditing}
                                className={`w-full border px-3 py-2.5 rounded-lg text-sm transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500
                                    ${isEditing ? 'bg-white text-gray-900 border-gray-300' : 'bg-gray-50 text-gray-600 border-gray-200'}`}
                            />
                            {errors.name && <p className="text-xs text-red-500 mt-1">{errors.name.message}</p>}
                        </div>

                        <div>
                            <label htmlFor="phoneNo" className="block text-xs font-semibold uppercase tracking-wider text-gray-500 mb-2">
                                Contact Number
                            </label>
                            <input 
                                id="phoneNo"
                                {...register("phoneNo")} 
                                type="text" 
                                disabled={!isEditing}
                                className={`w-full border px-3 py-2.5 rounded-lg text-sm transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500
                                    ${isEditing ? 'bg-white text-gray-900 border-gray-300' : 'bg-gray-50 text-gray-600 border-gray-200'}`}
                            />
                        </div>

                        <div className="sm:col-span-2">
                            <label htmlFor="roles" className="block text-xs font-semibold uppercase tracking-wider text-gray-500 mb-2">
                                Assigned POS Roles <span className="text-gray-400 font-normal">(Hold Ctrl/Cmd to select multiple)</span>
                            </label>
                            <select 
                                id="roles"
                                {...register("roles")} 
                                multiple
                                disabled={!isEditing}
                                className={`w-full border p-2 rounded-lg text-sm min-h-25 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500
                                    ${isEditing ? 'bg-white text-gray-900 border-gray-300' : 'bg-gray-50 text-gray-500 border-gray-200'}`}
                            >
                                {roles.map((role) => (
                                    <option key={role.identifier} value={role.identifier} className="p-1.5 rounded my-0.5">
                                        {role.identifier}
                                    </option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div className="pt-4 border-t border-gray-100 flex justify-end gap-3">
                        {isEditing ? (
                            <>
                                <button
                                    type="button"
                                    onClick={() => { setIsEditing(false); reset(); }}
                                    className="px-4 py-2 border border-gray-300 rounded-lg text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 transition-colors"
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    disabled={isSubmitting}
                                    className="px-4 py-2 border border-transparent rounded-lg text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 shadow-sm transition-colors disabled:opacity-50"
                                >
                                    {isSubmitting ? 'Saving...' : 'Save Changes'}
                                </button>
                            </>
                        ) : (
                            <button
                                type="button"
                                onClick={() => setIsEditing(true)}
                                className="px-4 py-2 border border-transparent rounded-lg text-sm font-medium text-white bg-gray-900 hover:bg-gray-800 shadow-sm transition-colors"
                            >
                                Edit Profile
                            </button>
                        )}
                    </div>
                </form>

            </div>
        </div>
    );
}

export default ProfilePage;