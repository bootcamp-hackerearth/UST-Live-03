<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Category</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css">

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Poppins', sans-serif;
            background-color: #f4f6fb;
        }

        .card {
            width: 450px;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
        }

        .card-header {
            background: linear-gradient(135deg, #4e73df, #6f42c1);
            color: #ffffff;
            text-align: center;
            font-weight: 600;
            border-radius: 12px 12px 0 0;
        }

        .form-control {
            border-radius: 8px;
            font-size: 14px;
        }

        .btn-primary {
            background-color: #4e73df;
            border: none;
            border-radius: 8px;
            font-weight: 500;
        }

        .btn-primary:hover {
            background-color: #224abe;
        }
    </style>
</head>

<body>
<div class="card">
    <div class="card-header">
        Add Category
    </div>
    <div class="card-body">
        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>
        <form action="${pageContext.request.contextPath}/category/add" method="post">
            <div class="form-group">
                <label>Category Identifier</label>
                <input type="text"
                       name="identifier"
                       class="form-control"
                       placeholder="Enter unique identifier"
                       required>
            </div>
            <div class="form-group">
                <label>Category Name</label>
                <input type="text"
                       name="name"
                       class="form-control"
                       placeholder="Enter category name"
                       required>
            </div>
            <div class="form-group">
                <label>Super Category</label>
                <select name="superCategory"
                        class="form-control"
                        multiple>
                    <c:forEach var="sc" items="${category}">
                        <option value="${sc.name}">
                            ${sc.name}
                        </option>
                    </c:forEach>
                </select>
            </div>
            <button type="submit" class="btn btn-primary btn-block">
                Add Category
            </button>
            <a href="${pageContext.request.contextPath}/category/list"
               class="btn btn-outline-secondary btn-block mt-2">
                Cancel
            </a>
        </form>
    </div>
</div>
</body>
</html>