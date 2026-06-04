<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <style>
        body {
            background: #f5f7fb;
        }

        .page-container {
            max-width: 600px;
            margin: auto;
        }

        .card-custom {
            background: #ffffff;
            border-radius: 14px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            padding: 30px;
        }

        h2 {
            text-align: center;
            font-weight: 600;
            margin-bottom: 25px;
            color: #0d6efd;
        }

        .form-label {
            font-weight: 600;
            color: #333;
        }

        .form-control {
            border-radius: 8px;
            padding: 10px;
        }

        .form-control:focus {
            border-color: #0d6efd;
            box-shadow: none;
        }

        .roles-box {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 15px;
        }

        .form-check {
            margin-bottom: 8px;
        }

        .btn-primary {
            padding: 10px 30px;
            border-radius: 30px;
            font-weight: 600;
        }

        .btn-secondary {
            padding: 10px 30px;
            border-radius: 30px;
            font-weight: 600;
        }

        .action-buttons {
            text-align: center;
            margin-top: 25px;
        }
    </style>
</head>

<body>

<div class="container mt-5 page-container">
    <div class="card-custom">

        <h2>Edit Node</h2>

        <form:form method="post"
                   action="${pageContext.request.contextPath}/node/update"
                   modelAttribute="node">

            <form:hidden path="id"/>

            <div class="mb-3">
                <label class="form-label">Node ID</label>
                <form:input path="id" class="form-control" readonly="true"/>
            </div>

            <div class="mb-3">
                <label class="form-label">Node Name</label>
                <form:input path="identifier" class="form-control" required="true" readonly="true"/>
            </div>

            <div class="mb-3">
                <label class="form-label">Node Path</label>
                <form:input path="path" class="form-control" required="true"/>
            </div>

            <div class="mb-3">
                <label class="form-label">Select Roles</label>

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
                <button type="submit" class="btn btn-primary">Update</button>

                <a href="${pageContext.request.contextPath}/node/list"
                   class="btn btn-secondary ms-3">Cancel</a>
            </div>

        </form:form>

    </div>
</div>

</body>
</html>