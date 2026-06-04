<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Stock List</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 30px;
        }

        h2 {
            margin-bottom: 15px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 10px;
            border: 1px solid #ddd;
            text-align: center;
        }

        th {
            background-color: #f4f4f4;
        }

        .btn {
            padding: 6px 12px;
            text-decoration: none;
            border-radius: 4px;
            color: white;
            font-size: 14px;
            margin-right: 8px;
            display: inline-block;
        }

        .btn-home {
            background-color: #6c757d;
        }

        .btn-add {
            background-color: #28a745;
        }

        .btn-edit {
            background-color: #007bff;
        }

        .btn-delete {
            background-color: #dc3545;
        }

        .message {
            margin-bottom: 15px;
            font-weight: bold;
        }

        .success { color: green; }
        .error { color: red; }

        /*  STATUS TOGGLE SWITCH */
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
            background-color: #dc3545; /* OFF */
            transition: 0.4s;
            border-radius: 30px;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            transition: 0.4s;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: #28a745; /* ON */
        }

        input:checked + .slider:before {
            transform: translateX(22px);
        }
    </style>
</head>

<body>

<h2>Stock List</h2>

<!--  FLASH MESSAGE -->
<c:if test="${not empty message}">
    <div class="message ${success ? 'success' : 'error'}">
        ${message}
    </div>
</c:if>

<!--  HOME + ADD BUTTONS -->
<div style="margin-bottom: 15px;">
    <a href="${pageContext.request.contextPath}/" class="btn btn-home">
        Home
    </a>

    <a href="${pageContext.request.contextPath}/stock/add" class="btn btn-add">
        Add Stock
    </a>
</div>

<table>
    <thead>
        <tr>
            <th>ID</th>
            <th>Product(Identifier)</th>
            <th>Warehouse</th>
            <th>Quantity</th>
            <th>Unit Price</th>
            <th>Status</th>
            <th>Actions</th>
        </tr>
    </thead>

    <tbody>
        <c:forEach var="stock" items="${stockList}">
            <tr>
                <td>${stock.id}</td>
                <td>${stock.identifier}</td>
                <td>${stock.warehouse}</td>
                <td>${stock.quantity}</td>
                <td>${stock.unitPrice}</td>

                <!--  STATUS TOGGLE -->
                <td>
                    <form method="get"
                          action="${pageContext.request.contextPath}/stock/toggle">
                        <input type="hidden" name="identifier"
                               value="${stock.identifier}" />

                        <label class="switch">
                            <input type="checkbox"
                                   name="status"
                                   value="true"
                                   onchange="this.form.submit()"
                                   <c:if test="${stock.status}">checked</c:if> />
                            <span class="slider"></span>
                        </label>
                    </form>
                </td>

                <!-- ACTIONS -->
                <td>
                    <a href="${pageContext.request.contextPath}/stock/update?id=${stock.id}"
                       class="btn btn-edit">
                        Edit
                    </a>

                    <a href="${pageContext.request.contextPath}/stock/delete?id=${stock.id}"
                       class="btn btn-delete"
                       onclick="return confirm('Are you sure you want to delete this stock?');">
                        Delete
                    </a>
                </td>
            </tr>
        </c:forEach>

        <!--  NO DATA -->
        <c:if test="${empty stockList}">
            <tr>
                <td colspan="8">No stock records found.</td>
            </tr>
        </c:if>
    </tbody>
</table>

</body>
</html>