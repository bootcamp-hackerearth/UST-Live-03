<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Home</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css"
          rel="stylesheet"/>

    <style>
        body {
            min-height: 100vh;
            margin: 0;
            background-color: #f4f6f9;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        /* Sidebar */
        .sidebar {
            width: 250px;
            min-height: 100vh;
            background: linear-gradient(180deg, #667eea, #764ba2);
            color: white;
        }

        .sidebar a {
            color: white;
            text-decoration: none;
            padding: 10px 14px;
            display: block;
            border-radius: 8px;
            font-weight: 500;
        }

        .sidebar a:hover {
            background-color: rgba(255, 255, 255, 0.2);
        }

        /* Content area */
        .content-area {
            padding: 30px;
        }

        /* Logout */
        .logout-btn {
            border: none;
            background: transparent;
            color: white;
            font-size: 1.2rem;
            cursor: pointer;
        }

        .logout-btn:hover {
            color: #ffdddd;
        }
    </style>
</head>

<body>

<div class="d-flex">

    <!-- ===== Sidebar ===== -->
    <div class="sidebar p-3">

        <!-- Header + Logout -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="mb-0">POS System</h5>

            <form action="${pageContext.request.contextPath}/logout" method="post">
                <button type="submit" class="logout-btn" title="Logout">
                    <i class="bi bi-box-arrow-right"></i>
                </button>
            </form>
        </div>

        <!-- No nodes -->
        <c:if test="${empty nodes}">
            <div class="alert alert-light text-center small">
                No nodes found
            </div>
        </c:if>

        <!-- Nodes list -->
        <c:if test="${not empty nodes}">
            <ul class="nav flex-column gap-2">
                <c:forEach var="n" items="${nodes}">
                    <li class="nav-item">
                        <a href="${n.path}">
                            <i class="bi bi-dot"></i>
                            ${n.identifier}
                        </a>
                    </li>
                </c:forEach>
            </ul>
        </c:if>

    </div>

    <!-- ===== Main Content ===== -->
    <div class="content-area flex-grow-1">
        <div class="card shadow-sm">
            <div class="card-body">
                <h4>Welcome</h4>
                <p class="text-muted">
                    Select an option from the sidebar to continue.
                </p>
            </div>
        </div>
    </div>

</div>

</body>
</html>