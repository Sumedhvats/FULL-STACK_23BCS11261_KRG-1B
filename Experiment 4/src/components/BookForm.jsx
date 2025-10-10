import React, { useState, useEffect } from 'react';

const BookForm = ({ onSubmit, editingBook, cancelEdit }) => {
  const [title, setTitle] = useState('');
  const [author, setAuthor] = useState('');
  const [year, setYear] = useState('');

  useEffect(() => {
    if (editingBook) {
      setTitle(editingBook.title);
      setAuthor(editingBook.author);
      setYear(editingBook.year);
    }
  }, [editingBook]);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!title || !author || !year) return;
    onSubmit({ id: editingBook?.id, title, author, year });
    setTitle('');
    setAuthor('');
    setYear('');
  };

  return (
    <form className="book-form" onSubmit={handleSubmit}>
      <input type="text" placeholder="Title" value={title} onChange={e => setTitle(e.target.value)} />
      <input type="text" placeholder="Author" value={author} onChange={e => setAuthor(e.target.value)} />
      <input type="number" placeholder="Year" value={year} onChange={e => setYear(e.target.value)} />
      <button type="submit">{editingBook ? 'Update Book' : 'Add Book'}</button>
      {editingBook && <button type="button" onClick={cancelEdit}>Cancel</button>}
    </form>
  );
};

export default BookForm;
