<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Racks Management | POS</title>

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
            color: #ffffff;
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
            padding: 6px 14px;
            border-radius: 8px;
            font-size: 13px;
            cursor: pointer;
        }

        .dashboard-btn:hover,
        .logout-btn:hover {
            background: rgba(255,255,255,0.25);
        }

        .content {
            padding: 40px;
        }

        .card {
            background: #ffffff;
            border-radius: 18px;
            padding: 25px;
            box-shadow: 0 25px 60px rgba(0,0,0,0.08);
        }

        table {
            font-size: 14px;
        }

        .edit {
            color: #4f46e5;
            text-decoration: none;
            margin-right: 10px;
            font-weight: 500;
        }

        .delete {
            color: #dc2626;
            text-decoration: none;
            font-weight: 500;
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
        <h5>Racks Management</h5>

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

    <div class="card">

        <div class="d-flex justify-content-between mb-3">
            <h5>Racks List</h5>

            <button class="btn btn-primary"
                    onclick="window.location.href='/racks/add'">
                + Add Rack
            </button>
        </div>

        <c:if test="${empty racks}">
            <div class="empty">No racks found</div>
        </c:if>

        <c:if test="${not empty racks}">
            <table class="table table-hover">

                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Rack Name</th>
                        <th>Shelves</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach var="rack" items="${racks}" varStatus="i">
                        <tr>

                            <td>${i.count}</td>

                            <td>${rack.identifier}</td>

                            <td>${rack.shelves}</td>

                            <td>
                                <label class="switch">
                                    <input type="checkbox"
                                           ${rack.status ? 'checked' : ''}
                                           onchange="toggleStatus('${rack.identifier}')">

                                    <span class="slider"></span>
                                </label>
                            </td>

                            <td>
                                <a href="/racks/get?identifier=${rack.identifier}"
                                   class="edit">
                                    Edit
                                </a>

                                <a href="/racks/delete?identifier=${rack.identifier}"
                                   class="delete"
                                   onclick="return confirm('Delete this rack?')">
                                    Delete
                                </a>
                            </td>

                        </tr>
                    </c:forEach>
                </tbody>

            </table>
        </c:if>

    </div>

</div>

<script>
    function toggleStatus(identifier) {
        window.location.href =
            '/racks/toggle?identifier=' + identifier;
    }
</script>

</body>
</html>