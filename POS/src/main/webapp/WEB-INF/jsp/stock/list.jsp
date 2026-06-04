<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Retail POS | Stock List</title>

<link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

<style>
    :root {
        --primary: #0f172a;
        --bg-light: #f8fafc;
        --text-main: #0f172a;
        --text-muted: #64748b;
        --border: #e2e8f0;
        --card-bg: #ffffff;
    }

    body {
        margin: 0;
        min-height: 100vh;
        font-family: system-ui, sans-serif;
        background: var(--bg-light);
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 40px;
    }

    .container {
        background: var(--card-bg);
        width: 100%;
        max-width: 1000px;
        padding: 40px;
        border-radius: 12px;
        border-top: 5px solid var(--primary);
        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
    }

    h2 {
        text-align: center;
        margin-bottom: 20px;
    }

    table {
        width: 100%;
        border-collapse: collapse;
    }

    th, td {
        padding: 14px;
        border-bottom: 1px solid var(--border);
        text-align: center;
        font-size: 14px;
    }

    th {
        background: #f1f5f9;
        font-size: 12px;
        text-transform: uppercase;
    }

    .out {
        color: #dc2626;
        font-weight: 600;
    }

    .low {
        color: #f59e0b;
        font-weight: 600;
    }

    .ok {
        color: #16a34a;
        font-weight: 600;
    }

    .action-icon {
        color: var(--primary);
        margin: 0 8px;
        text-decoration: none;
        font-size: 16px;
    }

    .action-icon:hover {
        color: black;
    }

    .alert {
        background: #fef3c7;
        border: 1px solid #fde68a;
        color: #92400e;
        padding: 14px;
        border-radius: 6px;
        text-align: center;
    }

    .footer-links {
        display: flex;
        justify-content: space-between;
        margin-top: 25px;

    }

    .footer-links a {
        padding: 10px 18px;
        background: var(--primary);
        color: white;
        text-decoration: none;
        border-radius: 6px;
        font-size: 14px;
        font-weight: 500;
    }

    .footer-links a:hover {
        background: #020617;
    }
</style>
</head>

<body>

<div class="container">

    <h2>Stock Inventory</h2>

    <c:if test="${empty stock}">
        <div class="alert">No stock records found</div>
    </c:if>

    <c:if test="${not empty stock}">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Identifier</th>
                    <th>Quantity</th>
                    <th>Minimum Stock</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>

            <tbody>
            <c:forEach var="s" items="${stock}">
                <tr>
                    <td>${s.id}</td>
                    <td>${s.identifier}</td>
                    <td>${s.quantity}</td>
                    <td>${s.minimumstock}</td>

                    <td>
                        <c:choose>
                            <c:when test="${s.quantity == 0}">
                                <span class="out">Out of Stock</span>
                            </c:when>
                            <c:when test="${s.quantity lt s.minimumstock}">
                                <span class="low">low stock</span>
                            </c:when>
                            <c:otherwise>
                                <span class="ok">Available</span>
                            </c:otherwise>
                        </c:choose>
                    </td>

                    <td>
                        <a class="action-icon"
                           href="${pageContext.request.contextPath}/stock/get?identifier=${s.identifier}">
                            <i class="fa-solid fa-pen"></i>
                        </a>

                        <a class="action-icon"
                           href="${pageContext.request.contextPath}/stock/delete?identifier=${s.identifier}"
                           onclick="return confirm('Delete this stock?');">
                            <i class="fa-solid fa-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <div class="footer-links">
        <a href="/">Home</a>
        <a href="${pageContext.request.contextPath}/stock/add">Add New Stock</a>
    </div>

</div>

</body>
</html>