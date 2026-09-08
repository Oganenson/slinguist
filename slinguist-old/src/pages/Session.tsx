/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useMemo, useEffect } from 'react';
import { RefreshCw, HeartCrack, CheckCircle2, X, Book, ArrowLeft } from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import { useNavigate } from 'react-router-dom';
import { cn } from '@/src/lib/utils';
import { dataService } from '@/src/services/dataService';
import { useTranslation } from '@/src/hooks/useTranslation';

export default function Session() {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isFlipped, setIsFlipped] = useState(false);
  const [showErrorTranslation, setShowErrorTranslation] = useState(false);
  
  const allWords = dataService.getWords();
  const config = dataService.getSessionConfig();
  const profile = dataService.getProfile();

  const reviewWords = useMemo(() => {
    let filtered = [...allWords];
    
    // Filter by category
    if (!config.categories.includes('All')) {
      filtered = filtered.filter(w => {
        if (config.categories.includes('Phrases')) {
          if (['Phrase', 'Expression'].includes(w.category)) return true;
        }
        return config.categories.includes(w.category);
      });
    }

    // Filter by difficulty
    if (config.difficulty !== 'All') {
      filtered = filtered.filter(w => w.difficulty.toLowerCase() === config.difficulty.toLowerCase());
    }

    // Shuffle and slice by length
    return filtered.sort(() => Math.random() - 0.5).slice(0, config.length);
  }, [allWords, config]);

  const totalCards = reviewWords.length;
  const currentWord = reviewWords[currentIndex];

  useEffect(() => {
    if (totalCards === 0) {
      const timer = setTimeout(() => navigate('/review'), 2000);
      return () => clearTimeout(timer);
    }
  }, [totalCards, navigate]);

  const handleNext = (known: boolean) => {
    if (currentIndex < totalCards - 1) {
      setIsFlipped(false);
      setShowErrorTranslation(false);
      setTimeout(() => setCurrentIndex(prev => prev + 1), 100);
    } else {
      navigate('/review');
    }
  };

  const handleStillLearning = () => {
    if (!isFlipped && !showErrorTranslation) {
      setShowErrorTranslation(true);
    } else {
      handleNext(false);
    }
  };

  if (totalCards === 0) {
    return (
      <div className="min-h-screen flex items-center justify-center p-8 text-center text-on-background">
        <div className="space-y-4">
          <Book className="mx-auto text-primary opacity-20" size={64} />
          <h2 className="text-2xl font-black">{t('not_enough_words')}</h2>
          <p className="text-secondary font-medium">{t('review_label')}...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background flex flex-col p-4 md:p-8 font-sans overflow-hidden text-on-background">
      {/* Header */}
      <header className="flex justify-between items-center max-w-lg mx-auto w-full mb-8">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-full overflow-hidden border-2 border-primary-container">
            <img 
              src={profile.avatar} 
              alt={profile.name}
              className="w-full h-full object-cover"
            />
          </div>
          <span className="font-bold text-violet-900 dark:text-violet-100">{profile.name.split(' ')[0]}</span>
        </div>
        <button 
          onClick={() => navigate(-1)}
          className="w-10 h-10 flex items-center justify-center rounded-xl bg-white dark:bg-surface-container border border-violet-100 dark:border-white/5 text-on-surface-variant hover:bg-violet-50 dark:hover:bg-white/5 transition-all active:scale-90 shadow-sm"
        >
          <X size={20} />
        </button>
      </header>

      <main className="flex-1 flex flex-col items-center justify-center max-w-lg mx-auto w-full space-y-12">
        {/* Progress */}
        <div className="w-full space-y-3 px-2">
          <div className="flex justify-between items-end">
            <span className="text-[10px] font-black text-on-surface-variant uppercase tracking-widest opacity-60">{t('review_session')}</span>
            <span className="text-sm font-black text-primary tracking-tight">{currentIndex + 1} / {totalCards} {t('words_label')}</span>
          </div>
          <div className="h-4 w-full bg-slate-100 dark:bg-surface-container rounded-full overflow-hidden p-0.5 shadow-inner">
            <motion.div 
              className="h-full bg-gradient-to-r from-primary to-primary-container rounded-full"
              initial={{ width: '0%' }}
              animate={{ width: `${((currentIndex + 1) / totalCards) * 100}%` }}
              transition={{ duration: 0.5, ease: "easeOut" }}
            />
          </div>
        </div>

        {/* Card Canvas */}
        <div className="relative w-full aspect-[3/4] perspective-1000 group">
          <motion.div
            className="w-full h-full relative preserve-3d transition-transform duration-700 cursor-pointer"
            animate={{ rotateY: isFlipped ? 180 : 0 }}
            onClick={() => {
              if (showErrorTranslation) return;
              setIsFlipped(!isFlipped);
              if ('vibrate' in navigator) {
                navigator.vibrate(15);
              }
            }}
          >
            {/* Front */}
            <div className="absolute inset-0 backface-hidden bg-white dark:bg-surface-container rounded-[40px] border-2 border-violet-50 dark:border-white/5 shadow-2xl shadow-primary/5 flex flex-col items-center justify-center p-8 text-center ring-1 ring-violet-50/50 dark:ring-white/5">
              <div className="absolute top-10 left-10 flex items-center gap-2 text-primary dark:text-primary opacity-40 uppercase tracking-[0.3em] font-black text-[10px]">
                <span className="material-symbols-outlined text-[14px]">language</span>
                {t(currentWord.language.toLowerCase() as any)}
              </div>
              
              <div className="w-full h-full flex flex-col items-center justify-center py-20 gap-4">
                <AnimatePresence mode="wait">
                  {!showErrorTranslation ? (
                    <motion.h2 
                      key="word"
                      initial={{ opacity: 0, scale: 0.9 }}
                      animate={{ opacity: 1, scale: 1 }}
                      exit={{ opacity: 0, scale: 1.1 }}
                      className={cn(
                        "font-black text-on-surface tracking-tighter leading-tight break-words px-4 text-center",
                        currentWord.foreign.length > 20 ? "text-3xl" : 
                        currentWord.foreign.length > 12 ? "text-4xl" : "text-6xl"
                      )}
                    >
                      {currentWord.foreign}
                    </motion.h2>
                  ) : (
                    <motion.div
                      key="translation"
                      initial={{ opacity: 0, y: 10 }}
                      animate={{ opacity: 1, y: 0 }}
                      className="space-y-6"
                    >
                      <div className="space-y-1">
                        <p className="text-[10px] font-black uppercase text-secondary tracking-widest opacity-40">{t('translation_label')}</p>
                        <h3 className="text-4xl font-black text-primary tracking-tight">
                          {currentWord.translation}
                        </h3>
                      </div>
                      <button 
                        onClick={(e) => { e.stopPropagation(); setShowErrorTranslation(false); }}
                        className="flex items-center gap-2 bg-slate-50 dark:bg-white/5 text-secondary dark:text-outline px-4 py-2 rounded-xl text-xs font-black uppercase tracking-widest hover:bg-slate-100 dark:hover:bg-white/10"
                      >
                        <ArrowLeft size={14} />
                        {t('back_to_word')}
                      </button>
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>

              {!showErrorTranslation && currentWord.pronunciation && (
                <p className="absolute bottom-32 text-lg text-on-surface-variant/60 dark:text-outline italic font-medium tracking-tight bg-violet-50 dark:bg-white/5 px-4 py-1.5 rounded-full ring-1 ring-violet-100 dark:ring-white/5">
                  {currentWord.pronunciation}
                </p>
              )}

              {!showErrorTranslation && (
                <div className="absolute bottom-16 w-full px-12 flex flex-col items-center">
                  <div className="flex flex-col items-center gap-3">
                    <div className="w-12 h-12 rounded-full bg-violet-50 dark:bg-white/5 flex items-center justify-center shadow-inner">
                      <RefreshCw size={20} className="text-primary" />
                    </div>
                    <span className="text-[10px] font-black text-primary uppercase tracking-widest opacity-40">{t('flip_card')}</span>
                  </div>
                </div>
              )}
            </div>

            {/* Back */}
            <div className="absolute inset-0 backface-hidden bg-gradient-to-br from-primary to-primary-container rounded-[40px] flex flex-col items-center justify-center p-8 text-center [transform:rotateY(180deg)] shadow-2xl shadow-primary/40">
              <div className="absolute top-10 left-10 text-white/40 uppercase tracking-[0.3em] font-black text-[10px]">{t('translation_label')}</div>
              
              <div className="w-full h-full flex items-center justify-center py-20 px-8 text-center uppercase">
                <h2 className={cn(
                  "font-black text-white tracking-tight leading-tight break-words",
                  currentWord.translation.length > 20 ? "text-3xl" : "text-5xl"
                )}>
                  {currentWord.translation}
                </h2>
              </div>
              
              <div className="absolute bottom-16 w-full px-12 flex flex-col items-center">
                <div className="flex flex-col items-center gap-3">
                  <div className="w-12 h-12 rounded-full border-2 border-white/20 flex items-center justify-center backdrop-blur-sm">
                    <RefreshCw size={20} className="text-white opacity-40" />
                  </div>
                  <span className="text-[10px] font-black text-white/60 uppercase tracking-widest">{t('back_to_word')}</span>
                </div>
              </div>
            </div>
          </motion.div>

          <div className="absolute -bottom-3 left-6 right-6 h-6 bg-white/60 dark:bg-white/10 border border-violet-100 dark:border-white/5 rounded-b-[32px] -z-10 shadow-sm" />
        </div>

        {/* Action Buttons */}
        <div className="w-full grid grid-cols-2 gap-6 pb-4">
          <button 
            onClick={(e) => { e.stopPropagation(); handleStillLearning(); }}
            className="flex flex-col items-center justify-center py-6 px-6 bg-white dark:bg-surface-container border-2 border-slate-100 dark:border-white/5 text-secondary dark:text-on-surface font-bold rounded-[32px] hover:bg-slate-50 dark:hover:bg-white/5 transition-all active:translate-y-1 shadow-[0_8px_0_0_#eeeeee] dark:shadow-[0_8px_0_0_#1a1a1a] active:shadow-none ring-1 ring-violet-50/50 dark:ring-white/5 group"
          >
            <HeartCrack size={32} className="text-red-400 mb-2 group-hover:scale-110 transition-transform" />
            <span className="text-[10px] font-black uppercase tracking-[0.2em]">{t('still_learning')}</span>
          </button>
          <button 
            onClick={(e) => { e.stopPropagation(); handleNext(true); }}
            className="flex flex-col items-center justify-center py-6 px-6 bg-primary text-white font-bold rounded-[32px] hover:bg-primary-container transition-all active:translate-y-1 shadow-[0_8px_0_0_#4c0bad] active:shadow-none ring-4 ring-primary/10 group"
          >
            <CheckCircle2 size={32} className="text-white mb-2 group-hover:scale-110 transition-transform" />
            <span className="text-[10px] font-black uppercase tracking-[0.2em]">{t('i_know_it')}</span>
          </button>
        </div>
      </main>

      {/* Background Orbs */}
      <div className="fixed top-0 left-0 -z-50 w-full h-full opacity-30 pointer-events-none overflow-hidden">
        <div className="absolute top-[-10%] left-[-10%] w-[500px] h-[500px] bg-violet-200 rounded-full blur-[100px]" />
        <div className="absolute bottom-[-10%] right-[-10%] w-[600px] h-[600px] bg-primary/10 rounded-full blur-[120px]" />
      </div>
    </div>
  );
}
