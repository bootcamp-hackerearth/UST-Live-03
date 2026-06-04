<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Category</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet"/>

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

        .card-container {
            width: 460px;
            background: #ffffff;
            padding: 34px 38px;
            border-radius: 20px;
            box-shadow: 0 22px 48px rgba(0, 0, 0, 0.18);
            position: relative;
        }

        h2 {
            text-align: center;
            margin-bottom: 30px;
            color: #243a6e;
            font-weight: 600;
            letter-spacing: 0.3px;
        }

        .form-group {
            margin-bottom: 22px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-weight: 500;
            font-size: 14px;
            color: #333;
        }

        .form-control {
            width: 100%;
            padding: 12px 15px;
            border-radius: 12px;
            border: 1px solid #ccd3e0;
            font-size: 14px;
            transition: border-color 0.25s ease, box-shadow 0.25s ease;
        }

        .form-control:focus {
            outline: none;
            border-color: #4b6cb7;
            box-shadow: 0 0 0 4px rgba(75, 108, 183, 0.15);
        }

        .alert {
            padding: 12px;
            border-radius: 12px;
            margin-bottom: 20px;
            text-align: center;
            background: rgba(220, 53, 69, 0.12);
            border: 1px solid #dc3545;
            color: #dc3545;
            font-size: 14px;
        }

        .btn-submit {
            width: 100%;
            padding: 14px;
            border-radius: 16px;
            border: none;
            cursor: pointer;
            font-weight: 600;
            font-size: 15px;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
            transition: all 0.2s ease;
            margin-top: 8px;
        }

        .btn-submit:hover {
            transform: translateY(-1px);
            box-shadow: 0 12px 26px rgba(75, 108, 183, 0.35);
        }

        .back-icon {
            position: absolute;
            top: 18px;
            left: 18px;
            width: 38px;
            height: 38px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            color: #4b6cb7;
            text-decoration: none;
            font-weight: 600;
            background: rgba(75, 108, 183, 0.10);
            border-radius: 50%;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            transition: all 0.25s ease;
        }

        .back-icon:hover {
            background: #4b6cb7;
            color: #ffffff;
            transform: translateX(-4px) scale(1.08);
        }
    </style>
</head>

<body>

<div class="card-container">
        <a href="/category/list" class="back-icon">←</a>

    <h2>Add Category</h2>

    <!-- Error message -->
    <c:if test="${not empty categoryDto.message}">
        <div class="alert">
            ${categoryDto.message}
        </div>
    </c:if>

    <form:form method="post"
               action="/category/add"
               modelAttribute="categoryDto">

        <!-- Category Identifier -->
        <div class="form-group">
            <label>Category</label>
            <form:input path="identifier"
                        cssClass="form-control"
                        placeholder="Category"
                        required="true"/>
        </div>

        <div class="form-group">
                                   <label>Super Category </label>
                                   <form:select path="superCategory" cssClass="form-control">
                                       <form:option value="" label="-- Select categories --" />
                                       <c:forEach items="${categories}" var="categories">
                                           <form:option value="${categories.identifier}">
                                               ${categories.identifier}
                                           </form:option>
                                       </c:forEach>
                                   </form:select>
                               </div>

        <button type="submit" class="btn-submit">
            Save Category
        </button>

    </form:form>

</div>

</body>
</html>