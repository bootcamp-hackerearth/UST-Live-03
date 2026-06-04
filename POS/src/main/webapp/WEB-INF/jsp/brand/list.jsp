<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Brand List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #E9EEF5;
            min-height: 100vh;
        }

        .card {
            border-radius: 16px;
        }

        .btn {
            border-radius: 10px;
        }

        .table th,
        .table td {
            vertical-align: middle;
        }

        .form-switch .form-check-input {
            width: 45px;
            height: 22px;
            cursor: pointer;
        }
    </style>
</head>

<body>

<nav class="navbar navbar-dark bg-dark shadow">
    <div class="container-fluid">

        <span class="navbar-brand fw-bold">
            Brand Management
        </span>

        <div class="d-flex gap-2">

            <a href="/"
               class="btn btn-outline-light btn-sm">
                Home
            </a>

            <a href="/brand/add"
               class="btn btn-light btn-sm fw-semibold">
                + Add Brand
            </a>

        </div>

    </div>
</nav>

<div class="container mt-5">

    <div class="card shadow p-3">

        <h3 class="fw-bold mb-3 text-center">
            Brand List
        </h3>

        <div class="table-responsive">

            <table class="table table-hover table-striped mb-0">

                <thead class="table-dark">
                    <tr>
                        <th>Identifier</th>
                        <th>Brand Name</th>
                        <th>Description</th>
                        <th>Status</th>
                        <th class="text-center">Actions</th>
                    </tr>
                </thead>

                <tbody>

                    <c:if test="${empty brands}">
                        <tr>
                            <td colspan="5"
                                class="text-center py-4 text-muted">
                                No brands found.
                            </td>
                        </tr>
                    </c:if>

                    <c:forEach items="${brands}" var="brand">

                        <tr>
                            <td class="fw-semibold">
                                ${brand.identifier}
                            </td>

                            <td>
                                ${brand.brandName}
                            </td>

                            <td>
                                ${brand.description}
                            </td>

                            <td class="text-center">
                                <div class="form-check form-switch">
                            <input class="form-check-input" type="checkbox"
                            onclick="toggleBrand('${brand.identifier}')"
                            <c:if test="${brand.status}">checked</c:if> />
                                </div>
                            </td>

                            <td class="text-center">

                                <a href="/brand/get?identifier=${brand.identifier}"
                                   class="btn btn-sm btn-outline-primary me-2">
                                    Update
                                </a>

                                <a href="/brand/delete?identifier=${brand.identifier}"
                                   class="btn btn-sm btn-outline-danger"
                                   onclick="return confirm('Are you sure you want to delete this brand?');">
                                    Delete
                                </a>

                            </td>

                        </tr>

                    </c:forEach>

                </tbody>

            </table>

        </div>

    </div>

</div>

<script>
    function toggleBrand(identifier) {
        window.location.href = "/brand/toggleStatus?identifier=" + identifier;
    }
</script>

</body>
</html>