import React from 'react';
import { RouterProvider } from 'react-router';
import { router } from './routes';
import { Toaster } from 'sonner';

export default function App() {
  return (
    <>
      <RouterProvider router={router} />
      <Toaster
        theme="dark"
        toastOptions={{
          style: {
            backgroundColor: '#272C33',
            border: '1px solid #363C45',
            color: '#F1F5F9',
            fontFamily: "'Ubuntu', sans-serif",
          },
        }}
      />
    </>
  );
}
