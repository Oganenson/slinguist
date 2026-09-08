/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState } from 'react';
import { Camera, Settings, Bell, Globe, Info, MessageCircle, School, Flame, Trophy, ChevronRight, X } from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import { useNavigate, Link } from 'react-router-dom';
import { cn } from '@/src/lib/utils';
import { dataService } from '@/src/services/dataService';
import { useTranslation } from '@/src/hooks/useTranslation';

export default function Profile() {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [profile, setProfile] = React.useState(dataService.getProfile());
  const stats = dataService.getStats();
  const [showInfo, setShowInfo] = useState(false);
  const [showSupport, setShowSupport] = useState(false);

  React.useEffect(() => {
    const handleStorageChange = () => {
      setProfile(dataService.getProfile());
    };
    window.addEventListener('storage', handleStorageChange);
    return () => window.removeEventListener('storage', handleStorageChange);
  }, []);

  return (
    <div className="max-w-2xl mx-auto space-y-12 pb-12 text-on-background">
      {/* Profile Header */}
      <section className="flex flex-col items-center">
        <div className="relative group">
          <div className="w-32 h-32 rounded-full p-1 bg-gradient-to-tr from-primary to-primary-fixed ring-4 ring-white shadow-xl overflow-hidden">
            <img 
              src={profile.avatar} 
              alt={profile.name}
              className="w-full h-full object-cover rounded-full group-hover:scale-105 transition-transform duration-500"
            />
          </div>
          <button 
            onClick={() => navigate('/settings')}
            className="absolute bottom-1 right-1 bg-primary text-white p-2 rounded-full shadow-lg border-2 border-white hover:scale-110 active:scale-95 transition-all"
          >
            <Camera size={18} />
          </button>
        </div>
        <div className="text-center mt-6 space-y-1">
          <h1 className="text-3xl font-black tracking-tight text-on-surface">{profile.name}</h1>
          <p className="text-secondary font-black tracking-tight opacity-70">{profile.email}</p>
        </div>
      </section>

      {/* Statistics Bento */}
      <section>
        <h2 className="text-[10px] font-black mb-4 text-primary-container uppercase tracking-widest pl-1">{t('my_statistics')}</h2>
        <div className="grid grid-cols-2 gap-4">
          <div className="col-span-2 bg-white dark:bg-surface-container p-8 rounded-[32px] border border-violet-50 dark:border-white/5 shadow-lg shadow-violet-500/5 flex items-center justify-between ring-1 ring-violet-50/50 dark:ring-white/5">
            <div className="space-y-1">
              <span className="text-[10px] font-black text-secondary dark:text-on-surface-variant uppercase tracking-widest">{t('words_learned')}</span>
              <div className="text-5xl font-black text-primary">{stats.wordsLearned.toLocaleString()}</div>
            </div>
            <div className="w-20 h-20 bg-violet-50 dark:bg-primary/20 rounded-3xl flex items-center justify-center text-primary group hover:bg-primary hover:text-white transition-all duration-500">
              <School size={36} />
            </div>
          </div>
          
          <div className="bg-white dark:bg-surface-container p-8 rounded-[32px] border border-violet-50 dark:border-white/5 shadow-lg shadow-violet-500/5 flex flex-col items-center justify-center text-center group hover:border-orange-200 transition-colors ring-1 ring-violet-50/50 dark:ring-white/5">
            <Flame className="text-orange-500 mb-2 group-hover:scale-125 transition-transform duration-500" size={28} fill="currentColor" />
            <span className="text-2xl font-black text-on-surface">
              {stats.streak} {
                stats.streak % 10 === 1 && stats.streak % 100 !== 11 
                  ? t('day_singular') 
                  : (stats.streak % 10 >= 2 && stats.streak % 10 <= 4 && (stats.streak % 100 < 10 || stats.streak % 100 >= 20))
                    ? 'дня'
                    : t('day_plural')
              }
            </span>
            <span className="text-[10px] font-black text-secondary dark:text-outline uppercase tracking-widest mt-1 opacity-60">{t('day_streak')}</span>
          </div>

          <div className="bg-white dark:bg-surface-container p-8 rounded-[32px] border border-violet-50 dark:border-white/5 shadow-lg shadow-violet-500/5 flex flex-col items-center justify-center text-center group hover:border-violet-200 transition-colors ring-1 ring-violet-50/50 dark:ring-white/5">
            <div className="text-xl font-black text-primary tracking-tight">{t('you_are_the_best')}</div>
            <div className="mt-2 flex gap-1">
              {[...Array(5)].map((_, i) => (
                <div key={i} className="w-2 h-2 rounded-full bg-primary/20" />
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Account Settings */}
      <section>
        <h2 className="text-[10px] font-black mb-4 text-primary-container uppercase tracking-widest pl-1">{t('account_options')}</h2>
        <div className="bg-white dark:bg-surface-container rounded-[32px] border border-violet-50 dark:border-white/5 shadow-lg shadow-violet-500/5 overflow-hidden ring-1 ring-violet-50/50 dark:ring-white/5">
          <SettingsItem 
            icon={<Bell size={20} />} 
            label={t('notifications')} 
            onClick={() => navigate('/settings')}
          />
          <SettingsItem 
            icon={<Globe size={20} />} 
            label={t('interface_language')} 
            onClick={() => navigate('/settings')}
          />
          <SettingsItem 
            icon={<Info size={20} />} 
            label={t('info_label')} 
            onClick={() => setShowInfo(true)}
          />
          <SettingsItem 
            icon={<MessageCircle size={20} />} 
            label={t('support_label')} 
            className="border-none" 
            onClick={() => setShowSupport(true)}
          />
        </div>
      </section>

      {/* Info Popup */}
      <AnimatePresence>
        {showInfo && (
          <div className="fixed inset-0 z-[100] flex items-center justify-center p-6">
            <motion.div 
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setShowInfo(false)}
              className="absolute inset-0 bg-on-background/40 backdrop-blur-sm"
            />
            <motion.div 
              initial={{ opacity: 0, scale: 0.9, y: 20 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.9, y: 20 }}
              className="relative w-full max-w-sm bg-white rounded-[40px] p-10 text-center shadow-2xl ring-1 ring-violet-50"
            >
              <button 
                onClick={() => setShowInfo(false)}
                className="absolute top-6 right-6 p-2 rounded-2xl hover:bg-slate-50 transition-colors"
              >
                <X size={20} className="text-secondary" />
              </button>
              <div className="mb-6 w-20 h-20 bg-primary/5 rounded-[32px] flex items-center justify-center mx-auto">
                <Info size={40} className="text-primary" />
              </div>
              <h3 className="text-3xl font-black text-on-surface tracking-tight mb-2">
                {t('info_label')}
              </h3>
              <p className="text-xl font-bold text-on-surface-variant mb-6">
                {t('info_popup')}
              </p>
              <button 
                onClick={() => setShowInfo(false)}
                className="w-full bg-primary text-white font-black py-4 rounded-[24px] shadow-xl shadow-primary/20 hover:scale-[1.02] active:scale-95 transition-all"
              >
                OK
              </button>
            </motion.div>
          </div>
        )}
      </AnimatePresence>

      {/* Support Popup */}
      <AnimatePresence>
        {showSupport && (
          <div className="fixed inset-0 z-[100] flex items-center justify-center p-6">
            <motion.div 
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setShowSupport(false)}
              className="absolute inset-0 bg-on-background/40 backdrop-blur-sm"
            />
            <motion.div 
              initial={{ opacity: 0, scale: 0.9, y: 20 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.9, y: 20 }}
              className="relative w-full max-w-sm bg-white rounded-[40px] p-10 text-center shadow-2xl ring-1 ring-violet-50"
            >
              <button 
                onClick={() => setShowSupport(false)}
                className="absolute top-6 right-6 p-2 rounded-2xl hover:bg-slate-50 transition-colors"
              >
                <X size={20} className="text-secondary" />
              </button>
              <div className="mb-6 w-20 h-20 bg-primary/5 rounded-[32px] flex items-center justify-center mx-auto">
                <MessageCircle size={40} className="text-primary" />
              </div>
              <h3 className="text-3xl font-black text-on-surface tracking-tight mb-2">
                {t('support_label')}
              </h3>
              <p className="text-xl font-bold text-on-surface-variant mb-6 whitespace-pre-line">
                {t('support_popup')}
              </p>
              <button 
                onClick={() => setShowSupport(false)}
                className="w-full bg-primary text-white font-black py-4 rounded-[24px] shadow-xl shadow-primary/20 hover:scale-[1.02] active:scale-95 transition-all"
              >
                {t('cancel')}
              </button>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </div>
  );
}

function SettingsItem({ 
  icon, 
  label, 
  danger = false, 
  className,
  onClick
}: { 
  icon: React.ReactNode; 
  label: string; 
  danger?: boolean;
  className?: string;
  onClick?: () => void;
}) {
  return (
    <div 
      onClick={onClick}
      className={cn(
        "w-full flex items-center justify-between px-6 py-6 border-b border-violet-50 dark:border-white/5 hover:bg-violet-50/50 dark:hover:bg-white/5 transition-all group active:scale-[0.99] cursor-pointer",
        className
      )}
    >
      <div className="flex items-center gap-4">
        <div className={cn(
          "w-11 h-11 rounded-xl flex items-center justify-center transition-colors",
          danger 
            ? "bg-error-container/30 text-error group-hover:bg-error-container" 
            : "bg-violet-100 dark:bg-primary/20 text-primary group-hover:bg-primary group-hover:text-white"
        )}>
          {icon}
        </div>
        <span className={cn(
          "font-bold text-lg capitalize tracking-tight",
          danger ? "text-error" : "text-on-surface"
        )}>
          {label}
        </span>
      </div>
      {!danger && (
        <ChevronRight size={20} className="text-outline group-hover:translate-x-1 transition-transform" />
      )}
    </div>
  );
}
