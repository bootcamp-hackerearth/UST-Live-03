<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update User</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet" />

    <style>
        body {
            min-height: 100vh;
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .update-card {
            width: 460px;
            background: #fff;
            padding: 30px 35px;
            border-radius: 15px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.25);
        }

        h3 {
            text-align: center;
            color: #4b6cb7;
            margin-bottom: 25px;
            font-weight: 600;
        }

        label {
            font-weight: 600;
            font-size: 14px;
            color: #333;
        }

        .form-control,
        select {
            border-radius: 8px;
            padding: 10px 12px;
        }

        select[multiple] {
            height: 120px;
        }

        .btn-update {
            width: 100%;
            padding: 12px;
            background: #4b6cb7;
            border: none;
            color: white;
            font-weight: 600;
            border-radius: 8px;
        }

        .btn-update:hover {
            background: #182848;
        }

        .back-link {
            text-decoration: none;
            font-weight: 500;
            color: #4b6cb7;
        }

        .back-link:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>

<c:if test="${not empty message}">
    <div class="alert alert-info text-center position-absolute top-0 mt-3">
        ${message}
    </div>
</c:if>

<div class="update-card">

    <h3>Update User</h3>

    <form:form action="/user/update" method="post" modelAttribute="user">

        <!-- Hidden ID -->
        <form:input type="hidden" path="id" />

        <!-- Name -->
        <div class="mb-3">
            <label>Name</label>
            <form:input path="name" cssClass="form-control" required="true" />
        </div>

        <!-- Email -->
        <div class="mb-3">
            <label>Email</label>
            <form:input type="email" path="username" cssClass="form-control" required="true" />
        </div>

        <!-- Phone -->
        <div class="mb-3">
            <label>Phone Number</label>
            <form:input
                path="phoneNo"
                cssClass="form-control"
                maxlength="10"
                pattern="[0-9]{10}"
                title="Enter exactly 10 digit phone number"
                oninput="this.value=this.value.replace(/[^0-9]/g,'')" />
        </div>

        <!-- Roles -->
        <div class="mb-3">
            <label>Roles</label>

            <div class="mb-2 text-muted">
                Current:
                <c:forEach var="r" items="${userDto.roles}">
                    <span class="badge bg-secondary me-1">${r}</span>
                </c:forEach>
            </div>

            <form:select path="roles" multiple="true" cssClass="form-control">
                <form:options items="${roles}"
                              itemValue="identifier"
                              itemLabel="identifier" />
            </form:select>
        </div>

        <!-- Submit -->
        <button type="submit" class="btn-update">
            Update User
        </button>

    </form:form>

    <!-- Back Link -->
    <div class="text-center mt-3">
        <a href="/user/list" class="back-link">← Back to User List</a>
    </div>
</div>
</body>
</html>