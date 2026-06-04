<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Node</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>

        body {
            background-color: #f3f5f7;
            font-family: "Segoe UI", sans-serif;
        }

        .page-container {
            max-width: 700px;
            margin: 50px auto;
        }

        .card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .card-header {
            background: white;
            border-bottom: 1px solid #e9ecef;
            padding: 20px 25px;
        }

        .card-header h3 {
            margin: 0;
            color: #2c3e50;
            font-weight: 600;
        }

        .card-body {
            padding: 30px;
        }

        .form-label {
            font-weight: 600;
            color: #495057;
        }

        .form-control,
        .form-select {
            border-radius: 8px;
        }

        .form-control:focus,
        .form-select:focus {
            box-shadow: none;
            border-color: #0d6efd;
        }

        .input-error {
            border: 2px solid #dc3545 !important;
            background-color: #fff5f5;
        }

        .hint {
            font-size: 13px;
            color: #6c757d;
            margin-top: 5px;
        }

        .error-box {
            background-color: #f8d7da;
            color: #842029;
            border: 1px solid #f5c2c7;
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 20px;
            text-align: center;
        }

        .btn {
            min-width: 120px;
        }

    </style>
</head>

<body>

<div class="container-fluid">

    <div class="page-container">

        <div class="card">

            <div class="card-header">
                <h3>Add New Node</h3>
            </div>

            <div class="card-body">

                <c:if test="${not empty error}">
                    <div class="error-box">
                        ${error}
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/node/add"
                      method="post">

                    <div class="mb-4">
                        <label class="form-label">
                            Node Name
                        </label>

                        <input type="text"
                               name="identifier"
                               class="form-control ${not empty error && error.contains('already') ? 'input-error' : ''}"
                               placeholder="e.g. Dashboard"
                               value="${param.identifier}"
                               required>
                    </div>

                    <div class="mb-4">
                        <label class="form-label">
                            Path
                        </label>

                        <input type="text"
                               name="path"
                               class="form-control"
                               placeholder="e.g. /admin/home"
                               value="${param.path}"
                               required>
                    </div>

                    <div class="mb-4">
                        <label class="form-label">
                            Roles
                        </label>

                        <select name="roles"
                                class="form-select"
                                multiple
                                size="5">

                            <c:forEach var="role" items="${roles}">
                                <option value="${role.identifier}">
                                    ${role.identifier}
                                </option>
                            </c:forEach>

                        </select>

                        <div class="hint">
                            Hold Ctrl (Windows) or Cmd (Mac) to select multiple roles.
                        </div>
                    </div>

                    <div class="d-flex justify-content-between mt-4">

                        <a href="${pageContext.request.contextPath}/node/list"
                           class="btn btn-secondary">
                            Cancel
                        </a>

                        <button type="submit"
                                class="btn btn-primary">
                            Save Node
                        </button>

                    </div>

                </form>

            </div>

        </div>

    </div>

</div>

</body>
</html>