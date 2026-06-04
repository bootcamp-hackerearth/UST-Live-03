<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Product List</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
            padding: 40px;
            color: #333;
        }

        .container {
            max-width: 1400px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        h2 { margin: 0; }

        .btn {
            padding: 8px 15px;
            text-decoration: none;
            border-radius: 4px;
            font-size: 13px;
            color: white;
            font-weight: bold;
        }

        .btn-home   { background: #6c757d; }
        .btn-add    { background: #28a745; }
        .btn-edit   { background: #007bff; padding: 6px 12px; }
        .btn-delete { background: #dc3545; padding: 6px 12px; }

        .header-buttons {
            display: flex;
            gap: 8px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background-color: #f8f9fa;
            padding: 12px 15px;
            text-align: left;
            border-bottom: 2px solid #dee2e6;
            white-space: nowrap;
        }

        td {
            padding: 12px 15px;
            border-bottom: 1px solid #eee;
            vertical-align: middle;
        }

        tr:hover { background-color: #fafafa; }

        .empty-msg {
            text-align: center;
            padding: 40px;
            color: #999;
        }

        .badge {
            display: inline-block;
            background: #e7f3ff;
            color: #0056b3;
            padding: 3px 8px;
            margin: 2px;
            border-radius: 4px;
            font-size: 12px;
        }

        .text-muted { color: #999; }

        .action-buttons {
            display: flex;
            gap: 8px;
            align-items: center;
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 50px;
            height: 26px;
        }

        .switch input { opacity: 0; width: 0; height: 0; }

        .slider {
            position: absolute;
            background-color: #dc3545;
            border-radius: 26px;
            top: 0; left: 0; right: 0; bottom: 0;
            transition: 0.3s;
            cursor: pointer;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 20px;
            width: 20px;
            left: 3px;
            bottom: 3px;
            background: white;
            border-radius: 50%;
            transition: 0.3s;
        }

        input:checked + .slider { background-color: #28a745; }
        input:checked + .slider:before { transform: translateX(24px); }

        .toggle-container {
            display: flex;
            justify-content: flex-start;
            align-items: center;
            cursor: pointer;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="header">
        <h2>Product List</h2>
        <div class="header-buttons">
            <a href="/" class="btn btn-home">Home</a>
            <a href="/product/add" class="btn btn-add">+ Add Product</a>
        </div>
    </div>

    <c:if test="${empty products}">
        <div class="empty-msg">No products found</div>
    </c:if>

    <c:if test="${not empty products}">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Identifier</th>
                    <th>Name</th>
                    <th>Categories</th>
                    <th>Brand</th>
                    <th>Model</th>
                    <th>Unit</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>

            <tbody>
            <c:forEach var="product" items="${products}">
                <tr>

                    <td>${product.id}</td>

                    <td>${product.identifier}</td>

                    <td>${product.name}</td>

                    <td>
                        <c:choose>
                            <c:when test="${empty product.categories}">
                                <span class="text-muted">—</span>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="cat" items="${product.categories}">
                                    <span class="badge">${cat}</span>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </td>

                    <td>
                        <c:choose>
                            <c:when test="${empty product.brand}">
                                <span class="text-muted">—</span>
                            </c:when>
                            <c:otherwise>${product.brand}</c:otherwise>
                        </c:choose>
                    </td>

                    <td>
                        <c:choose>
                            <c:when test="${empty product.model}">
                                <span class="text-muted">—</span>
                            </c:when>
                            <c:otherwise>${product.model}</c:otherwise>
                        </c:choose>
                    </td>

                    <td>
                        <c:choose>
                            <c:when test="${empty product.unit}">
                                <span class="text-muted">—</span>
                            </c:when>
                            <c:otherwise>${product.unit}</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <div class="toggle-container"
                             onclick="window.location.href='${pageContext.request.contextPath}/product/toggle?identifier=${product.identifier}'">
                            <label class="switch">
                                <input type="checkbox" ${product.status ? "checked" : ""} disabled>
                                <span class="slider"></span>
                            </label>
                        </div>
                    </td>

                    <td>
                        <div class="action-buttons">
                            <a href="/product/get?identifier=${product.identifier}"
                               class="btn btn-edit">Edit</a>
                            <a href="/product/delete?identifier=${product.identifier}"
                               class="btn btn-delete"
                               onclick="return confirm('Delete this product?')">Delete</a>
                        </div>
                    </td>

                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

</div>

</body>
</html>