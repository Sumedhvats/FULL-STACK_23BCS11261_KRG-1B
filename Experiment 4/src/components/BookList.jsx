import React from 'react';
import BookCard from './BookCard';

const BookList = ({ books, onDelete, onEdit }) => {
  if (books.length === 0) return <p>No books found.</p>;
  return (
    <div className="card-container">
      {books.map(book => (
        <BookCard key={book.id} book={book} onDelete={onDelete} onEdit={onEdit} />
      ))}
    </div>
  );
};

export default BookList;
