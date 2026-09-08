/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useMemo } from 'react';
import { Search, Star, Plus, ArrowUpDown, ChevronDown, Trash2, Check, X } from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import { Link } from 'react-router-dom';
import { cn } from '@/src/lib/utils';
import type { Word } from '@/src/types';
import { useTranslation } from '@/src/hooks/useTranslation';
import { dataService } from '@/src/services/dataService';

export default function Words() {
  const { t } = useTranslation();
  const [activeCategory, setActiveCategory] = useState('All');
  const [searchQuery, setSearchQuery] = useState('');
  const [sortBy, setSortBy] = useState<'newest' | 'alphabetical' | 'difficulty'>('newest');
  const [sortOrder, setSortOrder] = useState<'asc' | 'desc'>('asc');
  const [wordsFromStorage, setWordsFromStorage] = useState<Word[]>(dataService.getWords());
  const [isSelectionMode, setIsSelectionMode] = useState(false);
  const [selectedIds, setSelectedIds] = useState<Set<string>>(new Set());

  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [showCategoryMenu, setShowCategoryMenu] = useState(false);
  const [showSortMenu, setShowSortMenu] = useState(false);

  const toggleStar = (id: string) => {
    dataService.toggleStar(id);
    setWordsFromStorage(dataService.getWords());
  };

  const handleToggleSelect = (id: string) => {
    const newSelected = new Set(selectedIds);
    if (newSelected.has(id)) {
      newSelected.delete(id);
    } else {
      newSelected.add(id);
    }
    setSelectedIds(newSelected);
  };

  const handleDeleteSelected = () => {
    if (selectedIds.size === 0) return;
    const updated = dataService.deleteWords(selectedIds);
    setWordsFromStorage(updated);
    setSelectedIds(new Set());
    setIsSelectionMode(false);
    setShowDeleteConfirm(false);
  };

  const CATEGORIES = ['All', 'Starred', 'Verb', 'Noun', 'Adjective', 'Adverb', 'Phrase', 'Pronoun', 'Conjunction', 'Preposition', 'Numeral'];

  const filteredAndSortedWords = useMemo(() => {
    let result = [...wordsFromStorage].filter(word => {
      const matchesSearch = word.foreign.toLowerCase().includes(searchQuery.toLowerCase()) || 
                           word.translation.toLowerCase().includes(searchQuery.toLowerCase());
      
      const normalizedCategory = activeCategory.toLowerCase();
      let matchesCategory = activeCategory === 'All';
      if (activeCategory === 'Starred') {
        matchesCategory = !!word.isStarred;
      } else if (!matchesCategory) {
        matchesCategory = word.category.toLowerCase() === normalizedCategory;
      }
      return matchesSearch && matchesCategory;
    });

    result.sort((a, b) => {
      let comparison = 0;
      if (sortBy === 'alphabetical') {
        comparison = a.foreign.localeCompare(b.foreign);
      } else if (sortBy === 'difficulty') {
        const order = { easy: 0, medium: 1, hard: 2 };
        comparison = order[a.difficulty] - order[b.difficulty];
      } else {
        // 'newest' uses storage order (which is newest first, so we use index)
        comparison = wordsFromStorage.indexOf(a) - wordsFromStorage.indexOf(b);
      }
      return sortOrder === 'asc' ? comparison : -comparison;
    });

    return result;
  }, [searchQuery, activeCategory, sortBy, sortOrder, wordsFromStorage]);

  const toggleOrder = () => setSortOrder(prev => prev === 'asc' ? 'desc' : 'asc');

  return (
    <div className="max-w-3xl mx-auto space-y-8 pb-32 text-on-background">
      <div className="flex items-center justify-between px-1">
        <h1 className="text-4xl font-black tracking-tight text-on-surface">{t('words_label')}</h1>
        <div className="flex items-center gap-2">
          {isSelectionMode ? (
            <div className="flex items-center gap-2">
              <button 
                onClick={() => {
                  setIsSelectionMode(false);
                  setSelectedIds(new Set());
                }}
                className="px-4 py-2 rounded-xl border border-violet-100 dark:border-white/10 font-bold text-sm text-secondary dark:text-outline hover:bg-slate-50 dark:hover:bg-white/5 transition-all"
              >
                {t('cancel')}
              </button>
              <button 
                onClick={() => setShowDeleteConfirm(true)}
                disabled={selectedIds.size === 0}
                className={cn(
                  "px-6 py-2 rounded-xl font-bold text-sm transition-all shadow-lg active:scale-95",
                  selectedIds.size > 0 ? "bg-error text-white shadow-error/20" : "bg-slate-100 text-outline cursor-not-allowed"
                )}
              >
                {t('delete')} ({selectedIds.size})
              </button>
            </div>
          ) : (
            <button 
              onClick={() => setIsSelectionMode(true)}
              className="p-3 bg-white border border-violet-100 rounded-2xl text-outline hover:text-error hover:border-error-container transition-all active:scale-90 shadow-sm"
            >
              <Trash2 size={22} />
            </button>
          )}
        </div>
      </div>

      {/* Search & Select Controls */}
      <div className="flex flex-col md:flex-row gap-4">
        <div className="relative group flex-1">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-outline group-focus-within:text-primary transition-colors" size={20} />
          <input 
            type="text" 
            placeholder={t('search_placeholder')} 
            className="w-full bg-surface-container-low dark:bg-surface-container border-none rounded-2xl py-4 pl-12 pr-6 focus:ring-4 focus:ring-primary/10 focus:border-primary focus:bg-white dark:focus:bg-surface-container-highest transition-all font-bold text-on-surface ring-1 ring-violet-50/50 dark:ring-white/5 shadow-inner"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
        
        <div className="flex gap-2">
          {/* Category Selector */}
          <div className="relative flex-1">
            <button 
              onClick={() => setShowCategoryMenu(!showCategoryMenu)}
              className="w-full flex items-center justify-between px-6 py-4 rounded-2xl bg-white dark:bg-surface-container border border-violet-100 dark:border-white/5 font-bold text-primary shadow-sm hover:border-primary/20 transition-all outline-none"
            >
              <span className="truncate">{t(activeCategory === 'All' ? 'all_categories' : activeCategory.toLowerCase() as any)}</span>
              <ChevronDown size={18} className={cn("transition-transform", showCategoryMenu && "rotate-180")} />
            </button>
            <AnimatePresence>
              {showCategoryMenu && (
                <motion.div 
                  initial={{ opacity: 0, y: 10, scale: 0.95 }}
                  animate={{ opacity: 1, y: 0, scale: 1 }}
                  exit={{ opacity: 0, y: 10, scale: 0.95 }}
                  className="absolute top-full left-0 right-0 mt-3 bg-white rounded-[32px] shadow-2xl border border-violet-50 p-3 z-50 ring-1 ring-black/5 max-h-[300px] overflow-y-auto"
                >
                  {CATEGORIES.map(cat => (
                    <button
                      key={cat}
                      onClick={() => {
                        setActiveCategory(cat);
                        setShowCategoryMenu(false);
                      }}
                      className={cn(
                        "w-full text-left px-5 py-3 rounded-xl text-sm font-bold transition-all mb-1 last:mb-0",
                        activeCategory === cat ? "bg-primary text-white" : "hover:bg-violet-50 text-on-surface-variant"
                      )}
                    >
                      {t(cat === 'All' ? 'all_categories' : cat.toLowerCase() as any)}
                    </button>
                  ))}
                </motion.div>
              )}
            </AnimatePresence>
          </div>

          <div className="relative flex-1">
            <button 
              onClick={() => setShowSortMenu(!showSortMenu)}
              className="w-full flex items-center justify-between px-6 py-4 rounded-2xl bg-white dark:bg-surface-container border border-violet-100 dark:border-white/5 font-bold text-primary shadow-sm hover:border-primary/20 transition-all outline-none"
            >
              <span className="truncate">{t(sortBy)}</span>
              <ChevronDown size={18} className={cn("transition-transform", showSortMenu && "rotate-180")} />
            </button>
            <AnimatePresence>
              {showSortMenu && (
                <motion.div 
                  initial={{ opacity: 0, y: 10, scale: 0.95 }}
                  animate={{ opacity: 1, y: 0, scale: 1 }}
                  exit={{ opacity: 0, y: 10, scale: 0.95 }}
                  className="absolute top-full left-0 right-0 mt-3 bg-white rounded-[32px] shadow-2xl border border-violet-50 p-3 z-50 ring-1 ring-black/5"
                >
                  {(['newest', 'alphabetical', 'difficulty'] as const).map(sort => (
                    <button
                      key={sort}
                      onClick={() => {
                        setSortBy(sort);
                        setShowSortMenu(false);
                      }}
                      className={cn(
                        "w-full text-left px-5 py-3 rounded-xl text-sm font-bold transition-all mb-1 last:mb-0",
                        sortBy === sort ? "bg-primary text-white" : "hover:bg-violet-50 text-on-surface-variant"
                      )}
                    >
                      {t(sort)}
                    </button>
                  ))}
                </motion.div>
              )}
            </AnimatePresence>
          </div>
          
          <button 
            onClick={toggleOrder}
            className="w-14 h-14 bg-white dark:bg-surface-container border border-violet-100 dark:border-white/5 rounded-2xl flex items-center justify-center text-primary hover:bg-violet-50 dark:hover:bg-white/5 transition-all active:scale-90 shadow-sm"
          >
            <ArrowUpDown size={22} className={cn("transition-transform duration-300", sortOrder === 'desc' ? "rotate-180" : "")} />
          </button>
        </div>
      </div>

      {/* Vocabulary List */}
      <div className="grid grid-cols-1 gap-4">
        <AnimatePresence mode="popLayout text-on-surface">
          {filteredAndSortedWords.map((word) => (
            <motion.div
              layout
              key={word.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.9 }}
              transition={{ duration: 0.3 }}
              onClick={() => isSelectionMode && handleToggleSelect(word.id)}
              className={cn(
                "group bg-white dark:bg-surface-container rounded-[32px] p-6 border transition-all ring-1 ring-violet-50/50 dark:ring-white/5 relative overflow-hidden",
                isSelectionMode ? (
                  selectedIds.has(word.id) 
                    ? "border-primary shadow-xl shadow-primary/10 bg-violet-50/30 dark:bg-primary/10 ring-primary/20 cursor-pointer" 
                    : "border-violet-50 dark:border-white/10 shadow-sm cursor-pointer opacity-70"
                ) : (
                  "border-violet-50 dark:border-white/5 shadow-sm hover:border-primary/20 dark:hover:border-primary/40 hover:shadow-xl hover:shadow-primary/5 active:scale-[0.99]"
                )
              )}
            >
              {isSelectionMode && (
                <div className="absolute top-6 right-6">
                  <div className={cn(
                    "w-8 h-8 rounded-full flex items-center justify-center transition-all",
                    selectedIds.has(word.id) 
                      ? "bg-primary text-white scale-110 shadow-lg shadow-primary/20" 
                      : "bg-slate-100 text-outline border-2 border-dashed border-outline/20"
                  )}>
                    {selectedIds.has(word.id) ? <Check size={18} strokeWidth={4} /> : null}
                  </div>
                </div>
              )}

              <div className="flex justify-between items-start mb-4 pr-10">
                <div className="space-y-1">
                  <div className="flex items-center gap-3">
                    <h2 className="text-2xl font-black text-primary tracking-tight">{word.foreign}</h2>
                  </div>
                  <p className="text-on-surface-variant font-bold text-lg">{word.translation}</p>
                </div>
                
                <div className="absolute top-6 right-6 flex flex-col items-end gap-2">
                  <span className={cn(
                    "px-3 py-1 rounded-lg text-[9px] font-black tracking-widest uppercase ring-1 ring-inset shadow-sm",
                    word.difficulty === 'easy' ? "bg-green-50 text-green-700 ring-green-100/50" :
                    word.difficulty === 'medium' ? "bg-amber-50 text-amber-700 ring-amber-100/50" :
                    "bg-red-50 text-red-700 ring-red-100/50"
                  )}>
                    {t(word.difficulty as any)}
                  </span>
                </div>
              </div>

              <div className="pt-5 mt-2 border-t border-slate-50 dark:border-white/5 flex items-center justify-between">
                <div className="flex items-center gap-2 text-outline text-xs font-black uppercase tracking-widest opacity-60">
                  <span className="material-symbols-outlined text-[18px]">category</span>
                  {t(word.category.toLowerCase() as any)}
                </div>
                {!isSelectionMode && (
                  <button 
                    onClick={(e) => {
                      e.stopPropagation();
                      toggleStar(word.id);
                    }}
                    className={cn(
                      "p-2.5 rounded-xl transition-all active:scale-90 shadow-sm",
                      word.isStarred ? "text-primary bg-violet-100 dark:bg-primary/20 ring-4 ring-primary/5" : "text-outline bg-slate-50 dark:bg-white/5 hover:bg-violet-50 dark:hover:bg-white/10"
                    )}
                  >
                    <Star size={20} fill={word.isStarred ? "currentColor" : "none"} />
                  </button>
                )}
              </div>
            </motion.div>
          ))}
        </AnimatePresence>
      </div>

      {!isSelectionMode && (
        <Link 
          to="/create" 
          className="fixed bottom-28 right-6 md:right-12 w-18 h-18 bg-primary-container text-white rounded-3xl shadow-2xl shadow-primary/40 flex items-center justify-center hover:scale-110 active:scale-90 transition-all z-40 ring-4 ring-white"
        >
          <Plus size={36} />
        </Link>
      )}

      {/* Delete Confirmation Popup */}
      <AnimatePresence>
        {showDeleteConfirm && (
          <div className="fixed inset-0 z-[100] flex items-center justify-center p-6">
            <motion.div 
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setShowDeleteConfirm(false)}
              className="absolute inset-0 bg-on-background/40 backdrop-blur-sm"
            />
            <motion.div 
              initial={{ opacity: 0, scale: 0.9, y: 20 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.9, y: 20 }}
              className="relative w-full max-w-sm bg-white rounded-[40px] p-10 text-center shadow-2xl ring-1 ring-violet-50"
            >
              <div className="mb-6 w-20 h-20 bg-error/5 rounded-[32px] flex items-center justify-center mx-auto">
                <Trash2 size={40} className="text-error" />
              </div>
              <h3 className="text-2xl font-black text-on-surface tracking-tight mb-2">
                {t('delete_confirm')}
              </h3>
              <p className="text-on-surface-variant font-bold mb-8">
                {selectedIds.size} {t('words_label').toLowerCase()}
              </p>
              <div className="flex flex-col gap-3">
                <button 
                  onClick={handleDeleteSelected}
                  className="w-full bg-error text-white font-black py-4 rounded-[24px] shadow-xl shadow-error/20 hover:scale-[1.02] active:scale-95 transition-all"
                >
                  {t('delete')}
                </button>
                <button 
                  onClick={() => setShowDeleteConfirm(false)}
                  className="w-full bg-slate-50 text-secondary font-black py-4 rounded-[24px] hover:bg-slate-100 transition-all"
                >
                  {t('cancel')}
                </button>
              </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </div>
  );
}
