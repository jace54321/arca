import React from 'react';
import { Outlet } from 'react-router';
import { ArcaProvider } from './context/ArcaContext';

export function RootLayout() {
  return (
    <ArcaProvider>
      <Outlet />
    </ArcaProvider>
  );
}
