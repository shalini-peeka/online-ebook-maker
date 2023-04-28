package com.ebook.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ebook.dto.BookDto;
import com.ebook.entity.AppUser;
import com.ebook.entity.BookEntity;
import com.ebook.entity.Ebook;
import com.ebook.enums.RequestStatus;
import com.ebook.enums.Role;
import com.ebook.enums.StatusType;
import com.ebook.exceptions.DataViolationException;
import com.ebook.exceptions.DoesNotExistsException;
import com.ebook.exceptions.InvalidUserException;
import com.ebook.exceptions.NoBookException;
import com.ebook.exceptions.TitleExistsException;
import com.ebook.repo.BookRepo;
import com.ebook.repo.EbookRepo;
import com.ebook.repo.UserRepo;
import com.ebook.service.BookService;

@Service
public class BookServiceImpl implements BookService{

    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private EbookRepo ebookRepo;
 
    @Override
    public BookDto createBook(BookDto book) throws InvalidUserException, TitleExistsException {
        // TODO Auto-generated method stub

        BookEntity bookEntity=new BookEntity();
        if(bookRepo.findByTitle(book.getTitle())!=null) {
            throw new TitleExistsException(ExceptionsConstants.title_exist_exception);
        }
        else {
        bookEntity.setTitle(book.getTitle());
        bookEntity.setContent(book.getContent());
        bookEntity.setStatus(book.getStatus());
        bookEntity.setStartDate(book.getEndDate());
        bookEntity.setEndDate(book.getEndDate());
        AppUser authorDetails=userRepo.findById(book.getAuthorId()).orElseThrow(()->new InvalidUserException(ExceptionsConstants.invalid_user_exception));
        if(authorDetails.getRole().equals(Role.AUTHOR)) {
            bookEntity.setAuthor(authorDetails);
            bookRepo.save(bookEntity);
            return book;
        }
        else
        {
            throw new InvalidUserException(ExceptionsConstants.invalid_user_exception);
        }
        }
    }

    public BookDto getBookById(Long id) throws NoBookException {
        BookEntity bookEntity=bookRepo.findById(id).orElseThrow(()->new NoBookException(ExceptionsConstants.book_not_found_exception));

        BookDto bookDto=new BookDto();
    System.out.println("ServiceImpl "+bookEntity);

        bookDto.setBookId(bookEntity.getBookId());
        bookDto.setAuthorId(bookEntity.getAuthor().getUserName());
        bookDto.setContent(bookEntity.getContent());
        bookDto.setEndDate(bookEntity.getEndDate());
        bookDto.setStartDate(bookEntity.getStartDate());
        bookDto.setStatus(bookEntity.getStatus());
        bookDto.setTitle(bookEntity.getTitle());
        return bookDto;

    }
    @Override
    public List<BookDto> getAllBooks(String userName)throws NoBookException{
        // TODO Auto-generated method stub
        AppUser userdata=userRepo.findById(userName).orElseThrow(()->new NoBookException(ExceptionsConstants.no_book_exception));
        List<BookEntity> listOfBooks= bookRepo.findAll().stream().filter((x)->(x.getAuthor().getUserName().equals(userdata.getUserName()))).collect(Collectors.toList());
        List<BookDto> listOfBookDto=listOfBooks.stream().map(x->{
            BookDto dto=new    BookDto();
            dto.setAuthorId(x.getAuthor().getUserName());
            dto.setBookId(x.getBookId());
            dto.setContent(x.getContent());
            dto.setEndDate(x.getEndDate());
            dto.setStartDate(x.getStartDate());
            dto.setStatus(x.getStatus());
            dto.setTitle(x.getTitle());
 
            return dto;
        }).collect(Collectors.toList());
        return listOfBookDto;
 
    }

    @Override
    public String deleteBook(long bookId) throws NoBookException, DataViolationException {
        // TODO Auto-generated method stub
        BookEntity bookEntity=bookRepo.findById(bookId).orElseThrow(()->new NoBookException("There is no book with bookId: "+bookId+" to delete"));
        try {
        bookRepo.deleteById(bookId);
        return "Book with bookId "+bookId+" deleted successfully";
        }
        catch(DataIntegrityViolationException e) {
            throw new DataViolationException(ExceptionsConstants.data_violation_exception);
        }
    }
 

    @Override
    public String updateBook(BookDto bookDto, long bookId)throws NoBookException {
        // TODO Auto-generated method stub
        BookEntity bookEntity=bookRepo.findById(bookId).orElseThrow(()->new NoBookException(ExceptionsConstants.book_not_found_exception));
        bookEntity.setContent(bookDto.getContent());
        bookEntity.setEndDate(bookDto.getEndDate());
        bookEntity.setStartDate(bookDto.getStartDate());
        bookEntity.setStatus(bookDto.getStatus());
        bookEntity.setTitle(bookDto.getTitle());
        bookRepo.save(bookEntity);
        return SucessConstants.update_success;
    }
 
    @Override
    public long countIncompleteBooks() {
        // TODO Auto-generated method stub
        return bookRepo.findAll().stream().filter(book->book.getStatus()==StatusType.PAUSE||book.getStatus()==StatusType.RESUME).count();
 
    }
        @Override
        public List<BookDto> getCompletedBooksByAuthorname(String authorname) throws InvalidUserException {
            // TODO Auto-generated method stub
            AppUser user=userRepo.findById(authorname).orElseThrow(()->new InvalidUserException("Invalid username"));
            if(user.getRole().equals(Role.AUTHOR)) {
            List<BookEntity> listOfCompletedBooks = bookRepo.findAll().stream().filter(book -> book.getStatus().equals(StatusType.COMPLETED) && book.getAuthor().getUserName().equals(authorname)).collect(Collectors.toList());
            List<BookDto> listOfCompletedBooksDto = listOfCompletedBooks.stream().map(book ->{
                BookDto bookDto = new BookDto();
                bookDto.setBookId(book.getBookId());
                bookDto.setAuthorId(book.getAuthor().getUserName());
                bookDto.setContent(book.getContent());
                bookDto.setEndDate(book.getEndDate());
                bookDto.setStartDate(book.getStartDate());
                bookDto.setStatus(book.getStatus());
                bookDto.setTitle(book.getTitle());
                return bookDto;
            }).collect(Collectors.toList());
            return listOfCompletedBooksDto;
            }
            else {
                throw new InvalidUserException(ExceptionsConstants.user_not_found_exception);
            }
        }
 
        @Override
        public List<BookDto> getIncompleteBooksByAuthorname(String authorname) throws InvalidUserException {
            // TODO Auto-generated method stub
            AppUser user=userRepo.findById(authorname).orElseThrow(()->new InvalidUserException("Invalid author name"));
            if(user.getRole().equals(Role.AUTHOR)) {
            List<BookEntity> listOfIncompleteBooks = bookRepo.findAll().stream().filter(book -> !book.getStatus().equals(StatusType.COMPLETED) && book.getAuthor().getUserName().equals(authorname)).collect(Collectors.toList());
            List<BookDto> listOfIncompleteBooksDto = listOfIncompleteBooks.stream().map(book ->{
                BookDto bookDto = new BookDto();
                bookDto.setBookId(book.getBookId());
                bookDto.setAuthorId(book.getAuthor().getUserName());
                bookDto.setContent(book.getContent());
                bookDto.setEndDate(book.getEndDate());
                bookDto.setStartDate(book.getStartDate());
                bookDto.setStatus(book.getStatus());
                bookDto.setTitle(book.getTitle());
                return bookDto;
            }).collect(Collectors.toList());
            return listOfIncompleteBooksDto;
            }
            else {
                throw new InvalidUserException(ExceptionsConstants.user_not_found_exception);
            }
        }
        @Override
    	public List<Long> getAllBookIdsByAuthor(String authorname) {
    		    
    		        List<BookEntity> books = bookRepo.findByAuthorUserName(authorname);
    		        List<Long> bookIds = new ArrayList<>();
    		        for (BookEntity book : books) {
    		            bookIds.add(book.getBookId());
    		        }
    		        return bookIds;
    	}
 
}





