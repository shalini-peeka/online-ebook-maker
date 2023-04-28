package com.ebook.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ebook.entity.AppUser;
import com.ebook.entity.BookEntity;
@Repository
public interface BookRepo extends JpaRepository<BookEntity,Long>{

	List<BookEntity> findByAuthorUserName(String name);
	@Query("SELECT b.title from BookEntity b WHERE b.title=:title")
	String findByTitle(String title);
	
}
