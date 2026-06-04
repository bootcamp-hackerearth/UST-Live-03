<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Model</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #E9EEF5;
            min-height: 100vh;
        }

        .card {
            border-radius: 16px;
        }

        .form-control {
            border-radius: 10px;
        }

        .btn {
            border-radius: 10px;
        }
    </style>
</head>

<body>

<nav class="navbar navbar-dark bg-dark shadow">
    <div class="container-fluid">
        <span class="navbar-brand fw-bold">Model Management</span>
        <a href="${pageContext.request.contextPath}/models/list"
           class="btn btn-outline-light btn-sm">Back</a>
    </div>
</nav>

<div class="container d-flex justify-content-center align-items-center" style="min-height: 100vh;">
    <div class="card shadow p-4" style="width: 450px;">
        <h3 class="text-center mb-4 fw-bold">Add Model</h3>

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/models/save" method="post">

            <div class="mb-3">
                <label class="form-label fw-semibold">Model Name</label>
                <input type="text"
                       class="form-control"
                       name="modelName"
                       placeholder="Enter model name"
                       maxlength="50"
                       required>
            </div>

           <div class="mb-4">
               <label class="form-label fw-semibold">Status</label>

               <select class="form-control" name="status" required>
                   <option value="">-- Select Status --</option>
                   <option value="true">Active</option>
                   <option value="false">Inactive</option>
               </select>
           </div>

            <div class="d-flex gap-2">

                <button type="submit" class="btn btn-primary w-100">
                    Save Model
                </button>

                <a href="${pageContext.request.contextPath}/models/list"
                   class="btn btn-outline-secondary w-100">
                    Cancel
                </a>

            </div>

        </form>

    </div>
</div>

</body>
</html>