/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import { Flame, BookOpen, ChevronRight, Play } from 'lucide-react';
import { motion } from 'motion/react';
import { Link } from 'react-router-dom';
import { cn } from '@/src/lib/utils';
import type { LearningPath } from '@/src/types';

const MOCK_PATHS: LearningPath[] = [
  {
    id: 'fr',
    language: 'French',
    level: 'Intermediate',
    progress: 74,
    image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuByc1lNes9XK6jRDQj4WrvdwytfyKNvEYxFrdDzopA7frSRzUR6oyZ5k2d4h8o5QQXQujoDokkLkXkUGpN4WzlMDNQrEeauXlX_rq6verzlW7lDoWNtp-XBrr03qcvBUISYvRAORrmFzfCWN0pP6qzXQD9YPaFWIZRiVhYtzdLxeiscJmyrV9D85Qulg_7KTN1JTnFJP0OBd1Rakxi25pr-k7pi3d8g4i_IbG2F4XX0ssb5UGJvKdSC-ugovdKjqQH-EX8fJRMT-Cc'
  },
  {
    id: 'es',
    language: 'Spanish',
    level: 'Beginner',
    progress: 42,
    image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuD-ykJUu2oohkqr0uRlS_B6ZKv84CzRqxN_B4qZKUKuLVUcg9lrbSCevFrwcldzt_bYaFG_E9B2M0Xau4_6EmNEHzpmPejjmQyGGUYSQhGSPlqP7d5JpecZaoO1me4CqqktwQHci1dyWTo1ZCjZjEmFv3v-wYMDu8ukls5wnhT5vrmrZr9e_0zG3DBRIfJWF3VabWgzaXpOy_FH5mCY9nzCZfaAlVR94r_JGAOkwWkjKkI1aFQnyruXX6IGSszNDdu2WFQ2dxmEwzw'
  },
  {
    id: 'de',
    language: 'German',
    level: 'Expert',
    progress: 89,
    image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuACljFBbTKeHXjdq_d1OLYgoPOAniIdE6L9_WbyyEnG4TwHOhz_5I9wMBAE7GJpNmlJWYif5OqJA-6ED19eDUT1NG9TRl5yqZsbp78-Lk_LAbPYQkB0c3CbyS067ELfi8KdMjp0t-lDA_6pwWzT95WxznrF7MK3C6yqAtZ2kfi5rVaM-av8TBQwnhWy_XrsvaR4f4mhh78DCsE2xWRhLw7tW6a_LSU_Die7GBdsUX76T1CGgwRe3_n57kGAMdxCk2eloEKbDgedxvU'
  }
];

import { dataService } from '@/src/services/dataService';
import { useTranslation } from '@/src/hooks/useTranslation';

export default function Home() {
  const { t } = useTranslation();
  const [profile, setProfile] = React.useState(dataService.getProfile());
  const stats = dataService.getStats();

  React.useEffect(() => {
    const handleStorageChange = () => {
      setProfile(dataService.getProfile());
    };
    window.addEventListener('storage', handleStorageChange);
    return () => window.removeEventListener('storage', handleStorageChange);
  }, []);

  return (
    <div className="space-y-10 text-on-background">
      {/* Welcome Section */}
      <motion.section 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8 }}
        className="grid grid-cols-1 md:grid-cols-12 gap-6 items-center"
      >
        <div className="md:col-span-12 lg:col-span-8">
          <h1 className="text-4xl md:text-5xl font-black text-on-background mb-3 tracking-tight">
            {t('welcome').replace('Elena', profile.name.split(' ')[0])}
          </h1>
          <p className="text-lg text-on-surface-variant max-w-xl font-bold">
            {t('ready_to_master')}
          </p>
        </div>

          <div className="md:col-span-6 lg:col-span-2 bg-white dark:bg-surface-container rounded-[32px] p-6 border border-violet-50 dark:border-white/5 shadow-lg shadow-violet-500/5 flex flex-col items-center text-center ring-1 ring-violet-50/50 dark:ring-white/5">
            <div className="w-12 h-12 rounded-2xl bg-orange-50 dark:bg-orange-950/30 text-orange-500 flex items-center justify-center mb-3">
              <Flame size={24} fill="currentColor" />
            </div>
            <span className="text-3xl font-black text-on-surface">{stats.streak}</span>
            <span className="text-[10px] font-black text-on-surface-variant uppercase tracking-[0.2em] mt-1">
              {stats.streak % 10 === 1 && stats.streak % 100 !== 11 
                ? `${t('day_singular')} ${t('day_streak').split(' ')[1] || ''}`
                : t('day_streak')
              }
            </span>
          </div>

        <div className="md:col-span-6 lg:col-span-2 bg-white dark:bg-surface-container rounded-[32px] p-6 border border-violet-50 dark:border-white/5 shadow-lg shadow-violet-500/5 flex flex-col items-center text-center ring-1 ring-violet-50/50 dark:ring-white/5">
          <div className="w-12 h-12 rounded-2xl bg-violet-50 dark:bg-primary/20 text-violet-600 dark:text-primary flex items-center justify-center mb-3">
            <BookOpen size={24} />
          </div>
          <span className="text-3xl font-black text-on-surface">{stats.wordsLearned.toLocaleString()}</span>
          <span className="text-[10px] font-black text-on-surface-variant uppercase tracking-[0.2em] mt-1">{t('words_known')}</span>
        </div>
      </motion.section>

      {/* Daily Quest */}
      <section className="relative overflow-hidden bg-primary-container rounded-[32px] p-8 md:p-12 text-white">
        <div className="relative z-10 max-w-lg">
          <div className="inline-flex items-center gap-2 bg-white/20 px-4 py-1.5 rounded-full text-xs font-bold mb-6 backdrop-blur-md ring-1 ring-white/10 uppercase tracking-widest">
            <span className="w-2 h-2 rounded-full bg-orange-400 animate-pulse" />
            {t('daily_session')}
          </div>
          <h2 className="text-3xl md:text-4xl font-bold leading-tight mb-6 tracking-tight">
            {t('home_purple_block_title')}
          </h2>
          <p className="text-white/80 text-lg mb-8">
            {t('home_purple_block_desc')}
          </p>
          <Link to="/session" className="bg-white text-primary px-8 py-4 rounded-2xl font-bold text-lg hover:shadow-2xl hover:shadow-white/20 transition-all active:scale-95 inline-flex items-center gap-3">
            <Play size={20} fill="currentColor" />
            {t('review_label')}
          </Link>
        </div>

        {/* Decorative elements */}
        <div className="absolute top-0 right-0 -mr-20 -mt-20 w-80 h-80 bg-white/10 rounded-full blur-3xl" />
        <div className="absolute bottom-0 right-12 hidden lg:block">
           <img 
            src="https://lh3.googleusercontent.com/aida-public/AB6AXuBRWeM-dUe7Z6U7Ian2F21rNzRBUr4wAMh7E3_M4gD-a9JhlpBK5Z8_UmqQx8I71CKs5SsycnoJceLFEAz_ZRUbamPC80pZ_BBOIHCSgvrZDGjnCpsWEpKdcQkoTK0Iq2fj2qMU8Pa9UkJiKEZ4cKNVEA1gLrGy_pdmCFyKNt1uc7KFATWh45iVSeMOY2uKEOG_c-S-7RvZHtEOaN_CAUTevHf1zMlSlycGEZTfSSliyZshe483AAUERshKUuKtgz1Rj9T29x9AxsQ" 
            alt="Study" 
            className="w-80 h-auto rounded-3xl shadow-2xl rotate-3 ring-8 ring-white/5"
          />
        </div>
      </section>
    </div>
  );
}
