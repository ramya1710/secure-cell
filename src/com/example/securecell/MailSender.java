package com.example.securecell;


import java.util.Date; 
import java.util.Properties; 

import javax.activation.CommandMap; 
import javax.activation.DataHandler; 
import javax.activation.DataSource; 
import javax.activation.FileDataSource; 
import javax.activation.MailcapCommandMap; 
import javax.mail.BodyPart; 
import javax.mail.Message;
import javax.mail.Multipart; 
import javax.mail.PasswordAuthentication; 
import javax.mail.Session; 
import javax.mail.Transport;
import javax.mail.internet.InternetAddress; 
import javax.mail.internet.MimeBodyPart; 
import javax.mail.internet.MimeMessage; 
import javax.mail.internet.MimeMultipart; 

import android.os.StrictMode;
import android.util.Log;

public class MailSender extends javax.mail.Authenticator{
	
	private String user; 
	private String pass; 
	 
	private String[] to; 
	private String from; 
	private String port; 
	private String sport; 
	 
	private String host; 
	 
	private String subject; 
	private String body; 
	
	private double latitude;
	private double longitude;
	 
	//private boolean auth; 
	   
	//private boolean debuggable; 
	 
	private Multipart multipart; 
	
	BodyPart messagebodypart;
	
	public MailSender(){
		host = "smtp.gmail.com"; // default smtp server 
		port = "465"; // default smtp port 
	    sport = "465"; // default socketfactory port 
	 
	    user = ""; // username 
	    pass = ""; // password 
	    from = ""; // email sent from 
	    subject = ""; // email subject 
	    body = ""; // email body 
	 
	    //debuggable = false; // debug mode on or off - default off 
	    //auth = true; // smtp authentication - default on 
	   
	    
	    MailcapCommandMap mailcapcommandmap = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
	    
	    mailcapcommandmap.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
	    mailcapcommandmap.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml"); 
	    mailcapcommandmap.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain"); 
	    mailcapcommandmap.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
	    mailcapcommandmap.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822"); 
	    CommandMap.setDefaultCommandMap(mailcapcommandmap); 
	}
	
	public void initializer(String _user, String _pass){		
		this.user = _user;
		this.pass = _pass;		
	}
	
 	public boolean send(String audioFilename, String picFileName) throws Exception{
		Properties properties = new Properties();
		
		properties.setProperty("mail.transport.protocol", "smtp");  
		properties.put("mail.smtp.host", host);
		properties.put("mail.debug", "true");
		properties.put("mail.smtp.auth", "true"); 		
		properties.put("mail.smtp.port", port); 
		properties.put("mail.smtp.socketFactory.port", sport); 
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.smtp.quitwait", "false");   		
		properties.put("mail.smtp.socketFactory.fallback", "false"); 
		
		Log.d("User",user);
		Log.d("pass",pass);
		Log.d("To Length",""+to.length);
		Log.d("from",from);
		Log.d("subject",subject);
		Log.d("body",body);
		
		
		if(!user.equals("") && !pass.equals("") && to.length > 0 && !from.equals("") && !subject.equals("") ){
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			
			Session session = Session.getDefaultInstance(properties, 
														new javax.mail.Authenticator() {
															protected PasswordAuthentication getPasswordAuthentication() {
																Log.d("Auth:",""+new PasswordAuthentication(user, pass));
																return new PasswordAuthentication(user, pass);
															}
														}); 

			MimeMessage message = new MimeMessage(session); 		
			
			message.setFrom(new InternetAddress(from));
			
			InternetAddress[] addressTo = new InternetAddress[to.length]; 
			
			for (int i = 0; i < to.length; i++) { 
				addressTo[i] = new InternetAddress(to[i]); 
		    }
			
			message.setRecipients(Message.RecipientType.TO, addressTo);
			 
			message.setSubject(subject);
			message.setSentDate(new Date());
			
			BodyPart messagebodypart = new MimeBodyPart(); 
			
			//latitude = 36.88743398;
			//longitude = -76.30350349;
			
			body = "Your phone is being tried to unlock by unknown persons. <br/>"
					+"For Location of device tap below link or check the map Image<br/><br/>"
					+"http://maps.google.com/maps?q=" + latitude+","+longitude;
			
			String appLatitude = String.valueOf(latitude);
			String appLongitude = String.valueOf(longitude);
			
			String locCombi = appLatitude+","+appLongitude;
			
			String locImage = "<img src=\"http://www.google.com/staticmap?center="+locCombi+"&markers="+locCombi+",blue&zoom=17&size=450x250&key=ABQIAAAA6C4bndUCBastUbawfhKGURQviNTBAztVc6-FhSQEQv6BdFn_BBRfktMUHCKH-MICXpvRmJU3x-Ly0w \" />";
			
			String picImage = "<img src=\"cid:image\" height=\"250\" width=\"450\">";
			
			
			messagebodypart.setContent(body+"<br/><br/><br/>"+locImage+"<br/><br/><br/>"+picImage,"text/html");
			
			MimeBodyPart messagebodypart1 = new MimeBodyPart(); 
		    DataSource datasource1 = new FileDataSource(audioFilename); 		 
	    	messagebodypart1.setDataHandler(new DataHandler(datasource1));
	    	messagebodypart1.setFileName(audioFilename);
	    	
	    	MimeBodyPart messagebodypart2 = new MimeBodyPart(); 
		    DataSource datasource2 = new FileDataSource(picFileName); 		 
		    messagebodypart2.setDataHandler(new DataHandler(datasource2));
		    messagebodypart2.setHeader("Content-ID", "<image>");	    	
	    	
	    	multipart = new MimeMultipart();
	    	
	    	multipart.addBodyPart(messagebodypart);     	
		    multipart.addBodyPart(messagebodypart1); 
		    multipart.addBodyPart(messagebodypart2); 
				    
		    message.setContent(multipart);

			Transport.send(message); 

			return true;			 
		}
		else{
			return false;
		}
	}
	
	

	public String getBody() { 
        return body; 
      } 
    
    public void setBody(String _body) { 
        this.body = _body; 
    }

    public String[] getTo() {
        return to;
    }

    public void setTo(String[] _to) {
        this.to = _to;
    }
    
    public void setLatitude(double _latitude) {
        this.latitude = _latitude;
    }
    
    public void setLongitude(double _longitude) {
        this.longitude = _longitude;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String _from) {
        this.from = _from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String _subject) {
        this.subject = _subject;
    } 
	
	

}
