import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { TokenResponse } from './auth.interface';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  http = inject(HttpClient);
  baseApiUrl = 'http://localhost:8080/api/v1/auth/';

  get isAuth(): boolean {
    return !!localStorage.getItem('token');
  }

  get getUserType(): string | null {
    return localStorage.getItem('userType');
  }

  login(payload: {email: string, password: string}) {
    return this.http.post<TokenResponse>(
      `${this.baseApiUrl}login`,
      payload,
    ).pipe(
      tap(val => {
        // Zapisz token w localStorage
        localStorage.setItem('token', val.access_token);

        console.log("Login response:", val);
        const user = val.data.user;
        localStorage.setItem('userType', user.userType);
        localStorage.setItem('email', user.email);
        localStorage.setItem('firstName', user.firstName);
        localStorage.setItem('lastName', user.lastName);
        localStorage.setItem('userId', user.id.toString());
        localStorage.removeItem('profileTab');

        if (user.avatarPath == null) {
          localStorage.setItem('avatarPath', './assets/imgs/user.png');
        } else {
          localStorage.setItem('avatarPath', user.avatarPath);
        }
      })
    )
  }

  logout(): void {
    // Usuwanie wszystkich danych z localStorage
    localStorage.removeItem('token');
    localStorage.removeItem('userType');
    localStorage.removeItem('email');
    localStorage.removeItem('firstName');
    localStorage.removeItem('lastName');
    localStorage.removeItem('userId');
    localStorage.removeItem('avatarPath');
  }

  register(payload: RegisterPayload) {
    return this.http.post(`${this.baseApiUrl}register`, payload);
  }

  updateProfile(firstName: string, lastName: string) {
    const token = localStorage.getItem('token');

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
    const token = localStorage.getItem('token');
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

  getToken(): string | null {
    return localStorage.getItem('token');
  }
}

export interface RegisterPayload {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}
