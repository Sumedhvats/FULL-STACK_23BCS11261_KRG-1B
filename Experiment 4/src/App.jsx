import React, { useState, useEffect } from 'react';
import BookList from './components/BookList';
import BookForm from './components/BookForm';
import { fetchBooks, addBook, updateBook, deleteBook } from '../api/books';
import './index.css';

function App() {
  const [books, setBooks] = useState([]);
  const [search, setSearch] = useState('');
  const [editingBook, setEditingBook] = useState(null);

  useEffect(() => {
    loadBooks();
  }, []);

  const loadBooks = async () => {
    const data = await fetchBooks();
    setBooks(data);
  };

  const handleAddOrUpdate = async (book) => {
    if (book.id) {
      await updateBook(book);
    } else {
      await addBook(book);
    }
    setEditingBook(null);
    loadBooks();
  };

  const handleDelete = async (id) => {
    await deleteBook(id);
    loadBooks();
  };

  const handleEdit = (book) => setEditingBook(book);
  const cancelEdit = () => setEditingBook(null);

  const filteredBooks = books.filter(b =>
    b.title.toLowerCase().includes(search.toLowerCase()) ||
    b.author.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="app">
      <h1>Library Management</h1>
      <input
        type="text"
        placeholder="Search by title or author..."
        value={search}
        onChange={e => setSearch(e.target.value)}
        className="search-input"
      />
      <BookForm onSubmit={handleAddOrUpdate} editingBook={editingBook} cancelEdit={cancelEdit} />
      <BookList books={filteredBooks} onDelete={handleDelete} onEdit={handleEdit} />
    </div>
  );
}

export default App;
