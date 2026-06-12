<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Role List</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: #ffffff;
            min-height: 100vh;
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #4b6cb7;
            font-weight: 600;
        }

        .card-header {
            border-top-left-radius: 16px;
            border-top-right-radius: 16px;
        }

        table th {
            background-color: #0d6efd;
            color: white;
            padding: 12px;
            font-size: 14px;
        }

        td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid #eee;
            font-size: 14px;
            color: #333;
        }

        tr:hover {
            background: #f7f9ff;
        }

        .action-icon {
            font-size: 18px;
            margin: 0 6px;
            text-decoration: none;
            color: #4b6cb7;
            transition: 0.2s ease;
        }

        .action-icon:hover {
            color: #182848;
            transform: scale(1.2);
        }

        .alert {
            padding: 10px;
            border-radius: 8px;
            margin-bottom: 15px;
            text-align: center;
            font-size: 14px;
        }

        .alert-warning {
            background: #fff3cd;
            color: #856404;
        }

        .footer-actions {
            margin-top: 20px;
            display: flex;
            justify-content: center;
            gap: 12px;
        }

        .btn {
            padding: 10px 16px;
            border-radius: 10px;
            text-decoration: none;
            font-weight: 600;
            font-size: 14px;
            display: inline-block;
            transition: 0.25s ease;
        }

        .btn-home {
            background: #6c757d;
            color: white;
        }

        .btn-home:hover {
            background: #555;
        }

        .btn-add {
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
        }

        .btn-add:hover {
            transform: scale(1.05);
        }

        /* ✅ REMOVE UNDERLINE FROM ICON LINKS */
        .action-link {
            text-decoration: none;
            font-size: 18px;
            margin: 0 6px;
        }

        .action-link:hover {
            text-decoration: none;
            opacity: 0.8;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">

            <div class="card shadow-lg">
                <div class="card-header bg-primary text-white text-center">
                    <h4 class="mb-0">List of Roles</h4>
                </div>

                <div class="card-body">

                    <c:if test="${empty roles}">
                        <div class="alert alert-warning text-center">
                            No roles found
                        </div>
                    </c:if>

                    <c:if test="${not empty roles}">
                        <table class="table table-bordered table-hover text-center align-middle">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Role</th>
                                <th>Description</th>
                                <th>Action</th>
                            </tr>
                            </thead>

                            <tbody>
                            <c:forEach var="role" items="${roles}">
                                <tr>
                                    <td>${role.id}</td>
                                    <td>${role.identifier}</td>
                                    <td>${role.description}</td>
                                    <td>
                                        <!-- ✏️ Edit -->
                                        <a href="/role/get?identifier=${role.identifier}"
                                           class="action-link"
                                           title="Edit">
                                            ✏️
                                        </a>

                                        <!-- 🗑 Delete -->
                                        <a href="/role/delete?identifier=${role.identifier}"
                                           class="action-link"
                                           title="Delete"
                                           onclick="return confirm('Are you sure you want to delete this role?');">
                                            🗑
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:if>

                </div>

                <div class="card-footer text-center bg-light d-flex justify-content-center gap-3">
                    <a href="/" class="btn btn-secondary">
                        Home
                    </a>
                    <a href="/role/add" class="btn btn-success">
                        Add New Role
                    </a>
                </div>
            </div>

    <h2>List of Roles</h2>

    <c:if test="${empty roles}">
        <div class="alert alert-warning">
            No roles found
        </div>
    </c:if>

    <c:if test="${not empty roles}">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Role</th>
                <th>Description</th>
                <th>Action</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="role" items="${roles}">
                <tr>
                    <td>${role.id}</td>
                    <td>${role.identifier}</td>
                    <td>${role.description}</td>

                    <td>
                        <a href="/role/get?identifier=${role.identifier}"
                           class="action-icon"
                           title="Edit">✏️</a>

                        <a href="/role/delete?identifier=${role.identifier}"
                           class="action-icon"
                           title="Delete"
                           onclick="return confirm('Are you sure you want to delete this role?');">
                            🗑
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <div class="footer-actions">
        <a href="/" class="btn btn-home">Home</a>
        <a href="/role/add" class="btn btn-add">+ Add New Role</a>
    </div>

</div>

</body>
</html>