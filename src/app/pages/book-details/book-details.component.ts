import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BookService, BookDto } from '../../services/book.service';
import { MessageService } from '../../services/message.service';
import { BadMessageService } from '../../services/bad-message.service';

import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { User } from '../../auth/auth.interface';

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
  message: string | null = null;
  badMessage: string | null = null;

  constructor(private route: ActivatedRoute, private bookService: BookService, private router: Router, private messageService: MessageService, private badMessageService: BadMessageService, private http: HttpClient, private cdr: ChangeDetectorRef) {}

  userType: string | null = null;
  email: string | null = null;
  firstName: string = '';
  lastName: string = '';
  userId: number | null = null;
  user: User | null = null;

  userLoans: any[] = [];
  isMyLoan: boolean = false;
  myLoan: any = null;
  myReservation: any = null;
  hasReadyReservation: boolean = false;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (!id || isNaN(+id) || +id <= 0) {
      this.router.navigate(['/']);
      return;
    }

    this.userType = localStorage.getItem('userType');
    this.email = localStorage.getItem('email');
    this.firstName = localStorage.getItem('firstName') || '';
    this.lastName = localStorage.getItem('lastName') || '';
    this.userId = parseInt(localStorage.getItem('userId') || '0', 10);

    const key = `book-details-image-${id}`;
    const stored = localStorage.getItem(key);

    if (stored) {
      this.bookImageUrl = stored;
      this.fallbackUsed = true;
    }

    this.subscribeToMessages();

    this.bookService.getBookById(+id).subscribe({
      next: (book) => {
        if (!book) {
          this.router.navigate(['/']);
          this.loadError = true;
          return;
        }

        this.book = book;

        const key = `book-details-image-${book.id}`;
        const fallbackImage = localStorage.getItem(key);

        if (book.coverImage && !fallbackImage) {
          this.bookImageUrl = book.coverImage;
        } else if (fallbackImage) {
          this.bookImageUrl = fallbackImage;
          this.fallbackUsed = true;
        }

        this.fetchRentalsAndReservations();
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

  subscribeToMessages() {
    this.messageService.message$.subscribe(msg => {
      this.message = msg;
      if (msg) {
        if (this.badMessage) {
          this.badMessage = null;
        }
        this.autoClearMessage();
      }
    });

    const currentMessage = this.messageService.getCurrentMessage();
    if (currentMessage) {
      if (this.badMessage) {
        this.badMessage = null;
      }
      this.message = currentMessage;
      this.autoClearMessage();
    }

    this.badMessageService.message$.subscribe(msg => {
      this.badMessage = msg;
      if (msg) {
        if (this.message) {
          this.message = null;
        }
        this.autoClearBadMessage();
      }
    });

    const currentBadMessage = this.badMessageService.getCurrentMessage();
    if (currentBadMessage) {
      if (this.message) {
        this.message = null;
      }
      this.badMessage = currentBadMessage;
      this.autoClearBadMessage();
    }
  }

  fetchRentalsAndReservations() {
    if (this.userType !== 'READER') return;

    const token = localStorage.getItem('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    this.http.get<any>('http://localhost:8080/api/v1/loans/my', {
      headers: {
        Authorization: `Bearer ${token}`
      },
    }).subscribe({
      next: (response) => {
        this.userLoans = response.data?.loans || [];
        this.myLoan = this.userLoans.find(loan => loan.bookId === this.book?.id && loan.status === 'ACTIVE');
        this.isMyLoan = !!this.myLoan;

        console.log("Wypożyczenie:", this.myLoan);
      },
      error: (err) => {
        console.error('Błąd przy pobieraniu wypożyczeń:', err);
      }
    });

    this.http.get<any>('http://localhost:8080/api/v1/reservations/my', {
      headers: {
        Authorization: `Bearer ${token}`
      },

    }).subscribe({
      next: (response) => {
        const reservations = response.data?.reservations || [];
        const now = new Date();
        const validReservations = reservations.map((reservation: any) => {
          const expires = new Date(reservation.expiresAt);
          if (reservation.status === 'READY' && expires < now) {
            if (reservation.id) this.expireReservation(reservation.id);
            return null;
          }
          return reservation;
        }).filter(Boolean);

        this.myReservation = validReservations.find((r: any) =>
          r.bookId === this.book?.id &&
          !['CANCELLED', 'COMPLETED', 'EXPIRED'].includes(r.status)
        );
        this.hasReadyReservation = !!this.myReservation;

        console.log("Rezerwacje:", this.myReservation);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Błąd przy pobieraniu wypożyczeń:', err);
      }
    });
  }

  private messageTimeout: any;
  private badMessageTimeout: any;

  autoClearMessage() {
    if (this.messageTimeout) {
      clearTimeout(this.messageTimeout);
    }
    this.messageTimeout = setTimeout(() => {
      this.messageService.clearMessage();
      this.messageTimeout = null;
    }, 5000);
  }

  autoClearBadMessage() {
    if (this.badMessageTimeout) {
      clearTimeout(this.badMessageTimeout);
    }
    this.badMessageTimeout = setTimeout(() => {
      this.badMessageService.clearMessage();
      this.badMessageTimeout = null;
    }, 5000);
  }

  onImageError(event: Event): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;

    const key = `book-details-image-${id}`;
    let fallback = localStorage.getItem(key);

    if (!fallback) {
      fallback = `https://picsum.photos/seed/book${id}/216/216`;
      localStorage.setItem(key, fallback);
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

  extendLoan(loanId: number, extendedCount: number, dueDateStr: string, isReserved: boolean) {
    if (isReserved) {
      this.badMessageService.setMessage('Nie można przedłużyć wypożyczenia. Książka jest zarezerwowana przez innego użytkownika.');
      return;
    }

    if (extendedCount >= 3) {
      this.badMessageService.setMessage('Nie można przedłużyć wypożyczenia. Maksymalna liczba przedłużeń została osiągnięta.');
      return;
    }

    const dueDate = new Date(dueDateStr);
    const today = new Date();
    const diffInMs = dueDate.getTime() - today.getTime();
    const daysLeft = Math.ceil(diffInMs / (1000 * 60 * 60 * 24));

    if (daysLeft > 14) {
      this.badMessageService.setMessage('Nie można przedłużyć wypożyczenia. Do terminu zwrotu pozostało więcej niż 14 dni.');
      return;
    }

    const token = localStorage.getItem('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    this.http.post(`http://localhost:8080/api/v1/loans/${loanId}/extend`, {}, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }).subscribe({
      next: () => {
        this.messageService.setMessage('Wypożyczenie zostało przedłużone.');
        this.refreshData();
      },
      error: (err) => {
        console.error('Błąd przy przedłużaniu wypożyczenia:', err);
        this.badMessageService.setMessage('Nie udało się przedłużyć wypożyczenia.');
      }
    });
  }

  returnBook(loanId: number) {
    const token = localStorage.getItem('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    this.http.post(`http://localhost:8080/api/v1/loans/${loanId}/return`, {}, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }).subscribe({
      next: () => {
        this.messageService.setMessage('Książka została zwrócona.');
        this.refreshData();
      },
      error: (err) => {
        console.error('Błąd przy zwracaniu książki:', err);
        this.badMessageService.setMessage('Nie udało się zwrócić książki.');
      }
    });
  }

  refreshData() {
    this.ngOnInit();
  }

  canExtendLoan(extendedCount: number, dueDateStr: string): boolean {
    const dueDate = new Date(dueDateStr);
    const today = new Date();
    const diffInMs = dueDate.getTime() - today.getTime();
    const daysLeft = Math.ceil(diffInMs / (1000 * 60 * 60 * 24));

    return extendedCount < 3 && daysLeft <= 14;
  }

  expireReservation(reservationId: number) {
    const token = localStorage.getItem('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    this.http.post(`http://localhost:8080/api/v1/reservations/${reservationId}/expire`, {}, {
      headers: {
        Authorization: `Bearer ${token}`
      },

    }).subscribe({
      next: () => {
        // console.log(`Rezerwacja ${reservationId} została oznaczona jako EXPIRED`);
        this.fetchRentalsAndReservations();
      },
      error: (err) => {
        console.error(`Błąd przy oznaczaniu rezerwacji ${reservationId} jako EXPIRED:`, err);
      }
    });
  }

  cancelReservation(reservationId: number) {
    const token = localStorage.getItem('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    this.http.post(`http://localhost:8080/api/v1/reservations/${reservationId}/cancel`, {}, {
      headers: {
        Authorization: `Bearer ${token}`
      },

    }).subscribe({
      next: () => {
        this.messageService.setMessage('Rezerwacja została anulowana.');
        this.refreshData();
      },
      error: err => {
        console.error('Błąd przy anulowaniu rezerwacji:', err);
        this.badMessageService.setMessage('Nie udało się anulować rezerwacji.');
      }
    });
  }

  completeReservation(reservationId: number) {
    const token = localStorage.getItem('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    this.http.post(`http://localhost:8080/api/v1/reservations/${reservationId}/complete`, {}, {
      headers: {
        Authorization: `Bearer ${token}`
      },

    }).subscribe({
      next: () => {
        this.messageService.setMessage('Rezerwacja została zrealizowana.');
        this.refreshData();
      },
      error: err => {
        console.error('Błąd przy realizowaniu rezerwacji:', err);
        this.badMessageService.setMessage('Nie udało się zrealizować rezerwacji.');
      }
    });
  }

  extendReservation(reservationId: number, expiresAt: string) {
    if (!this.canExtendReservation(expiresAt)) {
      this.badMessageService.setMessage('Rezerwację można przedłużyć dopiero na 3 dni przed wygaśnięciem.');
      return;
    }

    const token = localStorage.getItem('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    this.http.post(`http://localhost:8080/api/v1/reservations/${reservationId}/extend`, {}, {
      headers: {
        Authorization: `Bearer ${token}`
      },

    }).subscribe({
      next: () => {
        this.messageService.setMessage('Rezerwacja została przedłużona.');
        this.refreshData();
      },
      error: err => {
        console.error('Błąd przy przedłużaniu rezerwacji:', err);
        this.badMessageService.setMessage('Nie udało się przedłużyć rezerwacji.');
      }
    });
  }

  canExtendReservation(expiresAt: string): boolean {
    const now = new Date();
    const expires = new Date(expiresAt);
    const diff = (expires.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);

    return diff <= 3;
  }

  markBookAsLost(loanId: number): void {
    if (this.userType !== 'READER') return;

    const token = localStorage.getItem('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    this.http.post(`http://localhost:8080/api/v1/loans/${loanId}/lost`, {}, {
      params: { notes: 'Zgubiona przez użytkownika' },
      headers: {
        Authorization: `Bearer ${token}`
      },

    }).subscribe({
      next: () => {
        this.messageService.setMessage('Książka została oznaczona jako zgubiona.');
        this.refreshData();
      },
      error: err => {
        console.error('Błąd przy oznaczaniu książki jako zgubionej:', err);
        this.badMessageService.setMessage('Nie udało się oznaczyć książki jako zgubionej.');
      }
    });
  }

  borrowBook(bookId: number): void {
    if (this.userType !== 'READER') return;

    const token = localStorage.getItem('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    const requestBody = {
      bookId: bookId,
      notes: ''
    };

    this.http.post(`http://localhost:8080/api/v1/loans/borrow/self`, requestBody, {
      headers: {
        Authorization: `Bearer ${token}`
      },

    }).subscribe({
      next: () => {
        this.messageService.setMessage('Książka została wypożyczona.');
        this.refreshData();
      },
      error: err => {
        console.error('Błąd przy wypożyczaniu:', err);
        this.badMessageService.setMessage('Nie udało się wypożyczyć książki.');
      }
    });
  }

  reserveBook(bookId: number): void {
    if (this.userType !== 'READER') return;

    const token = localStorage.getItem('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    this.http.post(`http://localhost:8080/api/v1/reservations/book/${bookId}`, {}, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }).subscribe({
      next: () => {
        this.messageService.setMessage('Książka została zarezerwowana.');
        this.refreshData();
      },
      error: err => {
        console.error('Błąd przy rezerwacji:', err);
        this.badMessageService.setMessage('Nie udało się zarezerwować książki.');
      }
    });
  }

  getBookImageUrl(coverImage: string | null): string {
    if (!coverImage) return '/assets/default-book.png';

    // Zamień broken placeholder services
    if (coverImage.includes('placeimg.com') ||
      coverImage.includes('placekitten.com')) {
      return 'https://via.placeholder.com/300x400/f0f0f0/666666?text=Book';
    }

    // Jeśli URL - zwróć bezpośrednio
    if (coverImage.startsWith('http')) return coverImage;

    // Jeśli base64 - dodaj prefix
    return `data:image/jpeg;base64,${coverImage}`;
  }
}
