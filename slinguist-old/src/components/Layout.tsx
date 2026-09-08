/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import { NavLink, Outlet, Link } from 'react-router-dom';
import { Home, History, Book, User, Settings as SettingsIcon } from 'lucide-react';
import { cn } from '@/src/lib/utils';
import { dataService } from '@/src/services/dataService';
import { useTranslation } from '@/src/hooks/useTranslation';

export default function Layout() {
  const { t } = useTranslation();
  const [profile, setProfile] = React.useState(dataService.getProfile());

  React.useEffect(() => {
    const handleStorageChange = () => {
      setProfile(dataService.getProfile());
    };
    window.addEventListener('storage', handleStorageChange);
    return () => window.removeEventListener('storage', handleStorageChange);
  }, []);

  return (
    <div className="min-h-screen flex flex-col bg-background pb-24 md:pb-0 md:pl-64 text-on-background">
      {/* Top Bar */}
      <header className="fixed top-0 left-0 right-0 h-16 bg-surface/80 dark:bg-surface-container/80 backdrop-blur-md border-b border-surface-container dark:border-white/5 z-50 px-4 md:pl-64">
        <div className="h-full flex items-center justify-between max-w-screen-xl mx-auto">
          <div className="flex items-center gap-3">
            <Link to="/profile" className="w-10 h-10 rounded-full overflow-hidden border-2 border-primary-container active:scale-95 transition-transform">
              <img 
                src={profile.avatar} 
                alt={profile.name}
                className="w-full h-full object-cover"
              />
            </Link>
            <span className="font-black text-on-background text-lg tracking-tight">{profile.name.split(' ')[0]}</span>
          </div>
          <Link to="/settings" className="p-2 rounded-full hover:bg-violet-100/50 dark:hover:bg-white/5 transition-colors text-primary active:scale-90">
            <SettingsIcon size={24} />
          </Link>
        </div>
      </header>

      {/* Desktop Sidebar */}
      <aside className="hidden md:flex fixed left-0 top-0 bottom-0 w-64 bg-white dark:bg-surface-container border-r border-surface-container dark:border-white/5 flex-col p-6 z-50">
        <div className="mb-12">
          <h1 className="text-2xl font-black text-primary tracking-tight">LinguistFlow</h1>
        </div>
        <nav className="flex flex-col gap-2">
          <SidebarLink to="/" icon={Home} label={t('home_label')} />
          <SidebarLink to="/review" icon={History} label={t('review_label')} />
          <SidebarLink to="/words" icon={Book} label={t('words_label')} />
          <SidebarLink to="/profile" icon={User} label={t('profile_label')} />
        </nav>
      </aside>

      {/* Main Content */}
      <main className="flex-1 pt-16">
        <div className="max-w-screen-xl mx-auto p-4 md:p-8">
          <Outlet />
        </div>
      </main>

      {/* Mobile Bottom Navigation */}
      <nav className="md:hidden fixed bottom-0 left-0 right-0 h-20 bg-white/80 dark:bg-surface-container/80 backdrop-blur-md border-t border-surface-container dark:border-white/5 flex items-center justify-around px-2 pb-safe z-50 transition-colors">
        <MobileNavLink to="/" icon={Home} label={t('home_label')} />
        <MobileNavLink to="/review" icon={History} label={t('review_label')} />
        <MobileNavLink to="/words" icon={Book} label={t('words_label')} />
        <MobileNavLink to="/profile" icon={User} label={t('profile_label')} />
      </nav>
    </div>
  );
}

function SidebarLink({ to, icon: Icon, label }: { to: string; icon: any; label: string }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) => cn(
        "flex items-center gap-3 px-4 py-3 rounded-xl transition-all",
        isActive ? "bg-primary text-white shadow-lg shadow-primary/20" : "text-secondary dark:text-on-surface-variant hover:bg-violet-50 dark:hover:bg-white/5"
      )}
    >
      <Icon size={20} />
      <span className="font-medium">{label}</span>
    </NavLink>
  );
}

function MobileNavLink({ to, icon: Icon, label }: { to: string; icon: any; label: string }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) => cn(
        "flex flex-col items-center justify-center gap-1 px-4 py-2 rounded-xl transition-all",
        isActive ? "bg-violet-100 dark:bg-primary/20 text-primary" : "text-slate-400 dark:text-outline"
      )}
    >
      {({ isActive }) => (
        <>
          <Icon size={24} strokeWidth={isActive ? 2.5 : 2} />
          <span className={cn("text-[12px] font-medium", isActive ? "font-bold" : "")}>{label}</span>
        </>
      )}
    </NavLink>
  );
}
