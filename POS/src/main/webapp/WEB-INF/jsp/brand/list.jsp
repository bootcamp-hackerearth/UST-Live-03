<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Brand Management</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background-color: #f6f7f9;
        }

        /* ===== TOP BAR ===== */
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
            padding: 7px 16px;
            background-color: #1e293b;
            color: #e5e7eb;
            text-decoration: none;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
            border: 1px solid #334155;
        }

        .logout-btn {
            background: #dc2626;
            border: none;
            color: white;
            padding: 7px 16px;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
        }

        .page-title {
            text-align: center;
            padding: 22px 0 14px;
            font-size: 28px;
            font-weight: 700;
            color: #020617;
        }

        .container {
            width: 95%;
            max-width: 1100px;
            margin: 0 auto 28px;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 6px 18px rgba(0,0,0,0.08);
            padding: 26px;
        }

        .list-actions {
            display: flex;
            justify-content: flex-end;
            margin-bottom: 18px;
        }

        .add-btn {
            padding: 9px 18px;
            background-color: #2563eb;
            color: #ffffff;
            text-decoration: none;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 16px;
            text-align: center;
            border-bottom: 1px solid #e5e7eb;
        }

        th {
            background-color: #e5e7eb;
            font-size: 13px;
            font-weight: 700;
        }

        /* STATUS TOGGLE */
        .status-toggle {
            padding: 6px 14px;
            border-radius: 999px;
            font-size: 13px;
            font-weight: 600;
            color: white;
            cursor: pointer;
            border: none;
        }

        .status-true {
            background-color: #16a34a;
        }

        .status-false {
            background-color: #dc2626;
        }

        .action-link {
            padding: 7px 16px;
            border-radius: 6px;
            text-decoration: none;
            font-weight: 600;
            font-size: 14px;
            color: #ffffff;
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
    <form action="${pageContext.request.contextPath}/logout" method="post">
        <button class="logout-btn">Logout</button>
    </form>
</div>

<div class="page-title">Brand Management</div>

<div class="container">

    <div class="list-actions">
        <a href="${pageContext.request.contextPath}/brand/add" class="add-btn">Add Brand</a>
    </div>

    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Brand</th>
            <th>Description</th>
            <th>Status</th>
            <th>Action</th>
        </tr>
        </thead>

        <tbody>
<c:forEach var="brand" items="${brands}">
    <tr>
        <td>${brand.id}</td>
        <td>${brand.identifier}</td>
        <td>${brand.description}</td>

        <!-- ✅ INLINE STATUS TOGGLE -->
        <td>
            <button
                class="status-toggle ${brand.status ? 'status-true' : 'status-false'}"
                onclick="toggleStatus('${brand.identifier}')">
                ${brand.status ? 'Active' : 'Inactive'}
            </button>
        </td>

        <td>
        <a href="${pageContext.request.contextPath}/brand/get?identifier=${brand.identifier}"
               class="action-link edit">Edit</a>

        <a href="${pageContext.request.contextPath}/brand/delete?identifier=${brand.identifier}"
               class="action-link delete"
               onclick="return confirm('Delete this brand?')">Delete</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<script>
    function toggleStatus(identifier) {
        fetch('${pageContext.request.contextPath}/brand/toggle-status?identifier=' + identifier, {
            method: 'POST'
        }).then(() => location.reload());
    }
</script>

</body>
</html>