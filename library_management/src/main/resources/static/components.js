function TopBar({ online, loading, onRefresh, currentUser, onLogout }) {
  return (
    <header className="topbar">
      <div className="topbar-inner">
        <div className="brand">
          <h1>Library Management</h1>
          <p>{currentUser?.role === "ADMIN" ? "Admin control panel" : "Member book desk"}</p>
        </div>
        <div className="status" title={online ? "Backend is responding" : "Backend request failed"}>
          <span className={`status-dot ${online ? "" : "error"}`}></span>
          <strong>{currentUser?.name} ({currentUser?.role})</strong>
          <button className="secondary" onClick={onRefresh} disabled={loading}>
            Refresh
          </button>
          <button className="secondary" onClick={onLogout}>
            Logout
          </button>
        </div>
      </div>
    </header>
  );
}

function SidebarTabs({ tabs, activeTab, onChange }) {
  return (
    <nav className="tabs" aria-label="Sections">
      {tabs.map((tab) => (
        <button key={tab} className={`tab ${activeTab === tab ? "active" : ""}`} onClick={() => onChange(tab)}>
          {tab}
        </button>
      ))}
    </nav>
  );
}

function Metrics({ metrics }) {
  return (
    <section className="metrics">
      {metrics.map(([label, value]) => (
        <div className="metric" key={label}>
          <span>{label}</span>
          <strong>{value}</strong>
        </div>
      ))}
    </section>
  );
}

function BooksPanel({ books, onReturn, onDelete, onBorrow, compact, role }) {
  return (
    <div className="panel">
      <div className="panel-header">
        <h2>{compact ? "Recent Books" : "Books"}</h2>
      </div>
      <div className="panel-body">
        <ItemList
          items={books}
          empty="No books yet."
          render={(book) => (
            <BookCard book={book} key={book.id}>
              {role === "USER" && book.availableCopies > 0 && (
                <div className="button-row" style={{ marginTop: 12 }}>
                  <button onClick={() => onBorrow(book.id)}>
                    Borrow
                  </button>
                </div>
              )}
              {book.user && role === "ADMIN" && (
                <div className="button-row" style={{ marginTop: 12 }}>
                  <button className="secondary" onClick={() => onReturn(book.id)}>
                    Return
                  </button>
                </div>
              )}
              {!compact && role === "ADMIN" && (
                <div className="button-row" style={{ marginTop: book.user ? 8 : 12 }}>
                  <button className="danger" onClick={() => onDelete(book.id)}>
                    Remove
                  </button>
                </div>
              )}
            </BookCard>
          )}
        />
      </div>
    </div>
  );
}

function BookCard({ book, children }) {
  const categoryNames = book.categories?.length
    ? book.categories.map((category) => `${category.name || "Category"} #${category.id}`).join(", ")
    : "No category";

  return (
    <div className="card">
      <div className="card-top">
        <div>
          <h3>{book.name}</h3>
          <p>Book #{book.id} - Price {book.price}</p>
          <p>Author: {book.author ? `${book.author.name} #${book.author.id}` : "Not set"}</p>
          <p>Categories: {categoryNames}</p>
          <p>Copies: {book.availableCopies ?? 0} available / {book.totalCopies ?? 1} total</p>
          <p>Borrowed by: {book.user ? `${book.user.name} #${book.user.id}` : "No one"}</p>
        </div>
        <span className={`badge ${book.availableCopies > 0 ? "" : "busy"}`}>{book.availableCopies > 0 ? "Available" : "Unavailable"}</span>
      </div>
      {children}
    </div>
  );
}

function UsersPanel({ users, onEdit, onDelete, compact }) {
  return (
    <div className="panel">
      <div className="panel-header">
        <h2>{compact ? "Recent Users" : "Users"}</h2>
      </div>
      <div className="panel-body">
        <ItemList
          items={users}
          empty="No users yet."
          render={(user) => (
            <div className="card" key={user.id}>
              <div className="card-top">
                <div>
                  <h3>{user.name}</h3>
                  <p>User #{user.id} - {user.role || "USER"}</p>
                  {user.profile && (
                    <p>
                      {user.profile.email} - {user.profile.phone}
                    </p>
                  )}
                </div>
              </div>
              {!compact && (
                <div className="button-row" style={{ marginTop: 12 }}>
                  <button className="secondary" onClick={() => onEdit(user)}>
                    Edit
                  </button>
                  <button className="danger" onClick={() => onDelete(user.id)}>
                    Delete
                  </button>
                </div>
              )}
            </div>
          )}
        />
      </div>
    </div>
  );
}

function SimplePanel({ title, items, empty }) {
  return (
    <div className="panel">
      <div className="panel-header">
        <h2>{title}</h2>
      </div>
      <div className="panel-body">
        <ItemList
          items={items}
          empty={empty}
          render={(item) => (
            <div className="card" key={item.id}>
              <h3>{item.name}</h3>
              <p>Id #{item.id}</p>
            </div>
          )}
        />
      </div>
    </div>
  );
}

function RecordsPanel({ title, records, empty, onReturn }) {
  return (
    <div className="panel">
      <div className="panel-header">
        <h2>{title}</h2>
      </div>
      <div className="panel-body">
        <ItemList
          items={records}
          empty={empty}
          render={(record) => (
            <div className="card" key={record.id}>
              <div className="card-top">
                <div>
                  <h3>{record.book?.name || "Book"} #{record.book?.id}</h3>
                  <p>User: {record.user?.name || "User"} #{record.user?.id}</p>
                  <p>Borrowed: {record.borrowDate} - Due: {record.dueDate}</p>
                  <p>Returned: {record.returnDate || "Not returned"} - Fine: {record.fineAmount || 0}</p>
                </div>
                <span className={`badge ${record.status === "BORROWED" ? "busy" : ""}`}>{record.status}</span>
              </div>
              {record.status === "BORROWED" && onReturn && (
                <div className="button-row" style={{ marginTop: 12 }}>
                  <button className="secondary" onClick={() => onReturn(record.book?.id, record.user?.id)}>
                    Return
                  </button>
                </div>
              )}
            </div>
          )}
        />
      </div>
    </div>
  );
}

function ItemList({ items, render, empty }) {
  if (!items.length) {
    return <div className="empty">{empty}</div>;
  }

  return <div className="list">{items.map(render)}</div>;
}

window.LibraryComponents = {
  TopBar,
  SidebarTabs,
  Metrics,
  BooksPanel,
  BookCard,
  UsersPanel,
  SimplePanel,
  RecordsPanel,
  ItemList,
};
