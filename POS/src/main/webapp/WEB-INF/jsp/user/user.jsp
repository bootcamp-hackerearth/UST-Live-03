<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update User</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        body {
            min-height: 100vh;
            background-color: #f6f7fb;
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .update-card {
            width: 520px;
            background: #ffffff;
            padding: 30px 35px;
            border-radius: 14px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
        }

        h3 {
            text-align: center;
            margin-bottom: 25px;
            font-weight: 600;
        }

        label {
            font-weight: 600;
            font-size: 14px;
        }

        .form-control,
        select {
            border-radius: 8px;
            padding: 10px 12px;
            font-size: 14px;
        }

        select[multiple] {
            height: 120px;
        }

        .btn-update {
            width: 100%;
            padding: 12px;
            background-color: #5563DE;
            border: none;
            color: white;
            font-weight: 600;
            border-radius: 30px;
        }

        .btn-update:hover {
            background-color: #3f4acb;
        }

        .badge {
            font-size: 12px;
            padding: 6px 10px;
            border-radius: 12px;
        }

        a {
            font-weight: 500;
            color: #5563DE;
            text-decoration: none;
        }

        a:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>

<c:if test="${not empty message}">
    <div class="alert alert-danger text-center position-absolute top-0 mt-3">
        ${message}
    </div>
</c:if>

<div class="update-card">

    <h3>
        <i class="bi bi-pencil-square"></i>
        Update User
    </h3>

    <form:form action="/user/update" method="post" modelAttribute="userDto">

        <form:hidden path="id" value="${user.id}" />
        <div class="mb-3">
            <label>Name</label>
            <form:input path="name"
                        cssClass="form-control"
                        value="${user.name}"
                        required="true" />
        </div>
        <div class="mb-3">
            <label>Email</label>
            <form:input path="username"
                        type="email"
                        cssClass="form-control"
                        value="${user.username}"
                        required="true" />
            <form:errors path="username" cssClass="text-danger small" />
        </div>
        <div class="mb-3">
            <label>Phone Number</label>
            <form:input path="phoneNo"
                        cssClass="form-control"
                        value="${user.phoneNo}"
                        maxlength="10"
                        pattern="[0-9]{10}"
                        title="Phone number must be exactly 10 digits"
                        required="true" />
        </div>
        <div class="mb-3">
            <label>Roles</label>
            <div class="mb-2 text-muted">
                Current:
                <c:forEach var="r" items="${user.roles}">
                    <span class="badge bg-secondary me-1">${r}</span>
                </c:forEach>
            </div>

            <form:select path="roles" multiple="true" cssClass="form-control">
                <form:options items="${roles}"
                              itemValue="identifier"
                              itemLabel="identifier" />
            </form:select>

            <small class="text-muted">
                Hold Ctrl (Windows/Linux) or Cmd (Mac) to select multiple
            </small>
        </div>
        <button type="submit" class="btn-update">
            <i class="bi bi-check-circle"></i>
            Update
        </button>

    </form:form>
    <div class="text-center mt-3">
        <a href="/user/list">
            <i class="bi bi-arrow-left-circle"></i>
            Back to User List
        </a>
    </div>

</div>

</body>
</html>