<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit User</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        body {
            margin: 0;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif;
            min-height: 100vh;

            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);
            color: #111827;
        }

        .container {
            margin-top: 60px;
        }

        .card {
            background: rgba(255, 255, 255, 0.85);
            border: 1px solid #e5e7eb;
            border-radius: 12px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.08);
        }

        .card-body {
            padding: 25px;
        }

        h3 {
            font-weight: 800;
            font-size: 26px;
            letter-spacing: 0.5px;
            color: #111827;
        }

        .table {
            color: #111827;
            margin-bottom: 0;
        }

        .table th {
            background: #e5e7eb;
            color: #111827;
            border: 1px solid #d1d5db;
            font-weight: 600;
        }

        .table td {
            background: #f9fafb;
            border: 1px solid #e5e7eb;
        }

        .form-control, .form-select {
            background: #ffffff;
            border: 1px solid #d1d5db;
            color: #111827;
            border-radius: 6px;
        }

        .form-control:focus, .form-select:focus {
            border-color: #3b82f6;
            box-shadow: 0 0 0 3px rgba(59,130,246,0.15);
        }

        .icon-btn {
            border: none;
            padding: 6px 10px;
            border-radius: 8px;
            cursor: pointer;
            transition: 0.2s ease;
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .icon-btn:hover {
            transform: scale(1.1);
        }

        .edit-btn {
            background: #2563eb;
        }

        .delete-btn {
            background: #ef4444;
        }

        td .btn {
            padding: 6px 10px;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="card">
        <div class="card-body">

            <h3 class="text-center mb-3">Edit User</h3>

            <form action="${pageContext.request.contextPath}/user/update" method="post">

                <table class="table table-bordered text-center align-middle">

                    <thead>
                    <tr>
                        <th>Id</th>
                        <th>Email</th>
                        <th>Name</th>
                        <th>Phone</th>
                        <th>Roles</th>
                        <th>Action</th>
                    </tr>
                    </thead>

                    <tbody>
                    <tr>

                        <td>
                            <input type="text" class="form-control"
                                   name="id"
                                   value="${user.id}"
                                   readonly>
                        </td>

                        <td>
                            <input type="text" class="form-control"
                                   name="username"
                                   value="${user.username}"
                                   required>
                        </td>

                        <td>
                            <input type="text" class="form-control"
                                   name="name"
                                   value="${user.name}"
                                   required>
                        </td>

                        <td>
                            <input type="text"
                                   class="form-control"
                                   name="phoneNo"
                                   value="${user.phoneNo}"
                                   required
                                   maxlength="10"
                                   pattern="^[0-9]{10}$"
                                   title="Phone number must be exactly 10 digits (numbers only)">
                        </td>

                        <td>
                            <select class="form-select" name="roles" multiple>
                                <c:forEach var="role" items="${roles}">
                                    <option value="${role.identifier}"
                                        <c:if test="${user.roles.contains(role.identifier)}">
                                            selected
                                        </c:if>>
                                        ${role.identifier}
                                    </option>
                                </c:forEach>
                            </select>
                        </td>

                        <td class="d-flex justify-content-center gap-2">

                            <button type="submit"
                                    class="icon-btn edit-btn"
                                    title="Update">
                                <i class="bi bi-pencil-square"></i>
                            </button>

                            <a class="icon-btn delete-btn"
                               href="${pageContext.request.contextPath}/user/delete?username=${user.username}"
                               onclick="return confirm('Delete this user?');"
                               title="Delete">
                                <i class="bi bi-trash"></i>
                            </a>

                        </td>

                    </tr>
                    </tbody>

                </table>

            </form>

        </div>
    </div>
</div>

</body>
</html>