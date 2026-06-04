<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>

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

        .dashboard-btn {
            background: rgba(255,255,255,0.15);
            border: none;
            color: #fff;
            padding: 8px 14px;
            border-radius: 10px;
        }

        .logout-btn {
            background: rgba(255,255,255,0.15);
            border: none;
            color: #fff;
            padding: 8px 16px;
            border-radius: 10px;
        }

        .content {
            padding: 40px;
        }

        .page-card {
            max-width: 1100px;
            margin: auto;
            background: #ffffff;
            border-radius: 18px;
            padding: 28px;
            box-shadow: 0 25px 60px rgba(0,0,0,0.08);
        }

        table thead th {
            background: #eef2ff;
            font-weight: 600;
        }

        .btn-primary {
            background: #4f46e5;
            border: none;
        }

        .btn-primary:hover {
            background: #4338ca;
        }

        .btn-danger {
            background: #dc2626;
            border: none;
        }

        .btn-danger:hover {
            background: #b91c1c;
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
    <h5>Customer Management</h5>

    <div>
        <button class="dashboard-btn" onclick="window.location.href='/'">
            Dashboard
        </button>

        <form action="/logout" method="post" style="display:inline;">
            <button class="logout-btn">Logout</button>
        </form>
    </div>
</div>

<div class="content">
    <div class="page-card">

        <div class="d-flex justify-content-between mb-3">
            <h5>Customer List</h5>

            <a href="/customer/add" class="btn btn-primary">
                + Add Customer
            </a>
        </div>

        <c:if test="${empty customer}">
            <div class="alert alert-warning text-center">
                No customers found
            </div>
        </c:if>

        <c:if test="${not empty customer}">
            <div class="table-responsive">
                <table class="table table-hover text-center align-middle">

                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Type</th>
                        <th>Balance</th>
                        <th>Credit</th>
                        <th>Status</th>
                        <th>Delete</th>
                        <th>Edit</th>
                    </tr>
                    </thead>

                    <tbody>
                    <c:forEach var="cat" items="${customer}">
                        <tr>
                            <td>${cat.id}</td>
                            <td>${cat.name}</td>
                            <td>${cat.identifier}</td>
                            <td>${cat.phoneNo}</td>
                            <td>${cat.userType}</td>
                            <td>${cat.balance}</td>
                            <td>${cat.creditLimit}</td>

                            <td>
                                <label class="switch">
                                    <input type="checkbox"
                                           ${cat.status ? 'checked' : ''}
                                           onchange="toggleStatus('${cat.identifier}')">

                                    <span class="slider"></span>
                                </label>
                            </td>

                            <td>
                                <a href="/customer/delete?identifier=${cat.identifier}"
                                   class="btn btn-danger btn-sm"
                                   onclick="return confirm('Delete?')">
                                    Delete
                                </a>
                            </td>

                            <td>
                                <a href="/customer/get?identifier=${cat.identifier}"
                                   class="btn btn-primary btn-sm">
                                    Edit
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>

                </table>
            </div>
        </c:if>

    </div>
</div>

<script>
    function toggleStatus(identifier) {
        window.location.href =
            '/customer/toggle?identifier=' + identifier;
    }
</script>

</body>
</html>