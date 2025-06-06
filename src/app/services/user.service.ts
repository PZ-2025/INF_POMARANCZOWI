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

  blockUser(userId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${userId}/block`, {});
  }

  unblockUser(userId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${userId}/unblock`, {});
  }

  verifyUser(userId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${userId}/verify`, {});
  }
}
