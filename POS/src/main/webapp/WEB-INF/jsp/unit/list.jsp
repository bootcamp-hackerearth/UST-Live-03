<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Unit List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
    body {
        min-height: 100vh;
        background: linear-gradient(135deg, #dbeafe, #93c5fd);
        font-family: 'Poppins', sans-serif;
    }

    /* TABLE CONTAINER */
    .table-wrapper {
        background: rgba(255, 255, 255, 0.92);
        padding: 20px;
        box-shadow: 0 20px 45px rgba(0, 0, 0, 0.15);
    }

    /* TITLE */
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

    /* ROWS */
    .table tbody tr {
        transition: 0.2s ease;
    }

    .table tbody tr:hover {
        background: rgba(147, 197, 253, 0.15);
        transform: scale(1.01);
    }

    /* REMOVE HEAVY BORDERS */
    .table td, .table th {
        border: none !important;
    }

    /* BUTTONS (keep clean) */
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
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">

            <!-- Page Title -->
            <h4 class="text-center mb-4">List of Units</h4>

            <c:if test="${not empty message}">
                <div id="customToast" class="custom-toast">
                    ${message}
                </div>
                <c:remove var="message" scope="session"/>
            </c:if>

            <!-- Empty State -->
            <c:if test="${empty units}">
                <div class="alert alert-warning text-center">
                    No units found
                </div>
            </c:if>

            <!-- Units Table -->
            <c:if test="${not empty units}">
                <div class="table-wrapper">
                    <table class="table align-middle">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Identifier</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="unit" items="${units}">
                        <tr>
                            <td>${unit.id}</td>
                            <td>${unit.identifier}</td>
                            <td class="text-center">
                                <div class="form-check form-switch">
                                    <input
                                        class="form-check-input unit-toggle"
                                        type="checkbox"
                                        data-identifier="${unit.identifier}"
                                        ${unit.status ? "checked" : ""} >
                                </div>
                            </td>
                            <td class="d-flex justify-content-center gap-2">
                                <a href="/unit/get?identifier=${unit.identifier}"
                                   class="btn btn-primary btn-sm">
                                    Edit
                                </a>

                                <a href="/unit/delete?identifier=${unit.identifier}"
                                   class="btn btn-danger btn-sm"
                                   onclick="return confirm('Are you sure you want to delete this unit?');">
                                    Delete
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                </div>
            </c:if>

            <div class="d-flex justify-content-center gap-3 mt-4">
                <a href="/" class="btn btn-secondary">Home</a>
                <a href="/unit/add" class="btn btn-primary">+ Add New Unit</a>
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
    if (!e.target.classList.contains("unit-toggle")) return;

    const checkbox = e.target;
    const identifier = checkbox.dataset.identifier;
    const status = checkbox.checked;

    fetch("/unit/toggle", {
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
        alert("Failed to update product status");
        checkbox.checked = !status;
    });
});
</script>
</body>
</html>
