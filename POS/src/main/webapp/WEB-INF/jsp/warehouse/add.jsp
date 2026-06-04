<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Warehouse</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: #2f2f2f;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: "Segoe UI", sans-serif;
        }

        .container {
            max-width: 450px;
        }

        .card {
            border: none;
            border-radius: 18px;
            background-color: #ffffff;
            box-shadow: 0 15px 40px rgba(0, 0, 0, 0.25);
        }

        .card-header {
            background: transparent;
            border-bottom: none;
            text-align: center;
        }

        .card-header h2 {
            font-size: 22px;
            font-weight: 600;
            color: #333;
        }

        label {
            font-weight: 600;
            font-size: 0.9rem;
            color: #555;
            margin-top: 12px;
        }

        input {
            width: 100%;
            padding: 10px 14px;
            margin-top: 6px;
            border-radius: 10px;
            border: 1px solid #ddd;
            font-size: 0.9rem;
        }

        button {
            width: 100%;
            margin-top: 24px;
            padding: 10px;
            background: linear-gradient(90deg, #4facfe, #00f2fe);
            color: #fff;
            border: none;
            border-radius: 25px;
            font-size: 1rem;
            font-weight: 600;
        }

        .back-link {
            display: block;
            text-align: center;
            margin-top: 18px;
            color: #0d6efd;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="card">

        <div class="card-header">
            <h2>Add Warehouse</h2>
        </div>

        <div class="card-body">

            <!-- ✅ ERROR / INFO MESSAGE -->
            <c:if test="${not empty message}">
                <div class="alert alert-danger text-center">
                    ${message}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/warehouse/add" method="post">

                <!-- Identifier -->
                <label>Identifier</label>
                <input type="text"
                       name="identifier"
                       value="${warehouse.identifier}"
                       required />

                <!-- Warehouse Code -->
                <label>Warehouse Code</label>
                <input type="text"
                       name="code"
                       value="${warehouse.code}"
                       required />

                <!-- Location -->
                <label>Location</label>
                <input type="text"
                       name="location"
                       value="${warehouse.location}"
                       required />

                <button type="submit">Save Warehouse</button>
            </form>

            <a class="back-link"
               href="${pageContext.request.contextPath}/warehouse/list">
                ← Back to Warehouse List
            </a>

        </div>

        <div class="card-footer text-center">
            POS Management System
        </div>

    </div>
</div>

</body>
</html>
