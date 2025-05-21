import { Component, inject } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { BookService } from '../services/book.service'

@Component({
  selector: 'app-add-edit-book',
  standalone: false,
  templateUrl: './add-edit-book.component.html',
  styleUrl: './add-edit-book.component.css'
})
export class AddEditBookComponent {
  authService: AuthService = inject(AuthService);
  router = inject(Router);
  bookService = inject(BookService);

  form = new FormGroup({
    title: new FormControl(null, Validators.required),
    genre: new FormControl(null, Validators.required),
    publisher: new FormControl(null, Validators.required),
    description: new FormControl(null, Validators.required)
  });

  userType: string | null = null;
  existingAuthors: any[] = [];
  existingPublishers: any[] = [];

  ngOnInit(): void {
    this.userType = localStorage.getItem('userType');

    if (this.userType !== 'ADMIN' && this.userType !== 'LIBRARIAN') {
      this.router.navigate(['/']);
      return;
    }

    this.loadAuthors();
    this.loadPublishers();
  }

  loadAuthors() {
    this.bookService.getAuthors().subscribe({
      next: (res: any) => {
        this.existingAuthors = res?.data?.authors || [];
        console.log('Autorzy:', this.existingAuthors);
      },
      error: (err) => {
        console.error('Błąd pobierania autorów:', err);
      }
    });
  }

  loadPublishers() {
    this.bookService.getPublishers().subscribe({
      next: (res: any) => {
        this.existingPublishers = res?.data?.publishers || [];
        console.log('Wydawcy:', this.existingPublishers);
      },
      error: (err) => {
        console.error('Błąd pobierania wydawców:', err);
      }
    });
  }
}
