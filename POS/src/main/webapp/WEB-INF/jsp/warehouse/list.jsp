<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Warehouse Management | POS</title>

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
            box-shadow: 0 4px 20px rgba(0,0,0,0.08);
        }

        .topbar-left {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .dashboard-btn {
            background: rgba(255,255,255,0.15);
            border: none;
            color: #fff;
            padding: 8px 14px;
            border-radius: 10px;
            font-size: 13px;
        }

        .logout-btn {
            background: rgba(255,255,255,0.15);
            border: none;
            color: #fff;
            padding: 8px 16px;
            border-radius: 10px;
            font-size: 13px;
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

        .page-title {
            font-size: 20px;
            font-weight: 600;
        }

        .add-btn {
            background: #4f46e5;
            color: #fff;
            border: none;
            padding: 10px 16px;
            border-radius: 10px;
            font-size: 13px;
        }

        table {
            font-size: 14px;
        }

        thead th {
            background: #f9fafb;
            color: #374151;
        }

        .edit-link {
            color: #4f46e5;
            text-decoration: none;
            margin-right: 10px;
        }

        .delete-link {
            color: #dc2626;
            text-decoration: none;
        }

        .empty {
            text-align: center;
            padding: 40px;
            color: #6b7280;
        }
    </style>
</head>

<body>

<div class="topbar">
    <div class="topbar-left">
        <h5>Warehouse Management</h5>

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
            <div class="page-title">Warehouse List</div>

            <button class="add-btn"
                    onclick="window.location.href='/warehouse/add'">
                + Add Warehouse
            </button>
        </div>

        <c:if test="${empty warehouses}">
            <div class="empty">No warehouses found</div>
        </c:if>

        <c:if test="${not empty warehouses}">
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Location</th>
                        <th>Region</th>
                        <th>Country</th>
                        <th>Contact</th>
                        <th>Action</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach var="w" items="${warehouses}">
                        <tr>
                            <td>${w.identifier}</td>
                            <td>${w.location}</td>
                            <td>${w.region}</td>
                            <td>${w.country}</td>
                            <td>${w.contactName} (${w.contactNumber})</td>
                            <td>
                                <a href="/warehouse/get?identifier=${w.identifier}" class="edit-link">Edit</a>
                                <a href="/warehouse/delete?identifier=${w.identifier}"
                                   onclick="return confirm('Delete this warehouse?')"
                                   class="delete-link">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>

    </div>

</div>

</body>
</html>