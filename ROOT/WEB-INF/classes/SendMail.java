import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

class SendMail
{
    public void SendTransferCode (String mailAddressTo, String transferCode, String transferAmount, String moneySender, String moneyRecipient)
    {
        String host = "localhost";
        
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.host", "smtp.gmail.com");
        properties.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.setProperty("mail.smtp.port", "587");

        //Session session = Session.getDefaultInstance(properties);

        Session session = Session.getInstance(properties,
          new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("pawtek@gmail.com", ""); // haslo nie kodowane, do dopracowania
            }
          });

        try
        {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("pawtek@gmail.com"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailAddressTo));
            message.setSubject("Your transfer code at Bank");
            message.setText(transferCode);
            //message.setText(String.Format("Your username: %s%nRecipient name: %s%nTransfer amount: %s%nTransferCode: %s", moneySender, moneyRecipient, transferAmount, transferCode));

            Transport.send(message);

        }
        catch (Exception x) 
        {
            x.printStackTrace();
        }

        // message.Subject = 
        // message.Body = String.format(;

        
        // client.Send(message);
        // message.Dispose();
    }
}