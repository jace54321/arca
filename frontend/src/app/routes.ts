import { createBrowserRouter, redirect } from 'react-router';
import { LoginPage } from './pages/LoginPage';
import { UnlockPage } from './pages/UnlockPage';
import { VaultLayout, VaultDashboardPage } from './pages/VaultDashboardPage';
import { SyncLogsPage } from './pages/SyncLogsPage';
import { SettingsPage } from './pages/SettingsPage';
import { LandingPage } from './pages/LandingPage';
import { RootLayout } from './RootLayout';

export const router = createBrowserRouter([
  {
    path: '/',
    Component: RootLayout,
    children: [
      {
        index: true,
        Component: LandingPage,
      },
      {
        path: 'login',
        Component: LoginPage,
      },
      {
        path: 'unlock',
        Component: UnlockPage,
      },
      {
        path: 'vault',
        Component: VaultLayout,
        children: [
          {
            index: true,
            Component: VaultDashboardPage,
          },
          {
            path: 'sync-logs',
            Component: SyncLogsPage,
          },
          {
            path: 'settings',
            Component: SettingsPage,
          },
          {
            path: 'category/:category',
            Component: VaultDashboardPage,
          },
        ],
      },
      {
        path: '*',
        loader: () => redirect('/login'),
      },
    ],
  },
]);