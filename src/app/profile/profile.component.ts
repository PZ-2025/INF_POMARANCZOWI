import { Component, ChangeDetectorRef } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService } from '../services/message.service';
import { BadMessageService } from '../services/bad-message.service';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import { User } from '../auth/auth.interface';
import { BookService, BookDto } from '../services/book.service';
import { ThemeService } from '../services/theme.service';
import { UserService } from '../services/user.service';

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

  constructor(private userService: UserService, private themeService: ThemeService, private authService: AuthService, private messageService: MessageService, private badMessageService: BadMessageService, private http: HttpClient, private cdr: ChangeDetectorRef, private bookService: BookService) {}

  userType: string | null = null;
  email: string | null = null;
  firstName: string = '';
  lastName: string = '';
  userId: number | null = null;
  avatarPath: string | null = null;
  user: User | null = null;
  isLocked: boolean = false;
  isVerified: boolean = false;

  isDarkTheme = false;
  libraryBooksFiltered: BookDto[] = [];
  bookStatuses: string[] = ['AVAILABLE', 'LOST', 'RESERVED', 'BORROWED'];
  selectedStatusesMap: { [key: string]: boolean } = {
    'AVAILABLE': false,
    'LOST': false,
    'RESERVED': false,
    'BORROWED': false
  };
  statusTranslations: { [key: string]: string } = {
    'AVAILABLE': 'Dostępne',
    'LOST': 'Zagubione',
    'RESERVED': 'Zarezerwowane',
    'BORROWED': 'Wypożyczone'
  };

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
      const basePath = rawPath.startsWith('http') ? rawPath : `http://localhost:8080${rawPath}`;
      this.avatarPath = `${basePath}?t=${Date.now()}`;
    }

    console.log('User type:', this.userType);
    console.log('Rmail:', this.email);
    console.log('First name:', this.firstName);
    console.log('Last name:', this.lastName);
    console.log('Avatar path:', this.avatarPath);
    console.log('Id:', this.userId);

    if (this.userType === 'READER') {
      this.isLocked = localStorage.getItem('locked') === 'true';
      this.isVerified = localStorage.getItem('verified') === 'true';
    }

    this.isDarkTheme = localStorage.getItem('dark-theme-enabled') === 'true';

    this.themeService.themeChanged.subscribe((val) => {
      this.isDarkTheme = val;
    });

    this.allMessageService();

    const tab = localStorage.getItem('profileTab');
    this.activeTab = tab || 'profile';

    this.fetchRentals();
    this.fetchReservations();

    if (this.userType === 'ADMIN' || this.userType === 'LIBRARIAN') {
      const storedBooksPerPage = localStorage.getItem('booksPerPage');
      if (storedBooksPerPage) {
        const parsed = parseInt(storedBooksPerPage, 10);
        if ([3, 5, 10, 15, 25, 50].includes(parsed)) {
          this.booksPerPage = parsed;
        }
      }

      const savedFilters = localStorage.getItem('library-status-filters');
      if (savedFilters) {
        this.selectedStatusesMap = JSON.parse(savedFilters);
      }

      this.fetchAllBooks();
    }

    if (this.userType === 'ADMIN') {
      const storedUsersPerPage = localStorage.getItem('usersPerPage');
      if (storedUsersPerPage) {
        const parsed = parseInt(storedUsersPerPage, 10);
        if ([3, 5, 10, 15, 25, 50].includes(parsed)) {
          this.usersPerPage = parsed;
        }
      }

      const savedUserFilters = localStorage.getItem('user-filter-settings');
      if (savedUserFilters) {
        this.userFilters = JSON.parse(savedUserFilters);
      }

      this.fetchAllUsers();
    }
  }

  filterLibraryBooks(): void {
    localStorage.setItem('library-status-filters', JSON.stringify(this.selectedStatusesMap));
    this.updateFilteredBooks();
  }

  updateFilteredBooks(): void {
    const selectedStatuses = Object.entries(this.selectedStatusesMap)
      .filter(([_, checked]) => checked)
      .map(([status]) => status);

    if (selectedStatuses.length === 0) {
      this.libraryBooksFiltered = [...this.allBooks];
    } else {
      this.libraryBooksFiltered = this.allBooks.filter(book =>
        selectedStatuses.includes(book.status)
      );
    }

    this.currentPage = 1;
  }

  allMessageService() {
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

  setActiveTab(tab: string) {
    this.activeTab = tab;
    localStorage.setItem('profileTab', tab);
  }

  allBooks: BookDto[] = [];
  loadAllBookError: boolean = false;
  currentPage: number = 1;
  booksPerPage: number = 10;

  get paginatedBooks(): BookDto[] {
    const start = (this.currentPage - 1) * this.booksPerPage;
    return this.libraryBooksFiltered.slice(start, start + this.booksPerPage);
  }

  get totalPages(): number {
    const total = Math.ceil(this.libraryBooksFiltered.length / this.booksPerPage);
    return total === 0 ? 0 : total;
  }

  onBooksPerPageChange(event: Event) {
    const select = event.target as HTMLSelectElement;
    const value = parseInt(select.value, 10);
    if (value > 0) {
      this.booksPerPage = value;
      this.currentPage = 1;
      localStorage.setItem('booksPerPage', value.toString());
    }
  }

  fetchAllBooks() {
    if (this.userType !== 'ADMIN' && this.userType !== 'LIBRARIAN') return;

    const token = localStorage.getItem('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      this.loadAllBookError = true;
      return;
    }

    this.bookService.getAllBooks().subscribe({
      next: (books) => {
        this.allBooks = books.map(book => ({
          ...book,

          coverImage: (() => {
            const key = `book-image-${book.id}`;
            const existing = localStorage.getItem(key);

            if (existing) return existing;

            const source = book.coverImage && book.coverImage.trim() !== ''
              ? book.coverImage
              : `https://picsum.photos/seed/book${book.id}/216/216`;

            localStorage.setItem(key, source);
            return source;
          })()
        }));

        console.log(this.allBooks);

        this.loadAllBookError = false;
        this.updateFilteredBooks();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Błąd przy pobieraniu wszystkich książek:', err);
        this.loadAllBookError = true;
      }
    });
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

  allUsers: User[] = [];
  loadUsersError: boolean = false;
  currentUsersPage: number = 1;
  usersPerPage: number = 5;

  userFilters = {
    verified: {
      true: false,
      false: false
    },
    locked: {
      true: false,
      false: false
    }
  };

  filteredUsers: User[] = [];

  get paginatedUsers(): User[] {
    const start = (this.currentUsersPage - 1) * this.usersPerPage;
    return this.filteredUsers.slice(start, start + this.usersPerPage);
  }

  get totalUsersPages(): number {
    const total = Math.ceil(this.filteredUsers.length / this.usersPerPage);
    return total === 0 ? 0 : total;
  }

  filterUsers(): void {
    localStorage.setItem('user-filter-settings', JSON.stringify(this.userFilters));
    this.updateFilteredUsers();
  }

  updateFilteredUsers(): void {
    const { verified, locked } = this.userFilters;

    const verifiedSelected = Object.entries(verified)
      .filter(([_, checked]) => checked)
      .map(([value]) => value === 'true');

    const lockedSelected = Object.entries(locked)
      .filter(([_, checked]) => checked)
      .map(([value]) => value === 'true');

    if (verifiedSelected.length === 0 && lockedSelected.length === 0) {
      this.filteredUsers = [...this.allUsers];
    } else {
      this.filteredUsers = this.allUsers.filter(user => {
        const matchVerified = verifiedSelected.length === 0 || verifiedSelected.includes(user.verified);
        const matchLocked = lockedSelected.length === 0 || lockedSelected.includes(user.locked);
        return matchVerified && matchLocked;
      });
    }

    this.currentUsersPage = 1;
  }

  onUsersPerPageChange(event: Event) {
    const select = event.target as HTMLSelectElement;
    const value = parseInt(select.value, 10);
    if (value > 0) {
      this.usersPerPage = value;
      this.currentUsersPage = 1;
      localStorage.setItem('usersPerPage', value.toString());
    }
  }

  fetchAllUsers() {
    if (this.userType !== 'ADMIN') return;

    const token = localStorage.getItem('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      this.loadUsersError = true;
      return;
    }

    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.allUsers = users.map(user => {
          const key = `user-avatar-${user.id}`;
          const rawPath = user.avatarPath;

          localStorage.setItem(key, rawPath || '/assets/imgs/user.png');

          const path =
            (!rawPath || rawPath === '/assets/imgs/user.png')
              ? '/assets/imgs/user.png'
              : rawPath.startsWith('http')
                ? `${rawPath}?t=${Date.now()}`
                : `http://localhost:8080${rawPath}?t=${Date.now()}`;

          return {
            ...user,
            avatarPath: path
          };
        });

        console.log(this.allUsers);

        this.loadUsersError = false;
        this.updateFilteredUsers();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Błąd przy pobieraniu użytkowników:', err);
        this.loadUsersError = true;
      }
    });
  }

  getUserAvatar(user: User): string {
    const fallback = '/assets/imgs/user.png';

    if (!user.avatarPath || user.avatarPath.trim() === '') {
      return fallback;
    }

    return user.avatarPath;
  }

  userToEdit: any = null;
  showUserModal: boolean = false;

  openUserEditModal(user: any) {
    this.userToEdit = { ...user };
    this.showUserModal = true;
  }

  cancelUserEdit() {
    this.userToEdit = null;
    this.showUserModal = false;
  }

  saveUserChanges() {
    const trimmedFirstName = this.userToEdit.firstName?.trim();
    const trimmedLastName = this.userToEdit.lastName?.trim();

    if (trimmedFirstName && trimmedLastName) {
      this.userToEdit.firstName = trimmedFirstName;
      this.userToEdit.lastName = trimmedLastName;

      this.userService.updateUser(this.userToEdit.id, {
        firstName: trimmedFirstName,
        lastName: trimmedLastName
      }).subscribe({
        next: () => {
          this.messageService.setMessage('Zaktualizowano dane użytkownika.');
          this.cancelUserEdit();
          this.fetchAllUsers();
        },
        error: (err) => {
          console.error('Błąd podczas aktualizacji danych użytkownika:', err);
          this.badMessageService.setMessage('Nie udało się zaktualizować danych użytkownika. Spróbuj ponownie później.');
        }
      });
    } else {
      this.badMessageService.setMessage('Oba pola są wymagane.');
    }
    this.cancelUserEdit();
  }

  blockUser(userId: number) {
    this.userService.blockUser(userId).subscribe({
      next: () => {
        this.messageService.setMessage('Użytkownik został zablokowany.');
        this.fetchAllUsers();
      },
      error: () => {
        this.badMessageService.setMessage('Nie udało się zablokować użytkownika.');
      }
    });
  }

  unblockUser(userId: number) {
    this.userService.unblockUser(userId).subscribe({
      next: () => {
        this.messageService.setMessage('Użytkownik został odblokowany.');
        this.fetchAllUsers();
      },
      error: () => {
        this.badMessageService.setMessage('Nie udało się odblokować użytkownika.');
      }
    });
  }

  verifyUser(userId: number) {
    this.userService.verifyUser(userId).subscribe({
      next: () => {
        this.messageService.setMessage('Użytkownik został zweryfikowany.');
        this.fetchAllUsers();
      },
      error: () => {
        this.badMessageService.setMessage('Nie udało się zweryfikować użytkownika.');
      }
    });
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

          if (this.userType === 'ADMIN') {
            this.fetchAllUsers();
          }
        },
        error: (err: HttpErrorResponse) => {
          console.error('Błąd podczas aktualizacji profilu:', err);
          this.badMessageService.setMessage('Nie udało się zaktualizować danych. Spróbuj ponownie później.');
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
          this.badMessageService.setMessage('Nie udało się zmienić hasła. Spróbuj ponownie później.');
        }
      }
    });
  }

  showAvatarModal = false;

  reloadUserAvatar() {
    const basePath = `/uploads/avatars/user-${this.userId}.jpg`;
    this.avatarPath = `http://localhost:8080${basePath}?t=${new Date().getTime()}`;
    localStorage.setItem('avatarPath', basePath);
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

    const token = localStorage.getItem('token');
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

        if (this.userType === 'ADMIN') {
          this.fetchAllUsers();
        }
      },
      error: (err) => {
        if (err.status === 413) {
          this.badMessageService.setMessage('Plik jest za duży. Maksymalny rozmiar to 5MB.');
        } else {
          this.badMessageService.setMessage('Nie udało się zaktualizować zdjęcia. Spróbuj ponownie później.');
        }
      }
    });
  }

  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`http://localhost:8080/api/v1/user/${id}`);
  }

  deleteAvatar() {
    this.showAvatarModal = false;

    if (this.avatarPath == '/assets/imgs/user.png') {
      this.badMessageService.setMessage('Nie masz żadnego wgranego zdjęcia.');
      return;
    }

    const token = localStorage.getItem('token');
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

        if (this.userType === 'ADMIN') {
          this.fetchAllUsers();
        }
      },
      error: (err) => {
        console.error('Błąd podczas usuwania zdjęcia:', err);
        this.badMessageService.setMessage('Nie udało się usunąć zdjęcia. Spróbuj ponownie później.');
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
              this.returnedRentals = loansWithBooks.filter(loan => loan.status === 'RETURNED' || loan.status === 'LOST');
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
        this.fetchRentals();
      },
      error: (err) => {
        console.error('Błąd przy przedłużaniu wypożyczenia:', err);
        this.badMessageService.setMessage('Nie udało się przedłużyć wypożyczenia. Spróbuj ponownie później.');
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
        this.fetchRentals();
      },
      error: (err) => {
        console.error('Błąd przy zwracaniu książki:', err);
        this.badMessageService.setMessage('Nie udało się zwrócić książki. Spróbuj ponownie później.');
      }
    });
  }

  userReservations: any[] = [];
  loadReservationError: boolean = false;

  fetchReservations() {
    if (this.userType !== 'READER') return;

    const token = localStorage.getItem('token');
    if (!token) {
      this.badMessageService.setMessage('Token użytkownika stracił ważność.');
      return;
    }

    this.http.get<any>('http://localhost:8080/api/v1/reservations/my', {
      headers: {
        Authorization: `Bearer ${token}`
      },

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
        this.fetchReservations();
      },
      error: err => {
        console.error('Błąd przy anulowaniu rezerwacji:', err);
        this.badMessageService.setMessage('Nie udało się anulować rezerwacji. Spróbuj ponownie później.');
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
        this.fetchReservations();
        this.fetchRentals();
      },
      error: err => {
        console.error('Błąd przy realizowaniu rezerwacji:', err);
        this.badMessageService.setMessage('Nie udało się zrealizować rezerwacji. Spróbuj ponownie później.');
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
        this.fetchReservations();
      },
      error: err => {
        console.error('Błąd przy przedłużaniu rezerwacji:', err);
        this.badMessageService.setMessage('Nie udało się przedłużyć rezerwacji. Spróbuj ponownie później.');
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
        this.fetchReservations();
        this.fetchRentals();
      },
      error: err => {
        console.error('Błąd przy oznaczaniu książki jako zgubionej:', err);
        this.badMessageService.setMessage('Nie udało się oznaczyć książki jako zgubionej. Spróbuj ponownie później.');
      }
    });
  }

  getBookImageUrl(coverImage: string): string {
    if (!coverImage) return '';

    // Sprawdź czy to już pełny URL
    if (coverImage.startsWith('http://') || coverImage.startsWith('https://')) {
      return coverImage;
    }

    // Sprawdź czy base64 już ma prefix
    if (coverImage.startsWith('data:image/')) {
      return coverImage;
    }

    // Dodaj prefix base64 jeśli go nie ma
    return `data:image/jpeg;base64,${coverImage}`;
  }

  // Alias dla kompatybilności
  getImageSrc(coverImage: string): string {
    return this.getBookImageUrl(coverImage);
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;

    img.src = '/assets/imgs/user.png';

    event.preventDefault();
    event.stopPropagation();
  }

  confirmActionVisible: boolean = false;
  confirmMessage: string = '';
  confirmCallback: (() => void) | null = null;

  openConfirmation(message: string, callback: () => void): void {
    this.confirmMessage = message;
    this.confirmCallback = callback;
    this.confirmActionVisible = true;
  }

  confirmAction(): void {
    if (this.confirmCallback) {
      this.confirmCallback();
    }
    this.confirmActionVisible = false;
  }

  cancelAction(): void {
    this.confirmActionVisible = false;
    this.confirmCallback = null;
  }

  confirmCancelReservation(reservationId: number): void {
    this.openConfirmation(
      'Czy na pewno chcesz zrezygnować z rezerwacji?',
      () => this.cancelReservation(reservationId)
    );
  }

  confirmCompleteReservation(reservationId: number): void {
    this.openConfirmation(
      'Czy na pewno chcesz wypożyczyć książkę?',
      () => this.completeReservation(reservationId)
    );
  }

  confirmExtendReservation(reservationId: number, expiresAt: string): void {
    this.openConfirmation(
      'Czy na pewno chcesz przedłużyć rezerwację?',
      () => this.extendReservation(reservationId, expiresAt)
    );
  }

  confirmReturnBook(loanId: number): void {
    this.openConfirmation(
      'Czy na pewno chcesz zwrócić książkę?',
      () => this.returnBook(loanId)
    );
  }

  confirmExtendLoan(loanId: number, extendedCount: number, dueDate: string, isReserved: boolean): void {
    this.openConfirmation(
      'Czy na pewno chcesz przedłużyć wypożyczenie?',
      () => this.extendLoan(loanId, extendedCount, dueDate, isReserved)
    );
  }

  confirmMarkBookAsLost(loanId: number): void {
    this.openConfirmation(
      'Czy na pewno chcesz oznaczyć książkę jako zgubioną?',
      () => this.markBookAsLost(loanId)
    );
  }
}
