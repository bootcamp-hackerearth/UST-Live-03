<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update User</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

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

        .error {
            font-size: 12px;
            color: #d9534f;
            margin-top: 4px;
            display: block;
        }

        small {
            font-size: 11px;
            color: #666;
        }
    </style>
</head>

<body>

<div class="update-card">
    <h3>Update User</h3>

    <form:form action="${pageContext.request.contextPath}/user/update"
               method="post"
               modelAttribute="userDto">

        <form:hidden path="id"/>

        <div class="mb-3">
            <label>Name</label>
            <form:input path="name"
                        cssClass="form-control"
                        required="true"/>
            <form:errors path="name" cssClass="error"/>
        </div>

        <div class="mb-3">
            <label>Email</label>
            <form:input path="username"
                        cssClass="form-control"
                        type="email"
                        required="true"
                        pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$"
                        title="Email must be lowercase and contain @"/>
            <form:errors path="username" cssClass="error"/>
        </div>

        <div class="mb-3">
            <label>Phone Number</label>
            <form:input path="phoneNo"
                        cssClass="form-control"
                        pattern="[0-9]{10}"
                        title="Phone number must be exactly 10 digits"
                        required="true"/>
            <form:errors path="phoneNo" cssClass="error"/>
        </div>

        <div class="mb-3">
            <label>Roles</label>

            <div class="mb-1 text-muted">
                Current:
                <c:forEach var="r" items="${userDto.roles}">
                    <span class="badge bg-secondary me-1">${r}</span>
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
            <form:errors path="roles" cssClass="error"/>

            <small>
                Hold Ctrl (Windows/Linux) or Cmd (Mac) to select multiple
            </small>

                        <div class="mb-4">
                            <label class="form-label fw-bold">Status</label>
                            <form:select path="status" class="form-select">
                                <form:option value="true">Active</form:option>
                                <form:option value="false">Deactive</form:option>
                            </form:select>
                        </div>
        </div>

        <button type="submit" class="btn-update">
            Update User
        </button>

    </form:form>

    <div class="text-center mt-3">
        <a href="${pageContext.request.contextPath}/user/list">
            ← Back to User List
        </a>
    </div>
</div>

</body>
</html>