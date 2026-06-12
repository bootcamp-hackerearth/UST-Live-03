<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer List</title>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", sans-serif;
            padding: 40px;
            background: radial-gradient(circle at 20% 30%, rgba(255,122,0,0.15), transparent 40%),
                        radial-gradient(circle at 80% 70%, rgba(255,72,0,0.15), transparent 40%),
                        linear-gradient(135deg, #1f1f1f, #3a2e2a);
        }

        .container {
            max-width: 1100px;
            margin: auto;
        }

        .card {
            background: rgba(255,255,255,0.12);
            backdrop-filter: blur(20px);
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 15px 40px rgba(0,0,0,0.3);
            border: 1px solid rgba(255,255,255,0.15);
        }

        h5 {
            color: #fff;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
            color: #fff;
        }

        th {
            background: rgba(255,255,255,0.1);
            padding: 12px;
            font-size: 13px;
        }

        td {
            padding: 12px;
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }

        tr:hover {
            background: rgba(255,255,255,0.05);
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .add-btn {
            padding: 10px 16px;
            border-radius: 20px;
            border: none;
            background: linear-gradient(90deg, #ff4800, #ff7a00);
            color: #fff;
            text-decoration: none;
            font-size: 13px;
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
            background-color: rgba(255,255,255,0.3);
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
            background-color: #ff4800;
        }

        input:checked + .slider:before {
            transform: translateX(24px);
        }

        .actions {
            display: flex;
            gap: 12px;
            justify-content: center;
        }

        .icon-btn {
            border: none;
            background: transparent;
            cursor: pointer;
            font-size: 16px;
        }

        .edit-icon {
            color: #ff7a00;
        }

        .delete-icon {
            color: #ff4d4d;
        }

        .icon-btn:hover {
            transform: scale(1.1);
        }

        .empty {
            text-align: center;
            color: #ffb3b3;
            margin-top: 15px;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="card">

        <div class="header">
            <h5>Customer List</h5>

            <a href="/customer/add" class="add-btn">
                + Add Customer
            </a>
        </div>

        <c:if test="${empty customer}">
            <div class="empty">No customers found</div>
        </c:if>

        <c:if test="${not empty customer}">
            <table>

                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Type</th>
                    <th>Status</th>
                    <th>Balance</th>
                    <th>Credit</th>
                    <th>Action</th>
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

                        <td>
                            <label class="switch">
                                <input type="checkbox"
                                       ${cat.status ? 'checked' : ''}
                                       onchange="toggleStatus('${cat.identifier}')">

                                <span class="slider"></span>
                            </label>
                        </td>

                        <td>${cat.balance}</td>
                        <td>${cat.creditLimit}</td>

                        <td>
                            <div class="actions">

                                <a href="/customer/get?identifier=${cat.identifier}" class="icon-btn edit-icon">
                                    <i class="fas fa-pen"></i>
                                </a>

                                <a href="/customer/delete?identifier=${cat.identifier}"
                                   class="icon-btn delete-icon"
                                   onclick="return confirm('Delete?')">
                                    <i class="fas fa-trash"></i>
                                </a>

                            </div>
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
            '/customer/toggle?identifier=' + identifier;
    }
</script>

</body>
</html>