import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {tap} from 'rxjs';
import {TokenResponse} from './auth.interface';
import {CookieService} from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  http =inject(HttpClient);
  baseApiUrl= 'http://localhost:8080/api/v1/auth/'
  cookieService =inject(CookieService)


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
        this.token=val.access_token;
        this.refreshToken=val.refresh_token;

        this.cookieService.set('token',this.token);
        this.cookieService.set('refreshToken',this.refreshToken);
      } )
    )
  }

  logout(): void {
    this.cookieService.delete('token', '');
    this.cookieService.delete('refreshToken', '');
    this.token = null;
    this.refreshToken = null;
  }

}
