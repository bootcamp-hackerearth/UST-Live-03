<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Update User</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>
    <style>
        body {
            min-height: 100vh;
            background-color: #FFF8F0;
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .update-card {
            width: 460px;
            background: #ffffff;
            padding: 30px 35px;
            border-radius: 18px;
            box-shadow: 0 18px 35px rgba(75, 46, 43, 0.25);
        }

        h3 {
            text-align: center;
            color: #4B2E2B;
            margin-bottom: 25px;
            font-weight: 600;
        }

        label {
            font-weight: 600;
            font-size: 14px;
            color: #4B2E2B;
        }

        .form-control,
        select {
            border-radius: 8px;
            padding: 10px 12px;
            border: 1px solid #ccb7b2;
        }

        .form-control:focus,
        select:focus {
            border-color: #4B2E2B;
            box-shadow: none;
        }

        select[multiple] {
            height: 120px;
        }

        .btn-update {
            width: 100%;
            padding: 12px;
            background-color: #4B2E2B;
            border: none;
            color: white;
            font-weight: 600;
            border-radius: 10px;
        }

        .btn-update:hover {
            background-color: #3a2421;
        }

        .badge {
            background-color: #6b4a46;
        }

        small {
            font-size: 11px;
            color: #6b4a46;
        }

        a {
            color: #4B2E2B;
            text-decoration: none;
            font-weight: 500;
        }

        a:hover {
            text-decoration: underline;
            color: #3a2421;
        }
    </style>
</head>
<body>
<div class="update-card">
    <h3>Update User</h3>
    <form:form action="/user/update" method="post" modelAttribute="userDto">
        <form:input type="hidden" path="id"/>
        <div class="mb-3">
            <label>Name</label>
            <form:input path="name" cssClass="form-control" required="true"/>
        </div>
        <div class="mb-3">
            <label>Email</label>
            <form:input path="username" cssClass="form-control" required="true"/>
        </div>
        <div class="mb-3">
            <label>Phone Number</label>
            <form:input path="phoneNo" cssClass="form-control" required="true"/>
        </div>
        <div class="mb-3">
            <label>Roles</label>
            <div class="mb-1 text-muted">
                Current:
                <c:forEach var="r" items="${userDto.roles}">
                    <span class="badge me-1">${r}</span>
                </c:forEach>
            </div>
            <form:select path="roles" multiple="true" cssClass="form-control">
                <form:options items="${roles}"
                    itemValue="identifier"
                    itemLabel="identifier"
                />
            </form:select>
            <small>
                Hold Ctrl (Windows/Linux) or Cmd (Mac) to select multiple
            </small>
        </div>
        <button type="submit" class="btn-update mt-2">
            Update User
        </button>
    </form:form>
    <div class="text-center mt-3">
        <a href="/user/list">← Back to User List</a>
    </div>
</div>
</body>
</html>