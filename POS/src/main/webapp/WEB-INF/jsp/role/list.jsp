<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Role List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

    <style>
        body {
            background: #2f2f2f;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: "Segoe UI", sans-serif;
        }

        .container {
            max-width: 900px;
        }

        .card {
            border: none;
            border-radius: 18px;
            background: #ffffff;
            box-shadow: 0 15px 40px rgba(0, 0, 0, 0.25);
        }

        .card-header {
            background: transparent;
            border-bottom: none;
            text-align: center;
            padding-top: 25px;
        }

        .card-header h4 {
            font-weight: 600;
            color: #333;
        }

        .table th {
            background-color: #f4f6fb;
            color: #555;
            font-weight: 600;
            border: none;
            text-align: center;
            font-size: 13px;
            text-transform: uppercase;
        }

        .table td {
            text-align: center;
            vertical-align: middle;
            border-color: #eee;
            font-size: 14px;
        }

        .table tbody tr:hover {
            background: #f9fafb;
        }

        .edit-btn {
            background: linear-gradient(90deg, #4facfe, #00f2fe);
            color: #fff;
            border-radius: 20px;
            padding: 6px 16px;
            font-size: 0.85rem;
            border: none;
            margin-right: 6px;
            transition: all 0.3s ease;
        }

        .edit-btn:hover {
            background: linear-gradient(90deg, #00c6ff, #0072ff);
            transform: translateY(-1px);
            color: #fff;
        }

        .btn-danger {
            border-radius: 20px;
            padding: 6px 16px;
            font-size: 0.85rem;
        }

        /* Footer Buttons */
        .card-footer {
            border-top: none;
            background: transparent;
        }

        .btn-success {
            background: linear-gradient(90deg, #4facfe, #00f2fe);
            border: none;
            border-radius: 25px;
            padding: 8px 24px;
            font-weight: 600;
        }

        .btn-secondary {
            border-radius: 25px;
            padding: 8px 24px;
            font-weight: 600;
        }
                 .toggle-switch {
                            position: relative;
                            width: 50px;
                            height: 26px;
                            display: inline-block;
                        }

                        .toggle-switch input {
                            opacity: 0;
                            width: 0;
                            height: 0;
                        }

                        .slider {
                            position: absolute;
                            cursor: pointer;
                            top: 0;
                            left: 0;
                            right: 0;
                            bottom: 0;
                            background-color: #dc3545;
                            transition: 0.3s;
                            border-radius: 30px;
                        }

                        .slider:before {
                            position: absolute;
                            content: "";
                            height: 20px;
                            width: 20px;
                            left: 3px;
                            bottom: 3px;
                            background-color: white;
                            transition: 0.3s;
                            border-radius: 50%;
                        }

                        input:checked + .slider {
                            background-color: #198754;
                        }

                        input:checked + .slider:before {
                            transform: translateX(24px);
                        }
    </style>
</head>

<body>

<div class="container">
    <div class="card">

        <div class="card-header">
            <h4>List of Roles</h4>
        </div>

        <div class="card-body">

            <c:if test="${empty roles}">
                <div class="alert alert-warning text-center">
                    No roles found
                </div>
            </c:if>

            <c:if test="${not empty roles}">
                <div class="table-responsive">
                    <table class="table table-bordered table-hover align-middle">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Role</th>
                            <th>Description</th>
                            <th>Status</th>
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
                                                   <form method="get" action="${pageContext.request.contextPath}/role/toggleStatus">
                                                   <input type="hidden" name="identifier" value="${role.identifier}"/>

                                                      <label class="toggle-switch">
                                                       <input type="checkbox"
                                                        name="status" onchange="this.form.submit()"
                                                        <c:if test="${role.status}">checked</c:if>>
                                                         <span class="slider"></span>
                                                          </label>
                                                           </form>
                                                            </td>
                                <td>
                                    <a href="/role/get?identifier=${role.identifier}"
                                       class="btn edit-btn">
                                        Edit
                                    </a>

                                    <a href="/role/delete?identifier=${role.identifier}"
                                       class="btn btn-danger"
                                       onclick="return confirm('Are you sure you want to delete this role?');">
                                        Delete
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>

        </div>

        <div class="card-footer d-flex justify-content-center gap-3 pb-4">
            <a href="/" class="btn btn-secondary">
                Home
            </a>

            <a href="/role/add" class="btn btn-success">
                + Add New Role
            </a>
        </div>

    </div>
</div>

</body>
</html>