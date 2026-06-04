<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Edit Role</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            background: #f7f7fb;
            height: 100vh;
            overflow: hidden;
            position: relative;
        }

        .blob {
            position: absolute;
            border-radius: 50%;
            filter: blur(60px);
            opacity: 0.5;
            animation: float 10s infinite ease-in-out;
        }

        .blob1 {
            width: 300px;
            height: 300px;
            background: #3b82f6;
            top: -80px;
            left: -80px;
        }

        .blob2 {
            width: 250px;
            height: 250px;
            background: #93c5fd;
            bottom: -80px;
            right: -80px;
            animation-delay: 3s;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0px) scale(1); }
            50% { transform: translateY(-30px) scale(1.05); }
        }

        .main-container {
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            position: relative;
            z-index: 2;
        }

        .card {
            width: 420px;
            padding: 25px;
            border-radius: 18px;

            background: rgba(255, 255, 255, 0.75);
            backdrop-filter: blur(18px) saturate(180%);
            -webkit-backdrop-filter: blur(18px) saturate(180%);

            border: 1px solid rgba(255,255,255,0.4);

            box-shadow:
                0 20px 50px rgba(0,0,0,0.15),
                inset 0 1px 0 rgba(255,255,255,0.6);

            animation: fadeInUp 0.5s ease;
        }

        @keyframes fadeInUp {
            from { opacity: 0; transform: translateY(40px); }
            to   { opacity: 1; transform: translateY(0); }
        }

        h4 {
            color: #3b82f6;
            font-weight: 700;
        }

        .form-control {
            border-radius: 10px;
            border: 1px solid #e5e7eb;
            transition: 0.25s ease;
        }

        .form-control:focus {
            border-color: #3b82f6;
            box-shadow: 0 0 0 3px rgba(59,130,246,0.15);
        }

        .form-control[readonly] {
            background: #f3f4f6;
            color: #6b7280;
            cursor: not-allowed;
        }

        .form-control[readonly]:user-invalid,
        .form-control[readonly]:user-valid,
        .form-control[readonly]:invalid,
        .form-control[readonly]:valid {
            border-color: #e5e7eb;
            box-shadow: none;
        }

        .form-control:user-invalid {
            border-color: #ef4444;
            box-shadow: 0 0 0 3px rgba(239,68,68,0.12);
        }

        .form-control:user-valid {
            border-color: #22c55e;
            box-shadow: 0 0 0 3px rgba(34,197,94,0.12);
        }

        .form-control:not(:placeholder-shown):invalid {
            border-color: #ef4444;
            box-shadow: 0 0 0 3px rgba(239,68,68,0.12);
        }

        .form-control:not(:placeholder-shown):valid {
            border-color: #22c55e;
            box-shadow: 0 0 0 3px rgba(34,197,94,0.12);
        }

        .field-hint {
            font-size: 11.5px;
            color: #9ca3af;
            margin-top: 4px;
            display: block;
        }

        .form-control:user-invalid ~ .field-hint,
        .form-control:not(:placeholder-shown):invalid ~ .field-hint {
            color: #ef4444;
        }

        .btn-success {
            background: #3b82f6;
            border: none;
            border-radius: 10px;
            font-weight: 600;
            box-shadow: 0 8px 20px rgba(37,99,235,0.25);
            transition: 0.2s;
        }

        .btn-success:hover {
            background: #2563eb;
            transform: translateY(-2px);
        }

        .btn-outline-secondary {
            border-radius: 10px;
        }

        .alert {
            border-radius: 10px;
        }
    </style>
</head>


<body class="bg-light d-flex justify-content-center align-items-center" style="height:100vh;">

<div class="card p-4 shadow" style="width: 400px;">

    <h4 class="text-center mb-3 text-primary">Edit Role</h4>

    <c:if test="${empty roleDto}">
        <div class="alert alert-danger text-center">
            Role not found
        </div>
    </c:if>

    <c:if test="${not empty roleDto}">
        <form:form action="/role/update"
                   method="post"
                   modelAttribute="roleDto">

            <form:hidden path="id"/>

            <div class="mb-3">
                <label>Role Name</label>
                <form:input path="identifier"
                            cssClass="form-control"
                            readonly="true"/>
            </div>

            <div class="mb-3">
                <label>Description *</label>
                <form:textarea path="description"
                               cssClass="form-control"
                               placeholder="Enter description"
                               required="true"/>
            </div>

            <div class="d-flex justify-content-between">
                <a href="/role/list" class="btn btn-secondary">
                    Cancel
                </a>

                <button type="submit" class="btn btn-primary">
                    Update
                </button>
            </div>

        </form:form>
    </c:if>

</div>

</body>

</html>
