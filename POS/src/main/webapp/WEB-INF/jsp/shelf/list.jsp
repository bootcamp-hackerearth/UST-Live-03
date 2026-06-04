<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Shelf Management | POS</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            margin: 0;
            font-family: 'Inter', 'Segoe UI', sans-serif;
            background: linear-gradient(135deg, #f6f8fb, #eef2f7);
        }

        .topbar {
            height: 70px;
            background: linear-gradient(135deg, #4f46e5, #6366f1);
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 30px;
            color: #fff;
        }

        .topbar-left {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .dashboard-btn,
        .logout-btn {
            background: rgba(255,255,255,0.15);
            border: none;
            color: #fff;
            padding: 8px 14px;
            border-radius: 10px;
            font-size: 13px;
            cursor: pointer;
        }

        .content {
            padding: 40px;
        }

        .page-card {
            max-width: 1000px;
            margin: auto;
            background: #ffffff;
            border-radius: 18px;
            padding: 28px;
            box-shadow: 0 25px 60px rgba(0,0,0,0.08);
        }

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .primary-btn {
            background: #4f46e5;
            color: #fff;
            border: none;
            border-radius: 12px;
            padding: 10px 18px;
        }

        .action-edit {
            color: #4f46e5;
            margin-right: 10px;
            text-decoration: none;
        }

        .action-delete {
            color: #dc2626;
            text-decoration: none;
        }

        .empty {
            text-align: center;
            padding: 40px;
            color: #6b7280;
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 48px;
            height: 24px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            cursor: pointer;
            inset: 0;
            background-color: #ef4444;
            transition: .4s;
            border-radius: 30px;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            transition: .4s;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: #22c55e;
        }

        input:checked + .slider:before {
            transform: translateX(24px);
        }
    </style>
</head>

<body>

<div class="topbar">
    <div class="topbar-left">
        <h5>Shelf Management</h5>

        <button class="dashboard-btn"
                onclick="window.location.href='/'">
            Dashboard
        </button>
    </div>

    <form action="/logout" method="post">
        <button class="logout-btn">Logout</button>
    </form>
</div>

<div class="content">

    <div class="page-card">

        <div class="page-header">
            <h5>Shelf List</h5>

            <button class="primary-btn"
                    onclick="window.location.href='/shelf/add'">
                + Add Shelf
            </button>
        </div>

        <table class="table table-hover">

            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>

            <tbody>

                <c:if test="${empty shelfs}">
                    <tr>
                        <td colspan="4" class="empty">
                            No shelves found
                        </td>
                    </tr>
                </c:if>

                <c:forEach var="s" items="${shelfs}" varStatus="i">
                    <tr>

                        <td>${i.count}</td>

                        <td>${s.identifier}</td>

                        <td>
                            <label class="switch">
                                <input type="checkbox"
                                       ${s.status ? 'checked' : ''}
                                       onchange="toggleStatus('${s.identifier}')">

                                <span class="slider"></span>
                            </label>
                        </td>

                        <td>
                            <a href="/shelf/get?identifier=${s.identifier}"
                               class="action-edit">
                                Edit
                            </a>

                            <a href="/shelf/delete?identifier=${s.identifier}"
                               onclick="return confirm('Delete this shelf?')"
                               class="action-delete">
                                Delete
                            </a>
                        </td>

                    </tr>
                </c:forEach>

            </tbody>

        </table>

    </div>

</div>

<script>
    function toggleStatus(identifier) {
        window.location.href =
            '/shelf/toggle?identifier=' + identifier;
    }
</script>

</body>
</html>