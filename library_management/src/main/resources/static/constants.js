window.LibraryConfig = {
  adminTabs: ["Dashboard", "Books", "Users", "Authors", "Categories", "Borrow Records"],
  userTabs: ["Browse Books", "My History"],
  initialForms: {
    auth: { mode: "login", name: "", email: "", phone: "", password: "" },
    author: { name: "" },
    category: { name: "" },
    user: { id: "", name: "", email: "", phone: "", password: "", role: "USER" },
    book: { name: "", price: "", totalCopies: "1", authorId: "", categoryIds: [] },
    borrow: { bookId: "", userId: "" },
    authorBooks: { authorId: "" },
  },
};
