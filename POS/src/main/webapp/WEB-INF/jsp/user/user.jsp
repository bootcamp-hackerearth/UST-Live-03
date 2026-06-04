<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update User</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

    <style>
        body {
            background-color: #f3f5f7;
            font-family: "Segoe UI", sans-serif;
        }

        .page-container {
            max-width: 750px;
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

        .form-control {
            border-radius: 8px;
        }

        .form-control:focus {
            box-shadow: none;
            border-color: #0d6efd;
        }

        .error {
            color: #dc3545;
            font-size: 13px;
            margin-top: 5px;
        }

        .message-box {
            background-color: #f8d7da;
            color: #842029;
            border: 1px solid #f5c2c7;
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 20px;
            text-align: center;
        }

        .role-badge {
            background-color: #e7f1ff;
            color: #0d6efd;
            border: 1px solid #b6d4fe;
            font-weight: 500;
            padding: 6px 10px;
            border-radius: 20px;
            display: inline-block;
            margin-right: 5px;
            margin-bottom: 5px;
        }

        .hint {
            font-size: 13px;
            color: #6c757d;
            margin-top: 5px;
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
                <h3>Update User</h3>
            </div>

            <div class="card-body">

                <c:if test="${not empty message}">
                    <div class="message-box">
                        ${message}
                    </div>
                </c:if>

                <form:form action="/user/update"
                           method="post"
                           modelAttribute="user">

                    <form:hidden path="id"/>

                    <div class="mb-4">
                        <label class="form-label">
                            Name
                        </label>

                        <form:input path="name"
                                    cssClass="form-control"
                                    required="true"/>

                        <form:errors path="name"
                                     cssClass="error"/>
                    </div>

                    <div class="mb-4">
                        <label class="form-label">
                            Email
                        </label>

                        <form:input path="username"
                                    type="email"
                                    cssClass="form-control"
                                    required="true"/>

                        <form:errors path="username"
                                     cssClass="error"/>
                    </div>

                    <div class="mb-4">
                        <label class="form-label">
                            Phone Number
                        </label>

                        <form:input path="phoneNo"
                                    cssClass="form-control"
                                    pattern="[0-9]{10}"
                                    title="Enter a valid 10 digit phone number"
                                    required="true"/>

                        <form:errors path="phoneNo"
                                     cssClass="error"/>
                    </div>

                    <div class="mb-4">

                        <label class="form-label">
                            Roles
                        </label>

                        <div class="mb-2">

                            <c:forEach var="r" items="${user.roles}">
                                <span class="role-badge">
                                    ${r}
                                </span>
                            </c:forEach>

                        </div>

                        <form:select path="roles"
                                     multiple="true"
                                     cssClass="form-control"
                                     required="true">

                            <form:options items="${roles}"
                                          itemValue="identifier"
                                          itemLabel="identifier"/>

                        </form:select>

                        <div class="hint">
                            Hold Ctrl (Windows) or Cmd (Mac) to select multiple roles.
                        </div>

                    </div>
                    <div class="d-flex justify-content-between mt-4">

                        <a href="/user/list"
                           class="btn btn-secondary">
                            Cancel
                        </a>

                        <button type="submit"
                                class="btn btn-primary">
                            Update User
                        </button>

                    </div>

                </form:form>

            </div>

        </div>

    </div>

</div>

</body>
</html>