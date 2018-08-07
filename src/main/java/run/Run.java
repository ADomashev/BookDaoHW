package run;


import java.util.Calendar;

import java.util.List;

import dao.BookDAO;
import dao.impl.BookDAOImpl;
import entity.Author;
import entity.Book;



public class Run {

	public static void main(String[] args) {

		Book bookThat = new Book("ONE", new Author("TolsToi","ONE",setTime( 1987,8,21)));
		Book toWhichChange = new Book("ONE", new Author("PЦЦЦЦЦЦЦЦЦ","ONE",setTime(1864,11,11)));
		Book bookDel = new Book("ONE", new Author("LermOntov","ONE",setTime( 1794,11,11)));
		
		BookDAO dao = new BookDAOImpl();
		Book book = dao.read(4);
		dao.add(bookDel);
		dao.add(toWhichChange);
		dao.add(bookThat);
		
		
		List<Book> list = dao.list();
		
		
		System.out.println("book"+book);
		System.out.println("list books" + list);
		
	}
	
	public static Calendar setTime(int year,int month,int day) {
		Calendar calendar=Calendar.getInstance();
		calendar.set(year, month-1, day);
		return calendar;
	}

}
