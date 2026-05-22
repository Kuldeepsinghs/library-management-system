const { useEffect, useMemo, useState } = React;
const { adminTabs, userTabs, initialForms } = window.LibraryConfig;
const api = window.LibraryApi;
const {
  TopBar,
  SidebarTabs,
  Metrics,
  BooksPanel,
  BookCard,
  UsersPanel,
  SimplePanel,
  ItemList,
  RecordsPanel,
} = window.LibraryComponents;

function App() {
  const [currentUser, setCurrentUser] = useState(api.getCurrentUser());
  const [activeTab, setActiveTab] = useState(currentUser?.role === "ADMIN" ? "Dashboard" : "Browse Books");
  const [authors, setAuthors] = useState([]);
  const [categories, setCategories] = useState([]);
  const [books, setBooks] = useState([]);
  const [users, setUsers] = useState([]);
  const [records, setRecords] = useState([]);
  const [overdueRecords, setOverdueRecords] = useState([]);
  const [authorBooks, setAuthorBooks] = useState([]);
  const [forms, setForms] = useState(initialForms);
  const [message, setMessage] = useState(null);
  const [loading, setLoading] = useState(false);
  const [online, setOnline] = useState(true);

  const isAdmin = currentUser?.role === "ADMIN";
  const tabs = isAdmin ? adminTabs : userTabs;

  const metrics = useMemo(() => {
    const borrowed = records.filter((record) => record.status === "BORROWED").length;
    const available = books.reduce((sum, book) => sum + (book.availableCopies || 0), 0);
    return [
      ["Books", books.length],
      ["Available Copies", available],
      ["Borrowed", borrowed],
      [isAdmin ? "Users" : "My Fines", isAdmin ? users.length : myFineTotal(records)],
    ];
  }, [books, records, users, isAdmin]);

  useEffect(() => {
    if (currentUser) {
      refreshAll();
    }
  }, [currentUser]);

  async function refreshAll() {
    setLoading(true);
    try {
      const [authorData, categoryData, bookData] = await Promise.all([
        api.getAuthors(),
        api.getCategories(),
        api.getBooks(),
      ]);

      setAuthors(authorData);
      setCategories(categoryData);
      setBooks(bookData);

      if (isAdmin) {
        const [userData, recordData, overdueData] = await Promise.all([
          api.getUsers(),
          api.getBorrowRecords(),
          api.getOverdueBooks(),
        ]);
        setUsers(userData);
        setRecords(recordData);
        setOverdueRecords(overdueData);
      } else {
        const history = await api.getBorrowHistory(currentUser.id);
        setUsers([currentUser]);
        setRecords(history);
        setOverdueRecords(history.filter((record) => record.status === "BORROWED" && record.dueDate < todayString()));
      }

      setOnline(true);
    } catch (error) {
      setOnline(false);
      showMessage(error.message, "error");
    } finally {
      setLoading(false);
    }
  }

  function updateForm(group, field, value) {
    setForms((current) => ({
      ...current,
      [group]: { ...current[group], [field]: value },
    }));
  }

  function resetForm(group) {
    setForms((current) => ({ ...current, [group]: initialForms[group] }));
  }

  function showMessage(text, type = "ok") {
    setMessage({ text, type });
  }

  async function submitAction(action, successReset) {
    setLoading(true);
    try {
      const payload = await action();
      showMessage(payload.message || "Done");
      if (successReset) resetForm(successReset);
      await refreshAll();
    } catch (error) {
      showMessage(error.message, "error");
    } finally {
      setLoading(false);
    }
  }

  async function submitAuth(event) {
    event.preventDefault();
    setLoading(true);
    try {
      const payload = forms.auth.mode === "login"
        ? await api.login({ email: forms.auth.email.trim(), password: forms.auth.password })
        : await api.register({
            name: forms.auth.name.trim(),
            password: forms.auth.password,
            profile: {
              email: forms.auth.email.trim(),
              phone: Number(forms.auth.phone || 0),
            },
          });

      api.setCurrentUser(payload.data);
      setCurrentUser(payload.data);
      setActiveTab(payload.data.role === "ADMIN" ? "Dashboard" : "Browse Books");
      showMessage(payload.message || "Welcome");
    } catch (error) {
      showMessage(error.message, "error");
    } finally {
      setLoading(false);
    }
  }

  function logout() {
    api.setCurrentUser(null);
    setCurrentUser(null);
    setActiveTab("Browse Books");
    setMessage(null);
  }

  function toggleBookCategory(categoryId) {
    setForms((current) => {
      const exists = current.book.categoryIds.includes(categoryId);
      return {
        ...current,
        book: {
          ...current.book,
          categoryIds: exists
            ? current.book.categoryIds.filter((id) => id !== categoryId)
            : [...current.book.categoryIds, categoryId],
        },
      };
    });
  }

  function createAuthor(event) {
    event.preventDefault();
    submitAction(() => api.createAuthor(forms.author.name.trim()), "author");
  }

  function createCategory(event) {
    event.preventDefault();
    submitAction(() => api.createCategory(forms.category.name.trim()), "category");
  }

  function createUser(event) {
    event.preventDefault();
    submitAction(() => api.createUser(buildUserPayload()), "user");
  }

  function updateUser(event) {
    event.preventDefault();
    submitAction(() => api.updateUser(buildUserPayload(Number(forms.user.id))), "user");
  }

  function buildUserPayload(id) {
    return {
      ...(id ? { id } : {}),
      name: forms.user.name.trim(),
      password: forms.user.password,
      role: forms.user.role,
      profile: {
        email: forms.user.email.trim(),
        phone: Number(forms.user.phone),
      },
    };
  }

  function createBook(event) {
    event.preventDefault();
    const categories = forms.book.categoryIds.map((id) => ({ id }));

    submitAction(
      () => api.createBook({
        name: forms.book.name.trim(),
        price: Number(forms.book.price),
        totalCopies: Number(forms.book.totalCopies || 1),
        availableCopies: Number(forms.book.totalCopies || 1),
        author: { id: Number(forms.book.authorId) },
        categories,
      }),
      "book"
    );
  }

  function borrowBook(eventOrBookId) {
    if (eventOrBookId?.preventDefault) {
      eventOrBookId.preventDefault();
      submitAction(() => api.borrowBook(Number(forms.borrow.bookId), Number(forms.borrow.userId)), "borrow");
      return;
    }

    submitAction(() => api.borrowBook(Number(eventOrBookId), currentUser.id));
  }

  function returnBook(bookId, userId) {
    submitAction(async () => {
      const payload = await api.returnBook(bookId, userId || currentUser.id);
      setAuthorBooks((current) =>
        current.map((book) => book.id === bookId ? { ...book, user: null } : book)
      );
      return payload;
    });
  }

  function deleteBook(bookId) {
    submitAction(async () => {
      const payload = await api.deleteBook(bookId);
      setAuthorBooks((current) => current.filter((book) => book.id !== bookId));
      return payload;
    });
  }

  function deleteUser(userId) {
    submitAction(() => api.deleteUser(userId));
  }

  function editUser(user) {
    setActiveTab("Users");
    setForms((current) => ({
      ...current,
      user: {
        id: user.id || "",
        name: user.name || "",
        email: user.profile?.email || "",
        phone: user.profile?.phone || "",
        password: "",
        role: user.role || "USER",
      },
    }));
  }

  async function fetchAuthorBooks(event) {
    event.preventDefault();
    setLoading(true);
    const authorId = Number(forms.authorBooks.authorId);
    try {
      const booksByAuthor = await api.getAuthorBooks(authorId);
      const visibleBooks = booksByAuthor.length
        ? booksByAuthor
        : books.filter((book) => book.author?.id === authorId);
      setAuthorBooks(visibleBooks);
      showMessage("Books fetched");
    } catch (error) {
      const visibleBooks = books.filter((book) => book.author?.id === authorId);
      setAuthorBooks(visibleBooks);
      showMessage(visibleBooks.length ? "Books fetched from loaded data" : error.message, visibleBooks.length ? "ok" : "error");
    } finally {
      setLoading(false);
    }
  }

  if (!currentUser) {
    return (
      <main className="auth-shell">
        <AuthPage forms={forms} loading={loading} updateForm={updateForm} submitAuth={submitAuth} message={message} />
      </main>
    );
  }

  return (
    <main className="app">
      <TopBar online={online} loading={loading} onRefresh={refreshAll} currentUser={currentUser} onLogout={logout} />

      <div className="layout">
        <SidebarTabs tabs={tabs} activeTab={activeTab} onChange={setActiveTab} />

        <section className="content">
          {message && <div className={`message ${message.type}`}>{message.text}</div>}

          {activeTab === "Dashboard" && isAdmin && (
            <>
              <Metrics metrics={metrics} />
              <section className="grid-2">
                <BooksPanel books={books.slice(0, 5)} onReturn={returnBook} compact role="ADMIN" />
                <RecordsPanel title="Overdue Books" records={overdueRecords} empty="No overdue books." onReturn={returnBook} />
              </section>
            </>
          )}

          {activeTab === "Books" && isAdmin && (
            <BooksPage
              books={books}
              authors={authors}
              categories={categories}
              users={users}
              forms={forms}
              loading={loading}
              updateForm={updateForm}
              toggleBookCategory={toggleBookCategory}
              createBook={createBook}
              borrowBook={borrowBook}
              returnBook={returnBook}
              deleteBook={deleteBook}
            />
          )}

          {activeTab === "Browse Books" && !isAdmin && (
            <>
              <Metrics metrics={metrics} />
              <BooksPanel books={books} onBorrow={borrowBook} role="USER" />
            </>
          )}

          {activeTab === "My History" && !isAdmin && (
            <RecordsPanel title="My Borrow History" records={records} empty="No borrowing history yet." onReturn={returnBook} />
          )}

          {activeTab === "Users" && isAdmin && (
            <UsersPage
              users={users}
              forms={forms}
              loading={loading}
              updateForm={updateForm}
              resetForm={resetForm}
              createUser={createUser}
              updateUser={updateUser}
              editUser={editUser}
              deleteUser={deleteUser}
            />
          )}

          {activeTab === "Authors" && isAdmin && (
            <AuthorsPage
              authors={authors}
              authorBooks={authorBooks}
              forms={forms}
              loading={loading}
              updateForm={updateForm}
              createAuthor={createAuthor}
              fetchAuthorBooks={fetchAuthorBooks}
              returnBook={returnBook}
              deleteBook={deleteBook}
            />
          )}

          {activeTab === "Categories" && isAdmin && (
            <CategoriesPage
              categories={categories}
              forms={forms}
              loading={loading}
              updateForm={updateForm}
              createCategory={createCategory}
            />
          )}

          {activeTab === "Borrow Records" && isAdmin && (
            <section className="grid-2">
              <RecordsPanel title="Borrow Records" records={records} empty="No borrow records yet." onReturn={returnBook} />
              <RecordsPanel title="Overdue Books" records={overdueRecords} empty="No overdue books." onReturn={returnBook} />
            </section>
          )}
        </section>
      </div>
    </main>
  );
}

function AuthPage({ forms, loading, updateForm, submitAuth, message }) {
  const isRegister = forms.auth.mode === "register";

  return (
    <section className="auth-card">
      <div>
        <h1>Library Management</h1>
        <p>{isRegister ? "Create your member account" : "Login to continue"}</p>
      </div>
      {message && <div className={`message ${message.type}`}>{message.text}</div>}
      <form className="form" onSubmit={submitAuth}>
        {isRegister && (
          <>
            <label>
              Name
              <input required value={forms.auth.name} onChange={(e) => updateForm("auth", "name", e.target.value)} />
            </label>
            <label>
              Phone
              <input min="0" type="number" value={forms.auth.phone} onChange={(e) => updateForm("auth", "phone", e.target.value)} />
            </label>
          </>
        )}
        <label>
          Email
          <input required type="email" value={forms.auth.email} onChange={(e) => updateForm("auth", "email", e.target.value)} />
        </label>
        <label>
          Password
          <input required type="password" value={forms.auth.password} onChange={(e) => updateForm("auth", "password", e.target.value)} />
        </label>
        <button disabled={loading}>{isRegister ? "Register" : "Login"}</button>
      </form>
      <button
        className="secondary"
        onClick={() => updateForm("auth", "mode", isRegister ? "login" : "register")}
      >
        {isRegister ? "Already registered? Login" : "New user? Register"}
      </button>
    </section>
  );
}

function BooksPage({ books, authors, categories, users, forms, loading, updateForm, toggleBookCategory, createBook, borrowBook, returnBook, deleteBook }) {
  return (
    <section className="grid-2">
      <div className="panel">
        <div className="panel-header">
          <h2>Add Book</h2>
        </div>
        <div className="panel-body">
          <form className="form" onSubmit={createBook}>
            <label>
              Book name
              <input required value={forms.book.name} onChange={(e) => updateForm("book", "name", e.target.value)} />
            </label>
            <label>
              Price
              <input required min="0" type="number" value={forms.book.price} onChange={(e) => updateForm("book", "price", e.target.value)} />
            </label>
            <label>
              Total copies
              <input required min="1" type="number" value={forms.book.totalCopies} onChange={(e) => updateForm("book", "totalCopies", e.target.value)} />
            </label>
            <label>
              Author
              <select required value={forms.book.authorId} onChange={(e) => updateForm("book", "authorId", e.target.value)}>
                <option value="">Select author</option>
                {authors.map((author) => (
                  <option key={author.id} value={author.id}>
                    #{author.id} {author.name}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Categories
              <div className="check-list">
                {categories.length ? (
                  categories.map((category) => (
                    <label className="check-item" key={category.id}>
                      <input
                        type="checkbox"
                        checked={forms.book.categoryIds.includes(category.id)}
                        onChange={() => toggleBookCategory(category.id)}
                      />
                      <span>{category.name}</span>
                    </label>
                  ))
                ) : (
                  <span className="muted">Add categories first from the Categories tab.</span>
                )}
              </div>
            </label>
            <button disabled={loading}>Save Book</button>
          </form>
        </div>
      </div>

      <div className="panel">
        <div className="panel-header">
          <h2>Borrow Book For User</h2>
        </div>
        <div className="panel-body">
          <form className="form" onSubmit={borrowBook}>
            <div className="form-row">
              <label>
                Book
                <select required value={forms.borrow.bookId} onChange={(e) => updateForm("borrow", "bookId", e.target.value)}>
                  <option value="">Select book</option>
                  {books.map((book) => (
                    <option key={book.id} value={book.id}>
                      #{book.id} {book.name} ({book.availableCopies || 0} left)
                    </option>
                  ))}
                </select>
              </label>
              <label>
                User
                <select required value={forms.borrow.userId} onChange={(e) => updateForm("borrow", "userId", e.target.value)}>
                  <option value="">Select user</option>
                  {users.map((user) => (
                    <option key={user.id} value={user.id}>
                      #{user.id} {user.name}
                    </option>
                  ))}
                </select>
              </label>
            </div>
            <button disabled={loading}>Borrow</button>
          </form>
        </div>
      </div>

      <BooksPanel books={books} onReturn={returnBook} onDelete={deleteBook} role="ADMIN" />
    </section>
  );
}

function UsersPage({ users, forms, loading, updateForm, resetForm, createUser, updateUser, editUser, deleteUser }) {
  return (
    <section className="grid-2">
      <div className="panel">
        <div className="panel-header">
          <h2>{forms.user.id ? "Update User" : "Add User"}</h2>
          {forms.user.id && (
            <button className="secondary" type="button" onClick={() => resetForm("user")}>
              Clear
            </button>
          )}
        </div>
        <div className="panel-body">
          <form className="form" onSubmit={forms.user.id ? updateUser : createUser}>
            <label>
              Name
              <input required value={forms.user.name} onChange={(e) => updateForm("user", "name", e.target.value)} />
            </label>
            <label>
              Email
              <input required type="email" value={forms.user.email} onChange={(e) => updateForm("user", "email", e.target.value)} />
            </label>
            <label>
              Phone
              <input required min="1" type="number" value={forms.user.phone} onChange={(e) => updateForm("user", "phone", e.target.value)} />
            </label>
            <label>
              Password
              <input type="password" value={forms.user.password} onChange={(e) => updateForm("user", "password", e.target.value)} placeholder={forms.user.id ? "Leave blank to keep old password" : ""} />
            </label>
            <label>
              Role
              <select value={forms.user.role} onChange={(e) => updateForm("user", "role", e.target.value)}>
                <option value="USER">USER</option>
                <option value="ADMIN">ADMIN</option>
              </select>
            </label>
            <button disabled={loading}>{forms.user.id ? "Update User" : "Save User"}</button>
          </form>
        </div>
      </div>
      <UsersPanel users={users} onEdit={editUser} onDelete={deleteUser} />
    </section>
  );
}

function AuthorsPage({ authors, authorBooks, forms, loading, updateForm, createAuthor, fetchAuthorBooks, returnBook, deleteBook }) {
  return (
    <section className="grid-2">
      <div className="panel">
        <div className="panel-header">
          <h2>Add Author</h2>
        </div>
        <div className="panel-body">
          <form className="form" onSubmit={createAuthor}>
            <label>
              Author name
              <input required value={forms.author.name} onChange={(e) => updateForm("author", "name", e.target.value)} />
            </label>
            <button disabled={loading}>Save Author</button>
          </form>
        </div>
      </div>

      <div className="panel">
        <div className="panel-header">
          <h2>Author Books</h2>
        </div>
        <div className="panel-body">
          <form className="form" onSubmit={fetchAuthorBooks}>
            <label>
              Author
              <select required value={forms.authorBooks.authorId} onChange={(e) => updateForm("authorBooks", "authorId", e.target.value)}>
                <option value="">Select author</option>
                {authors.map((author) => (
                  <option key={author.id} value={author.id}>
                    #{author.id} {author.name}
                  </option>
                ))}
              </select>
            </label>
            <button disabled={loading}>Fetch Books</button>
          </form>
          <div className="result-block">
            <h3>Books for selected author ({authorBooks.length})</h3>
            <ItemList
              items={authorBooks}
              empty="No books returned for this author."
              render={(book) => (
                <BookCard book={book} key={book.id}>
                  <div className="button-row" style={{ marginTop: 12 }}>
                    {book.user && (
                      <button className="secondary" type="button" onClick={() => returnBook(book.id)}>
                        Return
                      </button>
                    )}
                    <button className="danger" type="button" onClick={() => deleteBook(book.id)}>
                      Remove
                    </button>
                  </div>
                </BookCard>
              )}
            />
          </div>
        </div>
      </div>

      <SimplePanel title="Authors" items={authors} empty="No authors yet." />
    </section>
  );
}

function CategoriesPage({ categories, forms, loading, updateForm, createCategory }) {
  return (
    <section className="grid-2">
      <div className="panel">
        <div className="panel-header">
          <h2>Add Category</h2>
        </div>
        <div className="panel-body">
          <form className="form" onSubmit={createCategory}>
            <label>
              Category name
              <input required value={forms.category.name} onChange={(e) => updateForm("category", "name", e.target.value)} />
            </label>
            <button disabled={loading}>Save Category</button>
          </form>
        </div>
      </div>
      <SimplePanel title="Categories" items={categories} empty="No categories yet." />
    </section>
  );
}

function todayString() {
  return new Date().toISOString().slice(0, 10);
}

function myFineTotal(records) {
  return records.reduce((sum, record) => sum + (Number(record.fineAmount) || 0), 0);
}

window.App = App;
