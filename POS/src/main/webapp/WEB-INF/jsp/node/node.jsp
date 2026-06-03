<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        /* Main card matches add.jsp */
        .card {
            position: relative;
            width: 520px;
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        /* Heading */
        h4 {
            text-align: center;
            margin-bottom: 25px;
            color: #4b6cb7;
            font-weight: 600;
        }

        /* Labels */
        .form-label {
            display: block;
            margin-bottom: 6px;
            font-weight: 500;
            color: #333;
        }

        /* Inputs & Select */
        .form-control {
            width: 100%;
            padding: 10px 12px;
            border-radius: 10px;
            border: 1px solid #ccc;
            font-family: 'Poppins', sans-serif;
            font-size: 14px;
        }

        .form-control:focus {
            outline: none;
            border-color: #4b6cb7;
            box-shadow: 0 0 0 2px rgba(75, 108, 183, 0.2);
        }

        /* Multiple select hint */
        small {
            font-size: 12px;
            color: #777;
        }

        /* Buttons */
        .btn-primary {
            padding: 10px 22px;
            border-radius: 10px;
            border: none;
            cursor: pointer;
            font-weight: 600;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
            transition: 0.25s ease;
        }

        .btn-primary:hover {
            transform: scale(1.05);
        }

        /* Cancel button */
        .btn-outline-secondary {
            border-radius: 10px;
            font-weight: 500;
        }

        /* Alerts */
        .alert {
            border-radius: 8px;
            font-size: 14px;
        }

    </style>
</head>

<body>

${message}

<div class="card shadow-lg">
    <div class="card-body">

        <h4 class="text-center mb-4 text-primary">Edit Node</h4>

        <c:if test="${empty node}">
            <div class="alert alert-danger text-center">
                Node not found
            </div>
        </c:if>

        <c:if test="${not empty node}">
            <form:form action="/node/update"
                       method="post"
                       modelAttribute="node">

                <!-- ID (hidden) -->
                <form:hidden path="id"/>

                <!-- Node Name -->
                <div class="mb-3">
                    <label class="form-label">Node Name</label>
                    <form:input path="identifier"
                                cssClass="form-control"
                                required="true"
                                readonly="true"
                                />
                </div>

                <!-- ✅ Path -->
                <div class="mb-3">
                    <label class="form-label">Path</label>
                    <form:input path="path"
                                cssClass="form-control"
                                required="true"/>
                </div>

                <!-- ✅ Roles -->
                <div class="mb-4">
                    <label class="form-label">Roles</label>

                    <form:select path="roles"
                                 multiple="true"
                                 cssClass="form-control">

                        <form:options items="${roles}"
                                      itemValue="identifier"
                                      itemLabel="identifier"/>

                    </form:select>

                    <small class="text-muted">
                        Hold Ctrl (Windows/Linux) or Cmd (Mac) to select multiple roles
                    </small>
                </div>

                <div class="d-flex justify-content-between">
                    <a href="/node/list" class="btn btn-outline-secondary">
                        Cancel
                    </a>

                    <button type="submit" class="btn btn-primary">
                        Update
                    </button>
                </div>
            </form:form>
        </c:if>

    </div>
</div>

</body>
</html>
