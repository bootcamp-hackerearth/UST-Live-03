<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Stock Management</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #ffffff;
        }

        .container {
            width: 95%;
            max-width: 1000px;
            margin: 40px auto;
            background: #ffffff;
            padding: 18px;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }

        .app-title {
            text-align: center;
            font-size: 14px;
            font-weight: 600;
            color: #14b8a6;
            margin-bottom: 4px;
        }

        h2 {
            text-align: center;
            margin-bottom: 12px;
            font-size: 22px;
        }

        .list-actions {
            display: flex;
            justify-content: flex-end;
            gap: 8px;
            margin-bottom: 12px;
        }

        .home-btn {
            padding: 7px 16px;
            background: #ffffff;
            color: teal;
            text-decoration: none;
            border-radius: 18px;
            font-size: 13px;
            font-weight: 600;
            border: 1px solid teal;
        }

        .add-btn {
            padding: 7px 16px;
            background: teal;
            color: white;
            text-decoration: none;
            border-radius: 18px;
            font-size: 13px;
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 13px;
        }

        th, td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid #e5e7eb;
        }

        th {
            background: #f1f5f9;
            font-weight: 700;
        }

        tr {
            line-height: 1.3;
        }

        tbody tr:hover {
            background: #f8fafc;
        }

        .action-link {
            padding: 6px 12px;
            border-radius: 18px;
            font-weight: 600;
            color: white;
            text-decoration: none;
            font-size: 12px;
        }

        .edit { background: teal; }
        .delete { background: #ef4444; margin-left: 6px; }
    </style>
</head>

<body>

<div class="container">
    <div class="app-title">POS Application</div>

    <h2>Stock Management</h2>
    <div class="list-actions">
        <a href="${pageContext.request.contextPath}/" class="home-btn">Home</a>

        <a href="${pageContext.request.contextPath}/stocks/add" class="add-btn">
            Add Stock
        </a>
    </div>

    <c:if test="${empty stocks}">
        <div style="text-align:center;">No stock records found</div>
    </c:if>

    <c:if test="${not empty stocks}">
        <table>
            <thead>
            <tr>
                <th>Product Sku</th>
                <th>Available</th>
                <th>Incoming</th>
                <th>Outgoing</th>
                <th>Status</th>
                <th>Warehouse</th>
                <th>Action</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="stock" items="${stocks}">
                <tr>
                    <td>${stock.identifier}</td>
                    <td>${stock.availableStock}</td>
                    <td>${stock.incomingStock}</td>
                    <td>${stock.outgoingStock}</td>
                    <td>${stock.productStatus}</td>
                    <td>${stock.wareHouse}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/stocks/get?identifier=${stock.identifier}"
                           class="action-link edit">Edit</a>

                        <a href="${pageContext.request.contextPath}/stocks/delete?identifier=${stock.identifier}"
                           class="action-link delete"
                           onclick="return confirm('Are you sure you want to delete this product stock?');">
                           Delete
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>

</body>
</html>