<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Category Management</title>

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
            justify-content: space-between;
            align-items: center;
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
            box-shadow: 0 6px 18px rgba(0,0,0,0.08);
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
    <h2 style="text-align:center;">Category Management</h2>

    <div class="actions">
        <a href="${pageContext.request.contextPath}/category/add" class="add-btn">
            Add Category
        </a>
    </div>

    <c:if test="${empty categorys}">
        <div style="text-align:center;">No categories found</div>
    </c:if>

    <c:if test="${not empty categorys}">
        <table>
            <tr>
    <th>Category Name</th>
    <th>Super Category</th>
    <th>Status</th>
    <th>Action</th>
</tr>

<c:forEach var="cat" items="${categorys}">
    <tr>
        <td>${cat.identifier}</td>
        <td>${cat.superCategory}</td>

        <td>
            <button
                class="status-btn ${cat.status ? 'active' : 'inactive'}"
                onclick="toggleStatus('${cat.identifier}', this)">
                ${cat.status ? 'Active' : 'Inactive'}
            </button>
        </td>

        <td>
            <a href="${pageContext.request.contextPath}/category/get?identifier=${cat.identifier}"
               class="action-link edit">
                Edit
            </a>

            <a href="${pageContext.request.contextPath}/category/delete?identifier=${cat.identifier}"
               class="action-link delete"
               onclick="return confirm('Are you sure you want to delete this category?');">
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
        fetch('${pageContext.request.contextPath}/category/toggle-status', {
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