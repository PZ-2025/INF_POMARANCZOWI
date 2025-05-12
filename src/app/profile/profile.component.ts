import { Component, ChangeDetectorRef } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService } from '../services/message.service';
import { BadMessageService } from '../services/bad-message.service';
import { CookieService } from 'ngx-cookie-service';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import { User } from '../auth/auth.interface';

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  activeTab: string = 'profile';
  message: string | null = null;
  badMessage: string | null = null;

  constructor(private authService: AuthService, private messageService: MessageService, private badMessageService: BadMessageService, private cookieService: CookieService, private http: HttpClient, private cdr: ChangeDetectorRef) {}

  userType: string | null = null;
  email: string | null = null;
  firstName: string = '';
  lastName: string = '';
  userId: number | null = null;
  avatarPath: string | null = null;
  user: User | null = null;

  showEditForm = false;

  ngOnInit(): void {
    this.userType = localStorage.getItem('userType');
    this.email = localStorage.getItem('email');
    this.firstName = localStorage.getItem('firstName') || '';
    this.lastName = localStorage.getItem('lastName') || '';
    this.userId = parseInt(localStorage.getItem('userId') || '0', 10);
    this.avatarPath = localStorage.getItem('avatarPath');

    const rawPath = localStorage.getItem('avatarPath');
    if (!rawPath || rawPath === '/assets/imgs/user.png') {
      this.avatarPath = '/assets/imgs/user.png';
    } else {
      this.avatarPath = rawPath.startsWith('http') ? rawPath : `http://localhost:8080${rawPath}`;
    }
    localStorage.setItem('avatarPath', this.avatarPath);

    console.log('User type:', this.userType);
    console.log('Rmail:', this.email);
    console.log('First name:', this.firstName);
    console.log('Last name:', this.lastName);
    console.log('Avatar path:', this.avatarPath);
    console.log('Id:', this.userId);

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

    const tab = localStorage.getItem('profileTab');
    this.activeTab = tab || 'profile';

    this.fetchRentals();
    this.fetchReservations();
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
    localStorage.setItem('profileTab', tab);
  }

  showModal = false;
  tempFirstName: string = '';
  tempLastName: string = '';

  toggleModal() {
    this.tempFirstName = this.firstName;
    this.tempLastName = this.lastName;
    this.showModal = true;
  }

  saveChanges() {
    if (this.tempFirstName.trim() && this.tempLastName.trim()) {
      this.firstName = this.tempFirstName.trim();
      this.lastName = this.tempLastName.trim();

      this.authService.updateProfile(this.firstName, this.lastName).subscribe({
        next: () => {
          localStorage.setItem('firstName', this.firstName!);
          localStorage.setItem('lastName', this.lastName!);
          this.showEditForm = false;
          this.messageService.setMessage('Zaktualizowano dane.');
          console.log("Zapisano", this.firstName, this.lastName);
        },
        error: (err: HttpErrorResponse) => {
          console.error('Błąd podczas aktualizacji profilu:', err);
          this.badMessageService.setMessage('Nie udało się zaktualizować danych, spróbuj ponownie później.');
        }
      });
    } else {
      this.badMessageService.setMessage('Oba pola są wymagane.');
    }
    this.showModal = false;
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

  showPasswordModal = false;

  togglePasswordModal() {
    this.showPasswordModal = !this.showPasswordModal;
  }

  changePassword(data: { oldPassword: string; newPassword: string; confirmPassword: string }) {
    this.showPasswordModal = false;

    if (!data.oldPassword || !data.newPassword || !data.confirmPassword) {
      this.badMessageService.setMessage('Wszystkie pola są wymagane.');
      return;
    }

    if (data.newPassword.length < 6 || data.newPassword.length > 256) {
      this.badMessageService.setMessage('Hasło musi mieć od 6 do 256 znaków.');
      return;
    }

    if (data.confirmPassword.length < 6 || data.confirmPassword.length > 256) {
      this.badMessageService.setMessage('Hasło musi mieć od 6 do 256 znaków.');
      return;
    }

    if (data.newPassword !== data.confirmPassword) {
      this.badMessageService.setMessage('Nowe hasła nie są takie same.');
      return;
    }

    this.authService.changePassword(data.oldPassword, data.newPassword).subscribe({
      next: () => {
        this.messageService.setMessage('Hasło zostało zaktualizowane.');
      },
      error: (err: HttpErrorResponse) => {
        console.error('Błąd podczas zmiany hasła:', err);

        if (err.status === 403 || err.error?.message?.includes('Invalid old password')) {
          this.badMessageService.setMessage('Stare hasło jest niepoprawne.');
        } else {
          this.badMessageService.setMessage('Nie udało się zmienić hasła, spróbuj ponownie później.');
        }
      }
    });
  }

  showAvatarModal = false;

  reloadUserAvatar() {
    this.avatarPath = `http://localhost:8080/uploads/avatars/user-${this.userId}.jpg?${new Date().getTime()}`;
    localStorage.setItem('avatarPath', this.avatarPath);
  }

  toggleAvatarModal() {
    this.showAvatarModal = !this.showAvatarModal;
  }

  uploadAvatar(file: File | null) {
    this.showAvatarModal = false;

    if (!file) {
      this.badMessageService.setMessage('Nie wybrano pliku.');
      return;
    }

    const allowedTypes = ['image/jpeg', 'image/png', 'image/webp'];
    if (!allowedTypes.includes(file.type)) {
      this.badMessageService.setMessage('Nieobsługiwany format pliku, dozwolone formaty to JPG, PNG i WEBP.');
      return;
    }

    const formData = new FormData();
    formData.append('file', file);

    const token = this.cookieService.get('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    this.http.post('http://localhost:8080/api/v1/user/upload-avatar', formData, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }).subscribe({
      next: () => {
        this.messageService.setMessage('Zdjęcie zostało zaktualizowane.');
        this.reloadUserAvatar();
      },
      error: (err) => {
        if (err.status === 413) {
          this.badMessageService.setMessage('Plik jest za duży. Maksymalny rozmiar to 5MB.');
        } else {
          this.badMessageService.setMessage('Nie udało się zaktualizować zdjęcia, spróbuj ponownie później.');
        }
      }
    });
  }

  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`http://localhost:8080/api/v1/user/${id}`);
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;

    img.src = '/assets/imgs/user.png';

    event.preventDefault();
    event.stopPropagation();
  }

  deleteAvatar() {
    this.showAvatarModal = false;

    if (this.avatarPath == '/assets/imgs/user.png') {
      this.badMessageService.setMessage('Nie masz żadnego wgranego zdjęcia.');
      return;
    }

    const token = this.cookieService.get('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    this.http.delete('http://localhost:8080/api/v1/user/delete-avatar', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }).subscribe({
      next: () => {
        this.messageService.setMessage('Zdjęcie zostało usunięte.');
        this.avatarPath = '/assets/imgs/user.png';
        localStorage.removeItem('avatarPath');
      },
      error: (err) => {
        console.error('Błąd podczas usuwania zdjęcia:', err);
        this.badMessageService.setMessage('Nie udało się usunąć zdjęcia, spróbuj ponownie później.');
      }
    });
  }

  activeRentals: any[] = [];
  returnedRentals: any[] = [];

  onImageBookError(event: Event, bookId: number | undefined): void {
    const img = event.target as HTMLImageElement;

    if (!bookId) {
      img.src = '/assets/imgs/book.png';
      return;
    }

    const key = `book-image-${bookId}`;
    const fallbackUsed = img.dataset['fallbackUsed'] === 'true';
    const fallbackFailed = img.dataset['fallbackFailed'] === 'true';

    if (fallbackUsed && fallbackFailed) {
      img.src = '/assets/imgs/book.png';
      return;
    }

    if (!fallbackUsed) {
      const fallback = `https://picsum.photos/seed/book${bookId}/216/216`;
      img.src = fallback;
      img.dataset['fallbackUsed'] = 'true';
      localStorage.setItem(key, fallback);
      return;
    }

    if (fallbackUsed && !fallbackFailed) {
      img.src = '/assets/imgs/book.png';
      img.dataset['fallbackFailed'] = 'true';
      return;
    }
  }

  formatAuthors(authors: { firstName: string; lastName: string }[]): string {
    return authors.map(a => `${a.firstName} ${a.lastName}`).join(', ');
  }

  removeDot(text: string): string {
    return text.endsWith('.') ? text.slice(0, -1) : text;
  }

  loadError: boolean = false;

  fetchRentals() {
    if (this.userType !== 'READER') return;

    const token = this.cookieService.get('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    this.http.get<any>('http://localhost:8080/api/v1/loans/my', {
      headers: {
        Authorization: `Bearer ${token}`
      },
      withCredentials: true
    }).subscribe({
      next: (response) => {
        const loans = response.data?.loans || [];

        if (loans.length === 0) {
          this.activeRentals = [];
          this.returnedRentals = [];
          return;
        }

        const bookRequests = loans.map((loan: any) =>
          this.http.get<any>(`http://localhost:8080/api/v1/book/${loan.bookId}`, {
            headers: {
              Authorization: `Bearer ${token}`
            },
            withCredentials: true
          }).pipe(
            map(bookResponse => {
              const bookData = bookResponse.data?.book || bookResponse.data || bookResponse;

              return {
                ...loan,
                book: {
                  ...bookData,

                  coverImage: (() => {
                    const key = `book-image-${bookData.id}`;
                    const existing = localStorage.getItem(key);

                    if (existing) return existing;

                    const source = bookData.coverImage && bookData.coverImage.trim() !== ''
                      ? bookData.coverImage
                      : `https://picsum.photos/seed/book${bookData.id}/216/216`;

                    localStorage.setItem(key, source);
                    return source;
                  })()
                }
              };
            })
          )
        );

        if (bookRequests.length > 0) {
          forkJoin<any[]>(bookRequests).subscribe({
            next: (loansWithBooks) => {
              this.activeRentals = loansWithBooks.filter(loan => loan.status === 'ACTIVE');
              this.returnedRentals = loansWithBooks.filter(loan => loan.status === 'RETURNED');
              console.log('Wypożyczone książki:', this.activeRentals);
              console.log('Zwrócone książki:', this.returnedRentals);
              this.cdr.detectChanges();
            },
            error: (err) => {
              console.error('Błąd przy pobieraniu danych książek:', err);
            }
          });
        } else {
          this.activeRentals = [];
          this.returnedRentals = [];
        }
      },
      error: (err) => {
        console.error('Błąd przy pobieraniu wypożyczeń:', err);
        this.loadError = true;
      }
    });
  }

  canExtendLoan(extendedCount: number, dueDateStr: string): boolean {
    const dueDate = new Date(dueDateStr);
    const today = new Date();
    const diffInMs = dueDate.getTime() - today.getTime();
    const daysLeft = Math.ceil(diffInMs / (1000 * 60 * 60 * 24));

    return extendedCount < 3 && daysLeft <= 14;
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

    const token = this.cookieService.get('token');
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
        this.fetchRentals();
      },
      error: (err) => {
        console.error('Błąd przy przedłużaniu wypożyczenia:', err);
        this.badMessageService.setMessage('Nie udało się przedłużyć wypożyczenia.');
      }
    });
  }

  returnBook(loanId: number) {
    const token = this.cookieService.get('token');
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
        this.fetchRentals();
      },
      error: (err) => {
        console.error('Błąd przy zwracaniu książki:', err);
        this.badMessageService.setMessage('Nie udało się zwrócić książki.');
      }
    });
  }

  userReservations: any[] = [];
  loadReservationError: boolean = false;

  fetchReservations() {
    if (this.userType !== 'READER') return;

    const token = this.cookieService.get('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    this.http.get<any>('http://localhost:8080/api/v1/reservations/my', {
      headers: {
        Authorization: `Bearer ${token}`
      },
      withCredentials: true
    }).subscribe({
      next: (response) => {
        const reservations = response.data?.reservations || [];

        if (reservations.length === 0) {
          this.userReservations = [];
          this.loadReservationError = false;
          return;
        }

        const now = new Date();
        const validReservations = reservations.map((reservation: any) => {
          const expires = new Date(reservation.expiresAt);
          if (reservation.status === 'READY' && expires < now) {
            if (reservation.id) this.expireReservation(reservation.id);
            return null;
          }
          return reservation;
        }).filter(Boolean);

        const bookRequests = validReservations.map((reservation: any) =>
          this.http.get<any>(`http://localhost:8080/api/v1/book/${reservation.bookId}`, {
            headers: {
              Authorization: `Bearer ${token}`
            },
            withCredentials: true
          }).pipe(
            map(bookResponse => {
              const bookData = bookResponse.data?.book || bookResponse.data || bookResponse;

              return {
                ...reservation,
                book: {
                  ...bookData,

                  coverImage: (() => {
                    const key = `book-image-${bookData.id}`;
                    const existing = localStorage.getItem(key);

                    if (existing) return existing;

                    const source = bookData.coverImage && bookData.coverImage.trim() !== ''
                      ? bookData.coverImage
                      : `https://picsum.photos/seed/book${bookData.id}/216/216`;

                    localStorage.setItem(key, source);
                    return source;
                  })()
                }
              };
            })
          )
        );

        if (bookRequests.length > 0) {
          forkJoin<any[]>(bookRequests).subscribe({
            next: (reservationsWithBooks) => {
              // this.userReservations = reservationsWithBooks;
              this.userReservations = reservationsWithBooks.filter(r => r.status !== 'EXPIRED' && r.status !== 'CANCELLED' && r.status !== 'COMPLETED');
              this.loadReservationError = false;
              console.log('Rezerwacje z książkami:', this.userReservations);
              this.cdr.detectChanges();
            },
            error: (err) => {
              console.error('Błąd przy pobieraniu danych książek do rezerwacji:', err);
              this.loadReservationError = true;
            }
          });
        } else {
          this.userReservations = [];
        }
      },
      error: (err) => {
        console.error('Błąd przy pobieraniu rezerwacji:', err);
        this.loadReservationError = true;
      }
    });
  }

  translateReservationStatus(status: string): string {
    switch (status) {
      case 'CANCELLED': return 'Anulowana';
      case 'COMPLETED': return 'Zrealizowana';
      case 'EXPIRED': return 'Wygasła';
      case 'PENDING': return 'Oczekująca';
      case 'READY': return 'Gotowa do odbioru';
      default: return status;
    }
  }

  expireReservation(reservationId: number) {
    const token = this.cookieService.get('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    this.http.post(`http://localhost:8080/api/v1/reservations/${reservationId}/expire`, {}, {
      headers: {
        Authorization: `Bearer ${token}`
      },
      withCredentials: true
    }).subscribe({
      next: () => {
        // console.log(`Rezerwacja ${reservationId} została oznaczona jako EXPIRED`);
        this.fetchReservations();
      },
      error: (err) => {
        console.error(`Błąd przy oznaczaniu rezerwacji ${reservationId} jako EXPIRED:`, err);
      }
    });
  }

  cancelReservation(reservationId: number) {
    const reservation = this.userReservations.find(r => r.id === reservationId);
    if (reservation?.status === 'CANCELLED') {
      this.badMessageService.setMessage('Rezerwacja została już anulowana.');
      return;
    }

    const token = this.cookieService.get('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    this.http.post(`http://localhost:8080/api/v1/reservations/${reservationId}/cancel`, {}, {
      headers: {
        Authorization: `Bearer ${token}`
      },
      withCredentials: true
    }).subscribe({
      next: () => {
        this.messageService.setMessage('Rezerwacja została anulowana.');
        this.fetchReservations();
      },
      error: err => {
        console.error('Błąd przy anulowaniu rezerwacji:', err);
        this.badMessageService.setMessage('Nie udało się anulować rezerwacji.');
      }
    });
  }

  completeReservation(reservationId: number) {
    const token = this.cookieService.get('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    this.http.post(`http://localhost:8080/api/v1/reservations/${reservationId}/complete`, {}, {
      headers: {
        Authorization: `Bearer ${token}`
      },
      withCredentials: true
    }).subscribe({
      next: () => {
        this.messageService.setMessage('Rezerwacja została zrealizowana.');
        this.fetchReservations();
        this.fetchRentals();
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

    const token = this.cookieService.get('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    this.http.post(`http://localhost:8080/api/v1/reservations/${reservationId}/extend`, {}, {
      headers: {
        Authorization: `Bearer ${token}`
      },
      withCredentials: true
    }).subscribe({
      next: () => {
        this.messageService.setMessage('Rezerwacja została przedłużona.');
        this.fetchReservations();
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
}
