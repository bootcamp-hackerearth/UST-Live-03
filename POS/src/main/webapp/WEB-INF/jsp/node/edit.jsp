<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        body {
            min-height: 100vh;
            background-color: #f6f7fb;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .page-container {
            max-width: 640px;
            width: 100%;
        }

        .card-custom {
            background: #fff;
            border-radius: 16px;
            padding: 30px 35px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
        }

        h2 {
            text-align: center;
            font-weight: 600;
            margin-bottom: 25px;
        }

        .form-label {
            font-weight: 600;
            font-size: 14px;
        }

        .form-control {
            border-radius: 8px;
            padding: 10px 12px;
        }

        .roles-box {
            background: #f8f9fa;
            border-radius: 12px;
            padding: 15px;
        }

        .form-check {
            margin-bottom: 8px;
        }

        .btn-primary,
        .btn-secondary {
            padding: 12px 32px;
            border-radius: 30px;
            font-weight: 600;
        }

        .action-buttons {
            text-align: center;
            margin-top: 30px;
        }
    </style>
</head>

<body>

<div class="page-container">
    <div class="card-custom">

        <h2>
            <i class="bi bi-pencil-square"></i>
            Edit Node
        </h2>

        <form:form method="post"
                   action="${pageContext.request.contextPath}/node/update"
                   modelAttribute="node">

            <form:hidden path="id"/>

            <div class="mb-3">
                <label class="form-label">Node ID</label>
                <form:input path="id" class="form-control" readonly="true"/>
            </div>

            <div class="mb-3">
                <label class="form-label">Identifier</label>
                <form:input path="identifier" class="form-control" readonly="true"/>
            </div>

            <div class="mb-3">
                <label class="form-label">Node Path</label>
                <form:input path="path" class="form-control" required="true"/>
            </div>

            <div class="mb-3">
                <label class="form-label">Assign Roles</label>

                <div class="roles-box">
                    <form:checkboxes
                            path="roles"
                            items="${roles}"
                            itemValue="identifier"
                            itemLabel="identifier"
                            element="div"
                            cssClass="form-check-input me-2"
                            labelClass="form-check-label"/>
                </div>
            </div>

            <div class="action-buttons">
                <button type="submit" class="btn btn-primary me-3">
                    <i class="bi bi-check-circle"></i>
                    Update
                </button>

                <a href="${pageContext.request.contextPath}/node/list"
                   class="btn btn-secondary">
                    <i class="bi bi-arrow-left-circle"></i>
                    Cancel
                </a>
            </div>

        </form:form>

    </div>
</div>

</body>
</html>