<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Unit</title>

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
            <h4>Add Unit</h4>
            <p>Create a new unit</p>
        </div>
        <div class="form-card">
            <form action="${pageContext.request.contextPath}/unit/add" method="post">
                <div class="form-group">
                    <label for="identifier">Identifier</label>
                    <input type="text"
                           class="form-control"
                           id="identifier"
                           name="identifier"
                           placeholder="Enter unit identifier"
                           required>
                </div>
                <div class="form-group">
                    <label for="name">Unit Name</label>
                    <input type="text"
                           class="form-control"
                           id="name"
                           name="name"
                           placeholder="Enter unit name"
                           required>
                </div>
                <div class="form-group">
                    <label for="status">Status</label>
                    <select class="form-control"
                            id="status"
                            name="status"
                            required>
                        <option value="">-- Select Status --</option>
                        <option value="true">Active</option>
                        <option value="false">Inactive</option>
                    </select>
                </div>
                <c:if test="${not empty message}">
                    <div class="alert alert-danger">
                        ${message}
                    </div>
                </c:if>
                <div class="d-flex justify-content-between">
                    <a href="${pageContext.request.contextPath}/unit/list"
                       class="btn btn-secondary btn-sm">
                        Back
                    </a>
                    <button type="submit"
                            class="btn btn-primary btn-sm">
                        Save Unit
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>