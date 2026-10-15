# React + TypeScript + Vite + shadcn/ui

This is a template for a new Vite project with React, TypeScript, and shadcn/ui.

## Environment variables

Create a `.env` file in `frontend/` based on `.env.example` and set
`VITE_GOOGLE_CLIENT_ID` to the Google OAuth client ID used by the backend.
Vite only exposes variables prefixed with `VITE_` to browser code, so the
frontend and backend use separate environment variable names for the same
OAuth client ID.

Set `VITE_API_URL` to the backend URL (for example, `http://localhost:8080`).
Restart the Vite development server after changing environment variables.
The backend allows `http://localhost:5173` by default. To use a different
frontend origin, set `CORS_ALLOWED_ORIGINS` for the backend to a comma-separated
list of exact origins (for example, `https://app.example.com`).

## Adding components

To add components to your app, run the following command:

```bash
npx shadcn@latest add button
```

This will place the ui components in the `src/components` directory.

## Using components

To use the components in your app, import them as follows:

```tsx
import { Button } from "@/components/ui/button"
```
