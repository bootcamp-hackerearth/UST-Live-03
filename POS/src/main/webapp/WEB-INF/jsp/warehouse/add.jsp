<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Warehouse</title>

    <style>

        /* ===== BODY ===== */

        body {
            margin: 0;
            font-family: "Inter", sans-serif;
            background-color: #3f3f3f;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }

        /* ===== CARD ===== */

        .brand-card {
            background: #f3efe9;
            width: 470px;
            padding: 42px;
            box-sizing: border-box;
        }

        /* ===== BACK BUTTON ===== */

        .back-btn {
            display: inline-block;
            margin-bottom: 22px;
            text-decoration: none;
            color: #2f2f2f;
            font-size: 13px;
            font-weight: 700;
            letter-spacing: 1px;
        }

        .back-btn:hover {
            opacity: 0.7;
        }

        /* ===== TITLE ===== */

        h2 {
            margin: 0 0 34px;
            font-size: 26px;
            font-weight: 700;
            color: #2f2f2f;
        }

        /* ===== FORM ===== */

        .form-group {
            margin-bottom: 28px;
        }

        label {
            font-size: 12px;
            letter-spacing: 2px;
            color: #8a8a8a;
            display: block;
            margin-bottom: 10px;
        }

        input {
            width: 100%;
            box-sizing: border-box;
            padding: 10px 0;
            border: none;
            border-bottom: 3px solid #cfcfcf;
            background: transparent;
            font-size: 16px;
            outline: none;
            color: #2f2f2f;
            font-family: "Inter", sans-serif;
        }

        input:focus {
            border-bottom: 3px solid #3f3f3f;
        }

        /* ===== AUTOFILL FIX ===== */

        input:-webkit-autofill,
        input:-webkit-autofill:hover,
        input:-webkit-autofill:focus {

            -webkit-box-shadow: 0 0 0px 1000px #f3efe9 inset !important;
            -webkit-text-fill-color: #2f2f2f !important;
            transition: background-color 5000s ease-in-out 0s;

        }

        /* ===== BUTTON ===== */

        .add-btn {
            width: 100%;
            padding: 16px;
            margin-top: 8px;
            background: #3f3f3f;
            color: #ffffff;
            border: 2px solid #3f3f3f;
            font-size: 15px;
            font-weight: 700;
            letter-spacing: 2px;
            cursor: pointer;
            transition: 0.3s;
        }

        .add-btn:hover {
            background: transparent;
            color: #3f3f3f;
        }

        /* ===== ERROR ===== */

        .error-message {
            margin-top: 16px;
            padding: 12px;
            background: #ffe5e0;
            color: #b91c1c;
            font-size: 13px;
        }

    </style>

</head>

<body>

<div class="brand-card">

    <!-- ===== BACK ===== -->

    <a href="${pageContext.request.contextPath}/wareHouse/list"
       class="back-btn">

        ᐸ BACK

    </a>

    <!-- ===== TITLE ===== -->

    <h2>Add Warehouse</h2>

    <!-- ===== ERROR ===== -->

    <c:if test="${not empty message}">

        <div class="error-message">

            ${message}

        </div>

    </c:if>

    <!-- ===== FORM ===== -->

    <form:form
            action="${pageContext.request.contextPath}/wareHouse/add"
            method="post"
            modelAttribute="wareHouseDto">

        <!-- ===== WAREHOUSE NAME ===== -->

        <div class="form-group">

            <label>WAREHOUSE NAME</label>

            <form:input
                    path="identifier"
                    required="true"/>

        </div>

        <!-- ===== LOCATION ===== -->

        <div class="form-group">

            <label>LOCATION</label>

            <form:input
                    path="location"
                    required="true"/>

        </div>

        <!-- ===== MANAGER ===== -->

        <div class="form-group">

            <label>MANAGER</label>

            <form:input
                    path="manager"
                    required="true"/>

        </div>

        <!-- ===== BUTTON ===== -->

        <button type="submit"
                class="add-btn">

            ADD WAREHOUSE

        </button>

    </form:form>

</div>

</body>
</html>