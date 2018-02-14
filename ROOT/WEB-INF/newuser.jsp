<html>
    <body>
        <p>${userStatus}</p>
        <form action="connecttomysql" method="post">  
            Enter new user name: <input type="text" name="name"><br><br>
            Enter password:  <input type="password" name="password1"><br><br>
            Repeat password: <input type="password" name="password2"><br><br>
            <input type="submit" value="Create User" name="newUser">  
        </form>  
    <body>
</html>