<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>POS | Update User</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
      rel="stylesheet"/>

<style>
body {
    min-height: 100vh;
    background: #f8fafc;
    display: flex;
    justify-content: center;
    align-items: center;
    font-family: "Segoe UI", Arial, sans-serif;
}

.update-card {
    width: 460px;
    background: white;
    padding: 30px 35px;
    border-radius: 16px;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

h3 {
    text-align: center;
    color: #0f172a;
    margin-bottom: 15px;
    font-weight: 600;
}

label {
    font-weight: 600;
    font-size: 14px;
    color: #334155;
}

.form-control,
.form-select {
    border-radius: 8px;
    padding: 10px 12px;
}

select[multiple] {
    height: 130px;
}

.btn-update {
    width: 100%;
    padding: 12px;
    background: #0f766e;
    border: none;
    color: white;
    font-weight: 600;
    border-radius: 8px;
}

.btn-update:hover {
    background: #115e59;
}

.alert {
    border-radius: 8px;
}

.back-link {
    color: #0f766e;
    text-decoration: none;
    font-weight: 600;
}

.back-link:hover {
    text-decoration: underline;
}

small {
    font-size: 11px;
    color: #64748b;
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

    <form action="${pageContext.request.contextPath}/user/update" method="post">

        <input type="hidden" name="oldUsername" value="${user.username}" />

        <input type="hidden" name="id" value="${user.id}" />

        <div class="mb-3">
            <label>Name</label>
            <input type="text" name="name" class="form-control"
                   value="${user.name}" required />
        </div>

        <div class="mb-3">
            <label>Email</label>
            <input type="email" name="username" class="form-control"
                   value="${user.username}" required />
        </div>

        <div class="mb-3">
            <label>Phone Number</label>
            <input type="text" name="phoneNo" class="form-control"
                   value="${user.phoneNo}" required />
        </div>

        <div class="mb-3">
            <label class="fw-semibold">Select Roles</label>
            <select name="roles" id="roles" class="form-select" multiple required size="5">

                <c:forEach var="role" items="${roles}">
                    <option value="${role.identifier}"
                        <c:if test="${user.roles.contains(role.identifier)}">
                            selected
                        </c:if>>
                        ${role.identifier}
                    </option>
                </c:forEach>

            </select>

            <small>
                Hold <kbd>Ctrl</kbd> (Windows/Linux) or <kbd>Cmd</kbd> (Mac)
            </small>
        </div>

        <button type="submit" class="btn-update">
            Update User
        </button>

    </form>

    <div class="text-center mt-3">
        <a href="/user/list" class="back-link">
            ← Back to User List
        </a>
    </div>

</div>

<script>
document.querySelector("form").addEventListener("submit", function (e) {

    const name = this.name.value.trim();
    const phone = this.phoneNo.value.trim();
    const roles = document.getElementById("roles");

    if (!/^[A-Za-z ]{3,}$/.test(name)) {
        alert("Name must contain at least 3 letters.");
        e.preventDefault();
        return;
    }

    if (!/^[0-9]{10}$/.test(phone)) {
        alert("Phone number must be exactly 10 digits.");
        e.preventDefault();
        return;
    }

    if ([...roles.options].filter(o => o.selected).length === 0) {
        alert("Please select at least one role.");
        e.preventDefault();
    }
});
</script>

</body>
</html>