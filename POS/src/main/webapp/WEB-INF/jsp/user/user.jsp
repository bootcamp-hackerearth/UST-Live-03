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
            background: #2f2f2f;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .update-card {
            width: 340px;
            background: #ffffff;
            padding: 22px;
            border-radius: 14px;
            box-shadow: 0 14px 35px rgba(0, 0, 0, 0.25);
        }

        h3 {
            text-align: center;
            margin-bottom: 14px;
            font-weight: 600;
            color: #333;
            font-size: 19px;
        }

        label {
            font-weight: 600;
            font-size: 0.8rem;
            color: #555;
            margin-bottom: 3px;
        }

        .form-control,
        select {
            border-radius: 7px;
            padding: 7px 10px;
            font-size: 0.8rem;
        }

        .mb-3 {
            margin-bottom: 10px;
        }

        select[multiple] {
            height: 80px;
        }

        .badge {
            font-size: 10px;
            padding: 4px 7px;
        }

        .btn-update {
            width: 100%;
            padding: 9px;
            background: linear-gradient(90deg, #4facfe, #00f2fe);
            border: none;
            color: white;
            font-weight: 600;
            border-radius: 22px;
            font-size: 14px;
            margin-top: 6px;
        }

        .btn-update:hover {
            opacity: 0.9;
        }

        .back-link {
            display: block;
            text-align: center;
            margin-top: 12px;
            font-weight: 600;
            font-size: 13px;
            text-decoration: none;
            color: #0d6efd;
        }

        .back-link:hover {
            text-decoration: underline;
        }

        .alert {
            font-size: 13px;
            padding: 8px;
        }
    </style>
</head>

<body>

<div class="update-card">

    <h3>Update User</h3>

    <c:if test="${not empty message}">
        <div class="alert alert-danger text-center">
            ${message}
        </div>
    </c:if>

    <form:form action="/user/update" method="post" modelAttribute="user">

        <form:hidden path="id"/>

        <div class="mb-3">
            <label>Name</label>
            <form:input path="name" cssClass="form-control" required="true"/>
        </div>

        <div class="mb-3">
            <label>Email</label>
            <form:input path="username"
                        type="email"
                        cssClass="form-control"
                        required="true"/>
        </div>

        <div class="mb-3">
            <label>Phone Number</label>
            <form:input path="phoneNo"
                        cssClass="form-control"
                        pattern="[0-9]{10}"
                        title="Enter 10 digit phone number"
                        required="true"/>
        </div>

        <div class="mb-3">
            <label>Roles</label>

            <div class="mb-1 text-muted">
                <c:forEach var="r" items="${user.roles}">
                    <span class="badge bg-secondary me-1">${r}</span>
                </c:forEach>
            </div>

            <form:select path="roles" multiple="true" cssClass="form-control">
                <form:options items="${roles}"
                              itemValue="identifier"
                              itemLabel="identifier"/>
            </form:select>
        </div>

        <button type="submit" class="btn-update">
            Update User
        </button>

    </form:form>

    <a href="/user/list" class="back-link">
        ← Back to User List
    </a>

</div>

</body>
</html>