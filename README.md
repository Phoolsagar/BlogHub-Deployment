# BlogHub2 Render Deployment Structure

This folder separates your app into backend and frontend so deployment on Render is easier.

## Folder Layout

- backend: Spring Boot API service
- frontend: Static HTML/CSS/JS site
- render.yaml: Render blueprint for both services

## Backend (Render Web Service)

Service name in blueprint: `bloghub-backend`

Required environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

Recommended values already set in blueprint:

- `SESSION_COOKIE_SECURE=true`
- `SESSION_COOKIE_SAME_SITE=none`
- `FRONTEND_ORIGIN=https://bloghub-frontend.onrender.com`

## Frontend (Render Static Site)

Service name in blueprint: `bloghub-frontend`

The frontend uses this priority for API base URL:

1. `localStorage.BLOGHUB_API_BASE_URL`
2. `window.API_BASE_URL` (if manually set)
3. Local default: `http://localhost:8082`
4. Production fallback: `https://bloghub-backend.onrender.com`

If your backend URL is different, open browser console on frontend and run:

```js
localStorage.setItem(
  "BLOGHUB_API_BASE_URL",
  "https://your-backend-name.onrender.com",
);
location.reload();
```

## Deploy Steps

1. Push `bloghub2` folder to GitHub.
2. In Render, create a `Blueprint` from your repo.
3. Render will read `bloghub2/render.yaml`.
4. Set DB environment variables for backend.
5. Deploy both services.
6. Verify login and API calls from frontend.
