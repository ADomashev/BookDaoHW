package dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import dao.BookDAO;
import entity.Author;
import entity.Book;

import static dao.util.MySQLPropertieManager.*;

public class BookDAOImpl implements BookDAO {

	private static final String SELECT_BOOK_ID = "SELECT* FROM book join author on book.id_author=author.id WHERE book.id=? ";
	private static final String LIST_BOOK = "SELECT* FROM book join author on book.id_author =author.id ";;
	private static final String READ_AUTHOR = "SELECT* from author where name=? and surname=?";

	private static final String INSERT_BOOK = "insert into sqlexample.book( title,id_author)VALUES (?, ?)";
	private static final String INSERT_AUTHOR = "insert into sqlexample.author(name,surname,birthDay)VALUES (?,?,?)";

	private static final String DELETE_BOOK = "DELETE from book where title =?";
	private static final String UPDATE_BOOK = "UPDATE book set title=? where title=? ";

	private List<Book> listBook;
	private Book book;
	private Author author;

	@Override
	public Book read(int id) {
		try (Connection connection = DriverManager.getConnection(getDBURL(), getLogin(), getPass())) {
			PreparedStatement prepStat = connection.prepareStatement(SELECT_BOOK_ID);
			prepStat.setInt(1, id);
			ResultSet resSet = prepStat.executeQuery();
			if (resSet.next()) {
				buildBook(resSet);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return book;
	}

	@Override
	public List<Book> list() {
		listBook = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(getDBURL(), getLogin(), getPass())) {
			Statement statement = connection.createStatement();
			ResultSet resSet = statement.executeQuery(LIST_BOOK);
			while (resSet.next()) {
				listBook.add(buildBook(resSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listBook;
	}

	@SuppressWarnings("resource")
	@Override
	public boolean add(Book book) {
		try (Connection connection = DriverManager.getConnection(getDBURL(), getLogin(), getPass())) {
			Author author = book.getAuthor();
			ResultSet result = findAuthor(connection, book.getAuthor());
			if (authorExist(result)) {
				ResultSet res = findAuthor(connection, author);
				return insertBook(connection, book, res);
			} else {
				insertAuthor(connection, author);
				result = findAuthor(connection, author);
				return insertBook(connection, book, result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void delete(Book book) {
		try (Connection connection = DriverManager.getConnection(getDBURL(), getLogin(), getPass())) {
			PreparedStatement deleteStatement = connection.prepareStatement(DELETE_BOOK);
			deleteStatement.setString(1, book.getTitle());
			deleteStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(Book bookThat, Book toWhichChange) {

		try (Connection connection = DriverManager.getConnection(getDBURL(), getLogin(), getPass())) {
			PreparedStatement updateStatement = connection.prepareStatement(UPDATE_BOOK);
			updateStatement.setString(1, toWhichChange.getTitle());
			updateStatement.setString(2, bookThat.getTitle());
			updateStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private Book buildBook(ResultSet resSet) throws SQLException {
		book = new Book();
		author = new Author();
		book.setTitle(resSet.getString("title"));
		author.setName(resSet.getString("name"));
		author.setSurName(resSet.getString("surname"));
		author.setBirthDay(resSet.getDate("birthDay"));
		book.setAuthor(author);
		return book;
	}

	private ResultSet findAuthor(Connection connection, Author author) {
		PreparedStatement preparedAuthor = null;
		ResultSet res = null;
		try {
			preparedAuthor = connection.prepareStatement(READ_AUTHOR);
			preparedAuthor.setString(1, author.getName());
			preparedAuthor.setString(2, author.getSurName());
			res = preparedAuthor.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	private boolean insertBook(Connection connection, Book book, ResultSet result) {
		PreparedStatement insertBookStatement = null;
		try {
			insertBookStatement = connection.prepareStatement(INSERT_BOOK);
			insertBookStatement.setString(1, book.getTitle());
			if (result.next()) {
				int id = result.getInt("id");
				insertBookStatement.setInt(2, id);
			}

			insertBookStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	private boolean insertAuthor(Connection connection, Author author) {
		PreparedStatement insertAuthorStatement = null;

		try {
			insertAuthorStatement = connection.prepareStatement(INSERT_AUTHOR);
			insertAuthorStatement.setString(1, author.getName());
			insertAuthorStatement.setString(2, author.getSurName());
			insertAuthorStatement.setDate(3, author.getBirthDay());

			insertAuthorStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	private boolean authorExist(ResultSet result) {
		String name = "";
		try {
			if (result.next()) {
				name = result.getString("name") + " " + result.getString("surname");
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}
}
