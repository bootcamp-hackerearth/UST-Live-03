<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Registration</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            background: linear-gradient(135deg, #667eea, #764ba2);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .register-card {
            width: 430px;
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 30px;
            color: #4b6cb7;
            font-weight: 600;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            font-size: 13px;
            font-weight: 500;
            margin-bottom: 6px;
            display: block;
        }

        input {
            width: 100%;
            padding: 11px 14px;
            border-radius: 8px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        .error {
            color: red;
            font-size: 12px;
            margin-top: 3px;
        }

        #roleList {
            border: 1px solid #ccc;
            border-radius: 8px;
            padding: 8px;
            max-height: 150px;
            overflow-y: auto;
        }

        .role-item {
            padding: 6px 4px;
        }

        .role-item label {
            display: flex;
            align-items: center;
            gap: 10px;
            cursor: pointer;
        }

        .role-item input[type="checkbox"] {
            width: 16px;
            height: 16px;
            margin: 0;
        }

        #selectedRoles {
            margin-top: 10px;
            display: flex;
            flex-wrap: wrap;
            gap: 6px;
        }

        .role-chip {
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: #fff;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 13px;
            cursor: pointer;
        }

        .btn-submit {
            margin-top: 15px;
            width: 100%;
            padding: 13px;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
        }
    </style>
</head>

<body>

<div class="register-card">
    <h2>User Registration</h2>

    <form:form action="register" method="post" modelAttribute="userDto">

        <div class="form-group">
            <label>Name</label>
            <form:input path="name" required="true"/>
            <form:errors path="name" cssClass="error"/>
        </div>

        <div class="form-group">
            <label>Email</label>
            <form:input path="username"
                        type="email"
                        required="true"
                        pattern="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"/>
            <form:errors path="username" cssClass="error"/>
        </div>

        <div class="form-group">
            <label>Roles</label>

            <div id="roleList">
                <c:forEach items="${roles}" var="role">
                    <div class="role-item">
                        <label>
                            <input type="checkbox"
                                   name="roles"
                                   value="${role.identifier}"
                                   onchange="toggleRole(this)">
                            ${role.identifier}
                        </label>
                    </div>
                </c:forEach>
            </div>

            <div id="selectedRoles"></div>

            <form:errors path="roles" cssClass="error"/>
        </div>

        <div class="form-group">
            <label>Phone Number</label>
            <form:input path="phoneNo"
                        required="true"
                        pattern="[0-9]{10}"
                        maxlength="10"/>
            <form:errors path="phoneNo" cssClass="error"/>
        </div>

        <div class="form-group">
            <label>Password</label>
            <form:password path="password"
                           required="true"
                           minlength="8"
                           pattern="(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}"/>
            <form:errors path="password" cssClass="error"/>
        </div>

        <input type="submit" value="Register" class="btn-submit"/>

    </form:form>
</div>

<script>
function toggleRole(cb) {
    const roleItem = cb.closest('.role-item');
    const selected = document.getElementById('selectedRoles');
    const value = cb.value;

    if (cb.checked) {
        roleItem.style.display = 'none';

        const chip = document.createElement('div');
        chip.className = 'role-chip';
        chip.innerText = value;

        chip.onclick = function () {
            cb.checked = false;
            roleItem.style.display = 'block';
            chip.remove();
        };

        selected.appendChild(chip);
    }
}
</script>

</body>
</html>