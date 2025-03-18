import { Component } from '@angular/core';
import {AuthService} from '../auth/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
   isLoggedIn: boolean = false;

  constructor(private authService: AuthService) {}
  ngOnInit(): void {
    this.isLoggedIn = this.authService.isAuth;
  }

  logout() {
    this.authService.logout();
    this.isLoggedIn = false;
  }
}
