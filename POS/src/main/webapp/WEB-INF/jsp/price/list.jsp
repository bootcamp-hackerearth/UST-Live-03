<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Price List</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet"/>

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', Arial, sans-serif;
            background-color: #f4f6fb;
        }

        .container {
            width: 92%;
            margin: 40px auto;
            background-color: #ffffff;
            padding: 24px;
            border-radius: 12px;
            box-shadow: 0 12px 30px rgba(0,0,0,0.1);
        }

        h2 {
            text-align: center;
            color: #4e73df;
            margin-bottom: 24px;
            font-weight: 700;
        }

        .top-bar {
            display: flex;
            justify-content: space-between;
            margin-bottom: 16px;
        }

        .home-btn, .add-btn {
            padding: 10px 16px;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 600;
            text-decoration: none;
        }

        .home-btn {
            border: 2px solid #4e73df;
            color: #4e73df;
        }

        .add-btn {
            background-color: #4e73df;
            color: #ffffff;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background-color: #4e73df;
            color: white;
            padding: 12px;
        }

        td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid #e0e4ee;
        }

        .action-btn {
            padding: 6px 12px;
            border-radius: 6px;
            color: #fff;
            font-weight: 600;
            font-size: 13px;
            text-decoration: none;
        }

        .edit-btn { background-color: #1cc88a; }
        .delete-btn { background-color: #e74a3b; }
    </style>
</head>

<body>
<div class="container">
    <h2>Price List</h2>
    <div class="top-bar">
        <a href="${pageContext.request.contextPath}/" class="home-btn">Home</a>
        <a href="${pageContext.request.contextPath}/price/add" class="add-btn">+ Add Price</a>
    </div>
    <table>
        <thead>
        <tr>
            <th>Identifier</th>
            <th>Product</th>
            <th>Price Type</th>
            <th>Amount</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="p" items="${prices}">
            <tr>
                <td>${p.identifier}</td>
                <!-- Product identifier (or name if you later map it) -->
                <td>${p.product}</td>
                <!-- MRP / SELLING / DISCOUNT -->
                <td>${p.priceType}</td>
                <!-- Amount -->
                <td>${p.amount}</td>
                <td>
                    <a class="action-btn edit-btn"
                       href="${pageContext.request.contextPath}/price/get?identifier=${p.identifier}">
                        Update
                    </a>
                    <a class="action-btn delete-btn"
                       href="${pageContext.request.contextPath}/price/delete?identifier=${p.identifier}"
                       onclick="return confirm('Delete this price?');">
                        Delete
                    </a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>