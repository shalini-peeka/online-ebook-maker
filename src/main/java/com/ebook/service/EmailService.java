package com.ebook.service;
 
import java.io.File;
import java.util.Properties;
 
import org.apache.commons.mail.EmailException;

import com.ebook.exceptions.DoesNotExistsException;
 
 
 
 
public interface EmailService {
 
public void sendEmailWithAttachments(String to, String subject, String text, File pdfFile, File wordFile) throws EmailException, DoesNotExistsException ;
 
 
 
}

