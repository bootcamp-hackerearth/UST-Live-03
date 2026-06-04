<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Shelves List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: 'Segoe UI', sans-serif;
        }
        .card {
            width: 800px;
            border-radius: 15px;
            background: rgba(255,255,255,0.9);
            box-shadow: 0 10px 25px rgba(0,0,0,0.08);
        }
        .card-header {
            background: linear-gradient(135deg, #e5e7eb, #f3f4f6);
            font-weight: 600;
        }
        table th { background: #e5e7eb; }
        table td { background: #f9fafb; }

        .icon-btn {
            padding: 6px 10px;
            border-radius: 8px;
            color: white;
            text-decoration: none;
        }
        .edit-btn { background: #3b82f6; }
        .delete-btn { background: #ef4444; }

        .switch {
            position: relative;
            display: inline-block;
            width: 46px;
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
            background-color: #dc2626; /* OFF */
            border-radius: 24px;
            top: 0; left: 0; right: 0; bottom: 0;
            transition: 0.4s;
        }
        .slider:before {
            content: "";
            position: absolute;
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background: white;
            border-radius: 50%;
            transition: 0.4s;
        }
        input:checked + .slider {
            background-color: #22c55e; /* ON */
        }
        input:checked + .slider:before {
            transform: translateX(22px);
        }
        .btn-custom {
            padding: 10px 22px;
            font-size: 14px;
            font-weight: 600;
            border-radius: 10px;
            border: none;
            text-decoration: none;
            display: inline-block;
            transition: 0.25s ease;
        }
        .btn-home {
            background: #6b7280;
            color: #fff;
        }
        .btn-home:hover {
            background: #4b5563;
            transform: translateY(-2px);
        }
    </style>
</head>

<body>

<div class="card shadow-lg">
    <div class="card-header text-center">
        <h4 class="mb-0">Shelves List</h4>
    </div>
    <div class="card-body">
        <c:if test="${empty shelves}">
            <div class="alert alert-warning text-center">
                No shelves available
            </div>
        </c:if>
        <c:if test="${not empty shelves}">
            <table class="table table-bordered table-hover text-center align-middle">

                <thead>
                <tr>
                    <th>ID</th>
                    <th>Shelves Name</th>
                    <th>Status</th>
                    <th>Edit</th>
                    <th>Delete</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="sh" items="${shelves}" varStatus="loop">
                    <tr>
                        <td>${loop.index + 1}</td>
                        <td>${sh.identifier}</td>
                        <td>
                            <label class="switch">
                                <input type="checkbox"
                                       ${sh.status ? 'checked' : ''}
                                       onchange="window.location.href='${pageContext.request.contextPath}/shelves/toggle?identifier=${sh.identifier}'">
                                <span class="slider"></span>
                            </label>
                        </td>
                        <td>
                            <a class="icon-btn edit-btn"
                               href="${pageContext.request.contextPath}/shelves/get?identifier=${sh.identifier}">
                                <i class="bi bi-pencil-square"></i>
                            </a>
                        </td>
                        <td>
                            <a class="icon-btn delete-btn"
                               href="${pageContext.request.contextPath}/shelves/delete?identifier=${sh.identifier}"
                               onclick="return confirm('Delete this shelves?')">
                                <i class="bi bi-trash"></i>
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>
    <div class="card-footer text-center">
        <a href="${pageContext.request.contextPath}/"
           class="btn-custom btn-home me-2">
            Home
        </a>
        <a class="btn btn-primary"
           href="${pageContext.request.contextPath}/shelves/add">
            + Add Shelves
        </a>
    </div>
</div>
</body>
</html>