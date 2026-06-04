<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Category</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            background: linear-gradient(135deg, #eef2f7, #ffffff);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            width: 480px;
            background: #ffffff;
            padding: 32px 36px;
            border-radius: 20px;
            box-shadow: 0 22px 45px rgba(0, 0, 0, 0.18);
        }

        h4 {
            text-align: center;
            margin-bottom: 26px;
            color: #243a6e;
            font-weight: 600;
        }

        .form-label {
            font-weight: 500;
            font-size: 14px;
            margin-bottom: 6px;
            color: #333;
        }

        .form-control {
            padding: 11px 14px;
            border-radius: 12px;
            border: 1px solid #ccc;
            font-size: 14px;
            transition: border-color .25s, box-shadow .25s;
        }

        .form-control:focus {
            border-color: #4b6cb7;
            box-shadow: 0 0 0 3px rgba(75, 108, 183, 0.15);
        }

        .btn-primary {
            padding: 11px 26px;
            border-radius: 14px;
            font-weight: 600;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            border: none;
            transition: transform .15s ease, box-shadow .15s ease;
        }

        .btn-primary:hover {
            transform: translateY(-1px);
            box-shadow: 0 8px 22px rgba(75, 108, 183, 0.35);
        }

        .btn-outline-secondary {
            border-radius: 14px;
            font-weight: 500;
            padding: 11px 26px;
        }

        .alert {
            padding: 12px;
            border-radius: 12px;
            margin-bottom: 18px;
            text-align: center;
            font-size: 14px;
        }

        .actions {
            display: flex;
            justify-content: space-between;
            margin-top: 28px;
        }
    </style>
</head>

<body>

<div class="card shadow-lg">
    <div class="card-body">

        <h4>Edit Category</h4>

        <c:if test="${empty categories}">
            <div class="alert alert-danger">
                Category record not found
            </div>
        </c:if>

        <c:if test="${not empty categories}">
            <form:form action="/category/update"
                       method="post"
                       modelAttribute="categories">

                <form:hidden path="id"/>

                <div class="mb-4">
                    <label class="form-label">Category Identifier</label>
                    <form:input path="identifier"
                                cssClass="form-control"
                                readonly="true"/>
                </div>

                <div class="mb-4">
                    <label class="form-label">Super Category</label>
                    <form:select path="superCategory"
                                 cssClass="form-control"
                                 required="true">

                        <form:option value="" label="-- Select Category --"/>
                        <form:options items="${category}"
                                      itemValue="identifier"
                                      itemLabel="identifier"/>
                    </form:select>
                </div>

                <div class="actions">
                    <a href="/category/list" class="btn btn-outline-secondary">
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