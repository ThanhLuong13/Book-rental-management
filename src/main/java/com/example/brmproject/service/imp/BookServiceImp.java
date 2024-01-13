package com.example.brmproject.service.imp;

import com.example.brmproject.domain.dto.BookDTO;
import com.example.brmproject.domain.entities.BookEntity;
import com.example.brmproject.domain.entities.BookshelfCaseEntity;
import com.example.brmproject.exception.ResourceNotFoundException;
import com.example.brmproject.repositories.BookDetailEntityRepository;
import com.example.brmproject.repositories.BookEntityRepository;
import com.example.brmproject.repositories.BookshelfCaseEntityRepository;
import com.example.brmproject.service.BookService;
import com.example.brmproject.ultilities.SD.BookDetailStatus;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImp  implements BookService {

    ModelMapper modelMapper;
    BookEntityRepository bookRepo;

    BookDetailEntityRepository bookDetailRepo;
    BookshelfCaseEntityRepository caseEntityRepository;
    @Autowired
    public BookServiceImp(ModelMapper modelMapper, BookEntityRepository bookRepo, BookDetailEntityRepository bookDetailRepo, BookshelfCaseEntityRepository caseEntityRepository) {
        this.modelMapper = modelMapper;
        this.bookRepo = bookRepo;
        this.bookDetailRepo = bookDetailRepo;
        this.caseEntityRepository = caseEntityRepository;
    }

    @Override
    public List<BookDTO> findAll() {

        return bookRepo.findAll().stream().map(book -> countAvailable(book)).collect(Collectors.toList());
    }

    @Override
    public List<BookDTO> getListBookByBookId(List<Integer> bookIds) {
            List<BookDTO> list= new ArrayList<>();
        for (Integer bookId:bookIds)
        {
            BookDTO bookDTO= mapToDTO(bookRepo.findById(bookId).get());
            list.add(bookDTO);
        }
        return list;
    }

    @Override
    public BookDTO addNewBook(BookDTO bookDTO) {
        BookEntity newBook = bookRepo.save(mapToEntity(bookDTO));
        return mapToDTO(newBook);
    }

    @Override
    public BookDTO findBookById(Integer bookId) {
        Optional<BookEntity> bookEntity = bookRepo.findById(bookId);
        if (bookEntity.isPresent()) {
            return mapToDTO(bookEntity.get());
        } else {
            throw new ResourceNotFoundException("Book", "Id", String.valueOf(bookEntity));
        }
    }


    // Pagination in staff book list
    @Override
    public Page<BookDTO> findAllBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookEntity> bookPage= bookRepo.findAll(pageable);
        return bookPage.map(this::mapToDTO);
    }

    @Override
    public List<BookDTO> findAvailableBook() {
        return bookRepo.findAll().stream().map(book -> countAvailable(book)).collect(Collectors.toList());
    }

    @Override
    public Page<BookDTO> getAllBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookEntity> bookPage = bookRepo.findAll(pageable);
        return bookPage.map(this::mapToDTO);
    }

    @Override
    public void addQuantity(Integer number) {

    }

    @Override
    public Page<BookDTO> getBooksByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookEntity> listBook = bookRepo.findByTitle(title, pageable);
        return listBook.map(book -> mapToDTO(book));
    }

    @Override
    public boolean changeBookCase(Integer bookId, Integer caseId) {
       BookEntity book= bookRepo.findById(bookId).orElseThrow(()->new ResourceNotFoundException("Book","id",String.valueOf(bookId)));
        BookshelfCaseEntity myCase=caseEntityRepository.findById(caseId).orElseThrow(()->new ResourceNotFoundException("Case","id",String.valueOf(caseId)));
        if(myCase!=null) {
            book.setBookshelfCaseByBookshelfId(myCase);
            bookRepo.save(book);
            return true;
        }
        else{
            return false;
        }
    }

    public BookDTO mapToDTO(BookEntity book) {
        BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
        return bookDTO;

    }

    public BookEntity mapToEntity(BookDTO bookDTO) {
        BookEntity book = modelMapper.map(bookDTO, BookEntity.class);
        return book;
    }
    public BookDTO countAvailable(BookEntity book) {
        Long availableBook= book.getBookDetailsById().stream().filter(b->b.getStatus().equalsIgnoreCase(String.valueOf(BookDetailStatus.AVAILABLE))).count();
        //add count to book dto
        BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
        bookDTO.setAvailableBook(availableBook);
        return bookDTO;
    }


}
