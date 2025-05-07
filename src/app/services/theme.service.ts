import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  private darkThemeKey = 'dark-theme-enabled';
  themeChanged = new Subject<boolean>();

  constructor() {
    const dark = localStorage.getItem(this.darkThemeKey) === 'true';
    this.setDarkTheme(dark);
  }

  toggleTheme(): void {
    const html = document.documentElement;
    const isDark = html.classList.toggle('dark');
    localStorage.setItem(this.darkThemeKey, isDark.toString());
    this.themeChanged.next(isDark);
  }

  setDarkTheme(enabled: boolean): void {
    const html = document.documentElement;
    if (enabled) {
      html.classList.add('dark');
    } else {
      html.classList.remove('dark');
    }
    localStorage.setItem(this.darkThemeKey, enabled.toString());
    this.themeChanged.next(enabled);
  }
}
