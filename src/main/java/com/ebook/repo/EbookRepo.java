package com.ebook.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ebook.entity.BookEntity;
import com.ebook.entity.Ebook;
@Repository
public interface EbookRepo extends JpaRepository<Ebook, Long>{

	List<Ebook> findByBook(BookEntity book);

}
