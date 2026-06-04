<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Warehouse</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            position: relative;
            width: 500px;
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        h4 {
            text-align: center;
            margin-bottom: 25px;
            color: #4b6cb7;
            font-weight: 600;
        }

        .form-label {
            font-weight: 500;
            color: #333;
        }

        .form-control {
            padding: 10px 12px;
            border-radius: 10px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        .form-control:focus {
            border-color: #4b6cb7;
            box-shadow: 0 0 0 2px rgba(75, 108, 183, 0.2);
        }

        .btn-primary {
            padding: 10px 22px;
            border-radius: 12px;
            font-weight: 600;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            border: none;
        }

        .btn-primary:hover {
            transform: scale(1.05);
        }

        .btn-outline-secondary {
            border-radius: 10px;
            font-weight: 500;
        }

        .alert {
            padding: 10px;
            border-radius: 8px;
            margin-bottom: 15px;
            text-align: center;
            font-size: 14px;
        }
    </style>
</head>

<body>

<div class="card shadow-lg">
    <div class="card-body">

        <h4>Edit Warehouse</h4>

        <!-- ✅ Backend error message -->
        <c:if test="${not empty message}">
            <div class="alert alert-danger">
                ${message}
            </div>
        </c:if>

        <!-- ✅ When warehouse not found -->
        <c:if test="${empty warehouse}">
            <div class="alert alert-danger">
                Warehouse not found
            </div>
        </c:if>

        <!-- ✅ Edit form -->
        <c:if test="${not empty warehouse}">
            <form:form action="/warehouse/update"
                       method="post"
                       modelAttribute="warehouse">

                <!-- ✅ Hidden ID -->
                <form:hidden path="id"/>

                <!-- ✅ Identifier (readonly, single binding) -->
                <div class="mb-4">
                    <label class="form-label">Warehouse Name</label>
                    <form:input path="identifier"
                                cssClass="form-control"
                                readonly="true"/>
                </div>

                <div class="mb-4">
                    <label class="form-label">Country</label>
                    <form:input path="country"
                                cssClass="form-control"
                                required="true"/>
                </div>

                <div class="mb-4">
                    <label class="form-label">Pincode</label>
                    <form:input path="pincode"
                                cssClass="form-control"
                                maxlength="6"
                                pattern="[0-9]{6}"
                                required="true"/>
                </div>

                <div class="mb-4">
                    <label class="form-label">Address</label>
                    <form:input path="address"
                                cssClass="form-control"
                                required="true"/>
                </div>

                <div class="d-flex justify-content-between">
                    <a href="/warehouse/list" class="btn btn-outline-secondary">
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-primary">
                        Update
                    </button>
                </div>

            </form:form>
        </c:if>

    </div>
</div>

</body>
</html>
