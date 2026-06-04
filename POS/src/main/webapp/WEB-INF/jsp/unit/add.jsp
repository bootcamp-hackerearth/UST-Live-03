<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Unit | POS</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            background: radial-gradient(circle at 20% 30%, rgba(255,122,0,0.15), transparent 40%),
                        radial-gradient(circle at 80% 70%, rgba(255,72,0,0.15), transparent 40%),
                        linear-gradient(135deg, #1f1f1f, #3a2e2a);
        }

        .card {
            width: 420px;
            background: rgba(255,255,255,0.12);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 15px 40px rgba(0,0,0,0.3);
            border: 1px solid rgba(255,255,255,0.15);
        }

        h4 {
            text-align: center;
            margin-bottom: 20px;
            color: #fff;
            font-weight: 600;
        }

        label {
            font-size: 13px;
            color: #ddd;
            margin-bottom: 6px;
            display: block;
        }

        input {
            width: 100%;
            padding: 12px;
            border-radius: 20px;
            border: 1px solid rgba(255,255,255,0.2);
            background: rgba(255,255,255,0.1);
            color: #fff;
            font-size: 14px;
            margin-bottom: 12px;
        }

        input:focus {
            outline: none;
            border-color: #ff7a00;
            box-shadow: 0 0 6px rgba(255,122,0,0.3);
        }

        .btn-submit {
            width: 100%;
            padding: 12px;
            border-radius: 20px;
            border: none;
            background: linear-gradient(90deg, #ff4800, #ff7a00);
            color: white;
            font-size: 14px;
            cursor: pointer;
        }

        .btn-submit:hover {
            opacity: 0.9;
        }

        .btn-back {
            width: 100%;
            padding: 10px;
            border-radius: 20px;
            border: none;
            background: rgba(255,255,255,0.2);
            color: #fff;
            font-size: 14px;
            margin-top: 10px;
            cursor: pointer;
        }

        .btn-back:hover {
            background: rgba(255,255,255,0.3);
        }
    </style>
</head>

<body>

<div class="card">

    <h4>Create Unit</h4>

    <form:form action="/unit/add" method="post" modelAttribute="unitDto">

        <label>Unit Name</label>
        <form:input path="identifier" required="true"/>

        <button type="submit" class="btn-submit">
            Add Unit
        </button>

        <button type="button" class="btn-back" onclick="window.location.href='/unit/list'">
            Back to Unit List
        </button>

    </form:form>

</div>

</body>
</html>