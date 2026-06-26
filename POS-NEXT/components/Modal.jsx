"use client";

import PropTypes from "prop-types";

export default function Modal({
    open,
    onClose,
    title,
    children,
}) {
    if (!open) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
       <dialog
    open
    className="absolute left-1/2 top-1/2 m-0 w-full max-w-md -translate-x-1/2 -translate-y-1/2 rounded-xl bg-white p-6 shadow-xl"
    aria-labelledby="modal-title"
>
            <div className="mb-4 flex items-center justify-between">
                <h2 id="modal-title" className="text-lg font-semibold">
                    {title}
                </h2>

                <button
                    type="button"
                    onClick={onClose}
                    className="text-gray-500 hover:text-black"
                >
                    ✕
                </button>
            </div>

            {children}
        </dialog>
    </div>
    );
}

Modal.propTypes = {
    open: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
    title: PropTypes.string,
    children: PropTypes.node,
};
