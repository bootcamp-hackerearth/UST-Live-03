<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Registration</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <style>
        :root {
            --primary: #2563eb;
            --primary-hover: #1e40af;

            --bg-dark: #0f172a;
            --bg-light: #f8fafc;

            --card: rgba(255,255,255,0.75);
            --text: #0f172a;
            --muted: #64748b;

            --border: rgba(255,255,255,0.2);
            --radius: 18px;

            --shadow: 0 30px 60px rgba(2,6,23,0.25);
        }

        * {
            font-family: 'Inter', sans-serif;
            box-sizing: border-box;
        }

        body {
            margin: 0;
            min-height: 100vh;
            display: flex;
            background: linear-gradient(135deg, #0f172a, #1e293b);
        }

        /* LEFT PANEL */
        .left-panel {
            flex: 1;
            color: white;
            padding: 60px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .brand-title {
            font-size: 32px;
            font-weight: 700;
            margin-bottom: 16px;
        }

        .brand-sub {
            color: #cbd5f5;
            font-size: 15px;
            max-width: 420px;
            line-height: 1.6;
        }

        /* RIGHT PANEL */
        .right-panel {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, #f8fafc, #eef2f7);
            padding: 20px;
        }

        /* BACK BUTTON */
        .back-arrow {
            position: absolute;
            top: 25px;
            left: 25px;
            width: 42px;
            height: 42px;
            border-radius: 50%;
            background: rgba(255,255,255,0.9);
            display: flex;
            align-items: center;
            justify-content: center;
            text-decoration: none;
            color: var(--text);
            font-size: 18px;
            box-shadow: var(--shadow);
            transition: 0.2s;
        }

        .back-arrow:hover {
            transform: translateY(-2px);
            background: #e2e8f0;
        }

        /* CARD */
        .register-card {
            width: 760px;
            padding: 40px;
            border-radius: var(--radius);

            background: var(--card);
            backdrop-filter: blur(18px);

            border: 1px solid var(--border);
            box-shadow: var(--shadow);

            animation: fadeIn 0.6s ease;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(15px); }
            to { opacity: 1; transform: translateY(0); }
        }

        h2 {
            text-align: center;
            font-weight: 700;
            margin-bottom: 28px;
            color: var(--text);
        }

        label {
            font-size: 13px;
            font-weight: 500;
            color: var(--muted);
            margin-bottom: 6px;
        }

        .form-control, select {
            border-radius: 12px;
            padding: 12px 14px;
            font-size: 14px;

            border: 1px solid #e2e8f0;
            background: rgba(255,255,255,0.85);

            transition: all 0.2s ease;
        }

        .form-control:focus, select:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(37,99,235,0.2);
            background: #fff;
        }

        select[multiple] {
            height: 120px;
        }

        .btn-submit {
            margin-top: 24px;
            width: 100%;
            padding: 12px;
            border-radius: 12px;
            border: none;

            background: linear-gradient(135deg, var(--primary), var(--primary-hover));
            color: white;
            font-weight: 600;

            transition: all 0.2s ease;
        }

        .btn-submit:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 25px rgba(37,99,235,0.35);
        }

        .server-msg {
            background: #fee2e2;
            border: 1px solid #fecaca;
            color: #991b1b;
            padding: 10px;
            border-radius: 10px;
            font-size: 13px;
            text-align: center;
            margin-bottom: 16px;
        }

        /* RESPONSIVE */
        @media (max-width: 900px) {
            .left-panel {
                display: none;
            }

            .register-card {
                width: 100%;
            }
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

<!-- LEFT PANEL -->
<div class="left-panel">
    <div class="brand-title">POS Enterprise</div>
    <div class="brand-sub">
        Create a new user account to manage operations, track sales,
        and control access across your POS system.
    </div>
</div>

<!-- RIGHT PANEL -->
<div class="right-panel">

    <a href="${pageContext.request.contextPath}/user/list" class="back-arrow">←</a>

    <div class="register-card">

        <h2>Create Account</h2>

        <c:if test="${not empty errorMsg}">
            <div class="server-msg">
                ${errorMsg}
            </div>
        </c:if>

        <form:form
                action="${pageContext.request.contextPath}/user/register"
                method="post"
                modelAttribute="userDto"
                onsubmit="return validateForm()">

            <div class="row">

                <!-- LEFT -->
                <div class="col-md-6">

                    <div class="mb-3">
                        <label>Name</label>
                        <form:input path="name" cssClass="form-control" required="true"/>
                    </div>

                    <div class="mb-3">
                        <label>Email</label>
                        <form:input path="username" type="email" cssClass="form-control" required="true"/>
                    </div>

                    <div class="mb-3">
                        <label>Phone Number</label>
                        <form:input path="phoneNo"
                                    cssClass="form-control"
                                    pattern="[0-9]{10}"
                                    maxlength="10"
                                    required="true"/>
                    </div>

                </div>

                <!-- RIGHT -->
                <div class="col-md-6">

                    <div class="mb-3">
                        <label>Password</label>
                        <form:password path="password" cssClass="form-control" required="true"/>
                    </div>

                    <div class="mb-3">
                        <label>Roles</label>
                        <form:select path="roles" multiple="true" cssClass="form-control" required="true">
                            <form:options items="${roles}"
                                          itemValue="identifier"
                                          itemLabel="identifier"/>
                        </form:select>
                    </div>

                </div>

            </div>

            <button type="submit" class="btn-submit">
                Register User
            </button>

        </form:form>

    </div>

</div>

</body>
</html>