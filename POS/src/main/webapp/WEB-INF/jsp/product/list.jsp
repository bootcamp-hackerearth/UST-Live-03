<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Product Management</title>

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
            font-size: 22px;
            margin-bottom: 12px;
        }

        .actions {
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
            background: teal;
            color: white;
            text-decoration: none;
            padding: 7px 16px;
            border-radius: 18px;
            font-weight: 600;
            font-size: 13px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 13px;
        }

        th, td {
            padding: 12px;
            border-bottom: 1px solid #e5e7eb;
            text-align: center;
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
            color: white;
            text-decoration: none;
            font-weight: 600;
            font-size: 12px;
        }

        .edit { background: teal; }
        .delete { background: #ef4444; margin-left: 6px; }

        .status-btn {
            border: none;
            padding: 5px 12px;
            border-radius: 18px;
            font-size: 12px;
            font-weight: 600;
            color: white;
            cursor: pointer;
        }

        .status-btn.active { background: teal; }
        .status-btn.inactive { background: #9ca3af; }
    </style>
</head>

<body>

<div class="container">

    <div class="app-title">POS Application</div>

    <h2>Product Management</h2>
    <div class="actions">

        <a href="${pageContext.request.contextPath}/" class="home-btn">Home</a>

        <a href="${pageContext.request.contextPath}/product/add" class="add-btn">
            Add Product
        </a>
    </div>

    <c:if test="${empty products}">
        <div style="text-align:center;">No products found</div>
    </c:if>

    <c:if test="${not empty products}">
        <table>
            <tr>
                <th>Product Skucode</th>
                <th>Category</th>
                <th>Product name</th>
                <th>Brand</th>
                <th>Model</th>
                <th>Unit</th>
                <th>Status</th>
                <th>Action</th>
            </tr>

            <c:forEach var="product" items="${products}">
                <tr>
                    <td>${product.identifier}</td>
                    <td>${product.category}</td>
                    <td>${product.name}</td>
                    <td>${product.brand}</td>
                    <td>${product.model}</td>
                    <td>${product.unit}</td>

                    <td>
                        <button
                            class="status-btn ${product.status ? 'active' : 'inactive'}"
                            onclick="toggleStatus('${product.identifier}', this)">
                            ${product.status ? 'Active' : 'Inactive'}
                        </button>
                    </td>

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
        </table>
    </c:if>

</div>

<script>
function toggleStatus(identifier, button) {
    fetch('${pageContext.request.contextPath}/product/toggle-status', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: 'identifier=' + encodeURIComponent(identifier)
    })
    .then(() => {
        if (button.classList.contains('active')) {
            button.classList.remove('active');
            button.classList.add('inactive');
            button.innerText = 'Inactive';
        } else {
            button.classList.remove('inactive');
            button.classList.add('active');
            button.innerText = 'Active';
        }
    })
    .catch(() => {
        alert('Failed to update status');
    });
}
</script>

</body>
</html>