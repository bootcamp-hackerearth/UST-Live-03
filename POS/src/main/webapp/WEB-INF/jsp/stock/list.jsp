<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Stock List</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card-container {
            width: 900px;
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #4b6cb7;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background: #4b6cb7;
            color: white;
            padding: 12px;
            text-align: center;
        }

        td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid #eee;
        }

        tr:hover {
            background: #f7f9ff;
        }

        .action-icon {
            font-size: 18px;
            margin: 0 6px;
            text-decoration: none;
            color: #4b6cb7;
        }

        .alert {
            padding: 10px;
            border-radius: 8px;
            margin-bottom: 15px;
            text-align: center;
            background: #fff3cd;
            color: #856404;
        }

        .footer-actions {
            margin-top: 20px;
            display: flex;
            justify-content: center;
            gap: 12px;
        }

        .btn {
            padding: 10px 16px;
            border-radius: 10px;
            text-decoration: none;
            font-weight: 600;
            font-size: 14px;
        }

        .btn-home {
            background: #6c757d;
            color: white;
        }

        .btn-add {
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
        }

        /* Toggle Switch */
        .toggle-switch {
            position: relative;
            width: 52px;
            height: 26px;
            display: inline-block;
        }

        .toggle-switch input {
            display: none;
        }

        .toggle-slider {
            position: absolute;
            inset: 0;
            background-color: #e74c3c;
            border-radius: 30px;
            transition: .3s;
            cursor: pointer;
        }

        .toggle-slider:before {
            content: "";
            position: absolute;
            height: 20px;
            width: 20px;
            left: 3px;
            bottom: 3px;
            background: white;
            border-radius: 50%;
            transition: .3s;
        }

        .toggle-switch input:checked + .toggle-slider {
            background-color: #2ecc71;
        }

        .toggle-switch input:checked + .toggle-slider:before {
            transform: translateX(26px);
        }
    </style>
</head>

<body>

<div class="card-container">

    <h2>Stock List</h2>

    <c:if test="${empty stocks}">
        <div class="alert">No stock records found</div>
    </c:if>

    <c:if test="${not empty stocks}">
        <table>
            <thead>
            <tr>
                <th>Stock</th>
                <th>Product</th>
                <th>Warehouse</th>
                <th>Quantity</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="stock" items="${stocks}">
                <tr>
                    <td>${stock.identifier}</td>
                    <td>${stock.productName}</td>
                    <td>${stock.warehouseName}</td>
                    <td>${stock.noOfProducts}</td>

                    <td>
                        <form action="${pageContext.request.contextPath}/stock/toggleStatus"
                              method="post"
                              style="margin:0;">

                            <input type="hidden"
                                   name="identifier"
                                   value="${stock.identifier}" />

                            <label class="toggle-switch">
                                <input type="checkbox"
                                       ${stock.status ? "checked" : ""}
                                       onchange="this.form.submit()" />
                                <span class="toggle-slider"></span>
                            </label>

                        </form>
                    </td>

                    <td>
                        <a class="action-icon"
                           title="Edit"
                           href="${pageContext.request.contextPath}/stock/get?identifier=${stock.identifier}">
                            ✏️
                        </a>

                        <a class="action-icon"
                           title="Delete"
                           href="${pageContext.request.contextPath}/stock/delete?identifier=${stock.identifier}"
                           onclick="return confirm('Are you sure you want to delete this stock?');">
                            🗑
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <div class="footer-actions">
        <a href="${pageContext.request.contextPath}/" class="btn btn-home">
            Home
        </a>

        <a href="${pageContext.request.contextPath}/stock/add" class="btn btn-add">
            + Add Stock
        </a>
    </div>

</div>

</body>
</html>