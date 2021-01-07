package com.usecase.controller;

import com.usecase.entity.Book;
import com.usecase.service.BookService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BookController {

    @Autowired
    BookService bookService;
    
    @GetMapping("/books")
    public List<Book> getAllBooks(){
        return bookService.getAllBooks();
    }
    
    @GetMapping("/books/{book_id}")
    public Book getBookById(@PathVariable Long book_id){
        return bookService.getBookById(book_id);
    }

    @PostMapping("/books")
    public String addBook(@RequestBody Book book) {
        bookService.addBook(book);
        return "Book Added Successfully with ID - " + book.getBookId();
    }
    
    @DeleteMapping("/books/{book_id}")
    public String deleteBook(@PathVariable Long book_id) {
        bookService.deleteBook(book_id);
        return "Book Deleted Successfully with ID - " + book_id;
   }
    
    @PutMapping("/books/{book_id}")
    public String updateBook(@PathVariable Long book_id, @RequestBody Book book) {
        bookService.updateBook(book_id, book);
        return "Book Updated Successfully with ID - " + book_id;
    }
}
