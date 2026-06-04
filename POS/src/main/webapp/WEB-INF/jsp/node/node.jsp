<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
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

        .card-container {
            position: relative;
            width: 520px;
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #4b6cb7;
            font-weight: 600;
        }

        .back-icon {
            position: absolute;
            top: 16px;
            left: 16px;
            width: 36px;
            height: 36px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            color: #4b6cb7;
            text-decoration: none;
            font-weight: 600;
            background: rgba(75, 108, 183, 0.08);
            border-radius: 50%;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
            transition: 0.25s ease;
        }

        .back-icon:hover {
            background: #4b6cb7;
            color: #ffffff;
            transform: translateX(-4px) scale(1.05);
        }

        .form-group {
            margin-bottom: 18px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-weight: 500;
            color: #333;
        }

        .form-control {
            width: 100%;
            padding: 10px 12px;
            border-radius: 10px;
            border: 1px solid #ccc;
            font-family: 'Poppins', sans-serif;
            font-size: 14px;
        }

        .form-control:focus {
            outline: none;
            border-color: #4b6cb7;
            box-shadow: 0 0 0 2px rgba(75,108,183,0.2);
        }

        select.form-control {
            height: auto;
        }

        small {
            font-size: 12px;
            color: #777;
        }

        .alert {
            padding: 10px;
            border-radius: 8px;
            margin-bottom: 15px;
            text-align: center;
            font-size: 14px;
        }

        .alert-danger {
            background: #f8d7da;
            color: #721c24;
        }

        .action-buttons {
            margin-top: 20px;
            display: flex;
            justify-content: space-between;
        }

        .btn {
            padding: 10px 18px;
            border-radius: 10px;
            border: none;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
            text-decoration: none;
            transition: 0.25s ease;
        }

        .btn-cancel {
            background: #6c757d;
            color: white;
        }

        .btn-cancel:hover {
            background: #555;
        }

        .btn-update {
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
        }

        .btn-update:hover {
            transform: scale(1.05);
        }

        .footer-text {
            text-align: center;
            margin-top: 15px;
            font-size: 12px;
            color: #777;
        }
    </style>
</head>

<body>

<div class="card-container">

    <a href="/node/list" class="back-icon">←</a>

    <h2>Edit Node</h2>

    <c:if test="${empty node}">
        <div class="alert alert-danger">
            Node not found
        </div>
    </c:if>

    <c:if test="${not empty node}">
        <form:form action="/node/update"
                   method="post"
                   modelAttribute="node">

            <form:hidden path="id"/>

            <div class="form-group">
                <label>Node Name</label>
                <form:input path="identifier"
                            cssClass="form-control"
                            required="true" readonly="true"/>
            </div>

            <div class="form-group">
                <label>Path</label>
                <form:input path="path"
                            cssClass="form-control"
                            required="true"/>
            </div>

            <div class="form-group">
                <label>Roles</label>
                <form:select path="roles"
                             multiple="true"
                             cssClass="form-control">

                    <form:options items="${roles}"
                                  itemValue="identifier"
                                  itemLabel="identifier"/>
                </form:select>

                <small>
                    Hold Ctrl (Windows/Linux) or Cmd (Mac) to select multiple roles
                </small>
            </div>

            <div class="action-buttons">
                <a href="/node/list" class="btn btn-cancel">Cancel</a>
                <button type="submit" class="btn btn-update">Update</button>
            </div>

        </form:form>
    </c:if>

    <div class="footer-text">
        POS Management System
    </div>

</div>

</body>
</html>
