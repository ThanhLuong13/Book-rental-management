package com.example.brmproject.repositories;

import com.example.brmproject.domain.entities.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookEntityRepository extends JpaRepository<BookEntity, Integer> {

    @Query("select b from BookEntity b where b.title like %:title%")
    Page<BookEntity> findByTitle(String title, Pageable pageable);
}