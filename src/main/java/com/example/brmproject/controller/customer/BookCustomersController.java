package com.example.brmproject.controller.customer;

import com.example.brmproject.domain.dto.*;
import com.example.brmproject.service.BookService;
import com.example.brmproject.service.CategoryBookService;
import com.example.brmproject.service.CategoryService;
import com.example.brmproject.service.ReviewRatingService;
import com.example.brmproject.ultilities.StaticFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("customers")
public class BookCustomersController {

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryBookService categoryBookService;

    @Autowired
    private ReviewRatingService reviewRatingService;

    @GetMapping("/books")
    public String getAllBooksCustomer(Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size, @ModelAttribute("alertMessage") String alertMessage,
            @ModelAttribute("alertError") String alertError) {
        // alert

        StaticFunction.showAlert(model, alertMessage, alertError);

        Page<BookDTO> listBooks = bookService.getAllBooks(page - 1, size);
        List<CategoryDTO> categoryList = categoryService.findAll();
        int totalPages = listBooks.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        for (BookDTO book : listBooks) {
            ReviewRatingDTO reviewRatingDTO = reviewRatingService.getReviewRatingByBook(book.getId());
                book.setRating(reviewRatingDTO.getAvrRating());
        }
        model.addAttribute("books", listBooks);
        model.addAttribute("categories", categoryList);
        return "customerTemplate/books/showAllBook";
    }

    @GetMapping("/books/categoryId/{categoryId}")
    public String getBooksByCategoryId(@PathVariable Integer categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size,
            @ModelAttribute("title") String title,
            Model model, @ModelAttribute("alertMessage") String alertMessage,
            @ModelAttribute("alertError") String alertError) {
        // alert
        StaticFunction.showAlert(model, alertMessage, alertError);

        Page<CategoryBookDTO> categoryBooks = categoryBookService
                .findBooksByCategoryId(categoryId, page - 1, size);
        List<BookDTO> listBooks = categoryBooks
                .stream()
                .map(categoryBook -> categoryBook.getBookByBookId())
                .collect(Collectors.toList());
        List<CategoryDTO> categoryList = categoryService.findAll();
        int totalPages = categoryBooks.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        for (BookDTO book : listBooks) {
            ReviewRatingDTO reviewRatingDTO = reviewRatingService.getReviewRatingByBook(book.getId());
            book.setRating(reviewRatingDTO.getAvrRating());
        }
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categoryBooks", categoryBooks);
        model.addAttribute("books", listBooks);
        model.addAttribute("categories", categoryList);
        return "customerTemplate/books/showListBooksByCategoryID";
    }

    @GetMapping("/books/detail/{bookId}")
    public String getBookDetail(@PathVariable Integer bookId,
                                Model model,
                                @ModelAttribute("alertMessage") String alertMessage,
                                @ModelAttribute("alertError") String alertError) {
        StaticFunction.showAlert(model, alertMessage, alertError);

        ReviewRatingDTO reviewRatingDTO = reviewRatingService.getReviewRatingByBook(bookId);
        BookDTO bookDTO = bookService.findBookById(bookId);
        double avrRating = reviewRatingDTO.getAvrRating();
        if (avrRating == 0.0) {
            bookDTO.setRating(5);
        } else {
            bookDTO.setRating(avrRating);
        }


        List<UserReviewDTO> listUserReviews = reviewRatingDTO.getListReview();

        List<String> categories= categoryBookService.findByBookId(bookId)
                .stream().map(categoryBook -> categoryBook.getCategoryByBookId().getName())
                .collect(Collectors.toList());


        model.addAttribute("categories", categories);
        model.addAttribute("listUserReviews", listUserReviews);
        model.addAttribute("book", bookDTO);
        return "customerTemplate/books/bookDetail";
    }

    @GetMapping("/books/title")
    public String getBooksByTitle(@RequestParam("title") String title,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "4") int size,
                                  Model model){
        if (title == null) {
            return "redirect:/customers/books";
        }
        Page<BookDTO> listBooks = bookService.getBooksByTitle(title, page - 1, size);
        List<CategoryDTO> categoryList = categoryService.findAll();
        int totalPages = listBooks.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("books", listBooks);
        model.addAttribute("categories", categoryList);
        return "customerTemplate/books/showAllBook";
    }

}
