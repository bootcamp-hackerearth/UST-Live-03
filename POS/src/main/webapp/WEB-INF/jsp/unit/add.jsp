<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Management | Add Unit</title>

    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: #F4F5F7;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .container {
            width: 100%;
            max-width: 430px;
            background: #ffffff;
            border-radius: 16px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        }
        .brand-header {
            background: #0B3C5D;
            padding: 25px;
            color: #FFFFFF;
            text-align: center;
        }
        .form-body {
            padding: 35px 40px;
        }
        .form-group {
            margin-bottom: 18px;
        }
        label {
            font-weight: 600;
            display: block;
            margin-bottom: 8px;
        }
        input {
            width: 100%;
            padding: 12px;
            border-radius: 8px;
            border: 1px solid #E5E7EB;
        }
        .btn-submit {
            width: 100%;
            padding: 12px;
            background: #0B3C5D;
            color: white;
            border-radius: 8px;
            border: none;
            font-weight: 600;
            margin-top: 15px;
        }
        .back-link {
            display: block;
            text-align: center;
            margin-top: 18px;
            font-weight: 600;
            text-decoration: none;
            color: #6B7280;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="brand-header">
        <h1>POS Management</h1>
    </div>

    <div class="form-body">
        <h2 style="text-align:center;">Add Unit</h2>

        <!-- ✅ FIX: proper form tag -->
        <form action="${pageContext.request.contextPath}/unit/add" method="post">

            <div class="form-group">
                <label>Unit Identifier</label>
                <input type="text"
                       name="identifier"
                       placeholder="e.g. KG, LTR, PCS"
                       required />
            </div>

            <button type="submit" class="btn-submit">
                Save Unit
            </button>

            <a href="${pageContext.request.contextPath}/unit/list"
               class="back-link">
                ← Cancel and Return
            </a>

        </form>
        <!-- ✅ FORM CLOSED -->

    </div>
</div>

</body>
</html>