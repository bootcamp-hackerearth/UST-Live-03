<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Brand List</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css">

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">
          <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
                rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            background-color: #f4f6fb;
        }

        .content {
            margin: 50px auto;
            padding: 20px;
        }

        .page-wrapper {
            max-width: 900px;
            margin: 0 auto;
        }

        .header-banner {
            background: linear-gradient(135deg, #4e73df, #6f42c1);
            color: #ffffff;
            padding: 18px 22px;
            border-radius: 10px;
            margin-bottom: 20px;
        }

        .welcome-card {
            background: #ffffff;
            border-radius: 10px;
            padding: 22px;
        }

        table th {
            font-weight: 600;
            font-size: 14px;
            background-color: #f8f9fc;
        }

        table td {
            font-size: 14px;
            vertical-align: middle;
        }
.form-switch .form-check-input {
    cursor: pointer;
    width: 2.6em;
    height: 1.4em;
}
    </style>
</head>

<body>
<div class="content">
    <div class="page-wrapper">
        <div class="header-banner">
            <h2>Brand List</h2>
            <p>View and manage product brands</p>
        </div>
        <div class="welcome-card">
            <div class="d-flex justify-content-between mb-3">
                <a href="${pageContext.request.contextPath}/"
                   class="btn btn-secondary btn-sm">
                    Home
                </a>
                <a href="${pageContext.request.contextPath}/brand/add"
                   class="btn btn-primary btn-sm">
                    + Add Brand
                </a>
            </div>
            <c:if test="${empty brands}">
                <div class="alert alert-info text-center">No brands found.</div>
            </c:if>
            <c:if test="${not empty brands}">
                <table class="table table-bordered table-hover">
                    <thead>
                    <tr>
                        <th>Identifier</th>
                        <th>Brand Name</th>
                        <th>Description</th>
                        <th>Status</th>
                        <th style="width:180px;">Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${brands}" var="brand">
                        <tr>
                            <td>${brand.identifier}</td>
                            <td>${brand.brandName}</td>
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
                            <td class="text-center">
                                <a href="${pageContext.request.contextPath}/brand/get?identifier=${brand.identifier}"
                                   class="btn btn-success btn-sm mr-2">Update</a>
                                <a href="${pageContext.request.contextPath}/brand/delete?identifier=${brand.identifier}"
                                   class="btn btn-danger btn-sm"
                                   onclick="return confirm('Are you sure you want to delete this brand?');">
                                    Delete
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>

        </div>
    </div>
</div>
<script>
document.addEventListener("change", function (e) {
    if (!e.target.classList.contains("brand-toggle")) return;

    const checkbox = e.target;
    const identifier = checkbox.dataset.identifier;
    const status = checkbox.checked;

    fetch("/brand/toggleStatus", {
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
        } else {
            alert("Update Success");
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