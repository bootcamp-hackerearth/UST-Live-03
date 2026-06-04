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
        }

        .card {
            border-radius: 16px;
        }

        .card-header {
            border-top-left-radius: 16px;
            border-top-right-radius: 16px;
        }

        table th {
            background-color: #0d6efd;
            color: white;
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

        </div>
    </div>
</div>

</body>
</html>