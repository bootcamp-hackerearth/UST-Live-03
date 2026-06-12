<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Price Management</title>

    <style>
        body { margin:0; font-family:"Segoe UI", Roboto, Arial, sans-serif; background:#f6f7f9; }

        /* TOP BAR */
        .topbar {
            height:56px; background:#020617; display:flex;
            justify-content:space-between; align-items:center;
            padding:0 20px; border-bottom:1px solid #1e293b;
        }
        .topbar-left { display:flex; gap:14px; align-items:center; }
        .top-title { color:#e5e7eb; font-weight:600; }
        .home-btn {
            padding:6px 14px; background:#1e293b; color:#e5e7eb;
            text-decoration:none; border-radius:6px;
        }
        .logout-btn {
            background:#dc2626; color:white; border:none;
            padding:7px 16px; border-radius:6px; font-weight:600;
            cursor: pointer;
        }

        /* PAGE */
        .container {
            width:95%; max-width:1100px; margin:28px auto;
            background:#fff; padding:26px; border-radius:12px;
            box-shadow:0 6px 18px rgba(0,0,0,0.08);
        }

        .actions { text-align:right; margin-bottom:14px; }
        .add-btn {
            background:#2563eb; color:white;
            padding:7px 16px; border-radius:6px;
            text-decoration:none; font-weight:600;
        }

        table { width:100%; border-collapse:collapse; }
        th, td { padding:14px; border-bottom:1px solid #e5e7eb; text-align:center; }
        th { background:#e5e7eb; }

        .action-link {
            padding:6px 14px; border-radius:6px;
            color:white; text-decoration:none; font-weight:600;
        }
        .edit { background:#2563eb; }
        .delete { background:#dc2626; margin-left:8px; }
    </style>
</head>

<body>

<!-- TOP BAR -->
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
    <h2 style="text-align:center;">Price Management</h2>

    <div class="actions">
        <a href="${pageContext.request.contextPath}/price/add" class="add-btn">Add Price</a>
    </div>

    <c:if test="${empty prices}">
        <div style="text-align:center;">No price records found</div>
    </c:if>

    <c:if test="${not empty prices}">
        <table>
            <tr>
                <th>Sku Code</th>
                <th>MRP</th>
                <th>Selling Price</th>
                <th>Cost Price</th>
                <th>Effective From</th>
                <th>Action</th>
            </tr>

            <c:forEach var="price" items="${prices}">
                <tr>
    <td>${price.identifier}</td>
    <td>${price.mrp}</td>
    <td>${price.sellingPrice}</td>
    <td>${price.costPrice}</td>
    <td>${price.effectiveFrom}</td>
    <td>
        <a href="${pageContext.request.contextPath}/price/get?identifier=${price.identifier}"
           class="action-link edit">Edit</a>

        <a href="${pageContext.request.contextPath}/price/delete?identifier=${price.identifier}"
           class="action-link delete"
           onclick="return confirm('Delete price for this product?');">
                           Delete</a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

</div>

</body>
</html>