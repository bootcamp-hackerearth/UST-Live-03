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
       font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
       min-height: 100vh;
       background: linear-gradient(to bottom, #ffffff, #e5e5e5, #bbbbbb);
       display: flex;
       justify-content: center;
       align-items: center;
   }
    .register-card {
        width: 340px;
        background: #ffffff;
        padding: 20px 22px;
        border-radius: 6px;
        border: 1px solid #dcdcdc;
    }

    h2 {
        margin: 0 0 16px 0;
        font-size: 18px;
        font-weight: 600;
        color: #222;
        text-align: center;
    }

    .form-group {
        margin-bottom: 10px;
    }

    label {
        font-size: 12px;
        font-weight: 500;
        color: #444;
        display: block;
        margin-bottom: 3px;
    }

    input,
    select {
        width: 100%;
        padding: 7px 8px;
        font-size: 13px;
        border-radius: 4px;
        border: 1px solid #cfcfcf;
        background: #fff;
        color: #222;
    }

    input:focus,
    select:focus {
        outline: none;
        border-color: #4b6cb7;
    }

    select[multiple] {
        height: 80px;
    }

    .btn-submit {
        margin-top: 12px;
        width: 100%;
        padding: 9px;
        background: #4b6cb7;
        color: #fff;
        border: none;
        border-radius: 4px;
        font-size: 13px;
        font-weight: 500;
        cursor: pointer;
    }

    .btn-submit:hover {
        background: #3f5fa7;
    }

    .page-wrapper {
        display: flex;
        width: 760px;
        background: transparent;
    }

    .pos-banner {
        width: 320px;
        background: linear-gradient(to bottom, #2f3b52, #1f2937);
        color: #fff;
        border-radius: 8px 0 0 8px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 20px;
    }

    .pos-banner h1 {
        font-size: 22px;
        font-weight: 600;
        letter-spacing: 0.5px;
    }

    .banner-signin {
        color: #ffffff;
        text-decoration: none;
        font-size: 13px;
        padding: 8px 18px;
        border: 1px solid rgba(255,255,255,0.5);
        border-radius: 4px;
    }

    .banner-signin:hover {
        background: rgba(255,255,255,0.15);
    }

    /* Move form slightly right */
    .register-card {
        border-radius: 0 8px 8px 0;
    }
</style>
</head>
<body>
    <div class="page-wrapper">
        <div class="pos-banner">
            <h1>POS Application</h1>
            <a href="/user/list" class="banner-signin">Go Back</a>
        </div>

        <div class="register-card">
            <h2>User Registration</h2>
            <form:form action="register" method="post" modelAttribute="userDto">
                <div class="form-group">
                    <label>Name</label>
                    <form:input path="name" required="true"/>
                </div>

                <div class="form-group">
                    <label>Email</label>
                    <form:input path="username" type="email" required="true"/>
                </div>

                <div class="form-group">
                    <label>Roles</label>
                    <form:select path="roles" multiple="true">
                        <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
                    </form:select>
                </div>

                <div class="form-group">
                    <label>Phone Number</label>
                    <form:input
                        path="phoneNo"
                        required="true"
                        type="number"
                        maxlength="10"
                        pattern="[0-9]"
                    />
                </div>

                <div class="form-group">
                    <label>Password</label>
                    <form:password
                        path="password"
                        required="true"
                        minlength="8"
                        title="Min 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special character"
                        pattern="(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}" />
                </div>
                <input type="submit" value="Register" class="btn-submit"/>
            </form:form>

             <c:if test="${not empty message}">
                    <div style="
                        background:#f8d7da;
                        color:#721c24;
                        padding:10px;
                        margin-bottom:15px;
                        border-radius:4px;
                        text-align:center;">
                        ${message}
                    </div>
             </c:if>
        </div>
    </div>
</body>
</html>