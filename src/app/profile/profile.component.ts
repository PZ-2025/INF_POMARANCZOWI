import { Component } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService } from '../services/message.service';
import { BadMessageService } from '../services/bad-message.service';
import { CookieService } from 'ngx-cookie-service';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
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

  constructor(private authService: AuthService, private messageService: MessageService, private badMessageService: BadMessageService, private cookieService: CookieService, private http: HttpClient) {}

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
}
