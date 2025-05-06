import { Component } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { ThemeService } from '../services/theme.service';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  isLoggedIn: boolean = false;
  isDarkTheme: boolean = false;

  constructor(private authService: AuthService, private themeService: ThemeService) {}

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isAuth;
    this.isDarkTheme = localStorage.getItem('dark-theme-enabled') === 'true';

    this.themeService.themeChanged.subscribe((val) => {
      this.isDarkTheme = val;
    });
  }

  logout() {
    this.authService.logout();
    this.isLoggedIn = false;
  }

  toggleTheme() {
    this.themeService.toggleTheme();
  }
}
