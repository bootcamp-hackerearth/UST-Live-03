<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Price List</title>

    <style>
        body {
            font-family: "Segoe UI", Arial, sans-serif;
            background: #F6F7F9;
            padding: 30px;
        }

        h2 {
            text-align: center;
            color: #111827;
            margin-bottom: 20px;
            font-weight: 600;
        }

        .top-actions {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
            margin-bottom: 15px;
        }

        .top-actions button {
            padding: 8px 14px;
            border: none;
            border-radius: 8px;
            font-size: 13px;
            cursor: pointer;
            font-weight: 500;
        }

        .add-btn {
            background: #2B2B2B;
            color: white;
        }

        .add-btn:hover {
            background: #111111;
        }

        .home-btn {
            background: #E5E7EB;
            color: #111827;
        }

        .home-btn:hover {
            background: #D1D5DB;
        }

        .table-card {
            background: #FFFFFF;
            border-radius: 16px;
            padding: 20px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.06);
            border: 1px solid #E5E7EB;
        }

        table {
            border-collapse: collapse;
            width: 100%;
        }

        th, td {
            padding: 12px;
            text-align: center;
            font-size: 14px;
        }

        th {
            background: #2B2B2B;
            color: #FFFFFF;
        }

        tr:nth-child(even) {
            background: #F9FAFB;
        }

        .edit-btn {
            padding: 6px 12px;
            background: #2B2B2B;
            color: white;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            margin-right: 6px;
        }

        .delete-btn {
            padding: 6px 12px;
            background: #B91C1C;
            color: white;
            border: none;
            border-radius: 8px;
            cursor: pointer;
        }
    </style>
</head>

<body>

<h2>Price List</h2>

<div class="top-actions">
    <a href="/price/add">
        <button class="add-btn">Add Price</button>
    </a>
    <a href="/">
        <button class="home-btn">Home</button>
    </a>
</div>

<c:if test="${not empty message}">
    <div class="msg">${message}</div>
</c:if>

<div class="table-card">
    <table>
        <tr>
            <th>ID</th>
            <th>Identifier</th>
            <th>Product</th>
            <th>Price</th>
            <th>Price Type</th>
            <th>Action</th>
        </tr>

        <c:forEach items="${prices}" var="price">
            <tr>
                <td>${price.id}</td>
                <td>${price.identifier}</td>
                <td>${price.product}</td>
                <td>${price.priceAmount}</td>
                <td>${price.priceType}</td>
                <td>
                    <a href="/price/get?identifier=${price.identifier}">
                        <button class="edit-btn">Edit</button>
                    </a>
                    <a href="/price/delete?identifier=${price.identifier}"
                       onclick="return confirm('Are you sure?');">
                        <button class="delete-btn">Delete</button>
                    </a>
                </td>
            </tr>
        </c:forEach>

    </table>
</div>

</body>
</html>