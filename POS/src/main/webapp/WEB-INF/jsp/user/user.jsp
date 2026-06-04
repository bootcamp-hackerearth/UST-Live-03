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
            min-height: 100vh;
            background: linear-gradient(135deg, #667eea, #764ba2);
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

        small {
            font-size: 11px;
            color: #666;
        }
    </style>
</head>

<body>
<div class="update-card">
    <h3>Update User</h3>

    <form:form action="/user/update" method="post" modelAttribute="userDto">

        <form:input type="hidden" path="id" value="${user.id}"/>
        <p style="color:red;">${message}</p>
        <div class="mb-3">
            <label>Name</label>
            <form:input path="name" cssClass="form-control" value="${user.name}" required="true"/>
        </div>

         <div class="mb-3">
             <label>Email</label>
             <form:input path="username" type="email"
                 cssClass="form-control"
                 value="${user.username}"
                 required="true"
                 pattern="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$" />
             <form:errors path="username" cssClass="error"/>
         </div>

        <div class="mb-3">
            <label>Phone Number</label>
            <form:input path="phoneNo" cssClass="form-control" value="${user.phoneNo}" pattern="[0-9]{10}" maxlength="10" required="true"   title="Phone number must be exactly 10 digits"/>
        </div>

        <div class="mb-3">
            <label>Roles</label>

            <div class="mb-1 text-muted">
                Current:
                <c:forEach var="r" items="${user.roles}">
                    <span class="badge bg-secondary me-1">${r}</span>
                </c:forEach>
            </div>

            <form:select path="roles" multiple="true" cssClass="form-control">
                <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
            </form:select>


            <small>
                Hold Ctrl (Windows/Linux) or Cmd (Mac) to select multiple
            </small>
        </div>

        <button type="submit" class="btn-update">Update</button>

    </form:form>

    <div class="text-center mt-3">
        <a href="/user/list">← Back to User List</a>
    </div>

</div>

</body>
</html>