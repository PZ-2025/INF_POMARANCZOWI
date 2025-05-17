import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { User } from '../auth/auth.interface';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/v1/user';

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<User[]> {
    return this.http.get<{ data: { users: User[] } }>(`${this.apiUrl}/all`)
      .pipe(map(res => res.data.users));
  }

  updateUser(userId: number, data: { firstName: string, lastName: string }) {
    return this.http.put(`${this.apiUrl}/admin/${userId}`, data);
  }
}
