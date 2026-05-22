async function apiRequest(path, options = {}) {
  const currentUser = window.LibraryApi?.getCurrentUser?.();
  const headers = { "Content-Type": "application/json", ...(options.headers || {}) };

  if (currentUser?.id) {
    headers["X-User-Id"] = currentUser.id;
  }

  const response = await fetch(path, {
    headers,
    ...options,
  });

  const payload = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(payload.message || `Request failed with status ${response.status}`);
  }

  return payload;
}

function getCurrentUser() {
  try {
    return JSON.parse(localStorage.getItem("libraryUser"));
  } catch (error) {
    return null;
  }
}

function setCurrentUser(user) {
  if (user) {
    localStorage.setItem("libraryUser", JSON.stringify(user));
  } else {
    localStorage.removeItem("libraryUser");
  }
}

function toList(payload) {
  return Array.isArray(payload.data) ? payload.data : [];
}

window.LibraryApi = {
  getCurrentUser,
  setCurrentUser,

  login: (credentials) => apiRequest("/users/login", {
    method: "POST",
    body: JSON.stringify(credentials),
  }),

  register: (user) => apiRequest("/users/register", {
    method: "POST",
    body: JSON.stringify(user),
  }),

  getAuthors: () => apiRequest("/authors").then(toList),
  getCategories: () => apiRequest("/categories").then(toList),
  getBooks: () => apiRequest("/books").then(toList),
  getUsers: () => apiRequest("/users").then(toList),
  getBorrowRecords: () => apiRequest("/books/borrow-records").then(toList),
  getOverdueBooks: () => apiRequest("/books/overdue").then(toList),
  getBorrowHistory: (userId) => apiRequest(`/books/users/${userId}/history`).then(toList),

  createAuthor: (name) => apiRequest("/authors", {
    method: "POST",
    body: JSON.stringify({ name }),
  }),

  createCategory: (name) => apiRequest("/categories", {
    method: "POST",
    body: JSON.stringify({ name }),
  }),

  createUser: (user) => apiRequest("/users", {
    method: "POST",
    body: JSON.stringify(user),
  }),

  updateUser: (user) => apiRequest("/users", {
    method: "PUT",
    body: JSON.stringify(user),
  }),

  deleteUser: (userId) => apiRequest(`/users/${userId}`, {
    method: "DELETE",
  }),

  createBook: (book) => apiRequest("/books", {
    method: "POST",
    body: JSON.stringify(book),
  }),

  borrowBook: (bookId, userId) => apiRequest(`/books/${bookId}/borrow/${userId}`, {
    method: "PUT",
  }),

  returnBook: (bookId, userId) => apiRequest(userId ? `/books/${bookId}/return/${userId}` : `/books/${bookId}/return`, {
    method: "PUT",
  }),

  deleteBook: (bookId) => apiRequest(`/books/${bookId}`, {
    method: "DELETE",
  }),

  getAuthorBooks: (authorId) => apiRequest(`/authors/${authorId}/books`).then(toList),
};
