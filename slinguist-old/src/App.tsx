/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { BrowserRouter, Routes, Route } from 'react-router-dom';
import React, { useEffect } from 'react';
import Layout from './components/Layout';
import Home from './pages/Home';
import Review from './pages/Review';
import Words from './pages/Words';
import Profile from './pages/Profile';
import CreateCard from './pages/CreateCard';
import Session from './pages/Session';
import Settings from './pages/Settings';
import { dataService } from './services/dataService';

export default function App() {
  useEffect(() => {
    const theme = dataService.getTheme();
    document.documentElement.classList.toggle('dark', theme === 'dark');

    const handlePrompt = (e: any) => {
      e.preventDefault();
      window.deferredInstallPrompt = e;
      window.dispatchEvent(new Event('appinstalleravailable'));
    };

    window.addEventListener('beforeinstallprompt', handlePrompt);
    return () => {
      window.removeEventListener('beforeinstallprompt', handlePrompt);
    };
  }, []);

  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<Home />} />
          <Route path="/review" element={<Review />} />
          <Route path="/words" element={<Words />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/settings" element={<Settings />} />
        </Route>
        <Route path="/create" element={<CreateCard />} />
        <Route path="/session" element={<Session />} />
      </Routes>
    </BrowserRouter>
  );
}
