<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>User Registration</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f1f5f9;
            color: #1e293b;
        }

        .main {
            display: flex;
            height: 100vh;
        }

        .left {
            flex: 1;
            padding: 80px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            background: linear-gradient(135deg, #e2e8f0, #f8fafc);
        }

        .left h1 {
            font-size: 52px;
            font-weight: 800;
            margin-bottom: 16px;
        }

        .left p {
            font-size: 17px;
            color: #475569;
            max-width: 480px;
            line-height: 1.6;
        }

        .feature {
            margin-top: 20px;
            font-size: 14px;
            color: #334155;
        }

        .right {
            width: 460px;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 40px;
        }

        .container {
            background: #ffffff;
            width: 100%;
            padding: 36px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);
            border-top: 6px solid #1e293b;
        }

        h2 {
            text-align: center;
            margin-bottom: 26px;
            font-size: 22px;
            font-weight: 700;
        }

        .message {
            margin-bottom: 16px;
            padding: 10px;
            border-radius: 6px;
            background-color: #e0f2fe;
            color: #0369a1;
            text-align: center;
            font-size: 13px;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            font-size: 12px;
            font-weight: 600;
            color: #334155;
            margin-bottom: 5px;
            display: block;
        }

        input, select {
            width: 100%;
            padding: 12px;
            border-radius: 8px;
            border: 1px solid #cbd5e1;
            background: #f8fafc;
            font-size: 14px;
            transition: 0.2s;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #1e293b;
            background: #ffffff;
            box-shadow: 0 0 0 3px rgba(30,41,59,0.15);
        }

        select {
            min-height: 100px;
        }

        .btn-group {
            display: flex;
            gap: 10px;
            margin-top: 22px;
        }

        .btn {
            flex: 1;
            padding: 12px;
            border-radius: 8px;
            border: none;
            font-weight: 600;
            cursor: pointer;
            transition: 0.2s;
        }

        .btn-submit {
            background: #1e293b;
            color: white;
        }

        .btn-submit:hover {
            background: #0f172a;
            box-shadow: 0 8px 18px rgba(0,0,0,0.25);
        }

        .btn-clear {
            background: #e2e8f0;
            color: #1e293b;
        }

        .btn-clear:hover {
            background: #cbd5e1;
        }

        .back-login {
            text-align: center;
            margin-top: 18px;
            font-size: 13px;
            color: #64748b;
        }

        .back-login a {
            color: #1e293b;
            font-weight: 600;
            text-decoration: none;
        }

        .back-login a:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>

<div class="main">

    <div class="left">
        <h1>Create your POS account</h1>

        <p>
            Start managing your retail with a modern system.
        </p>
    </div>

    <div class="right">

        <div class="container">
            <h2>User Registration</h2>

            <c:if test="${not empty message}">
                <div class="message">
                    ${message}
                </div>
            </c:if>

            <form method="post">

                <div class="form-group">
                    <label>Full Name</label>
                    <input type="text" name="name" placeholder="Enter full name" required>
                </div>

                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="username"
                           placeholder="Enter Gmail address"
                           pattern="^[a-zA-Z0-9._%+-]+@gmail\.com$"
                           required>
                </div>

                <div class="form-group">
                    <label>Password</label>
                    <input type="password"
                           name="password"
                           placeholder="Enter password"
                           pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$"
                           title="Password must contain at least 8 characters, one uppercase letter, one lowercase letter, one number and one special character"
                           required>
                </div>

                <div class="form-group">
                    <label>Phone Number</label>
                    <input type="tel"
                           name="phoneNo"
                           placeholder="Enter mobile number"
                           maxlength="10"
                           pattern="^[6-9][0-9]{9}$"
                           title="Enter a valid 10-digit mobile number"
                           oninput="this.value=this.value.replace(/[^0-9]/g,'')"
                           required>
                </div>

                <div class="form-group">
                    <label>Assigned Roles</label>
                    <select name="roles" id="roles" multiple required>
                        <c:forEach items="${roles}" var="role">
                            <option value="${role.identifier}">
                                ${role.identifier}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="btn-group">
                    <button type="submit" class="btn btn-submit">
                        Register
                    </button>

                    <button type="reset" class="btn btn-clear">
                        Clear
                    </button>
                </div>

                <div class="back-login">
                    Already have an account?
                    <a href="${pageContext.request.contextPath}/login">
                        Login here
                    </a>
                </div>

            </form>
        </div>

    </div>

</div>

</body>
</html>