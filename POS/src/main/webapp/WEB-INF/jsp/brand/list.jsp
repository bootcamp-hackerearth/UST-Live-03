<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Brand List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
    body {
        min-height: 100vh;
        background: linear-gradient(135deg, #dbeafe, #93c5fd);
        font-family: 'Poppins', sans-serif;
    }

    .table-wrapper {
        background: rgba(255, 255, 255, 0.92);
        padding: 20px;
        box-shadow: 0 20px 45px rgba(0, 0, 0, 0.15);
    }

    h4 {
        color: white;
        font-weight: 700;
    }

    .table {
        margin-bottom: 0;
        background: transparent;
    }

    .table thead th {
        background: transparent;
        border-bottom: 1.5px solid rgb(217 217 217 / 60%) !important;
        font-weight: 600;
        color: #111;
    }

    .table tbody tr {
        transition: 0.2s ease;
    }

    .table tbody tr:hover {
        background: rgba(147, 197, 253, 0.15);
        transform: scale(1.01);
    }

    .table td, .table th {
        border: none !important;
    }

    .btn-primary {
        background: #3b82f6;
        border: none;
    }

    .btn-primary:hover {
        background: #2563eb;
    }

    .btn-danger {
        background: #ef4444;
        border: none;
    }

    .btn-danger:hover {
        background: #dc2626;
    }

    .btn-success {
        background: #3b82f6;
        border: none;
    }

    .btn-success:hover {
        background: #2563eb;
    }

    .btn-secondary {
        background: #94a3b8;
        border: none;
    }

    .btn-secondary:hover {
        background: #64748b;
    }

    .custom-toast {
        position: fixed;
        bottom: 30px;
        left: 50%;
        transform: translateX(-50%) translateY(20px);

        min-width: 260px;
        max-width: 80%;

        padding: 14px 18px;
        border-radius: 14px;

        text-align: center;

        font-size: 14px;
        font-weight: 500;
        color: rgba(31, 59, 59, 0.9);

        background: rgba(255, 255, 255, 0.18);
        backdrop-filter: blur(18px) saturate(180%);
        -webkit-backdrop-filter: blur(18px) saturate(180%);

        background-image: linear-gradient(
            135deg,
            rgba(255, 255, 255, 0.25),
            rgba(255, 255, 255, 0.08)
        );

        border: 1px solid rgba(255, 255, 255, 0.35);
        box-shadow:
            0 12px 30px rgba(0, 0, 0, 0.12),
            inset 0 1px 0 rgba(255, 255, 255, 0.4);

        z-index: 9999;
        opacity: 0;
        animation: toastIn 0.4s ease forwards;
    }

    .custom-toast::before {
        content: "";
        position: absolute;
        inset: -2px;
        border-radius: inherit;
        background: radial-gradient(
            circle at center,
            rgba(74, 166, 163, 0.25),
            transparent 70%
        );
        z-index: -1;
        filter: blur(12px);
    }

    @keyframes toastIn {
        from {
            opacity: 0;
            transform: translateX(-50%) translateY(20px);
        }
        to {
            opacity: 1;
            transform: translateX(-50%) translateY(0);
        }
    }

    .custom-toast.hide {
        opacity: 0;
        transform: translateX(-50%) translateY(20px);
        transition: all 0.4s ease;
    }
    .form-switch .form-check-input {
        cursor: pointer;
        width: 2.6em;
        height: 1.4em;
    }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">

            <h4 class="text-center mb-4">List of Brands</h4>

            <c:if test="${not empty message}">
                <div id="customToast" class="custom-toast">
                    ${message}
                </div>
                <c:remove var="message" scope="session"/>
            </c:if>

            <c:if test="${empty brands}">
                <div class="alert alert-warning text-center">
                    No brands found
                </div>
            </c:if>

            <c:if test="${not empty brands}">
                <div class="table-wrapper">
                    <table class="table align-middle">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Identifier</th>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="brand" items="${brands}">
                        <tr>
                            <td>${brand.id}</td>
                            <td>${brand.identifier}</td>
                            <td>${brand.name}</td>
                            <td>${brand.description}</td>
                            <td class="text-center">
                                <div class="form-check form-switch">
                                    <input
                                        class="form-check-input brand-toggle"
                                        type="checkbox"
                                        data-identifier="${brand.identifier}"
                                        ${brand.status ? "checked" : ""}
                                    >
                                </div>
                            </td>
                            <td class="d-flex justify-content-center gap-2">
                                <a href="/brand/get?identifier=${brand.identifier}"
                                   class="btn btn-primary btn-sm">
                                    Edit
                                </a>

                                <a href="/brand/delete?identifier=${brand.identifier}"
                                   class="btn btn-danger btn-sm"
                                   onclick="return confirm('Are you sure you want to delete this brand?');">
                                    Delete
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>

                <c:if test="${totalPages > 1}">
                    <nav class="mt-4">
                        <ul class="pagination justify-content-center">

                            <li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
                                <a class="page-link"
                                   href="?page=${currentPage - 1}&size=5">
                                    Previous
                                </a>
                            </li>

                            <c:forEach begin="0" end="${totalPages - 1}" var="i">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link"
                                       href="?page=${i}&size=5">
                                        ${i + 1}
                                    </a>
                                </li>
                            </c:forEach>

                            <li class="page-item ${currentPage == totalPages - 1 ? 'disabled' : ''}">
                                <a class="page-link"
                                   href="?page=${currentPage + 1}&size=5">
                                    Next
                                </a>
                            </li>

                        </ul>
                    </nav>
                </c:if>

                </div>
            </c:if>

            <div class="d-flex justify-content-center gap-3 mt-4">
                <a href="/" class="btn btn-secondary">Home</a>
                <a href="/brand/add" class="btn btn-primary">+ Add New Brand</a>
            </div>

        </div>
    </div>
</div>

<script>
window.addEventListener("DOMContentLoaded", function () {
    const toast = document.getElementById("customToast");

    if (toast) {
        setTimeout(() => {
            toast.classList.add("hide");
            setTimeout(() => toast.remove(), 400);
        }, 3500);
    }
});
</script>
<script>
document.addEventListener("change", function (e) {
    if (!e.target.classList.contains("brand-toggle")) return;

    const checkbox = e.target;
    const identifier = checkbox.dataset.identifier;
    const status = checkbox.checked;

    fetch("/brand/toggle", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            identifier: identifier,
            status: status
        })
    })
    .then(res => res.json())
    .then(data => {
        if (!data.success) {
            alert(data.message || "Update failed");
            checkbox.checked = !status;
        }
    })
    .catch(() => {
        alert("Failed to update brand status");
        checkbox.checked = !status;
    });
});
</script>
</body>
</html>
