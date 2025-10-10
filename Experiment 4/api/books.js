const API_URL = 'http://localhost:5000/books';

export const fetchBooks = async () => {
  const res = await fetch(API_URL);
  return res.json();
};

export const addBook = async (book) => {
  const res = await fetch(API_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(book)
  });
  return res.json();
};

export const updateBook = async (book) => {
  const res = await fetch(`${API_URL}/${book.id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(book)
  });
  return res.json();
};

export const deleteBook = async (id) => {
  return fetch(`${API_URL}/${id}`, { method: 'DELETE' });
};
