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
            background-color: #f6f7f9;
        }

        .topbar {
            height: 56px;
            background-color: #020617;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 20px;
            border-bottom: 1px solid #1e293b;
        }

        .topbar-left {
            display: flex;
            align-items: center;
            gap: 14px;
        }

        .top-title {
            font-size: 16px;
            font-weight: 600;
            color: #e5e7eb;
        }

        .home-btn {
            padding: 6px 14px;
            background-color: #1e293b;
            color: #e5e7eb;
            text-decoration: none;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 600;
            border: 1px solid #334155;
        }

        .home-btn:hover {
            background-color: #334155;
        }

        .logout-btn {
            background: #dc2626;
            border: none;
            color: white;
            padding: 7px 16px;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
        }

        .container {
            width: 95%;
            max-width: 1100px;
            margin: 32px auto;
            background: #ffffff;
            padding: 26px;
            border-radius: 12px;
            box-shadow: 0 6px 18px rgba(0,0,0,0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        .list-actions {
            display: flex;
            justify-content: flex-end;
            margin-bottom: 14px;
        }

        .add-btn {
            padding: 7px 16px;
            background-color: #2563eb;
            color: #ffffff;
            text-decoration: none;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
        }

        .add-btn:hover {
            background-color: #1d4ed8;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 15px;
        }

        th, td {
            padding: 14px;
            text-align: center;
            border-bottom: 1px solid #e5e7eb;
        }

        th {
            background-color: #e5e7eb;
            font-weight: 600;
        }

        tbody tr:hover {
            background-color: #f8fafc;
        }

        .action-link {
            padding: 6px 14px;
            border-radius: 6px;
            text-decoration: none;
            font-weight: 600;
            color: #ffffff;
            display: inline-block;
        }

        .edit {
            background-color: #2563eb;
        }

        .delete {
            background-color: #dc2626;
            margin-left: 8px;
        }
    </style>
</head>

<body>

<div class="topbar">
    <div class="topbar-left">
        <div class="top-title">POS Application</div>
        <a href="${pageContext.request.contextPath}/" class="home-btn">Home</a>
    </div>

    <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;">
        <button type="submit" class="logout-btn">Logout</button>
    </form>
</div>

<div class="container">

    <h2>Stock Management</h2>

    <div class="list-actions">
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
                <th>Product Name</th>
                <th>SKU Code</th>
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
<td>${stock.skuCode}</td>
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