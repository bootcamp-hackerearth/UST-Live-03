<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Stock Management</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #ffffff;
        }

        /* ===== CONTAINER ===== */
        .container {
            width: 95%;
            max-width: 1100px;
            margin: 40px auto;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            padding: 18px;
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
            font-size: 22px;
            margin-bottom: 12px;
        }

        /* ===== ACTIONS ===== */
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
            color: #ffffff;
            text-decoration: none;
            border-radius: 18px;
            font-size: 13px;
            font-weight: 600;
        }

        /* ===== TABLE ===== */
        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 12px;
            text-align: center;
        }

        th {
            background-color: #f1f5f9;
            font-size: 13px;
            font-weight: 700;
            border-bottom: 1px solid #e5e7eb;
        }

        td {
            border-bottom: 1px solid #e5e7eb;
            font-size: 13px;
        }

        tr:hover {
            background: #f8fafc;
        }

        /* ===== ACTION BUTTONS ===== */
        .action-link {
            padding: 6px 12px;
            border-radius: 18px;
            font-size: 12px;
            font-weight: 600;
            color: #ffffff;
            text-decoration: none;
        }

        .edit {
            background-color: teal;
        }

        .delete {
            background-color: #ef4444;
            margin-left: 6px;
        }

        .empty {
            text-align: center;
            padding: 18px;
            font-size: 14px;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="app-title">POS Application</div>

    <h2>Stock Management</h2>

    <div class="list-actions">

        <a href="${pageContext.request.contextPath}/" class="home-btn">
            Home
        </a>

        <a href="${pageContext.request.contextPath}/stocks/add" class="add-btn">
            Add Stock
        </a>

    </div>

    <c:if test="${empty stocks}">
        <div class="empty">No stock records found</div>
    </c:if>

    <c:if test="${not empty stocks}">

        <table>

            <thead>
            <tr>
                <th>Sku Code</th>
                <th>Product Name</th>
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
                    <td>${stock.name}</td>
                    <td>${stock.availableStock}</td>
                    <td>${stock.incomingStock}</td>
                    <td>${stock.outgoingStock}</td>
                    <td>${stock.productStatus}</td>
                    <td>${stock.wareHouse}</td>

                    <td>

                        <a href="${pageContext.request.contextPath}/stocks/get?identifier=${stock.identifier}"
                           class="action-link edit">
                            Edit
                        </a>

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