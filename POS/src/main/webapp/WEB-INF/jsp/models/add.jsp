<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Model</title>

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
            <h4>Add Model</h4>
            <p>Create a new model</p>
        </div>
        <div class="form-card">
            <form action="${pageContext.request.contextPath}/models/add" method="post">
                <div class="form-group">
                    <label for="identifier">Identifier</label>
                    <input type="text"
                           class="form-control"
                           id="identifier"
                           name="identifier"
                           placeholder="Enter model identifier"
                           required>
                </div>
                <div class="form-group">
                    <label for="modelName">Model Name</label>
                    <input type="text"
                           class="form-control"
                           id="modelName"
                           name="modelName"
                           placeholder="Enter model name"
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
                        <option value="false">Deactive</option>
                    </select>
                </div>
                <c:if test="${not empty message}">
                    <div class="alert alert-danger">
                        ${message}
                    </div>
                </c:if>
                <div class="d-flex justify-content-between">
                    <a href="${pageContext.request.contextPath}/models/list"
                       class="btn btn-secondary btn-sm">
                        Back
                    </a>
                    <button type="submit"
                            class="btn btn-primary btn-sm">
                        Save Model
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>