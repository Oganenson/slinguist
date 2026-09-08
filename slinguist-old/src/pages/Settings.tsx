/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useRef } from 'react';
import { Camera, Save, User, Mail, Bell, Shield, Smartphone, Check, Moon, Sun } from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import { cn } from '@/src/lib/utils';
import { dataService, AppLanguage, AppTheme } from '@/src/services/dataService';
import { useTranslation } from '@/src/hooks/useTranslation';

export default function Settings() {
  const { t, lang } = useTranslation();
  const profile = dataService.getProfile();
  
  const [name, setName] = useState(profile.name);
  const [email, setEmail] = useState(profile.email);
  const [avatar, setAvatar] = useState(profile.avatar);
  const [currentLang, setCurrentLang] = useState<AppLanguage>(lang);
  const [currentTheme, setCurrentTheme] = useState<AppTheme>(dataService.getTheme());
  
  const [isSaving, setIsSaving] = useState(false);
  const [showToast, setShowToast] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [isInstallAvailable, setIsInstallAvailable] = useState(!!window.deferredInstallPrompt);
  const [showManualInstructions, setShowManualInstructions] = useState(false);

  React.useEffect(() => {
    const handleInstallerAvailable = () => {
      setIsInstallAvailable(true);
    };
    window.addEventListener('appinstalleravailable', handleInstallerAvailable);
    return () => {
      window.removeEventListener('appinstalleravailable', handleInstallerAvailable);
    };
  }, []);

  const handleInstallApp = async () => {
    const promptEvent = window.deferredInstallPrompt;
    if (!promptEvent) {
      setShowManualInstructions(true);
      return;
    }
    await promptEvent.prompt();
    const { outcome } = await promptEvent.userChoice;
    if (outcome === 'accepted') {
      window.deferredInstallPrompt = null;
      setIsInstallAvailable(false);
    }
  };

  const handleImageUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setAvatar(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSave = () => {
    setIsSaving(true);
    setTimeout(() => {
      dataService.updateProfile({ name, email, avatar });
      dataService.setTheme(currentTheme);
      if (currentLang !== lang) {
        dataService.setAppLanguage(currentLang);
      }
      setShowToast(true);
      setIsSaving(false);
      setTimeout(() => setShowToast(false), 3000);
    }, 1000);
  };

  return (
    <div className="max-w-2xl mx-auto space-y-12 pb-12 relative">
      <AnimatePresence>
        {showToast && (
          <motion.div 
            initial={{ opacity: 0, y: 50 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 20 }}
            className="fixed bottom-24 left-1/2 -translate-x-1/2 z-50 bg-on-background text-white px-6 py-3 rounded-full font-bold shadow-2xl flex items-center gap-3"
          >
            <div className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
            {t('saved_successfully')}
          </motion.div>
        )}
      </AnimatePresence>

      <div className="text-center md:text-left">
        <h1 className="text-3xl font-black text-on-background tracking-tight">{t('account_settings')}</h1>
        <p className="text-on-surface-variant font-medium mt-1">{t('manage_profile')}</p>
      </div>

      {/* Avatar Section */}
      <section className="bg-white dark:bg-surface-container rounded-[32px] p-8 border border-violet-50 dark:border-white/5 shadow-lg shadow-violet-500/5 transition-all flex flex-col md:flex-row items-center gap-8 ring-1 ring-violet-50/50 dark:ring-white/5">
        <div className="relative group">
          <div className="w-32 h-32 rounded-full p-1 bg-gradient-to-tr from-primary to-primary-fixed ring-4 ring-white dark:ring-surface-container-highest shadow-xl overflow-hidden">
            <img 
              src={avatar} 
              alt="Profile"
              className="w-full h-full object-cover rounded-full group-hover:scale-105 transition-transform duration-500"
            />
          </div>
          <button 
            onClick={() => fileInputRef.current?.click()}
            className="absolute bottom-1 right-1 bg-primary text-white p-2.5 rounded-full shadow-lg border-2 border-white hover:scale-110 active:scale-95 transition-all"
          >
            <Camera size={20} />
          </button>
          <input 
            type="file" 
            ref={fileInputRef} 
            onChange={handleImageUpload} 
            className="hidden" 
            accept="image/*"
          />
        </div>
        <div className="text-center md:text-left space-y-2">
          <h3 className="text-xl font-bold text-on-surface tracking-tight">{t('personal_info')}</h3>
          <p className="text-sm text-on-surface-variant max-w-xs font-medium">{t('manage_profile')}</p>
          <button 
            onClick={() => fileInputRef.current?.click()}
            className="text-primary font-bold text-sm hover:underline"
          >
            {t('change_photo')}
          </button>
        </div>
      </section>

      {/* Basic Info */}
      <section className="space-y-6">
        <h2 className="text-lg font-bold text-primary tracking-widest pl-1 uppercase text-[10px]">{t('personal_info')}</h2>
        <div className="bg-white dark:bg-surface-container rounded-[32px] p-8 border border-violet-50 dark:border-white/5 shadow-lg shadow-violet-500/5 space-y-6 ring-1 ring-violet-50/50 dark:ring-white/5">
          <div className="space-y-2">
            <label className="text-[10px] font-black text-on-surface-variant ml-1 uppercase tracking-widest opacity-60">{t('full_name')}</label>
            <div className="relative group">
              <User className="absolute left-4 top-1/2 -translate-y-1/2 text-outline opacity-40 group-focus-within:text-primary transition-colors" size={20} />
              <input 
                type="text" 
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="w-full h-14 pl-12 pr-6 bg-slate-50 dark:bg-white/5 border-2 border-transparent rounded-2xl font-bold text-on-surface focus:ring-4 focus:ring-primary/10 focus:border-primary focus:bg-white dark:focus:bg-surface-container transition-all outline-none"
              />
            </div>
          </div>

          <div className="space-y-2">
            <label className="text-[10px] font-black text-on-surface-variant ml-1 uppercase tracking-widest opacity-60">{t('email_address')}</label>
            <div className="relative group">
              <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-outline opacity-40 group-focus-within:text-primary transition-colors" size={20} />
              <input 
                type="email" 
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full h-14 pl-12 pr-6 bg-slate-50 dark:bg-white/5 border-2 border-transparent rounded-2xl font-bold text-on-surface focus:ring-4 focus:ring-primary/10 focus:border-primary focus:bg-white dark:focus:bg-surface-container transition-all outline-none"
              />
            </div>
          </div>
        </div>
      </section>

      {/* Theme */}
      <section className="space-y-6">
        <h2 className="text-lg font-bold text-primary tracking-widest pl-1 uppercase text-[10px]">{t('theme')}</h2>
        <div className="bg-white dark:bg-surface-container rounded-[32px] p-8 border border-violet-50 dark:border-white/5 shadow-lg shadow-violet-500/5 grid grid-cols-2 gap-4 ring-1 ring-violet-50/50 dark:ring-white/5">
          <button 
            onClick={() => setCurrentTheme('light')}
            className={cn(
              "h-14 rounded-2xl flex items-center justify-center gap-3 px-6 transition-all font-bold border-2",
              currentTheme === 'light' ? "border-primary bg-primary/5 text-primary" : "border-transparent bg-slate-50 dark:bg-white/5 text-secondary dark:text-on-surface-variant hover:bg-slate-100 dark:hover:bg-white/10"
            )}
          >
            <Sun size={20} />
            {t('light')}
          </button>
          <button 
            onClick={() => setCurrentTheme('dark')}
            className={cn(
              "h-14 rounded-2xl flex items-center justify-center gap-3 px-6 transition-all font-bold border-2",
              currentTheme === 'dark' ? "border-primary bg-primary/5 text-primary" : "border-transparent bg-slate-50 dark:bg-white/5 text-secondary dark:text-on-surface-variant hover:bg-slate-100 dark:hover:bg-white/10"
            )}
          >
            <Moon size={20} />
            {t('dark')}
          </button>
        </div>
      </section>

      {/* Android PWA Install */}
      <section className="space-y-6">
        <h2 className="text-lg font-bold text-primary tracking-widest pl-1 uppercase text-[10px]">{t('install_app')}</h2>
        <div className="bg-white dark:bg-surface-container rounded-[32px] p-8 border border-violet-50 dark:border-white/5 shadow-lg shadow-violet-500/5 space-y-6 ring-1 ring-violet-50/50 dark:ring-white/5">
          <div className="flex items-start gap-5">
            <div className="w-12 h-12 rounded-2xl bg-violet-50 dark:bg-primary/20 text-primary flex items-center justify-center shrink-0">
              <Smartphone size={24} />
            </div>
            <div className="space-y-1">
              <h3 className="text-base font-bold text-on-surface">{t('install_app')}</h3>
              <p className="text-sm text-on-surface-variant font-medium leading-relaxed">
                {t('install_desc')}
              </p>
            </div>
          </div>

          <div className="flex flex-col gap-3">
            {isInstallAvailable ? (
              <button
                onClick={handleInstallApp}
                className="w-full h-14 bg-primary text-white font-bold rounded-2xl shadow-lg shadow-primary/20 hover:shadow-xl hover:shadow-primary/35 transition-all text-sm active:translate-y-0.5"
              >
                {t('install_btn')}
              </button>
            ) : (
              <button
                onClick={() => setShowManualInstructions(!showManualInstructions)}
                className="w-full h-14 bg-slate-50 dark:bg-white/5 border border-violet-100 dark:border-white/10 text-primary font-bold rounded-2xl hover:bg-slate-100 dark:hover:bg-white/10 transition-all text-sm flex items-center justify-center gap-2"
              >
                {t('install_how_to')}
              </button>
            )}

            <AnimatePresence>
              {showManualInstructions && (
                <motion.div
                  initial={{ opacity: 0, height: 0 }}
                  animate={{ opacity: 1, height: 'auto' }}
                  exit={{ opacity: 0, height: 0 }}
                  className="overflow-hidden"
                >
                  <div className="p-5 rounded-2xl bg-violet-50/50 dark:bg-white/5 border border-violet-100/50 dark:border-white/5 text-xs text-on-surface-variant font-medium space-y-3 leading-relaxed">
                    <p className="font-bold text-primary">{t('install_how_to')}:</p>
                    <p>{t('install_instructions')}</p>
                    <div className="flex items-center gap-2 pt-2 border-t border-violet-100 dark:border-white/5">
                      <div className="w-2 h-2 rounded-full bg-emerald-400" />
                      <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Fast & lightweight PWA integration</span>
                    </div>
                  </div>
                </motion.div>
              )}
            </AnimatePresence>
          </div>
        </div>
      </section>

      {/* Language */}
      <section className="space-y-6">
        <h2 className="text-lg font-bold text-primary tracking-widest pl-1 uppercase text-[10px]">{t('interface_language')}</h2>
        <div className="bg-white dark:bg-surface-container rounded-[32px] p-8 border border-violet-50 dark:border-white/5 shadow-lg shadow-violet-500/5 grid grid-cols-2 gap-4 ring-1 ring-violet-50/50 dark:ring-white/5">
          <button 
            onClick={() => setCurrentLang('EN')}
            className={cn(
              "h-14 rounded-2xl flex items-center justify-between px-6 transition-all font-bold border-2",
              currentLang === 'EN' ? "border-primary bg-primary/5 dark:bg-primary/10 text-primary" : "border-transparent bg-slate-50 dark:bg-white/5 text-secondary dark:text-outline hover:bg-slate-100 dark:hover:bg-white/10"
            )}
          >
            English {currentLang === 'EN' && <Check size={18} />}
          </button>
          <button 
            onClick={() => setCurrentLang('RU')}
            className={cn(
              "h-14 rounded-2xl flex items-center justify-between px-6 transition-all font-bold border-2",
              currentLang === 'RU' ? "border-primary bg-primary/5 dark:bg-primary/10 text-primary" : "border-transparent bg-slate-50 dark:bg-white/5 text-secondary dark:text-outline hover:bg-slate-100 dark:hover:bg-white/10"
            )}
          >
            Русский {currentLang === 'RU' && <Check size={18} />}
          </button>
        </div>
      </section>

      <div className="pt-4">
        <button 
          onClick={handleSave}
          disabled={isSaving}
          className={cn(
            "w-full h-16 text-white font-black text-lg rounded-[24px] shadow-xl transition-all flex items-center justify-center gap-3 group",
            isSaving ? "bg-slate-400 cursor-not-allowed shadow-none" : "bg-primary shadow-primary/25 hover:shadow-2xl hover:shadow-primary/40 active:translate-y-1 active:shadow-none"
          )}
        >
          {isSaving ? (
             <div className="w-6 h-6 border-4 border-white/30 border-t-white rounded-full animate-spin" />
          ) : (
            <>
              <Save size={24} />
              {t('save_changes')}
            </>
          )}
        </button>
      </div>
    </div>
  );
}
