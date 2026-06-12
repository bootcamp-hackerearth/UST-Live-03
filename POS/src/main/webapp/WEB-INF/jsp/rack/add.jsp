<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Entity</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            background-color: #f4f7f6;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .card {
            width: 420px;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }

        .card-header {
            background-color: #0d6efd;
            color: white;
            border-radius: 15px 15px 0 0;
        }

        .form-label {
            font-weight: 600;
        }
    </style>
</head>
<body>

<div class="card">

    <div class="card-header text-center py-3">
        <h4 class="mb-0">Add New Entry</h4>
    </div>

    <div class="card-body p-4">

        <!-- ✅ Message -->
        <c:if test="${not empty message}">
            <div class="alert ${success ? 'alert-success' : 'alert-danger'} text-center">
                ${message}
            </div>
        </c:if>

        <!-- ✅ Add Form -->
        <form action="${pageContext.request.contextPath}/rack/add" method="post">

            <!-- Identifier -->
            <div class="mb-3">
                <label class="form-label">Identifier</label>
                <input type="text"
                       class="form-control"
                       name="identifier"
                       placeholder="Enter identifier"
                       required />
            </div>

            <!-- Shelfs -->
            <div class="mb-3">
                <label class="form-label">Shelfs</label>

                <select class="form-select" name="shelfs" required>
                    <option value="">-- Select Shelf --</option>

                    <c:forEach var="shelf" items="${shelves}">
                        <option value="${shelf.identifier}">
                            ${shelf.identifier}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <!-- Status -->
            <div class="mb-4">
                <label class="form-label">Status</label>
                <select class="form-select" name="status">
                    <option value="true" selected>Active</option>
                    <option value="false">Inactive</option>
                </select>
            </div>

            <!-- Buttons -->
            <div class="d-flex justify-content-between">
                <a href="${pageContext.request.contextPath}/rack/list"
                   class="btn btn-light">
                    Cancel
                </a>

                <button type="submit" class="btn btn-primary px-4">
                    Save
                </button>
            </div>

        </form>
    </div>
</div>

</body>
</html>