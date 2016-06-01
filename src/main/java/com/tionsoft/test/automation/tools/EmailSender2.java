package com.tionsoft.test.automation.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class EmailSender2 {
	protected final Log logger = LogFactory.getLog("kari");

	@SuppressWarnings("deprecation")
	public void sendEmail(String from, String to, String subject, String content) {
		Properties prop = new Properties();
		
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", ConfigProperty.get("SMTP_ADDRESS"));
		prop.put("mail.smtp.auth","true");
		prop.put("mail.smtp.port", "25");
		prop.put("mail.debug", "false");
		
		Session session = Session.getInstance(prop,
				  new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(ConfigProperty.get("SMTP_USER"), ConfigProperty.get("SMTP_USER_PASSWORD"));
					}
				  });		
		
		try {
			MimeMessage message = new MimeMessage(session);
			InternetAddress inetfrom = new InternetAddress(from, ConfigProperty.get("SMTP_USER"), "euc-kr");
			message.setFrom(inetfrom);
			
			InternetAddress inetto = new InternetAddress(to);
			InternetAddress[] toList = { inetto };
			message.setRecipients(Message.RecipientType.TO, toList);
			message.setSubject(subject,"EUC-KR");
			
			BodyPart body = new MimeBodyPart();
            // freemarker stuff.
            Configuration cfg = new Configuration();
            Template template = cfg.getTemplate(ConfigProperty.get("SMTP_TEMPLATE"));
           
            Map<String, String> rootMap = new HashMap<String, String>();
            rootMap.put("body", content);
            Writer out = new StringWriter();
            template.process(rootMap, out);
            // freemarker stuff ends.
            
            /* you can add html tags in your text to decorate it. */
            body.setContent(out.toString(), "text/html; charset=euc-kr");

            MimeMultipart multipart = new MimeMultipart();
            multipart.setSubType("related"); 
            multipart.addBodyPart(body);
            
            multipart.addBodyPart(createInlineImagePart(ConfigProperty.get("MAIL_IMAGE_01")));            
			message.setContent(multipart);
			message.setHeader("MIME-Version", "1.0");
			message.setHeader("Content-Type", multipart.getContentType());			
			
			Transport.send(message);
			logger.info("======= Comment Email Send =======");
			logger.info("FROM :["+from+"]");
			logger.info("TO :["+to+"]");
			logger.info("==================================");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();			
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	class EmailAuthenticator extends Authenticator {
		private String id;
		private String pw;

		public EmailAuthenticator(String id, String pw) {
			this.id = id;
			this.pw = pw;
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(id, pw);
		}
	}
	
	BodyPart createInlineImagePart(String imagePath) throws MessagingException, FileNotFoundException, IOException {
		File img = new File(imagePath);
		byte[] imgBytes = IOUtils.toByteArray(new FileInputStream(img));
		byte[] base64EncodedImageContentByteArray = Base64.encodeBase64(imgBytes);
		
		String[] pathTokens = imagePath.split("/");
		String filename = pathTokens[pathTokens.length - 1];
		String[] filenameTokens = filename.split("[.]");
		String contentType = "image/" + filenameTokens[filenameTokens.length - 1];
		
		InternetHeaders headers = new InternetHeaders();
		
		headers.addHeader("Content-Type", contentType);
		headers.addHeader("Content-Transfer-Encoding", "base64");
		MimeBodyPart imagePart = new MimeBodyPart(headers, base64EncodedImageContentByteArray);
		imagePart.setDisposition(MimeBodyPart.INLINE);
		//imagePart.setContentID("&lt;" + filename.replace(".", "_") + "&gt;");
		imagePart.setContentID("<" + filename.replace(".", "_") + ">");
		imagePart.setFileName(filename);
		
		return imagePart;
	}
}