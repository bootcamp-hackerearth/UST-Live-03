<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Home</title>

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: Arial, sans-serif;
        }
         html,body{
            height:100%;
         }
        body {
            background-color: #f5f6fc;
            color:#667eea;
        }

        .header {
            height: 50px;
            background-color: #f5f6fc;
            color: white;
            display: flex;
            align-items: center;
            padding: 0 20px;
            color:#667eea;
            box-shadow: 0px 0px 7px 1px rgba(75, 108, 183, 0.25);
        }

        .header a {
            text-decoration: none;
            color: white;
            padding: 5px;
            font-size: 12px;
            background: #667eea;
            border-radius: 5px;
            margin-left: 87vw;
        }

        .hamburger {
            font-size: 22px;
            cursor: pointer;
            margin-right: 15px;
        }

        .sidebar {
            position: fixed;
            top: 0;
            left: -250px;
            width: 250px;
            height: 100vh;
            background-color: #f5f6fc;
            transition: 0.3s ease;
            padding-top: 15px;
        }

        .sidebar.active {
            left: 0;
            border-radius: 10px;
            box-shadow: 0px 0px 20px 10px rgba(75, 108, 183, 0.25);
        }

        .close-btn {
            color: #667eea;
            font-size: 20px;
            cursor: pointer;
            text-align: right;
            padding: 10px 20px;
        }

        .sidebar a {
                display: block;
                padding: 12px 20px;
                color: #667eea;
                text-decoration: none;
                border: solid 0.5px #667eea;
                margin: 5px 10px 5px 10px;
                border-radius: 5px;
                font-size: 14px;
        }

        .sidebar a:hover {
                background-color: #667eeaeb;
                color: white;
                border: none;
        }

        .content {
            padding: 20px;
            transition: margin-left 0.3s ease;
        }

        .content.shift {
            margin-left: 250px;
        }
    </style>
</head>

<body>

    <div id="sidebar" class="sidebar">
       <div class="close-btn" onclick="toggleSidebar()">✖</div>

       <c:forEach var="node" items="${nodes}">
           <a href="${node.path}"> ${node.identifier}</a>
       </c:forEach>
    </div>

    <div class="header">
        <span class="hamburger" onclick="toggleSidebar()">☰</span>
        <h3>Home</h3>
        <h4><a href="/logout">Logout</a></h4>
    </div>

    <div id="content" class="content">
        <h2>Welcome</h2>
        <p>${userName}</p>

    </div>

    <script>
        function toggleSidebar() {
            document.getElementById("sidebar").classList.toggle("active");
            document.getElementById("content").classList.toggle("shift");
        }
    </script>

</body>
</html>