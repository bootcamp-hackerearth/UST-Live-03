<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Product List</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
            padding: 20px;
        }

        h2 {
            margin-bottom: 15px;
        }

        .btn {
            padding: 7px 14px;
            text-decoration: none;
            border-radius: 4px;
            color: #fff;
            font-size: 14px;
            margin-right: 5px;
            display: inline-block;
        }

        .btn-home { background-color: #6c757d; }
        .btn-add { background-color: #28a745; }
        .btn-edit { background-color: #007bff; }
        .btn-delete { background-color: #dc3545; }

        table {
            width: 100%;
            border-collapse: collapse;
            background: #fff;
        }

        th, td {
            padding: 12px;
            border-bottom: 1px solid #ddd;
            text-align: center;
        }

        th {
            background-color: #007bff;
            color: #fff;
        }

        tr:hover {
            background-color: #f1f1f1;
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 46px;
            height: 24px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            cursor: pointer;
            inset: 0;
            background-color: #dc3545;
            transition: .4s;
            border-radius: 34px;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            transition: .4s;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: #28a745;
        }

        input:checked + .slider:before {
            transform: translateX(22px);
        }

        .no-data {
            font-style: italic;
            text-align: center;
        }
    </style>
</head>

<body>

<h2>Product List</h2>

<!--  HOME & ADD PRODUCT -->
<a href="/" class="btn btn-home">
    Home
</a>

<a href="${pageContext.request.contextPath}/product/get"
   class="btn btn-add">
    + Add Product
</a>

<br/><br/>

<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Identifier</th>
        <th>Description</th>
        <th>Brand</th>
        <th>Category</th>
        <th>Status</th>
        <th>Action</th>
    </tr>
    </thead>

    <tbody>
    <c:forEach var="product" items="${productDto}">
        <tr>
            <td>${product.id}</td>
            <td>${product.identifier}</td>
            <td>${product.description}</td>
            <td>${product.brand}</td>
            <td>${product.category}</td>

            <!-- STATUS TOGGLE -->
            <td>
                <form method="get"
                      action="${pageContext.request.contextPath}/product/toggle">
                    <input type="hidden" name="identifier"
                           value="${product.identifier}" />

                    <label class="switch">
                        <input type="checkbox"
                               name="status"
                               value="true"
                               onchange="this.form.submit()"
                               <c:if test="${product.status}">checked</c:if>>
                        <span class="slider"></span>
                    </label>
                </form>
            </td>

            <td>
                <a href="${pageContext.request.contextPath}/product/update?id=${product.id}"
                   class="btn btn-edit">
                    Edit
                </a>

                <a href="${pageContext.request.contextPath}/product/delete?id=${product.id}"
                   class="btn btn-delete"
                   onclick="return confirm('Are you sure you want to delete this product?');">
                    Delete
                </a>
            </td>
        </tr>
    </c:forEach>

    <c:if test="${empty productDto}">
        <tr>
            <td colspan="6" class="no-data">
                No products found
            </td>
        </tr>
    </c:if>
    </tbody>
</table>

</body>
</html>