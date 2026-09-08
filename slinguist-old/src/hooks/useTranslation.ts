import { dataService } from '../services/dataService';
import { translations, TranslationKey } from '../lib/translations';

export function useTranslation() {
  const lang = dataService.getAppLanguage();
  const t = (key: TranslationKey): string => {
    return translations[lang][key] || translations.EN[key] || key;
  };

  return { t, lang };
}
