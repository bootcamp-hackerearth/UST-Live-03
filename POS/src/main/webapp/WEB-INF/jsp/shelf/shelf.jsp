<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Update Shelf</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css">

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            background-color: #f4f6fb;
        }

        .content {
            margin: 50px auto;
            padding: 20px;
        }

        .page-wrapper {
            max-width: 600px;
            margin: 0 auto;
        }

        .header-banner {
            background: linear-gradient(135deg, #4e73df, #6f42c1);
            color: #ffffff;
            padding: 18px 22px;
            border-radius: 10px;
            margin-bottom: 20px;
        }

        .form-card {
            background: #ffffff;
            border-radius: 10px;
            padding: 22px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.06);
        }

        label {
            font-weight: 500;
        }
    </style>
</head>
<body>
<div class="content">
    <div class="page-wrapper">
        <div class="header-banner">
            <h4>Update Shelf</h4>
            <p>Edit shelf details</p>
        </div>
        <div class="form-card">
            <form action="${pageContext.request.contextPath}/shelf/update" method="post">
                <div class="form-group">
                    <label>Identifier</label>
                    <input type="text"
                           class="form-control"
                           name="identifier"
                           value="${shelf.identifier}"
                           readonly>
                </div>
                <div class="form-group">
                    <label>Shelf Name</label>
                    <input type="text"
                           class="form-control"
                           name="name"
                           value="${shelf.name}"
                           required>
                </div>
                <div class="form-group">
                    <label>Status</label>
                    <select class="form-control"
                            name="status"
                            required>
                        <option value="true"
                                <c:if test="${shelf.status}">selected</c:if>>
                            Active
                        </option>
                        <option value="false"
                                <c:if test="${not shelf.status}">selected</c:if>>
                            Inactive
                        </option>
                    </select>
                </div>
                <c:if test="${not empty message}">
                    <div class="alert alert-danger">
                        ${message}
                    </div>
                </c:if>
                <div class="d-flex justify-content-between">
                    <a href="${pageContext.request.contextPath}/shelf/list"
                       class="btn btn-secondary btn-sm">
                        Back
                    </a>
                    <button type="submit"
                            class="btn btn-primary btn-sm">
                        Update Shelf
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>