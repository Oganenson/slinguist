/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState } from 'react';
import { Timer, Brain, LayoutGrid, CheckCircle, ChevronRight, Play } from 'lucide-react';
import { motion } from 'motion/react';
import { useNavigate } from 'react-router-dom';
import { cn } from '@/src/lib/utils';

import { dataService } from '@/src/services/dataService';
import { useTranslation } from '@/src/hooks/useTranslation';

export default function Review() {
  const { t } = useTranslation();
  const [config, setConfig] = useState(dataService.getSessionConfig());
  const navigate = useNavigate();
  const words = dataService.getWords();
  
  const getCount = (cat: string) => {
    if (cat === 'All') return words.length;
    return words.filter(w => w.category === cat).length;
  };

  const toggleCategory = (cat: string) => {
    setConfig(prev => {
      let newCats;
      if (cat === 'All') {
        newCats = ['All'];
      } else {
        const withoutAll = prev.categories.filter(c => c !== 'All');
        if (withoutAll.includes(cat)) {
          newCats = withoutAll.filter(c => c !== cat);
          if (newCats.length === 0) newCats = ['All'];
        } else {
          newCats = [...withoutAll, cat];
        }
      }
      return { ...prev, categories: newCats };
    });
  };

  const handleStart = () => {
    dataService.setSessionConfig(config);
    navigate('/session');
  };

  return (
    <div className="max-w-xl mx-auto space-y-12 text-on-background">
      <div className="text-center space-y-2">
        <h1 className="text-4xl font-black text-on-background tracking-tight">{t('review_settings')}</h1>
        <p className="text-on-surface-variant text-lg font-medium">{t('ready_to_master').split('.')[0]}.</p>
      </div>

      <div className="space-y-10">
        {/* Session Length */}
        <section className="space-y-4">
          <div className="flex items-center gap-2 px-1">
            <Timer className="text-primary" size={20} />
            <h3 className="font-bold text-xl text-on-surface tracking-tight">{t('session_length')}</h3>
          </div>
          <div className="grid grid-cols-3 gap-4">
            {[10, 20, 50].map((len) => (
              <button
                key={len}
                onClick={() => setConfig({ ...config, length: len })}
                className={cn(
                  "py-6 rounded-2xl flex flex-col items-center justify-center transition-all active:scale-95 border-2",
                  config.length === len 
                    ? "bg-primary/5 border-primary text-primary shadow-xl shadow-primary/5 shadow-inner" 
                    : "bg-white dark:bg-surface-container border-violet-50 dark:border-white/5 text-secondary dark:text-on-surface-variant hover:bg-slate-50 dark:hover:bg-white/5"
                )}
              >
                <span className="text-2xl font-black">{len}</span>
                <span className="text-[10px] font-black uppercase tracking-[0.2em] opacity-60">{t('words_label')}</span>
              </button>
            ))}
          </div>
        </section>

        {/* Difficulty */}
        <section className="space-y-4">
          <div className="flex items-center gap-2 px-1">
            <Brain className="text-primary" size={20} />
            <h3 className="font-bold text-xl text-on-surface tracking-tight">{t('session_difficulty')}</h3>
          </div>
          <div className="bg-slate-50 dark:bg-surface-container-low p-1.5 rounded-[24px] border border-violet-50 dark:border-white/5 flex gap-1 shadow-inner">
            {['All', 'Easy', 'Medium', 'Hard'].map((d) => (
              <button
                key={d}
                onClick={() => setConfig({ ...config, difficulty: d })}
                className={cn(
                  "flex-1 py-3.5 rounded-[18px] text-center font-bold transition-all",
                  config.difficulty === d 
                    ? "bg-white dark:bg-surface-container-highest text-primary shadow-md border border-violet-100 dark:border-white/10" 
                    : "text-secondary dark:text-outline hover:bg-slate-100 dark:hover:bg-white/5"
                )}
              >
                {d === 'All' ? t('all_categories') : t(d.toLowerCase() as any)}
              </button>
            ))}
          </div>
        </section>

        {/* Categories */}
        <section className="space-y-4">
          <div className="flex items-center gap-2 px-1">
            <LayoutGrid className="text-primary" size={20} />
            <h3 className="font-bold text-xl text-on-surface tracking-tight">{t('categories')}</h3>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <CategoryCard 
              id="All" 
              label={t('all_categories')}
              count={getCount('All')} 
              icon="dashboard" 
              isSelected={config.categories.includes('All')} 
              onClick={() => toggleCategory('All')}
            />
            {[
              { id: 'Noun', label: t('nouns'), icon: 'book' },
              { id: 'Verb', label: t('verbs'), icon: 'directions_run' },
              { id: 'Adjective', label: t('adjectives'), icon: 'style' },
              { id: 'Adverb', label: t('adverb'), icon: 'speed' },
              { id: 'Phrase', label: t('phrase'), icon: 'forum' },
              { id: 'Pronoun', label: t('pronoun'), icon: 'person' },
              { id: 'Conjunction', label: t('conjunction'), icon: 'link' },
              { id: 'Preposition', label: t('preposition'), icon: 'location_on' },
              { id: 'Numeral', label: t('numeral'), icon: '123' },
            ].map(cat => (
              <CategoryCard 
                key={cat.id}
                id={cat.id} 
                label={cat.label}
                count={getCount(cat.id)} 
                icon={cat.icon} 
                isSelected={config.categories.includes(cat.id)} 
                onClick={() => toggleCategory(cat.id)}
              />
            ))}
          </div>
        </section>
      </div>

      {/* Start Button */}
      <div className="pt-4 pb-12">
        <button 
          onClick={handleStart}
          className="w-full bg-primary text-white font-black text-xl py-6 rounded-[32px] shadow-xl shadow-primary/20 hover:shadow-2xl hover:shadow-primary/30 ring-4 ring-primary/10 active:scale-95 transition-all flex items-center justify-center gap-3 group"
        >
          <Play size={24} fill="currentColor" className="group-hover:translate-x-1 transition-transform" />
          {t('start_session')}
        </button>
      </div>
    </div>
  );
}

function CategoryCard({ id, label, count, icon, isSelected, onClick }: any) {
  const { t } = useTranslation();
  return (
    <div 
      onClick={onClick}
      className={cn(
        "relative group cursor-pointer bg-white dark:bg-surface-container p-6 rounded-[32px] flex items-center gap-4 text-left transition-all hover:translate-y-[-2px] border-2 ring-1 ring-violet-50/50 dark:ring-white/5",
        isSelected ? "border-primary bg-primary/5 dark:bg-primary/10 shadow-xl shadow-primary/5" : "border-violet-50 dark:border-white/5 hover:bg-slate-50 dark:hover:bg-white/5"
      )}
    >
      <div className={cn(
        "w-12 h-12 rounded-2xl flex items-center justify-center transition-colors shadow-inner",
        isSelected ? "bg-primary text-white" : "bg-slate-50 dark:bg-white/5 text-slate-400 dark:text-outline"
      )}>
        <span className="material-symbols-outlined text-[24px]">{icon}</span>
      </div>
      <div>
        <p className={cn("font-black text-lg tracking-tight", isSelected ? "text-primary" : "text-on-surface")}>{label}</p>
        <p className="text-[10px] font-bold text-slate-400 dark:text-outline uppercase tracking-widest">{count} {t('items_in_collection').split(' ')[0]}</p>
      </div>
    </div>
  );
}
