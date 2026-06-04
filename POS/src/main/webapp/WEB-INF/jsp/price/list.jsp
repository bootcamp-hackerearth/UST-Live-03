<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Price List</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f1f5f9;
            min-height: 100vh;
            padding: 30px;
        }

        .container {
            width: 85%;
            margin: auto;
            background: #ffffff;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);
            border-top: 6px solid #1e293b;
        }

        h2 {
            text-align: center;
            margin-bottom: 24px;
            color: #1e293b;
            font-size: 22px;
            font-weight: 700;
        }

        .top-bar {
            display: flex;
            justify-content: space-between;
            margin-bottom: 20px;
        }

        .btn {
            padding: 10px 16px;
            border-radius: 6px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            color: #ffffff;
            background-color: #1e293b;
        }

        .btn-edit {
            background-color: #2563eb;
        }

        .btn-edit:hover {
            background-color: #1e40af;
        }

        .btn-delete {
            background-color: #dc2626;
        }

        .btn-delete:hover {
            background-color: #b91c1c;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background-color: #1e293b;
            color: #ffffff;
            padding: 12px;
            font-size: 14px;
            text-align: center;
        }

        td {
            padding: 12px;
            font-size: 14px;
            color: #334155;
            text-align: center;
            border-bottom: 1px solid #e2e8f0;
        }

        tr:nth-child(even) {
            background-color: #f8fafc;
        }

        .action-cell {
            display: flex;
            justify-content: center;
            gap: 10px;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="top-bar">
        <a href="${pageContext.request.contextPath}/" class="btn">
            Home
        </a>

        <a href="${pageContext.request.contextPath}/price/add" class="btn">
            + Add Price
        </a>
    </div>

    <h2>Price List</h2>

    <table>
        <tr>
            <th>ID</th>
            <th>Identifier</th>
            <th>Product</th>
            <th>Type</th>
            <th>Amount</th>
            <th>Currency</th>
            <th>Action</th>
        </tr>

        <c:forEach var="price" items="${prices}">
            <tr>
                <td>${price.id}</td>
                <td>${price.identifier}</td>
                <td>${price.product}</td>
                <td>${price.type}</td>
                <td>${price.amount}</td>
                <td>${price.currency}</td>

                <td class="action-cell">
                    <a
                        href="${pageContext.request.contextPath}/price/get?identifier=${price.identifier}"
                        class="btn btn-edit"
                    >
                        Edit
                    </a>

                    <a
                        href="${pageContext.request.contextPath}/price/delete?identifier=${price.identifier}"
                        class="btn btn-delete"
                        onclick="return confirm('Are you sure you want to delete this price?');"
                    >
                        Delete
                    </a>
                </td>
            </tr>
        </c:forEach>

        <c:if test="${empty prices}">
            <tr>
                <td colspan="7" style="text-align:center;">
                    No prices available
                </td>
            </tr>
        </c:if>

    </table>

</div>

</body>
</html>
