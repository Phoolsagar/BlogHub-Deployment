# Frontend Deployment Notes

This is a static site for Render.

## API URL Configuration

By default, `js/main.js` points to:

- Local: `http://localhost:8082`
- Render fallback: `https://bloghub-backend.onrender.com`

If your backend URL is different, set it with browser localStorage:

```js
localStorage.setItem(
  "BLOGHUB_API_BASE_URL",
  "https://your-backend.onrender.com",
);
```

Then reload the page.
