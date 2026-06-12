<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Product Management</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #ffffff;
            color: #020617;
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

        /* ===== ACTION BAR ===== */
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

        /* ===== STATUS TOGGLE ===== */
        .status-toggle {
            padding: 5px 12px;
            border-radius: 18px;
            font-size: 12px;
            font-weight: 600;
            color: white;
            border: none;
            cursor: pointer;
        }

        .status-true {
            background-color: teal;
        }

        .status-false {
            background-color: #9ca3af;
        }

        /* ===== ACTION BUTTONS ===== */
        .action-link {
            padding: 6px 12px;
            border-radius: 18px;
            text-decoration: none;
            font-weight: 600;
            font-size: 12px;
            color: #ffffff;
        }

        .edit {
            background: teal;
        }

        .delete {
            background: #ef4444;
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
    <h2>Product Management</h2>

    <div class="list-actions">
        <a href="${pageContext.request.contextPath}/" class="home-btn">Home</a>

        <a href="${pageContext.request.contextPath}/product/add" class="add-btn">
            Add Product
        </a>
    </div>

    <c:choose>

        <c:when test="${empty products}">
            <div class="empty">No products found</div>
        </c:when>

        <c:otherwise>

            <table>

                <thead>
                <tr>
                    <th>Sku Code</th>
                    <th>Category</th>
                    <th>Product Name</th>
                    <th>Brand</th>
                    <th>Model</th>
                    <th>Unit</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                </thead>

                <tbody>

                <c:forEach var="product" items="${products}">
                    <tr>

                        <td>${product.identifier}</td>
                        <td>${product.category}</td>
                        <td>${product.name}</td>
                        <td>${product.brand}</td>
                        <td>${product.model}</td>
                        <td>${product.unit}</td>

                        <!-- STATUS -->
                        <td>

                            <button
                                class="status-toggle ${product.status ? 'status-true' : 'status-false'}"
                                onclick="toggleStatus('${product.identifier}')">

                                ${product.status ? 'Active' : 'Inactive'}

                            </button>

                        </td>

                        <!-- ACTION -->
                        <td>

                            <a class="action-link edit"
                               href="${pageContext.request.contextPath}/product/get/${product.identifier}">
                                Edit
                            </a>

                            <a class="action-link delete"
                               href="${pageContext.request.contextPath}/product/delete/${product.identifier}"
                               onclick="return confirm('Delete this product?');">
                                Delete
                            </a>

                        </td>

                    </tr>
                </c:forEach>

                </tbody>

            </table>

        </c:otherwise>

    </c:choose>

</div>

<script>

function toggleStatus(identifier) {

    fetch('${pageContext.request.contextPath}/product/toggle-status?identifier=' + identifier, {
        method: 'POST'
    }).then(() => location.reload());

}

</script>

</body>
</html>