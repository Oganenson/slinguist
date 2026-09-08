/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { Word, UserStats } from '../types';

const WORDS_KEY = 'linguistflow_words';
const STATS_KEY = 'linguistflow_stats';
const PROFILE_KEY = 'linguistflow_profile';
const LANG_KEY = 'linguistflow_app_lang';
const THEME_KEY = 'linguistflow_theme';
const SESSION_CONFIG_KEY = 'linguistflow_session_config';

export type AppLanguage = 'EN' | 'RU';
export type AppTheme = 'light' | 'dark';

export interface SessionConfig {
  length: number;
  difficulty: string;
  categories: string[];
}

const DEFAULT_PROFILE = {
  name: 'Elena Rodriguez',
  email: 'elena.rod@linguistflow.edu',
  avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCS1ZqAi5ht-Z44VGELJj7G1eOARCPLiK8L0VWPbsUYVwg-U7jmyThnco1P8whw0GYfiyxsEQd6Zz5lrqaxtQJQ7DawksmG4Zx3f2CGM1pvMp985yykYXsKoNW2lutcipk9CbhH743f3sSNDiPz8kyjG5G0wqdLN0IyboJZZrrbaiXdMsVAkPBg2-GzJVxhbKOmLmpAhJHB_AbFJUnxF7l1LEzRj4k2piznniIfzAFn6gQ75HyjY7nUchsJAn90lHvcEz9JQcLMYNk'
};

const DEFAULT_WORDS: Word[] = [
  {
    id: '1',
    foreign: 'Bonjour',
    translation: 'Hello / Good morning',
    difficulty: 'easy',
    category: 'Greeting',
    language: 'French',
    learnedAt: Date.now()
  },
  {
    id: '2',
    foreign: 'Bibliothèque',
    translation: 'Library',
    difficulty: 'medium',
    category: 'Noun',
    language: 'French',
    isStarred: true,
    learnedAt: Date.now()
  },
  {
    id: '3',
    foreign: 'Épanouissement',
    translation: 'Fulfillment / Flourishing',
    difficulty: 'hard',
    category: 'Noun',
    language: 'French'
  },
  {
    id: '4',
    foreign: 'Comprendre',
    translation: 'To understand',
    difficulty: 'easy',
    category: 'Verb',
    language: 'French',
    learnedAt: Date.now()
  },
  {
    id: '5',
    foreign: 'Magnifique',
    translation: 'Magnificent / Beautiful',
    difficulty: 'medium',
    category: 'Adjective',
    language: 'French'
  }
];

export const dataService = {
  getWords: (): Word[] => {
    const saved = localStorage.getItem(WORDS_KEY);
    if (!saved) {
      localStorage.setItem(WORDS_KEY, JSON.stringify(DEFAULT_WORDS));
      return DEFAULT_WORDS;
    }
    return JSON.parse(saved);
  },

  addWord: (word: Omit<Word, 'id'>) => {
    const words = dataService.getWords();
    const newWord = { ...word, id: Math.random().toString(36).substr(2, 9) };
    const updated = [newWord, ...words];
    localStorage.setItem(WORDS_KEY, JSON.stringify(updated));
    return newWord;
  },

  toggleStar: (id: string) => {
    const words = dataService.getWords();
    const updated = words.map(w => w.id === id ? { ...w, isStarred: !w.isStarred } : w);
    localStorage.setItem(WORDS_KEY, JSON.stringify(updated));
  },

  saveWords: (words: Word[]) => {
    localStorage.setItem(WORDS_KEY, JSON.stringify(words));
    dataService.updateStreak();
  },

  updateStreak: () => {
    const stats = dataService.getStats();
    const lastDate = localStorage.getItem('linguistflow_last_active');
    const today = new Date().toDateString();

    if (lastDate !== today) {
      if (lastDate) {
        const last = new Date(lastDate);
        const yesterday = new Date();
        yesterday.setDate(yesterday.getDate() - 1);
        
        if (last.toDateString() === yesterday.toDateString()) {
          stats.streak += 1;
        } else {
          stats.streak = 1;
        }
      } else {
        stats.streak = 1;
      }
      localStorage.setItem('linguistflow_last_active', today);
      localStorage.setItem(STATS_KEY, JSON.stringify(stats));
    }
  },

  getStats: (): UserStats => {
    const saved = localStorage.getItem(STATS_KEY);
    const words = dataService.getWords();
    const learnedCount = words.filter(w => w.learnedAt).length;
    
    if (!saved) {
      const initialStats = {
        wordsLearned: learnedCount,
        streak: 1,
        globalRank: 'Gold'
      };
      localStorage.setItem(STATS_KEY, JSON.stringify(initialStats));
      return initialStats;
    }
    
    const stats = JSON.parse(saved);
    stats.wordsLearned = learnedCount;
    return stats;
  },

  getSessionConfig: (): SessionConfig => {
    const saved = localStorage.getItem(SESSION_CONFIG_KEY);
    return saved ? JSON.parse(saved) : { length: 15, difficulty: 'All', categories: ['All'] };
  },

  setSessionConfig: (config: SessionConfig) => {
    localStorage.setItem(SESSION_CONFIG_KEY, JSON.stringify(config));
  },

  getProfile: () => {
    const saved = localStorage.getItem(PROFILE_KEY);
    return saved ? JSON.parse(saved) : DEFAULT_PROFILE;
  },

  updateProfile: (profile: Partial<typeof DEFAULT_PROFILE>) => {
    const current = dataService.getProfile();
    const updated = { ...current, ...profile };
    localStorage.setItem(PROFILE_KEY, JSON.stringify(updated));
    window.dispatchEvent(new Event('storage'));
    return updated;
  },

  getAppLanguage: (): AppLanguage => {
    return (localStorage.getItem(LANG_KEY) as AppLanguage) || 'EN';
  },

  setAppLanguage: (lang: AppLanguage) => {
    localStorage.setItem(LANG_KEY, lang);
    window.location.reload(); // Quick way to apply translations globally
  },

  getTheme: (): AppTheme => {
    return (localStorage.getItem(THEME_KEY) as AppTheme) || 'light';
  },

  setTheme: (theme: AppTheme) => {
    localStorage.setItem(THEME_KEY, theme);
    document.documentElement.classList.toggle('dark', theme === 'dark');
    window.dispatchEvent(new Event('storage'));
  },

  deleteWord: (id: string) => {
    const words = dataService.getWords();
    const updated = words.filter(w => w.id !== id);
    localStorage.setItem(WORDS_KEY, JSON.stringify(updated));
    return updated;
  },

  deleteWords: (ids: string[] | Set<string>) => {
    const idsToDelete = ids instanceof Set ? Array.from(ids) : ids;
    const words = dataService.getWords();
    const updated = words.filter(w => !idsToDelete.includes(w.id));
    localStorage.setItem(WORDS_KEY, JSON.stringify(updated));
    return updated;
  }
};
