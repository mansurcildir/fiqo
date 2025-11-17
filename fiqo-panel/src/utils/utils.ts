import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import { createContext, useContext } from 'react';

export const SPRING_BASE_URL = import.meta.env.VITE_SPRING_BASE_URL || 'http://localhost:8080';

dayjs.extend(relativeTime);

type Theme = 'light' | 'dark';

type ThemeContextType = {
  theme: Theme;
  toggleTheme: () => void;
};

type SidebarContextType = {
  isExpanded: boolean;
  isMobileOpen: boolean;
  isHovered: boolean;
  activeItem: string | null;
  openSubmenu: string | null;
  toggleSidebar: () => void;
  toggleMobileSidebar: () => void;
  setIsHovered: (isHovered: boolean) => void;
  setActiveItem: (item: string | null) => void;
  toggleSubmenu: (item: string) => void;
};

export const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export const SidebarContext = createContext<SidebarContextType | undefined>(undefined);

export const useTheme = () => {
  const context = useContext(ThemeContext);
  if (context === undefined) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
};

export const useSidebar = () => {
  const context = useContext(SidebarContext);
  if (!context) {
    throw new Error('useSidebar must be used within a SidebarProvider');
  }
  return context;
};

export const formatFileSize = (bytes: number): string => {
  if (bytes < 0) return '0 B';

  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  let size = bytes;
  let unitIndex = 0;

  while (size >= 1024 && unitIndex < units.length - 1) {
    size /= 1024;
    unitIndex++;
  }

  return `${size.toFixed(1)} ${units[unitIndex]}`;
};

export const formatDate = (dateStr: string, formatStr = 'DD/MM/YYYY HH:mm'): string => {
  return dayjs(dateStr).format(formatStr);
};

export const fromNow = (dateStr: string): string => {
  return dayjs(dateStr).fromNow();
};

export const getDay = (dateStr: string): string => {
  return dayjs(dateStr).date().toString();
};

export const normalizeEntries = <T extends object>(form: T): T => {
  return Object.fromEntries(
    Object.entries(form).map(([key, value]) => [key, typeof value === 'string' && value.trim() === '' ? null : value])
  ) as T;
};
