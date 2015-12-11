package pisec.util;

import pisec.security.AlarmManager;
import pisec.util.enums.Setting;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by binar on 9/21/2015.
 */

public class SendEmail {

    public static void send(AlarmManager.Alarm alarm) {

        String to = Settings.GetSettingForKey(Setting.EMAIL_ADDR);

        //***** smtp login details *****
        //Dont save these details in a file
        //Strongly recommended to generate a single application access key from google
        //Dont use your actual password!
        String from = "@gmail.com";
        final String username = "@gmail.com";
        final String password = "";

        Properties props = new Properties();
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                }
        );
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("PiSec Alarm Notice");

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(alarm.toString());

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            try {
                if(!alarm.getPictureLocation().equals("")) {
                    File zippedImages = zipImages(alarm);
                    String filename = zippedImages.getAbsolutePath();
                    DataSource source = new FileDataSource(filename);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(filename);
                    multipart.addBodyPart(messageBodyPart);
                }
            }catch(Exception e){
                Logger.Log("Unable to attach zipped images!");
            }

            message.setContent(multipart);

            Transport.send(message);
            Logger.Log("Email Sent!");

        } catch (MessagingException e) {
            e.printStackTrace();
            Logger.Log("Unable to send email! "+e.getMessage());

        }
    }

    private static File zipImages(AlarmManager.Alarm alarm){

        StringBuffer output = new StringBuffer();

        try {
            final String cmd = "zip -r "+alarm.getPictureLocation()+".zip "+alarm.getPictureLocation()+"/";
            Logger.Log(cmd);

            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();


            InputStreamReader is = new InputStreamReader(p.getInputStream());
            StringBuilder sb=new StringBuilder();
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();

            while(read != null) {
                //System.out.println(read);
                sb.append(read);
                read =br.readLine();
            }

            Logger.Log(sb.toString());
        }catch(Exception e){
            Logger.Log(e.getMessage());
        }

        return new File(alarm.getPictureLocation()+".zip");
    }

}
