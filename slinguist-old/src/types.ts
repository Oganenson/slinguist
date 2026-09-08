/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

export interface Word {
  id: string;
  foreign: string;
  translation: string;
  pronunciation?: string;
  difficulty: 'easy' | 'medium' | 'hard';
  category: string;
  language: string;
  isStarred?: boolean;
  learnedAt?: number;
}

export interface LearningPath {
  id: string;
  language: string;
  level: string;
  progress: number; // 0 to 100
  image: string;
}

export interface UserStats {
  wordsLearned: number;
  streak: number;
  globalRank: string;
}

export interface BeforeInstallPromptEvent extends Event {
  readonly platforms: string[];
  readonly userChoice: Promise<{
    outcome: 'accepted' | 'dismissed';
    platform: string;
  }>;
  prompt(): Promise<void>;
}

declare global {
  interface Window {
    deferredInstallPrompt?: BeforeInstallPromptEvent | null;
  }
}

