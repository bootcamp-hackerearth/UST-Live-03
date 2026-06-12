<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Brand Management | POS</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", sans-serif;
            min-height: 100vh;
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
            -webkit-backdrop-filter: blur(20px);
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 15px 40px rgba(0,0,0,0.3);
            border: 1px solid rgba(255,255,255,0.15);
        }

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .page-title {
            font-size: 18px;
            font-weight: 600;
            color: #fff;
        }

        .add-btn {
            background: linear-gradient(90deg, #ff4800, #ff7a00);
            color: #fff;
            border: none;
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 13px;
            cursor: pointer;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 14px;
        }

        th {
            background: rgba(255,255,255,0.18);
            color: #fff;
            padding: 12px 14px;
            text-align: left;
            backdrop-filter: blur(10px);
        }

        td {
            padding: 12px 14px;
            border-bottom: 1px solid rgba(255,255,255,0.15);
            color: #f1f1f1;
        }

        tr:hover {
            background: rgba(255,255,255,0.08);
        }

        .edit-link {
            color: #ff7a00;
            text-decoration: none;
            margin-right: 12px;
        }

        .delete-link {
            color: #ff4d4d;
            text-decoration: none;
        }

        .empty {
            text-align: center;
            padding: 30px;
            color: #ddd;
        }

        .footer {
            margin-top: 20px;
            text-align: center;
        }

        .btn-secondary {
            background: rgba(255,255,255,0.2);
            color: #fff;
            padding: 8px 16px;
            border-radius: 20px;
            text-decoration: none;
            margin-right: 10px;
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
            background-color: #ff4d4d;
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
            background-color: #ff7a00;
        }

        input:checked + .slider:before {
            transform: translateX(24px);
        }
    </style>
</head>

<body>

<div class="container">

    <div class="card">

        <div class="page-header">
            <div class="page-title">Brand List</div>

            <button class="add-btn"
                    onclick="window.location.href='/brand/add'">
                + Add Brand
            </button>
        </div>

        <c:if test="${empty brands}">
            <div class="empty">No brands found</div>
        </c:if>

        <c:if test="${not empty brands}">
            <table>
                <thead>
                    <tr>
                        <th>Brand</th>
                        <th>Description</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach var="b" items="${brands}">
                        <tr>

                            <td>${b.identifier}</td>

                            <td>${b.description}</td>

                            <td>
                                <label class="switch">
                                    <input type="checkbox"
                                           ${b.status ? 'checked' : ''}
                                           onchange="toggleStatus('${b.identifier}')">

                                    <span class="slider"></span>
                                </label>
                            </td>

                            <td>
                                <a href="${pageContext.request.contextPath}/brand/get?identifier=${b.identifier}"
                                   class="edit-link">
                                    Edit
                                </a>

                                <a href="${pageContext.request.contextPath}/brand/delete?identifier=${b.identifier}"
                                   onclick="return confirm('Delete this brand?')"
                                   class="delete-link">
                                    Delete
                                </a>
                            </td>

                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>

    </div>

    <div class="footer">
        <a href="/" class="btn-secondary">Home</a>
    </div>

</div>

<script>
    function toggleStatus(identifier) {
        window.location.href =
            '/brand/toggle?identifier=' + identifier;
    }
</script>

</body>
</html>