<body>
        <p>${transferStatus}</p>
        <form action="connecttomysql" method="post">  
            <table>
                <tr>
                    <td>Enter recipients name:</td> 
                    <td><input type="text" name="transferRecipient"></td>
                </tr>
                <tr>
                    <td>Enter amount:</td>
                    <td><input type="text" name="transferAmount"></td>
                </tr>
                <tr>
                <tr>
                    <td><input type="submit" value="Send code to mail" name="codeToMail"></td>
                    <td><input type="submit" value="Log Out" name="logOut"></td>
                </tr>
            </table>  
        </form>  
    <body>