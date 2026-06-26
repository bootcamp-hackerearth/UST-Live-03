"use client"
import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import axios from 'axios';
 
const Register = () => {
    const router = useRouter();
    const API = process.env.NEXT_PUBLIC_API_URL;
    const [user, setUser] = useState({
        name: '',
        username: '',
        password: '',
        phoneNo: '',
        roles: []
    });
 
    const [roles, setRoles] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
 
    useEffect(() => {
        fetchRoles();
    }, []);

    const fetchRoles = async () => { 
        try {
            const response = await axios.post(
                `${API}/role/list`,
                {
                    page: 0,
                    sizePerPage: 50,
                    sortDirection: 'ASC',
                    sortField: 'identifier'
                }
            );
            setRoles(response.data.dtoList || []);
        } catch (err) {
            console.error(err);
            setError('Failed to load roles');
        }
    };
 
    const handleChange = (e) => {
        setUser({
            ...user,
            [e.target.name]: e.target.value
        });
    };
 
    const handleRoleChange = (e) => {
        const { value, checked } = e.target;
        if (checked) {
            setUser({
                ...user,
                roles: [...user.roles, value]
            });
 
        } else {
            setUser({
                ...user,
                roles: user.roles.filter(role => role !== value)
            });
        }
    };
 
    const validate = () => {
        if (user.name.trim().length < 3) {
            setError('Name must be minimum 3 characters');
            return false;
        }
 
        const emailRegex = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
        if (!emailRegex.test(user.username)) {
            setError('Enter valid Gmail address');
            return false;
        }
 
        const phoneRegex = /^\d{10}$/;
        if (!phoneRegex.test(user.phoneNo)) {
            setError('Phone number must be 10 digits');
            return false;
        }
 
        const passwordRegex =
            /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d).{8,}$/;
        if (!passwordRegex.test(user.password)) {
            setError(
                'Password must contain uppercase lowercase number and 8 characters'
            );
            return false;
        }
 
        if (user.roles.length === 0) {
            setError('Select at least one role');
            return false;
        }
        return true;
    };
 
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        if (!validate()) {
            return;
        }
        setLoading(true);

        try {
            const response = await axios.post(
                `${API}/user/register`,
                user
            );
 
            if (response.data.success) {
                setSuccess('Registration Successful');
                setTimeout(() => {
                    router.push('/login');
                }, 1500);
 
            } else {
                setError(
                    response.data.message ||
                    'Registration failed'
                );
            }
        } catch (error) {
            console.error(error);
            setError('Server error');
        } finally {
            setLoading(false);
        }
    };
 
    return (
 
        <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center px-4">
            <div className="mb-8 text-center">

                <h1 className="text-4xl font-bold text-zinc-900">
                    POS SYSTEM
                </h1>

                <p className="text-slate-500 mt-2">
                    Create your account
                </p>

            </div>
 
            <div className="w-full max-w-md flex items-center justify-center">
                <div className="bg-white w-full p-9 rounded-xl shadow-2xl border-t-[6px] border-cyan-500">
 
                    <h2 className="text-center text-2xl font-bold mb-7">
                        User Registration
                    </h2>
                    {error && (
                        <div className="mb-4 p-3 rounded-md bg-red-100 text-red-600 text-sm text-center">
                            {error}
                        </div>
                    )}
 
                    {success && (
                        <div className="mb-4 p-3 rounded-md bg-cyan-100 text-cyan-700 text-sm text-center">
                            {success}
                        </div>
                    )}
 
                    <form onSubmit={handleSubmit}>
                        <div className="mb-4">
                            <label 
                                htmlFor="name"
                                className="block text-xs font-semibold text-slate-700 mb-1">
                                Full Name <span className="text-red-500">*</span>
                            </label>
 
                            <input
                                id="name"
                                type="text"
                                name="name"
                                value={user.name}
                                onChange={handleChange}
                                placeholder="Enter full name"
                                required
                                className="w-full p-3 rounded-lg border border-slate-200 bg-white text-sm focus:outline-none focus:border-cyan-500 focus:bg-white focus:ring-4 focus:ring-cyan-100 transition"
                            />
                        </div>
 
                        <div className="mb-4">
                            <label 
                                htmlFor="username"
                                className="block text-xs font-semibold text-slate-700 mb-1">
                                Email <span className="text-red-500">*</span>
                            </label>
 
                            <input
                                id="username"
                                type="email"
                                name="username"
                                value={user.username}
                                onChange={handleChange}
                                placeholder="Enter Gmail address"
                                pattern="^[a-zA-Z0-9._%+-]+@gmail\.com$"
                                required
                                className="w-full p-3 rounded-lg border border-slate-200 bg-white text-sm focus:outline-none focus:border-cyan-500 focus:bg-white focus:ring-4 focus:ring-cyan-100 transition"
                            />
                        </div>
 
                        <div className="mb-4">
 
                            <label 
                                htmlFor="password"
                                className="block text-xs font-semibold text-slate-700 mb-1">
                                Password <span className="text-red-500">*</span>
                            </label>
 
                            <input
                                id="password"
                                type="password"
                                name="password"
                                value={user.password}
                                onChange={handleChange}
                                placeholder="Enter password"
                                required
                                className="w-full p-3 rounded-lg border border-slate-200 bg-white text-sm focus:outline-none focus:border-cyan-500 focus:bg-white focus:ring-4 focus:ring-cyan-100 transition"
                            />
                        </div>
 
                        <div className="mb-4">
 
                            <label 
                                htmlFor="phoneNo"
                                className="block text-xs font-semibold text-slate-700 mb-1">
                                Phone Number <span className="text-red-500">*</span>
                            </label>
 
                            <input
                                id="phoneNo"
                                type="tel"
                                name="phoneNo"
                                value={user.phoneNo}
                                onChange={(e) => {
                                    const value =
                                        e.target.value.replaceAll(/\D/g, '');
                                    setUser({
                                        ...user,
                                        phoneNo: value
                                    });
                                }}
                                maxLength="10"
                                pattern="^[0-9]{10}$"
                                placeholder="Enter mobile number"
                                required
                                className="w-full p-3 rounded-lg border border-slate-200 bg-white text-sm focus:outline-none focus:border-cyan-500 focus:bg-white focus:ring-4 focus:ring-cyan-100 transition"
                            />
                        </div>
 
                        <fieldset className="mb-5">
                            <legend className="block text-xs font-semibold text-slate-700 mb-3">
                                Assigned Roles <span className="text-red-500">*</span>
                            </legend>
 
                            <div className="space-y-2">
                                {roles.map((role) => (
                                    <div
                                        key={role.identifier}
                                        className="flex items-center gap-3"
                                    >
                                        <input
                                            type="checkbox"
                                            id={`role_${role.identifier}`}
                                            value={role.identifier}
                                            checked={user.roles.includes(role.identifier)}
                                            onChange={handleRoleChange}
                                            className="w-4 h-4 cursor-pointer accent-cyan-500"
                                        />
 
                                        <label
                                            htmlFor={`role_${role.identifier}`}
                                            className="text-sm cursor-pointer text-slate-700"
                                        >
                                            {role.identifier}
                                        </label>
 
                                    </div>
                                ))}
                            </div>
                        </fieldset>
 
                        <div className="flex gap-3 mt-6">
 
                            <button
                                type="submit"
                                disabled={loading}
                                className="flex-1 bg-zinc-900 hover:bg-zinc-800 text-white py-3 rounded-lg font-semibold transition shadow-lg hover:shadow-xl"
                            >
                                {loading
                                    ? 'Registering...'
                                    : 'Register'}
                            </button>
 
                            <button
                                type="reset"
                                onClick={() => {
                                    setUser({
                                        name: '',
                                        username: '',
                                        password: '',
                                        phoneNo: '',
                                        roles: []
                                    });
                                }}
                                className="flex-1 bg-cyan-50 hover:bg-cyan-100 text-cyan-700 py-3 rounded-lg font-semibold transition"
                            >
                                Clear
                            </button>
 
                        </div>
 
                        <div className="text-center mt-5 text-sm text-slate-500">
 
                            Already have an account?{' '}
 
                            <button
                                onClick={() => router.push('/login')}
                                className="text-cyan-600 hover:text-cyan-700 font-semibold cursor-pointer hover:underline"
                            >
                                Login here
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};
 
export default Register;