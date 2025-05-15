import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { TokenResponse } from './auth.interface';
import { CookieService } from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  http = inject(HttpClient);
  baseApiUrl = 'http://localhost:8080/api/v1/auth/'
  cookieService = inject(CookieService)

  token: string |null = null;
  refreshToken: string |null = null;

  get isAuth(): boolean {
    if (!this.token) {
      this.token = this.cookieService.get('token');
    }
    return !!this.token;
  }

  login(payload: {email: string, password: string}) {
    return this.http.post<TokenResponse>(
      `${this.baseApiUrl}login`,
      payload,
    ).pipe(
      tap(val => {
        this.token = val.access_token;
        this.refreshToken = val.refresh_token;

        this.cookieService.set('token', this.token);
        this.cookieService.set('refreshToken', this.refreshToken);
        console.log("Login response:", val);
        const user = val.data.user;
        localStorage.setItem('userType', user.userType);
        localStorage.setItem('email', user.email);
        localStorage.setItem('firstName', user.firstName);
        localStorage.setItem('lastName', user.lastName);
        localStorage.setItem('userId', user.id.toString());
        localStorage.removeItem('profileTab');

        if (user.avatarPath == null) {
          localStorage.setItem('avatarPath', '/assets/imgs/user.png');
        } else {
          localStorage.setItem('avatarPath', user.avatarPath);
        }
      })
    )
  }

  logout(): void {
    this.cookieService.delete('token', '');
    this.cookieService.delete('refreshToken', '');
    this.token = null;
    this.refreshToken = null;
  }

  register(payload: RegisterPayload) {
    return this.http.post(`${this.baseApiUrl}register`, payload);
  }

  updateProfile(firstName: string, lastName: string) {
    const token = this.cookieService.get('token');

    return this.http.put(
      'http://localhost:8080/api/v1/user/me',
      { firstName, lastName },
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
  }

  changePassword(oldPassword: string, newPassword: string) {
    const token = this.cookieService.get('token');
    const body = {
      oldPassword,
      newPassword
    };
    return this.http.post(`${this.baseApiUrl}changePassword`, body, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  getToken(): string {
    return this.cookieService.get('token');
  }
}

export interface RegisterPayload {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}
