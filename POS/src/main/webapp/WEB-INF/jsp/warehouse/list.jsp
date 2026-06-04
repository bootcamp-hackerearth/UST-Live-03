<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Warehouse List</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: #2f2f2f;
            min-height: 100vh;
            font-family: "Segoe UI", sans-serif;
            padding: 40px;
        }

        .container {
            max-width: 900px;
        }

        .card {
            border: none;
            border-radius: 18px;
            background-color: #ffffff;
            box-shadow: 0 15px 40px rgba(0, 0, 0, 0.25);
        }

        .card-header {
            background: transparent;
            border-bottom: none;
            padding: 20px;
        }

        .card-header h2 {
            font-size: 22px;
            font-weight: 600;
            color: #333;
        }

        table th {
            font-weight: 600;
            font-size: 0.9rem;
            color: #444;
        }

        table td {
            font-size: 0.9rem;
        }

        /* ✅ Home Button */
        .btn-home {
            background: #6c757d;
            color: #ffffff;
            border-radius: 20px;
            font-weight: 600;
        }

        .btn-home:hover {
            background: #5a6268;
            color: #ffffff;
        }

        /* ✅ Add Button */
        .btn-add {
            background: linear-gradient(90deg, #4facfe, #00f2fe);
            color: #fff;
            border-radius: 20px;
            font-weight: 600;
        }

        .card-footer {
            text-align: center;
            font-size: 0.85rem;
            color: #555;
        }
              /* ✅ Toggle Switch */
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
                    inset: 0;
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
                    background-color: #ffffff;
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

        <!-- ✅ HEADER WITH HOME BUTTON -->
        <div class="card-header d-flex justify-content-between align-items-center">
            <h2>Warehouse List</h2>
            <div class="d-flex gap-2">
                <a href="${pageContext.request.contextPath}/"
                   class="btn btn-home">
                     Home
                </a>
                <a href="${pageContext.request.contextPath}/warehouse/add"
                   class="btn btn-add">
                    + Add Warehouse
                </a>
            </div>
        </div>

        <div class="card-body">

            <!-- ✅ EMPTY MESSAGE -->
            <c:if test="${empty warehouses}">
                <div class="alert alert-info text-center">
                    No warehouses available
                </div>
            </c:if>

            <!-- ✅ WAREHOUSE TABLE -->
            <c:if test="${not empty warehouses}">
                <table class="table table-bordered table-hover">
                    <thead class="table-light">
                        <tr>
                            <th>Identifier</th>
                            <th>Code</th>
                            <th>Location</th>
                            <th>Status</th>
                            <th style="width: 160px;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="warehouse" items="${warehouses}">
                            <tr>
                                <td>${warehouse.identifier}</td>
                                <td>${warehouse.code}</td>
                                <td>${warehouse.location}</td>
                                  <td>
                                                    <form method="get"
                                                          action="${pageContext.request.contextPath}/warehouse/toggleStatus">
                                                        <input type="hidden" name="identifier" value="${warehouse.identifier}" />

                                                        <label class="toggle-switch">
                                                            <input type="checkbox"
                                                                   onchange="this.form.submit()"
                                                                   <c:if test="${warehouse.status}">checked</c:if>>
                                                            <span class="slider"></span>
                                                        </label>
                                                    </form>
                                                </td>

                                <td>
                                    <a href="${pageContext.request.contextPath}/warehouse/get?identifier=${warehouse.identifier}"
                                       class="btn btn-sm btn-warning">
                                        Edit
                                    </a>
                                    <a href="${pageContext.request.contextPath}/warehouse/delete?identifier=${warehouse.identifier}"
                                       class="btn btn-sm btn-danger"
                                       onclick="return confirm('Are you sure?');">
                                        Delete
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>

        </div>

        <div class="card-footer">
            POS Management System
        </div>

    </div>
</div>

</body>
</html>