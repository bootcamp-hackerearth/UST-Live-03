<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Warehouse</title>

    <style>
        body {
            background: #2f2f2f;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .edit-card {
            width: 460px;
            background: #ffffff;
            padding: 35px 40px;
            border-radius: 18px;
            box-shadow: 0 18px 45px rgba(0, 0, 0, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            font-weight: 600;
            color: #333;
        }

        label {
            font-weight: 600;
            font-size: 0.9rem;
            margin-top: 12px;
            display: block;
        }

        input {
            width: 100%;
            padding: 10px;
            margin-top: 6px;
            border-radius: 8px;
            border: 1px solid #ddd;
        }

        input[readonly] {
            background-color: #f1f3f5;
        }

        button {
            width: 100%;
            margin-top: 25px;
            padding: 12px;
            background: linear-gradient(90deg, #4facfe, #00f2fe);
            border: none;
            color: #ffffff;
            font-weight: 600;
            border-radius: 25px;
            font-size: 1rem;
        }

        .back-link {
            display: block;
            text-align: center;
            margin-top: 18px;
            font-weight: 600;
            color: #0d6efd;
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="edit-card">

    <h2>Update Warehouse</h2>

    <!-- ✅ ERROR MESSAGE -->
    <c:if test="${not empty message}">
        <div class="alert alert-danger text-center">
            ${message}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/warehouse/update" method="post">

        <!-- ✅ Identifier (readonly, used for update) -->
        <label>Identifier</label>
        <input type="text"
               name="identifier"
               value="${warehouse.identifier}"
               readonly />

        <!-- ✅ Warehouse Code -->
        <label>Warehouse Code</label>
        <input type="text"
               name="code"
               value="${warehouse.code}"
               required />

        <!-- ✅ Location -->
        <label>Location</label>
        <input type="text"
               name="location"
               value="${warehouse.location}"
               required />

        <button type="submit">Update Warehouse</button>
    </form>

    <a class="back-link"
       href="${pageContext.request.contextPath}/warehouse/list">
        ← Back to Warehouse List
    </a>

</div>

</body>
</html>