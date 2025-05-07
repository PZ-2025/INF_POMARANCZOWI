import { Component } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { ThemeService } from '../services/theme.service';
import { Router } from '@angular/router';
import { MessageService } from '../services/message.service';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  isLoggedIn = false;
  isDarkTheme = false;

  constructor(private authService: AuthService, private themeService: ThemeService, private router: Router, private messageService: MessageService) {}

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

    this.router.navigate(['']).then(() => {
      setTimeout(() => {
        this.messageService.setMessage('Wylogowano pomyślnie!');
      }, 100);
    });
  }

  toggleTheme() {
    this.themeService.toggleTheme();
  }
}
