import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BookService, BookDto } from '../../services/book.service';

@Component({
  selector: 'app-book-details',
  standalone: false,
  templateUrl: './book-details.component.html',
  styleUrl: './book-details.component.css'
})
export class BookDetailsComponent implements OnInit {
  book: BookDto | null = null;
  bookImageUrl: string = '';
  fallbackUsed = false;
  loadError = false;

  constructor(private route: ActivatedRoute, private bookService: BookService, private router: Router) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (!id || isNaN(+id) || +id <= 0) {
      this.router.navigate(['/']);
      return;
    }

    const key = `book-image-${id}`;
    const stored = localStorage.getItem(key);

    if (stored) {
      this.bookImageUrl = stored;
      this.fallbackUsed = true;
    }

    this.bookService.getBookById(+id).subscribe({
      next: (book) => {
        if (!book) {
          this.router.navigate(['/']);
          this.loadError = true;
          return;
        }

        this.book = book;

        if (book.coverImage && !this.fallbackUsed) {
          this.bookImageUrl = book.coverImage;
        }
      },
      error: (error) => {
        if (error.status === 0) {
          this.loadError = true;
          console.error('Backend niedostępny lub nie działa');
        }
        else if (error.status === 404) {
          this.router.navigate(['/']);
        } else {
          console.error('Inny błąd:', error);
          this.router.navigate(['/']);
        }
      }
    });
  }

  onImageError(event: Event): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;

    const key = `book-image-${id}`;
    let fallback = localStorage.getItem(key);

    if (!fallback) {
      fallback = `https://picsum.photos/seed/book${id}/216/216`;
      localStorage.setItem(key, fallback);
      console.log('[ERROR] New fallback saved:', fallback);
    }

    this.bookImageUrl = fallback;
  }

  formatAuthors(authors: { firstName: string; lastName: string }[]): string {
    return authors.map(a => `${a.firstName} ${a.lastName}`).join(', ');
  }

  removeDot(text: string): string {
    return text.endsWith('.') ? text.slice(0, -1) : text;
  }

  getPolishStatus(status: string): string {
    switch (status) {
      case 'AVAILABLE':
        return 'Dostępna';
      case 'RESERVED':
        return 'Zarezerwowana';
      case 'BORROWED':
        return 'Wypożyczona';
      case 'LOST':
        return 'Zagubiona';
      default:
        return status;
    }
  }

  getStatusColorVar(status: string): string {
    switch (status) {
      case 'AVAILABLE':
        return 'var(--status-available)';
      case 'RESERVED':
        return 'var(--status-reserved)';
      case 'BORROWED':
        return 'var(--status-borrowed)';
      case 'LOST':
        return 'var(--status-lost)';
      default:
        return 'var(--text-color)';
    }
  }
}
