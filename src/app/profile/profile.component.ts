import { Component } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService } from '../services/message.service';
import { BadMessageService } from '../services/bad-message.service';

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

  constructor(private authService: AuthService, private messageService: MessageService, private badMessageService: BadMessageService) {}

  userType: string | null = null;
  email: string | null = null;
  firstName: string = '';
  lastName: string = '';
  userId: number | null = null;

  showEditForm = false;

  ngOnInit(): void {
    this.userType = localStorage.getItem('userType');
    this.email = localStorage.getItem('email');
    this.firstName = localStorage.getItem('firstName') || '';
    this.lastName = localStorage.getItem('lastName') || '';
    this.userId = parseInt(localStorage.getItem('userId') || '0', 10);

    console.log('User type:', this.userType);
    console.log('Rmail:', this.email);
    console.log('First name:', this.firstName);
    console.log('Last name:', this.lastName);
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
}
