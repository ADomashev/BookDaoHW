package dao;

import java.util.List;

import entity.Book;

public interface BookDAO {
	Book read (int id) ;
	
	List<Book> list();
	
	boolean add(Book book);
	
	void delete (Book book);
	
	void update(Book book,Book toWhichChange);
}
