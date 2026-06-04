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
            background: #f6f7f9;
        }

        .topbar {
            height: 56px;
            background: #020617;
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
            color: #e5e7eb;
            font-weight: 600;
        }

        .home-btn {
            padding: 6px 14px;
            background: #1e293b;
            color: #e5e7eb;
            text-decoration: none;
            border-radius: 6px;
            font-weight: 600;
        }

        .logout-btn {
            background: #dc2626;
            color: white;
            border: none;
            padding: 7px 16px;
            border-radius: 6px;
            font-weight: 600;
            cursor: pointer;
        }

        .container {
            width: 95%;
            max-width: 1100px;
            margin: 32px auto;
            background: #fff;
            padding: 26px;
            border-radius: 12px;
            box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
        }

        .actions {
            text-align: right;
            margin-bottom: 14px;
        }

        .add-btn {
            background: #2563eb;
            color: white;
            text-decoration: none;
            padding: 7px 16px;
            border-radius: 6px;
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 14px;
            border-bottom: 1px solid #e5e7eb;
            text-align: center;
        }

        th {
            background: #e5e7eb;
        }

        .action-link {
            padding: 6px 14px;
            border-radius: 6px;
            color: white;
            text-decoration: none;
            font-weight: 600;
        }

        .edit {
            background: #2563eb;
        }

        .delete {
            background: #dc2626;
            margin-left: 8px;
        }

        .status-btn {
            border: none;
            padding: 6px 16px;
            border-radius: 16px;
            font-weight: 600;
            cursor: pointer;
        }

        .status-btn.active {
            background: #16a34a;
            color: white;
        }

        .status-btn.inactive {
            background: #dc2626;
            color: white;
        }
    </style>
</head>

<body>

<div class="topbar">
    <div class="topbar-left">
        <div class="top-title">POS Application</div>
        <a href="${pageContext.request.contextPath}/" class="home-btn">Home</a>
    </div>

    <form action="${pageContext.request.contextPath}/logout" method="post">
        <button class="logout-btn" type="submit">Logout</button>
    </form>
</div>

<div class="container">
    <h2 style="text-align:center;">Product Management</h2>

    <div class="actions">
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
                <th>Product Name</th>
                <th>Category</th>
                <th>SKU Code</th>
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
                    <td>${product.skuCode}</td>
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