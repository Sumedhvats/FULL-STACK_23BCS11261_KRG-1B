import React from 'react';

const BookCard = ({ book, onDelete, onEdit }) => {
  return (
    <div className="card">
      <h3>{book.title}</h3>
      <p>Author: {book.author}</p>
      <p>Year: {book.year}</p>
      <div className="card-buttons">
        <button className="edit-btn" onClick={() => onEdit(book)}>Edit</button>
        <button className="delete-btn" onClick={() => onDelete(book.id)}>Delete</button>
      </div>
    </div>
  );
};

export default BookCard;
