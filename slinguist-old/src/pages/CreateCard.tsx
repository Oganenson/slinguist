/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState } from 'react';
import { ArrowLeft, Edit3, Languages, Globe, ChevronDown, Save, Check, RefreshCw, Star, BookOpen } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { AnimatePresence, motion } from 'motion/react';
import { cn } from '@/src/lib/utils';

const LANGUAGES = [
  { code: 'FR', name: 'French' },
  { code: 'ES', name: 'Spanish' },
  { code: 'DE', name: 'German' },
  { code: 'IT', name: 'Italian' },
  { code: 'PT', name: 'Portuguese' },
  { code: 'RU', name: 'Russian' },
  { code: 'JA', name: 'Japanese' },
  { code: 'ZH', name: 'Chinese' },
  { code: 'KO', name: 'Korean' },
  { code: 'KK', name: 'Kazakh' },
  { code: 'TR', name: 'Turkish' },
  { code: 'NL', name: 'Dutch' }
];

const WORD_TYPES = ['Noun', 'Verb', 'Adjective', 'Adverb', 'Phrase', 'Pronoun', 'Conjunction', 'Preposition', 'Numeral'];

const LANG_SAMPLES: Record<string, string> = {
  'French': 'Bonjour',
  'Spanish': 'Hola',
  'German': 'Hallo',
  'Russian': 'Привет',
  'Italian': 'Ciao',
  'Kazakh': 'Сәлем',
  'Turkish': 'Merhaba',
  'Japanese': 'こんにちは',
  'Chinese': '你好',
  'Korean': '안녕하세요',
  'Dutch': 'Hallo',
  'Portuguese': 'Olá'
};

import { dataService } from '@/src/services/dataService';
import { useTranslation } from '@/src/hooks/useTranslation';

export default function CreateCard() {
  const navigate = useNavigate();
  const { t, lang: appLanguage } = useTranslation();
  const [foreign, setForeign] = useState('');
  const [translation, setTranslation] = useState('');
  const [difficulty, setDifficulty] = useState('Medium');
  const [language, setLanguage] = useState(LANGUAGES[0]);
  const [wordType, setWordType] = useState('Noun');
  const [isStarred, setIsStarred] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [showToast, setShowToast] = useState(false);
  const [showLangMenu, setShowLangMenu] = useState(false);
  const [showCategoryMenu, setShowCategoryMenu] = useState(false);

  const getPlaceholders = () => {
    const foreignPlaceholder = LANG_SAMPLES[language.name] || 'Word';
    const translationPlaceholder = appLanguage === 'RU' ? 'Привет' : 'Hello';
    
    // If selecting Russian word, the app language version swap logic for the sample
    if (language.name === 'Russian') {
      return { foreign: 'Привет', translation: 'Hello' };
    }
    
    return { foreign: foreignPlaceholder, translation: translationPlaceholder };
  };

  const placeholders = getPlaceholders();

  const handleLanguageChange = (lang: typeof LANGUAGES[0]) => {
    setLanguage(lang);
    setShowLangMenu(false);
  };

  const handleSave = () => {
    if (!foreign || !translation) return;

    setIsSaving(true);
    
    // Simulate slight delay for "saving" feel
    setTimeout(() => {
      dataService.addWord({
        foreign,
        translation,
        difficulty: difficulty.toLowerCase() as any,
        category: wordType,
        language: language.name,
        isStarred
      });

      setIsSaving(false);
      setShowToast(true);
      
      setTimeout(() => {
        navigate('/words');
      }, 1000);
    }, 800);
  };

  return (
    <div className="min-h-screen bg-background flex flex-col font-sans pb-12">
      {/* TopAppBar */}
      <header className="sticky top-0 z-50 bg-white/80 dark:bg-surface-container/80 backdrop-blur-md border-b border-surface-container dark:border-white/5 flex items-center justify-between w-full h-16 px-4">
        <div className="flex items-center gap-4">
          <button 
            onClick={() => navigate(-1)}
            className="hover:bg-violet-50 dark:hover:bg-white/5 rounded-full transition-all p-2 active:scale-95 text-primary"
          >
            <ArrowLeft size={24} />
          </button>
          <h1 className="text-xl font-black text-on-background tracking-tight">{t('create_card_title')}</h1>
        </div>
        <button 
          onClick={() => setIsStarred(!isStarred)}
          className={cn(
            "p-2.5 rounded-2xl transition-all active:scale-90 shadow-sm border",
            isStarred ? "bg-violet-100 dark:bg-primary/20 border-primary/20 text-primary" : "bg-white dark:bg-surface-container border-violet-100 dark:border-white/5 text-outline opacity-60"
          )}
        >
          <Star size={22} fill={isStarred ? "currentColor" : "none"} />
        </button>
      </header>

      <main className="max-w-xl mx-auto px-6 py-12 flex-1 w-full space-y-12">
        {/* Decorative Header */}
        <div className="text-center space-y-3">
          <div className="inline-flex items-center justify-center w-24 h-24 rounded-full bg-secondary-container dark:bg-primary/10 text-primary shadow-inner">
            <Edit3 size={48} />
          </div>
          <h2 className="text-3xl font-black text-on-surface tracking-tight">{t('new_study_material')}</h2>
          <p className="text-lg text-on-surface-variant font-bold">{t('build_personal_library')}</p>
        </div>

        <div className="space-y-10">
          {/* Foreign Word Group */}
          <div className="space-y-4">
            <div className="flex justify-between items-end px-1">
              <label className="text-xs font-black text-on-surface-variant uppercase tracking-widest opacity-60">{t('foreign_word_label')}</label>
              <div className="relative">
                <button 
                  onClick={() => setShowLangMenu(!showLangMenu)}
                  className="flex items-center gap-2 px-4 py-2 rounded-xl bg-violet-50 dark:bg-primary/20 text-primary font-black text-xs border border-primary/10 dark:border-primary/30 shadow-sm transition-all hover:bg-violet-100 dark:hover:bg-primary/30"
                >
                  <Languages size={14} />
                  <span>{t(language.name.toLowerCase() as any)} ({language.code})</span>
                  <ChevronDown size={12} className={cn("transition-transform", showLangMenu && "rotate-180")} />
                </button>
                <AnimatePresence>
                  {showLangMenu && (
                    <motion.div 
                      initial={{ opacity: 0, y: 10, scale: 0.95 }}
                      animate={{ opacity: 1, y: 0, scale: 1 }}
                      exit={{ opacity: 0, y: 10, scale: 0.95 }}
                      className="absolute top-full right-0 mt-2 w-48 bg-white dark:bg-surface-container rounded-2xl shadow-2xl border border-violet-50 dark:border-white/10 p-2 z-50 ring-1 ring-black/5"
                    >
                      {LANGUAGES.map(lang => (
                        <button
                          key={lang.code}
                          onClick={() => handleLanguageChange(lang)}
                          className={cn(
                            "w-full text-left px-4 py-2.5 rounded-xl text-sm font-bold transition-all",
                            language.code === lang.code ? "bg-primary text-white" : "hover:bg-violet-50 dark:hover:bg-white/5 text-on-surface-variant"
                          )}
                        >
                          {t(lang.name.toLowerCase() as any)}
                        </button>
                      ))}
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>
            </div>

            <div className="relative group">
               <input 
                type="text" 
                value={foreign}
                onChange={(e) => setForeign(e.target.value)}
                placeholder={`e.g. ${placeholders.foreign}`}
                className="w-full h-18 px-8 bg-secondary-fixed/20 dark:bg-surface-container border-2 border-transparent rounded-[28px] font-black text-2xl text-on-surface focus:ring-4 focus:ring-primary/10 focus:border-primary focus:bg-white dark:focus:bg-surface-container-highest transition-all outline-none placeholder:opacity-30"
              />
              <Globe className="absolute right-6 top-1/2 -translate-y-1/2 text-outline group-focus-within:text-primary opacity-40" size={24} />
            </div>
          </div>

          {/* Translation */}
          <div className="space-y-4">
            <label className="text-xs font-black text-on-surface-variant ml-1 uppercase tracking-widest opacity-60">{t('translation_label')}</label>
            <div className="relative group">
              <input 
                type="text" 
                value={translation}
                onChange={(e) => setTranslation(e.target.value)}
                placeholder={`e.g. ${placeholders.translation}`}
                className="w-full h-18 px-8 bg-secondary-fixed/20 dark:bg-surface-container border-2 border-transparent rounded-[28px] font-black text-2xl text-on-surface focus:ring-4 focus:ring-primary/10 focus:border-primary focus:bg-white dark:focus:bg-surface-container-highest transition-all outline-none placeholder:opacity-30"
              />
              <BookOpen className="absolute right-6 top-1/2 -translate-y-1/2 text-outline group-focus-within:text-primary opacity-40" size={24} />
            </div>
          </div>

          {/* Word Type Selector */}
          <div className="space-y-4">
            <label className="text-xs font-black text-on-surface-variant ml-1 uppercase tracking-widest opacity-60">{t('word_category_label')}</label>
            <div className="relative">
              <button 
                onClick={() => setShowCategoryMenu(!showCategoryMenu)}
                className="w-full flex items-center justify-between px-8 py-5 rounded-[28px] bg-white dark:bg-surface-container border-2 border-violet-100 dark:border-white/5 font-black text-xl text-primary shadow-sm hover:border-primary/20 transition-all outline-none"
              >
                <div className="flex items-center gap-4">
                  <BookOpen size={24} className="opacity-40" />
                  <span>{t(wordType.toLowerCase() as any)}</span>
                </div>
                <ChevronDown size={20} className={cn("transition-transform opacity-60", showCategoryMenu && "rotate-180")} />
              </button>
              <AnimatePresence>
                {showCategoryMenu && (
                  <motion.div 
                    initial={{ opacity: 0, y: 10, scale: 0.95 }}
                    animate={{ opacity: 1, y: 0, scale: 1 }}
                    exit={{ opacity: 0, y: 10, scale: 0.95 }}
                    className="absolute bottom-full left-0 right-0 mb-3 bg-white dark:bg-surface-container rounded-[32px] shadow-2xl border border-violet-50 dark:border-white/10 p-3 z-50 ring-1 ring-black/5"
                  >
                    {WORD_TYPES.map(type => (
                      <button
                        key={type}
                        onClick={() => {
                          setWordType(type);
                          setShowCategoryMenu(false);
                        }}
                        className={cn(
                          "w-full text-left px-6 py-4 rounded-2xl text-lg font-black transition-all mb-1 last:mb-0",
                          wordType === type ? "bg-primary text-white" : "hover:bg-violet-50 dark:hover:bg-white/5 text-on-surface-variant"
                        )}
                      >
                        {t(type.toLowerCase() as any)}
                      </button>
                    ))}
                  </motion.div>
                )}
              </AnimatePresence>
            </div>
          </div>

          {/* Difficulty Level */}
          <div className="space-y-4">
            <label className="text-xs font-black text-on-surface-variant ml-1 uppercase tracking-widest opacity-60">{t('difficulty_level_label')}</label>
            <div className="flex p-2 bg-surface-container-low dark:bg-surface-container rounded-[28px] shadow-inner ring-1 ring-violet-100 dark:ring-white/5">
              {['Easy', 'Medium', 'Hard'].map((lvl) => (
                <button
                  key={lvl}
                  onClick={() => setDifficulty(lvl)}
                  className={cn(
                    "flex-1 py-4 text-center rounded-[22px] font-black tracking-tight transition-all active:scale-95",
                    difficulty === lvl 
                      ? "bg-white dark:bg-surface-container-highest shadow-xl shadow-primary/10 text-primary ring-1 ring-primary/10 dark:ring-white/10" 
                      : "text-on-surface-variant dark:text-outline hover:bg-white/50 dark:hover:bg-white/10"
                  )}
                >
                  {t(lvl.toLowerCase() as any)}
                </button>
              ))}
            </div>
          </div>

          {/* Preview Card */}
          <div className="pt-8">
             <motion.div 
               layout
               className="relative w-full aspect-[16/10] bg-gradient-to-br from-white dark:from-surface-container to-violet-50 dark:to-surface-container-highest rounded-[40px] border border-violet-100 dark:border-white/5 flex flex-col items-center justify-center p-12 shadow-2xl shadow-primary/10 overflow-hidden ring-1 ring-violet-50/50 dark:ring-white/5"
             >
                <div className="absolute top-6 left-8 flex items-center gap-2 text-primary/40 uppercase tracking-[0.2em] font-black text-[10px]">
                  <span>{t(language.name.toLowerCase() as any)}</span>
                  <span className="w-1 h-1 rounded-full bg-primary/20" />
                  <span>{t(wordType.toLowerCase() as any)}</span>
                </div>
                {isStarred && <Star className="absolute top-6 right-8 text-primary" size={20} fill="currentColor" />}
                
                <div className="text-center pt-6">
                  <h3 className="text-4xl font-black text-on-surface mb-2 tracking-tighter truncate max-w-[80vw] md:max-w-[400px]">
                    {foreign || placeholders.foreign}
                  </h3>
                  <div className="w-12 h-1 bg-primary/10 rounded-full mx-auto my-4" />
                  <p className="text-xl font-bold text-secondary italic opacity-80 tracking-tight truncate max-w-[80vw] md:max-w-[400px]">
                    {translation || placeholders.translation}
                  </p>
                </div>
                
                {/* Decoration */}
                <div className="absolute top-[-20%] right-[-10%] w-64 h-64 bg-primary/5 rounded-full blur-[80px]" />
                <div className="absolute bottom-[-20%] left-[-10%] w-64 h-64 bg-violet-200/20 rounded-full blur-[80px]" />
             </motion.div>
          </div>
        </div>

        {/* Action Button */}
        <div className="pt-8 space-y-6">
          <button 
            disabled={isSaving}
            onClick={handleSave}
            className="w-full h-20 bg-primary text-white font-black text-2xl rounded-[32px] shadow-xl shadow-primary/30 hover:shadow-2xl hover:shadow-primary/40 active:translate-y-1 active:shadow-none transition-all flex items-center justify-center gap-4 group disabled:opacity-70 disabled:cursor-not-allowed"
          >
            {isSaving ? (
              <RefreshCw className="animate-spin" size={28} />
            ) : (
              <>
                <Save size={28} className="group-hover:scale-110 transition-transform" />
                {t('save_card_button')}
              </>
            )}
          </button>
          <div className="flex flex-col items-center gap-1.5">
            <p className="font-black text-sm text-on-surface-variant opacity-60 tracking-tight">
              {dataService.getWords().length} {t('cards_created_this_week')}
            </p>
            <div className="h-1.5 w-32 bg-slate-100 rounded-full overflow-hidden">
               <motion.div initial={{ width: 0 }} animate={{ width: '100%' }} className="h-full bg-primary rounded-full" />
            </div>
          </div>
        </div>

        <AnimatePresence>
          {showToast && (
            <motion.div 
              initial={{ opacity: 0, y: 100 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: 100 }}
              className="fixed bottom-12 left-6 right-6 md:left-1/2 md:-translate-x-1/2 md:w-max bg-on-background text-white px-10 py-5 rounded-[24px] flex items-center gap-4 shadow-[0_20px_50px_rgba(0,0,0,0.3)] z-[100] font-black"
            >
              <div className="w-8 h-8 rounded-full bg-green-500 flex items-center justify-center shadow-lg shadow-green-500/20">
                <Check size={20} strokeWidth={3} />
              </div>
              {t('word_added_to')} {t(language.name.toLowerCase() as any)} {t('collection_suffix')}
            </motion.div>
          )}
        </AnimatePresence>
      </main>
    </div>
  );
}
