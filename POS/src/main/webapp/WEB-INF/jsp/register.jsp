<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Registration</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
        :root {
            --bg: #f6fff8;
            --card: #ffffff;
            --text: #1f2937;
            --muted: #6b7280;

            --primary: #28a745;
            --primary-hover: #218838;

            --accent: #ffc107;

            --border: #e5e7eb;

            --radius: 14px;
            --shadow: 0 10px 30px rgba(0,0,0,0.08);
        }

        * {
            font-family: 'Inter', sans-serif;
            box-sizing: border-box;
        }

        body {
            position: relative;
            background: var(--bg);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        /* BACK BUTTON */
        .back-arrow {
            position: absolute;
            top: 20px;
            left: 20px;

            width: 42px;
            height: 42px;

            display: flex;
            align-items: center;
            justify-content: center;

            border-radius: 50%;
            background: var(--card);
            border: 1px solid var(--border);

            color: var(--text);
            font-size: 18px;
            text-decoration: none;

            box-shadow: var(--shadow);
            transition: 0.2s;
        }

        .back-arrow:hover {
            background: var(--accent);
            color: #000;
        }

        .register-card {
            width: 720px;
            background: var(--card);
            padding: 28px;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            font-weight: 600;
            color: var(--text);
        }

        label {
            font-size: 13px;
            color: var(--muted);
            margin-bottom: 5px;
            font-weight: 500;
        }

        .form-control, select {
            border-radius: 10px;
            border: 1px solid var(--border);
            padding: 10px;
            font-size: 14px;
        }

        .form-control:focus, select:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(40,167,69,0.15);
        }

        select[multiple] {
            height: 100px;
        }

        .btn-submit {
            margin-top: 20px;
            background: var(--primary);
            color: white;
            border-radius: 10px;
            padding: 10px;
            font-weight: 600;
            border: none;
        }

        .btn-submit:hover {
            background: var(--primary-hover);
        }
    </style>

    <script>
        function validateForm() {
            let name = document.getElementsByName("name")[0].value.trim();
            let email = document.getElementsByName("username")[0].value.trim();
            let phone = document.getElementsByName("phoneNo")[0].value.trim();
            let password = document.getElementsByName("password")[0].value.trim();

            if (name === "" || email === "" || phone === "" || password === "") {
                alert("All fields are required!");
                return false;
            }

            let emailPattern = /^[^ ]+@[^ ]+\.[a-z]{2,}$/;
            if (!email.match(emailPattern)) {
                alert("Invalid email format!");
                return false;
            }

            let phonePattern = /^[0-9]{10}$/;
            if (!phone.match(phonePattern)) {
                alert("Phone number must be exactly 10 digits!");
                return false;
            }

            return true;
        }
    </script>
</head>

<body>

<a href="${pageContext.request.contextPath}/user/list" class="back-arrow">←</a>

<div class="register-card">

    <h2>Register User</h2>

    <c:if test="${not empty errorMsg}">
        <div class="alert alert-danger text-center">
            ${errorMsg}
        </div>
    </c:if>

    <form:form action="register"
               method="post"
               modelAttribute="userDto"
               onsubmit="return validateForm()">

        <div class="row">

            <div class="col-md-6">

                <div class="mb-3">
                    <label>Name</label>
                    <form:input path="name" cssClass="form-control" required="true"/>
                </div>

                <div class="mb-3">
                    <label>Email</label>
                    <form:input path="username" cssClass="form-control" type="email" required="true"/>
                </div>

                <div class="mb-3">
                    <label>Phone Number</label>
                    <form:input path="phoneNo"
                                cssClass="form-control"
                                type="tel"
                                pattern="[0-9]{10}"
                                maxlength="10"
                                required="true"/>
                </div>

            </div>

            <!-- RIGHT SIDE -->
            <div class="col-md-6">

                <div class="mb-3">
                    <label>Password</label>
                    <form:password path="password" cssClass="form-control" required="true"/>
                </div>

                <div class="mb-3">
                    <label>Roles</label>
                    <form:select path="roles" multiple="true" cssClass="form-control" required="true">
                        <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
                    </form:select>
                </div>

            </div>

        </div>

        <button type="submit" class="btn-submit w-100">
            Register
        </button>

    </form:form>
</div>

</body>
</html>