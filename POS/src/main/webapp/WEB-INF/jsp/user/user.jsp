<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Update User</title>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Poppins', sans-serif;
}

body {
    min-height: 100vh;
    background: linear-gradient(135deg, #1a1b26, #2a2b3d);
    display: flex;
    justify-content: center;
    align-items: center;
}

.card {
    width: 460px;
    padding: 30px;
    border-radius: 15px;

    background: rgba(255,255,255,0.05);
    backdrop-filter: blur(12px);

    border: 1px solid rgba(255,255,255,0.2);
    box-shadow: 0 8px 32px rgba(0,0,0,0.4);
}

.card-header {
    text-align: center;
    margin-bottom: 20px;
}

.card-header h5 {
    color: #fff;
}

.form-label {
    font-size: 13px;
    color: #ccc;
}

.form-control {
    width: 100%;
    padding: 10px;
    margin-top: 6px;
    margin-bottom: 15px;

    border-radius: 8px;
    border: none;
    outline: none;

    background: rgba(255,255,255,0.1);
    color: #fff;
}

.form-control:focus {
    border: 1px solid #00ffff;
    box-shadow: 0 0 8px #00ffff;
}

select[multiple] {
    height: 120px;
}

.current-roles {
    font-size: 12px;
    margin-bottom: 8px;
    color: #aaa;
}

.badge {
    background: rgba(0,255,255,0.2);
    color: #00ffff;
    padding: 4px 8px;
    border-radius: 6px;
    margin-right: 4px;
}

.btn-primary {
    width: 100%;
    padding: 12px;
    border-radius: 8px;
    border: none;

    background: #00ffff;
    color: #000;
    font-weight: bold;
    cursor: pointer;
}

.btn-primary:hover {
    box-shadow: 0 0 15px #00ffff,
                0 0 30px #00ffff;
}

.alert {
    text-align: center;
    padding: 10px;
    margin-bottom: 12px;
    border-radius: 6px;
    color: #00ffcc;
    font-size: 13px;
}

.card-footer {
    text-align: center;
    margin-top: 10px;
    font-size: 12px;
}

.card-footer a {
    color: #00ffff;
    text-decoration: none;
}

.card-footer a:hover {
    text-decoration: underline;
}
</style>
</head>

<body>

<div class="card">

    <div class="card-header">
        <h5>Update User</h5>
    </div>

    <c:if test="${not empty message}">
        <div class="alert">
            ${message}
        </div>
    </c:if>

    <form:form action="/user/update" method="post" modelAttribute="userDto">

        <form:hidden path="id"/>

        <label class="form-label">Name</label>
        <form:input path="name"
                    cssClass="form-control"
                    required="true"/>

        <label class="form-label">Email</label>
        <form:input path="username"
                    cssClass="form-control"/>

        <label class="form-label">Phone Number</label>
        <form:input path="phoneNo"
                    cssClass="form-control"
                    required="true"/>

        <label class="form-label">Roles</label>

        <div class="current-roles">
            Current:
            <c:forEach var="r" items="${userDto.roles}">
                <span class="badge">${r}</span>
            </c:forEach>
        </div>

        <form:select path="roles"
                     multiple="true"
                     cssClass="form-control">
            <form:options items="${roles}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>

        <div class="current-roles">
            Hold Ctrl (Windows/Linux) or Cmd (Mac) to select multiple roles
        </div>

        <button type="submit" class="btn-primary">
            Update User
        </button>

    </form:form>

    <div class="card-footer">
        <a href="/user/list">← Back to User List</a>
    </div>

</div>

</body>
</html>