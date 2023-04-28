package com.ebook.serviceImpl;
 
import java.io.File;
import java.util.Optional;
import java.util.Properties;
 
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
 
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebook.entity.AppUser;
import com.ebook.exceptions.DoesNotExistsException;
import com.ebook.repo.UserRepo;
import com.ebook.service.EmailService;
 

@Service
public class EmailServiceImpl  implements EmailService{
	@Autowired
	private UserRepo appUserRepository;
	
    public void sendEmailWithAttachments(String to, String subject, String text, File pdfFile, File wordFile) throws EmailException, DoesNotExistsException {
    	Optional<AppUser> author = appUserRepository.findByuserName(to);
    	if (author.isEmpty()) {
    	throw new DoesNotExistsException("invalid author");
    	}

        // Configure SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
 
        // Configure authentication
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("ebookmaker12@gmail.com", "ohrnmwljkflwxndw");
            }
        };
 
        // Create email object
        MultiPartEmail email = new MultiPartEmail();
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(587);
        email.setAuthenticator(auth);
        email.setStartTLSEnabled(true);
        email.setFrom("ebookmaker12@gmail.com");
        email.addTo(to);
        email.setSubject(subject);
        email.setMsg(text);
 
        // Attach PDF file
        if (pdfFile != null) {
            EmailAttachment pdfAttachment = new EmailAttachment();
            pdfAttachment.setDisposition(EmailAttachment.ATTACHMENT);
            pdfAttachment.setName(pdfFile.getName());
            pdfAttachment.setPath(pdfFile.getAbsolutePath());
            email.attach(pdfAttachment);
        }
 
        // Attach Word file
        if (wordFile != null) {
            EmailAttachment wordAttachment = new EmailAttachment();
            wordAttachment.setDisposition(EmailAttachment.ATTACHMENT);
            wordAttachment.setName(wordFile.getName());
            wordAttachment.setPath(wordFile.getAbsolutePath());
            email.attach(wordAttachment);
        }
 
        // Send email
        email.send();
    }
 
}

