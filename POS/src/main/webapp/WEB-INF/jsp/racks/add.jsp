<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Rack</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
            margin: 0;
            padding: 0;
        }

        .container {
            width: 40%;
            margin: 60px auto;
            background: #fff;
            padding: 25px;
            border-radius: 6px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #333;
        }

        label {
            font-weight: bold;
            display: block;
            margin-bottom: 6px;
        }

        input[type="text"] {
            width: 100%;
            padding: 8px;
            margin-bottom: 18px;
            border-radius: 4px;
            border: 1px solid #ccc;
        }

        .btn {
            padding: 8px 15px;
            border: none;
            border-radius: 4px;
            font-size: 14px;
            cursor: pointer;
        }

        .save {
            background-color: #28a745;
            color: #fff;
        }

        .cancel {
            background-color: #6c757d;
            color: #fff;
            text-decoration: none;
            padding: 8px 15px;
            margin-left: 10px;
            display: inline-block;
        }

        .btn:hover,
        .cancel:hover {
            opacity: 0.9;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Add Rack</h2>

    <form:form action="${pageContext.request.contextPath}/racks/add"
               method="post"
               modelAttribute="rackDto">

        <!-- Rack Identifier -->
        <label for="identifier">Identifier</label>
        <form:input path="identifier" id="identifier"/>

        <div class="form-group">
                 <label>Shelfs</label>
                 <form:select path="shelf" multiple="true" required="true">
                     <form:options items="${shelfList}" itemValue="identifier" itemLabel="identifier"/>
                 </form:select>
             </div>

        <!-- Actions -->
        <button type="submit" class="btn save">Save</button>

        <a href="${pageContext.request.contextPath}/racks/list"
           class="cancel">Cancel</a>

    </form:form>
</div>

</body>
</html>