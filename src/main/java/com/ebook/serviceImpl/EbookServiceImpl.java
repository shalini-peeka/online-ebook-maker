package com.ebook.serviceImpl;
 

import java.io.ByteArrayInputStream;
 
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.springframework.stereotype.Service;

import com.ebook.dto.EbookDto;
import com.ebook.entity.AppUser;
import com.ebook.entity.BookEntity;
import com.ebook.entity.Ebook;
import com.ebook.entity.EbookManagement;
import com.ebook.enums.RequestStatus;
import com.ebook.exceptions.DoesNotExistsException;
import com.ebook.repo.BookRepo;
import com.ebook.repo.EbookManagementRepo;
import com.ebook.repo.EbookRepo;
import com.ebook.repo.UserRepo;
import com.ebook.service.EbookService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import ch.qos.logback.classic.Logger;
import javax.persistence.ManyToOne;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class EbookServiceImpl implements EbookService {
    @Autowired
    private BookRepo bookrepo;
    @Autowired
    private EbookManagementRepo managmentrepo;
    @Autowired
    private EbookRepo ebookrepo;
    @Autowired
    private UserRepo userRepo;
 
    @Override
    public Ebook createEbook(long requestId)throws DoesNotExistsException {
        // TODO Auto-generated method stub
        Optional<EbookManagement> mang = managmentrepo.findById(requestId);
        if (mang.isPresent()) {
            RequestStatus status = mang.get().getRequestStatus();
            if(status.equals(RequestStatus.APPROVED)) {
                String format = mang.get().getFormat();
                long bookId = mang.get().getBoook().getBookId();
                Optional<BookEntity> optionalbook = bookrepo.findById(bookId);
                if (optionalbook.isPresent()) {
                    BookEntity book = optionalbook.get();
                    String title = book.getTitle();
                    String content = book.getContent();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    if (format.equalsIgnoreCase("pdf")) {
                        try {
                            Document document = new Document(PageSize.A4);
                            PdfWriter.getInstance(document, out);
                            document.open();
                            Paragraph paragraphTitle = new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
                            paragraphTitle.setAlignment(Element.ALIGN_CENTER);
                            document.add(paragraphTitle);
                            document.add(new Paragraph(content));
                            document.close();
                        } catch (Exception e) {
                            throw new RuntimeException(ExceptionsConstants.error_generating_pdf, e);
                        }
                    } else if (format.equalsIgnoreCase("docx")) {
                        try {
                            XWPFDocument document = new XWPFDocument();
                            XWPFParagraph paragraphTitle = document.createParagraph();
                            XWPFRun runTitle = paragraphTitle.createRun();
                            runTitle.setText(title);
                            runTitle.setBold(true);
                            runTitle.setFontSize(18);
                            runTitle.addBreak();
                            XWPFParagraph paragraphContent = document.createParagraph();
                            XWPFRun runContent = paragraphContent.createRun();
                            runContent.setText(content);
                            document.write(out);
                            document.close();
                        } catch (Exception e) {
                            throw new RuntimeException(ExceptionsConstants.error_generating_docx, e);
                        }
                    } else {
                        throw new DoesNotExistsException(ExceptionsConstants.invalid_format_exception);
                    }
                    byte[] data = out.toByteArray();
                    Ebook ebook = new Ebook();
                    ebook.setData(data);
                    ebook.seteBookName(title);
                    ebook.setFormat(format);
                    ebook.setBook(book);
                   return ebookrepo.save(ebook);
                } else {
                    throw new DoesNotExistsException(ExceptionsConstants.book_not_found_exception);
                }
            }
            else if(status.equals(RequestStatus.PENDING))
            {
                throw new DoesNotExistsException(ExceptionsConstants.pending_status_exception);
            }
            else
            {
                throw new DoesNotExistsException(ExceptionsConstants.rejected_status_exception);
            }
        } 
        else {
            throw new DoesNotExistsException(ExceptionsConstants.invalid_request_id_exception);
        }
    }
//    @Override
//    public List<Ebook> getAllEbooks() {
//        // TODO Auto-generated method stub
//        List<Ebook> list=ebookrepo.findAll();
//        //System.out.println(list.get(0).getEbookId()+" "+list.get(0).geteBookName()+" "+list.get(0).getData().toString());
//        return list;
//    }
    @Override
    public List<EbookDto> getAllEbooks() {
    List<Ebook> ebookList = ebookrepo.findAll();
    List<EbookDto> ebooks = new ArrayList<>();
     
    // iterate over each ebook and create an EbookDto object with all the attributes
    for (Ebook ebook : ebookList) {
    EbookDto ebookDto = new EbookDto();
    ebookDto.setEbookId(ebook.getEbookId());
    ebookDto.seteBookName(ebook.geteBookName());
    ebookDto.setData(ebook.getData());
    ebookDto.setFormat(ebook.getFormat());
    ebookDto.setUserName(ebook.getBook().getAuthor().getUserName());
    ebooks.add(ebookDto);
    }
     
    return ebooks;
    }
     


    @Override
    public Ebook getEbookById(long ebookId) throws DoesNotExistsException{
        // TODO Auto-generated method stub
        Optional<Ebook> ebbok = ebookrepo.findById(ebookId);
        if(ebbok.isPresent())
        {
            return ebbok.get();
        }
        else
        {
            throw new DoesNotExistsException(ExceptionsConstants.ebook_not_found_exception);
        }
    }
    @Override
    public String deleteEbook(long ebookId) throws DoesNotExistsException{
        Optional<Ebook> ebbok = ebookrepo.findById(ebookId);
        if(ebbok.isPresent()) {
            ebookrepo.deleteById(ebookId);
            return SucessConstants.delete_success;
        }
        else
        {
            throw new DoesNotExistsException(ExceptionsConstants.ebook_not_found_exception);
        }
    }

    @Override
    public List<Ebook> getAllEbooksByAuthor(String userName) throws DoesNotExistsException {
    // Find the author by the provided username
         Optional<AppUser> author = userRepo.findByuserName(userName);
         if (author.isEmpty()) {
                throw new DoesNotExistsException(ExceptionsConstants.invalid_author_exception);
            }

    // Find all books authored by the author
    List<BookEntity> books = bookrepo.findByAuthorUserName(author.get().getUserName());

    // Find all ebooks associated with the books
    List<Ebook> ebooks = new ArrayList<>();
    for (BookEntity book : books) {
    List<Ebook> bookEbooks = ebookrepo.findByBook(book);
    ebooks.addAll(bookEbooks);
    }

    return ebooks;
    }
    @Override
    public String getDocumentById(Long id) throws DoesNotExistsException {
        // TODO Auto-generated method stub
        //Optional<Ebook> bk = ebookrepo.findById(id);
        Optional<Ebook> bk = ebookrepo.findById(id);
        if(bk.isPresent())
        {
            if(bk.get().getFormat().equalsIgnoreCase("pdf"))
            {
                 byte[] data = bk.get().getData(); 
                 String encodedData = "data:application/pdf;base64,"+Base64.getEncoder().encodeToString(data);
                 return encodedData;

            }
            else if(bk.get().getFormat().equalsIgnoreCase("docx")) {
                byte[] data = bk.get().getData();
                String encodedData = "data:application/vnd.openxmlformata-officedocument.wordprocessingml.document;base64,"+Base64.getEncoder().encodeToString(data);
                return encodedData;

            }
            else
            {
                throw new DoesNotExistsException(ExceptionsConstants.book_not_found_exception);
            }
        }
        else
        {
            throw new DoesNotExistsException(ExceptionsConstants.ebook_not_found_exception);
        }
    }

 
}

