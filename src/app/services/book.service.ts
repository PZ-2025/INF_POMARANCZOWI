import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthService } from '../auth/auth.service';

export interface BookDto {
  id: number;
  title: string;
  authors: { firstName: string; lastName: string } [];
  publisher: { name: string };
  description: string;
  genre: string;
  status: string;
  coverImage: string;
  created_at: string;
  updated_at: string;
}

interface BookApiResponse {
  data: {
    books: BookDto[];
  };
}

@Injectable({ providedIn: 'root' })
export class BookService {
  private apiUrl = 'http://localhost:8080/api/v1/book';

  constructor(private http: HttpClient, private authService: AuthService) {}

  getRandomBooks(): Observable<BookDto[]> {
    return this.http.get<BookApiResponse>(`${this.apiUrl}/random`)
      .pipe(map((response: BookApiResponse) => response.data.books));
  }

  getRandomBooksByGenre(genre: string): Observable<BookDto[]> {
    return this.http.get<BookApiResponse>(`${this.apiUrl}/random/category/${genre}`)
      .pipe(map(res => res.data.books));
  }

  getTopGenres(): Observable<string[]> {
    return this.http.get<{ data: { genres: string[] } }>(`${this.apiUrl}/top-genres`)
      .pipe(map(res => res.data.genres));
  }

  searchBooks(query: string): Observable<BookDto[]> {
    return this.http.get<BookApiResponse>(`${this.apiUrl}/search?query=${encodeURIComponent(query)}`)
      .pipe(map(res => res.data.books));
  }

  getBookById(id: number): Observable<BookDto> {
    return this.http.get<{ data: { book: BookDto } }>(`${this.apiUrl}/${id}`)
      .pipe(map(res => res.data.book));
  }
}
