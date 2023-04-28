package com.ebook.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ebook.dto.BookDto;
import com.ebook.entity.BookEntity;
import com.ebook.entity.Ebook;
import com.ebook.exceptions.DataViolationException;
import com.ebook.exceptions.InvalidUserException;
import com.ebook.exceptions.NoBookException;
import com.ebook.exceptions.TitleExistsException;
import com.ebook.repo.BookRepo;


public interface BookService {

	public BookDto createBook(BookDto book)  throws InvalidUserException, TitleExistsException;
	public List<BookDto> getAllBooks(String username)throws NoBookException;//,Integer pageNumber,Integer pageSize);
	public String deleteBook(long bookId)throws NoBookException,DataViolationException;
	public String updateBook(BookDto bookDto,long bookId)throws NoBookException;
	public BookDto getBookById(Long id)throws NoBookException;
	public long countIncompleteBooks();
	List<BookDto> getCompletedBooksByAuthorname(String authorname)throws InvalidUserException;
	List<BookDto> getIncompleteBooksByAuthorname(String authorname)throws InvalidUserException;
	List<Long> getAllBookIdsByAuthor(String authorId);
	
}
