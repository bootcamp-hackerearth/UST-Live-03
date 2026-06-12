<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Registration</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        html,body{
            height:100%;
        }

        body {
            margin: 0;
            height: 100vh;
            font-family: Arial, sans-serif;
            background-color:#edebeb;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        #container{
            display: flex;
            justify-content: center;
            align-items: center;
            width: 100%;
            height:100%;
        }

        #leftDiv{
            width: 100%;
            height: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
            text-wrap: auto;
            background-color: #5b68a0;
            color:white;
            border-right: solid 10px white;
        }

        #rightDiv{
            width: 100%;
            height: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
            background-color: #d5dbf3;
        }

        form {
            background: #ffffffc2;
            padding: 30px 35px;
            width: 450px;
            border-radius: 10px;
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #667eea;
        }

        form div {
            margin-bottom: 18px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-size: 14px;
            color: #555;
        }

        input {
            width: 100%;
            padding: 10px;
            font-size: 14px;
            border: 1px solid #ccc;
            border-radius: 6px;
        }

        button {
            width: 100%;
            padding: 10px;
            background-color: #667eea;
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 15px;
            cursor: pointer;
            margin-bottom: 10px;

        }

        button:hover {
            background-color: #5a67d8;
        }

        form p {
            padding:5px;
            color:red;
        }

        form a {
            text-decoration: none;
            color: #667eea;
            padding: 5px;
            border-radius: 6px;
            margin-top: 15px;
            font-size: 13px;
        }
        form p{
        color:green;
        font-size:12px;
        }
         input, select {
         width: 100%;
         padding: 11px 14px;
         border-radius: 8px;
         border: 1px solid #ccc;
         font-size: 14px;
          }

          select[multiple] {
          height: 70px;
          }

          input:focus {
           outline: none;
           border-color: #667eea;
          }
          select:focus {
          outline: none;
          border-color: #667eea;
          }

          small {
          color: #666;
          font-size: 11px;
          }

         #message{
         font-size: 12px;
         color: red;
         padding
         }

    </style>
</head>

<body>
<div id="container">
    <div id="leftDiv">
        <h1>Point of Sales Application</h1>
    </div>
    <div id="rightDiv">
        <form:form action="register" method="post" modelAttribute="userDto">
                <h2>User Registration</h2>

                <div>
                    <label>Name</label>
                    <form:input path="name" required="true"/>
                </div>

                <div>
                    <label>Email</label>
                    <form:input path="username" type="email" required="true" id="email"/>
                </div>

                <div>
                    <label>Roles</label>
                    <form:select path="roles" multiple="true" required="true">
                        <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
                    </form:select>
                </div>

                <div>
                    <label>Phone Number</label>
                    <form:input
                    path="phoneNo"
                    type="tel"
                    pattern="[0-9]{10}"
                    title="Enter a valid 10-digit phone number"
                    required="true"/>
                </div>

                <div>
                    <label>Password</label>
                    <form:password path="password"
                     required="true"
                     minlength="4"
                     pattern="(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{4,}"
                     title="Should be 4 digit , one upper or lower case and one symbol"/>
                </div>

                <button type="submit">Register</button>
                <a href="/login">Login</a>
                 <p id="message">${message}</p>
        </form:form>
    </div>
</div>
</body>
</html>