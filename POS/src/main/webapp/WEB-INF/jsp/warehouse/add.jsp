<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Warehouse</title>

    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
        }


        .back-btn {
            position: fixed;
            top: 20px;
            left: 20px;
            padding: 8px 14px;
            background: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-size: 13px;
        }

        .back-btn:hover {
            background: #5a6268;
        }


        .card {
            width: 420px;
            margin: 100px auto;
            background: #fff;
            padding: 30px 35px;
            border-radius: 12px;
            border: 1px solid #ddd;
            box-shadow: 0 4px 10px rgba(0,0,0,0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #333333;
        }

        label {
            display: block;
            margin-top: 15px;
            font-size: 13px;
            font-weight: bold;
            color: #444;
        }

        input {
            width: 100%;
            margin-top: 6px;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 14px;
        }

        input:focus {
            outline: none;
            border-color: #007bff;
        }


        .error-msg {
            margin-bottom: 12px;
            padding: 10px;
            text-align: center;
            border-radius: 6px;
            background: #fee2e2;
            color: #b91c1c;
            font-size: 13px;
        }

        .btn-submit {
            margin-top: 25px;
            width: 100%;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 15px;
            cursor: pointer;
        }

        .btn-submit:hover {
            background-color: #0056b3;
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/warehouse/list" class="back-btn">
    ← Back
</a>

<div class="card">

    <h2>Add Warehouse</h2>

    <c:if test="${not empty successMessage}">
               <div class="alert alert-success text-center">
                   ${successMessage}
               </div>
           </c:if>

           <c:if test="${not empty errorMessage}">
               <div class="alert alert-danger text-center">
                   ${errorMessage}
               </div>
           </c:if>


    <form:form action="add" method="post" modelAttribute="warehouses">

        <label>Warehouse Name</label>
        <form:input path="identifier" required="true"/>

        <label>Location</label>
        <form:input path="location" required="true"/>


        <label>Manager</label>
        <form:input path="manager" required="true"/>

        <input type="submit" value="Add Warehouse" class="btn-submit"/>

    </form:form>

</div>

</body>
</html>