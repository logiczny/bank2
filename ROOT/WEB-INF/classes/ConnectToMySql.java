import javax.servlet.http.*;  
import javax.servlet.*;
import javax.*;
import java.io.*;
import java.lang.System;
import java.sql.*;
import java.lang.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Random;
import com.mysql.*;


public class ConnectToMySql extends HttpServlet
{  
    public final String     DB_URL = "jdbc:mysql://localhost:3306/jkbase";
    public final String     DB_USER = "root";
    public final String     DB_PW = "xxxxx";



    public void init() throws ServletException
    {
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {  
        HttpSession session = request.getSession(true);

        String loggedInUser = (String)session.getAttribute("userName");
        
        if (request.getParameter("gotoLogIn") != null)
        {
            request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
        }
        else if (request.getParameter("logIn") != null)
        {
            logIn(request, response);
        }
        else if (session.getAttribute("userName") == null)
        {
            request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
        }
        else if (request.getParameter("logOut") != null)
        {
            session.invalidate();
            request.setAttribute("logStatus", "You are logged out.");
            request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
        }
        else if (request.getParameter("gotoNewUser") != null)
        {
            request.getRequestDispatcher("/WEB-INF/newuser.jsp").forward(request, response);
        }
        else if (request.getParameter("newUser") != null)
        {
            newUser(request, response);
        }
        else if (request.getParameter("doMoneyTransfer") != null)
        {
            request.getRequestDispatcher("/WEB-INF/moneytransfer.jsp").forward(request, response);
        }
        else if (request.getParameter("codeToMail") != null)
        {
            moneyTransfer(request, response);
        }
        else if (request.getParameter("finalizeTransfer") != null)
        {
            finalizeTransfer(request, response);
        }
        else
        {
            showMainPage(request, response);
        }       
    }

    public void showMainPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession(true);

        request.setAttribute("accountBalance", (Double)session.getAttribute("accountBalance"));
        request.setAttribute("userName", (String)session.getAttribute("userName"));
        request.getRequestDispatcher("/WEB-INF/result.jsp").forward(request, response);
    }

    public void finalizeTransfer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession(true);
        String enteredTransferCode = request.getParameter("transferCode");
        String sentTransferCode     = (String)session.getAttribute("transactionCode");
        Double transferAmount       = (Double)session.getAttribute("transferAmount");
        String transferRecipient    = (String)session.getAttribute("transferRecipient");
        String userName             = (String)session.getAttribute("userName");
        Double newAccountBalance       = (Double)session.getAttribute("accountBalance") - transferAmount;
        Double newAccountBalanceRecipient       = (Double)session.getAttribute("accountBalanceRecipient") + transferAmount;
        String sqlquery = "";
        Double accountBalanceRecipient = 0.0;




        if (sentTransferCode.equals(enteredTransferCode) && !enteredTransferCode.equals(""))
        {
            session.setAttribute("transactionCode", "");
            //request.setAttribute("transferStatus", "Transfer done");
            //request.getRequestDispatcher("/WEB-INF/moneytransfer.jsp").forward(request, response); 

            try 
            {
                Class.forName("com.mysql.jdbc.Driver");

                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PW);
                
                sqlquery = "update logindata set accountbalance='" + newAccountBalance + "' where lower(username)='" + userName +"'"; 
                PreparedStatement stmt1 = conn.prepareStatement(sqlquery);
                stmt1.execute();  
                stmt1.close();

                sqlquery = "update logindata set accountbalance='" + newAccountBalanceRecipient + "' where lower(username)='" + transferRecipient +"'"; 
                stmt1 = conn.prepareStatement(sqlquery);
                stmt1.execute();  
                stmt1.close();                

                request.setAttribute("statusLog", "Transfer successful");
                request.setAttribute("accountBalance", newAccountBalance);
                request.setAttribute("userName", userName);
                request.getRequestDispatcher("/WEB-INF/result.jsp").forward(request, response);

                
                conn.close();
            }
            catch (Exception se){}
        }
        else
        {
            request.setAttribute("statusLog", "Wrong code. Try again or generate new code");
            request.getRequestDispatcher("/WEB-INF/finalize.jsp").forward(request, response); 
        }
        
    }
    public void moneyTransfer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession(true);

        Double accountBalance = (Double)session.getAttribute("accountBalance");
        
        String userName             = request.getParameter("userName");
        String transferRecipient    = request.getParameter("transferRecipient");
        String transferAmount       = request.getParameter("transferAmount");
        Double transferAmountDbl    = Double.parseDouble(transferAmount);
        String currRecipient;

        if (transferAmountDbl == 0.0)
        {
            request.setAttribute("transferStatus", "Enter transfer amount");
            request.getRequestDispatcher("/WEB-INF/moneytransfer.jsp").forward(request, response);
            return;
        }

        int counter = 0;

        try
        {
            Class.forName("com.mysql.jdbc.Driver");

            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PW);
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("select username, accountbalance from logindata where username='" + transferRecipient + "'");        

            while (rs.next())
            {
                counter++;
                session.setAttribute("accountBalanceRecipient", rs.getDouble("accountbalance"));
            }
                
            rs.close();
            stmt.close();
            conn.close();  
        }
        catch (Exception x) {}
        
        if (counter == 0)
        {
            request.setAttribute("transferStatus", "Recipient does not exists");
            request.getRequestDispatcher("/WEB-INF/moneytransfer.jsp").forward(request, response);
            return;
        }

        if (accountBalance < transferAmountDbl)
        {
            request.setAttribute("transferStatus", "You do not have enough money to make this transfer");
            request.getRequestDispatcher("/WEB-INF/moneytransfer.jsp").forward(request, response);
            return;            
        }
        else
        {
            /*Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(1000000);
            SendMail sendmail = new SendMail();
            session.setAttribute("transactionCode", String.valueOf(randomInt));
            session.setAttribute("transferAmount", transferAmountDbl);
            session.setAttribute("transferRecipient", transferRecipient);


            System.out.println(randomInt);

            sendmail.SendTransferCode("pawtek@gmail.com", String.valueOf(randomInt), transferAmount, userName, transferRecipient);*/

            request.setAttribute("statusLog", "Transfercode was sent to your mail address");
            request.setAttribute("transferAmount", transferAmountDbl);
            request.setAttribute("transferRecipient", transferRecipient);
            request.getRequestDispatcher("/WEB-INF/finalize.jsp").forward(request, response);
        }

    }

    public int resultSetCount(ResultSet rs)
    {
        int counter = 0;

        try
        {
            while (rs.next())
                counter++;
        }
        catch (Exception x) {}
        
        return counter;
    }

    public void newUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
        String enteredUser = request.getParameter("name");
        String enteredPw1 = request.getParameter("password1");
        String enteredPw2 = request.getParameter("password2");
        String currUser = "";
        String sqlquery = "";
        boolean isUser = false;

        if (!enteredPw1.equals(enteredPw2))
        {
            request.setAttribute("userStatus", "Password 1 and password 2 and not the same. Try again.");
            request.getRequestDispatcher("/WEB-INF/newuser.jsp").forward(request, response);
        }
        else
        {
                try
                {
                    Class.forName("com.mysql.jdbc.Driver");

                    Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PW);
                    Statement stmt = conn.createStatement();
                    sqlquery = "select username from logindata where username='" + enteredUser +"'";

                    ResultSet rs = stmt.executeQuery(sqlquery);

                    while (rs.next())
                    {             
                        currUser = rs.getString("username");

                        if (currUser.toLowerCase().equals(enteredUser.toLowerCase()))
                        {
                            isUser = true;
                            break;
                        }                    
                    }

                    rs.close();
                    stmt.close();
                    conn.close();

                    if(isUser)
                    {
                        request.setAttribute("userStatus", "User already exists. Try again.");
                        request.getRequestDispatcher("/WEB-INF/newuser.jsp").forward(request, response);
                    }
                    else
                    {
                        Class.forName("com.mysql.jdbc.Driver");

                        Connection conn1 = DriverManager.getConnection(DB_URL, DB_USER, DB_PW);
                        sqlquery = "insert into logindata (username, password) values ('" + enteredUser + "','" + enteredPw1 + "')"; 
                        PreparedStatement stmt1 = conn1.prepareStatement(sqlquery);
                        
                        stmt1.execute();  

                        request.setAttribute("logStatus", "New user added. Please log on.");
                        request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);

                        stmt1.close();
                        conn1.close();
                    }

                }
                catch (Exception se){}
        }
    }

    public ResultSet doSqlQuery(String sqlQuery)
    {
        ResultSet rsFinal = null;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");

            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PW);
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(sqlQuery);

            rsFinal = rs;            

            rs.close();
            stmt.close();
            conn.close();  
        }
        catch (Exception x) {}

            int counter = 0;
            try
            {
                while (rsFinal.next())
                    counter++;
            }
            catch (Exception x) {}

        return rsFinal;
    }
    public void logIn(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        boolean   accessGranted = false;
        boolean   accountBlocked = false;
        
        Date dateNow = new Date();
        Date lockedUntil;
        String lockedUntilString;

        HttpSession session = request.getSession(true);
		
        int loginCounter = 0;      
         
        String enteredUser     = request.getParameter("name").toLowerCase();
        String enteredPw       = request.getParameter("password");
        if (enteredUser == "" || enteredPw == "")
        {
            request.setAttribute("logStatus", "Password or user cannot be empty. Try again.");
            request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
            return;
        }

        response.setContentType("text/html");  
		
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PW);
            Statement stmt = conn.createStatement();
            PreparedStatement stmt1;
            String sqlquery = "select username, password, accountbalance, lockeduntil, logincounter from logindata where lower(username)='" + enteredUser + "'";

            ResultSet rs = stmt.executeQuery(sqlquery);
            while (rs.next())
            {             
                session.setAttribute("userName", rs.getString("username"));
                session.setAttribute("accountBalance", rs.getDouble("accountbalance"));
                
                loginCounter = rs.getInt("logincounter");
                String test = rs.getString("logincounter");
                System.out.println("AAA " + test);
                lockedUntilString = rs.getString("lockeduntil");
                
                String passwordDB = rs.getString("password"); 

                if (lockedUntilString != null)
                {
                    lockedUntil = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lockedUntilString);

                    if (dateNow.before(lockedUntil))
                        accountBlocked = true;
                }
                
                if (!accountBlocked)
                {
                    if (passwordDB.equals(enteredPw))
                        accessGranted = true;
                }
      
            }

            rs.close();


            if (accessGranted)
            {
                sqlquery = "update logindata set logincounter=0 where lower(username)='" + enteredUser +"'"; 
                stmt1 = conn.prepareStatement(sqlquery); 
                stmt1.execute();
                stmt1.close();
                showMainPage(request, response);
            }
            else if (accountBlocked)
            {
                request.setAttribute("logStatus", "The account is blocked");
                request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
                return;
            }
            else
            {
                loginCounter++;
                int triesLeft = 3 - loginCounter;
                
                if (loginCounter >=3)
                {

                    Date plusFifteenMinutes = new Date(System.currentTimeMillis()+15*60*1000);
                    String plusFifteenMinutesString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(plusFifteenMinutes);

                    sqlquery = "update logindata set lockeduntil='" + plusFifteenMinutesString + "' where lower(username)='" + enteredUser +"'"; 
                    stmt1 = conn.prepareStatement(sqlquery); 
                    stmt1.execute();
                    stmt1.close();

                    sqlquery = "update logindata set logincounter=0 where lower(username)='" + enteredUser +"'"; 
                    stmt1 = conn.prepareStatement(sqlquery); 
                    stmt1.execute();
                    stmt1.close();
                    
                    request.setAttribute("logStatus", "Your account is locked for 15 minutes.");
                    request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
                }
                else
                {
                    sqlquery = "update logindata set logincounter=" + loginCounter +" where lower(username)='" + enteredUser +"'"; 
                    stmt1 = conn.prepareStatement(sqlquery); 
                    stmt1.execute();
                    stmt1.close();

                    request.setAttribute("logStatus", "Wrong login data. You have " + triesLeft + " tries left.");
                    request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
                }
            }

            
            stmt.close();
            conn.close();
        }
        catch (Exception se) 
        {System.out.println(se);}
    }

    public void destroy()
    {

    }
}  