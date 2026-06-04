<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
      rel="stylesheet">

<style>
    body {
        background: #2f2f2f;
        min-height: 100vh;
        font-family: "Segoe UI", sans-serif;
        padding: 40px;
    }

    .container {
        max-width: 900px;
    }

    .card {
        border: none;
        border-radius: 20px;
        background-color: #ffffff;
        box-shadow: 0 15px 40px rgba(0, 0, 0, 0.25);
    }

    .card-header {
        background: transparent;
        border-bottom: none;
        padding: 24px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .card-header h2 {
        font-size: 24px;
        font-weight: 600;
        color: #333;
        margin: 0;
    }

    .header-actions {
        display: flex;
        gap: 12px;
    }

    .btn-home {
        background-color: #6c757d;
        color: #fff;
        border-radius: 20px;
        font-weight: 600;
    }

    .btn-add {
        background: linear-gradient(90deg, #4facfe, #00f2fe);
        color: #fff;
        border-radius: 20px;
        font-weight: 600;
    }

    table th {
        font-weight: 600;
        font-size: 0.9rem;
        color: #444;
    }

    table td {
        font-size: 0.9rem;
    }

    .card-footer {
        text-align: center;
        font-size: 0.85rem;
        color: #555;
        padding: 14px;
        border-top: 1px solid #eee;
    }

    .toggle-switch {
        position: relative;
        width: 50px;
        height: 26px;
        display: inline-block;
    }

    .toggle-switch input {
        opacity: 0;
        width: 0;
        height: 0;
    }

    .slider {
        position: absolute;
        cursor: pointer;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background-color: #dc3545;
        transition: 0.3s;
        border-radius: 30px;
    }

    .slider:before {
        position: absolute;
        content: "";
        height: 20px;
        width: 20px;
        left: 3px;
        bottom: 3px;
        background-color: white;
        transition: 0.3s;
        border-radius: 50%;
    }

    input:checked + .slider {
        background-color: #198754;
    }

    input:checked + .slider:before {
        transform: translateX(24px);
    }
</style>

<div class="container">
    <div class="card">

        <div class="card-header">

            <h2>Product List</h2>

            <div class="header-actions">
                <a href="${pageContext.request.contextPath}/"
                   class="btn btn-home">
                    Home
                </a>

                <a href="${pageContext.request.contextPath}/product/add"
                   class="btn btn-add">
                    + Add Product
                </a>
            </div>
        </div>

        <div class="card-body">

            <c:if test="${empty products}">
                <div class="alert alert-info text-center">
                    No products available
                </div>
            </c:if>

            <c:if test="${not empty products}">
                <table class="table table-bordered table-hover">
                    <thead class="table-light">
                    <tr>
                        <th>Identifier</th>
                        <th>Product Name</th>
                        <th>Category</th>
                        <th>Brand</th>
                        <th>Model</th>
                        <th>Unit</th>
                        <th>Status</th>
                        <th style="width: 160px;">Actions</th>
                    </tr>
                    </thead>

                    <tbody>
                    <c:forEach var="product" items="${products}">
                        <tr>
                            <td>${product.identifier}</td>
                            <td>${product.name}</td>
                            <td>${product.category}</td>
                            <td>${product.brand}</td>
                            <td>${product.model}</td>
                            <td>${product.unit}</td>

                            <td>
                                <form method="get"
                                      action="${pageContext.request.contextPath}/product/toggleStatus">
                                    <input type="hidden"
                                           name="identifier"
                                           value="${product.identifier}"/>

                                    <label class="toggle-switch">
                                        <input type="checkbox"
                                               name="status"
                                               onchange="this.form.submit()"
                                               <c:if test="${product.status}">checked</c:if>>
                                        <span class="slider"></span>
                                    </label>
                                </form>
                            </td>

                            <td>
                                <a href="${pageContext.request.contextPath}/product/get?identifier=${product.identifier}"
                                   class="btn btn-sm btn-warning">
                                    Edit
                                </a>

                                <a href="${pageContext.request.contextPath}/product/delete?identifier=${product.identifier}"
                                   class="btn btn-sm btn-danger"
                                   onclick="return confirm('Are you sure?');">
                                    Delete
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>

                </table>
            </c:if>

        </div>

        <div class="card-footer">
            POS Management System
        </div>

    </div>
</div>