package com.usecase.service;

import com.usecase.entity.Book;
import com.usecase.repository.BookRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }
    
    public Book getBookById(Long id){
        return bookRepository.findById(id).orElse(new Book());
    }

    public void addBook(Book book) {
         bookRepository.save(book);
    }
    
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
   }

    public void updateBook(Long id, Book updatedBook) {
        Book book = getBookById(id);
        book.setAuthor(updatedBook.getAuthor());
        book.setTitle(updatedBook.getTitle());
        book.setCategory(updatedBook.getCategory());
        book.setDescription(updatedBook.getDescription());
        
        addBook(book);
    }
}
