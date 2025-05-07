import { Component, inject } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MessageService } from '../services/message.service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  authService: AuthService = inject(AuthService);
  router = inject(Router);

  form = new FormGroup({
    email: new FormControl(null, Validators.required),
    password: new FormControl(null, Validators.required)
  })

  constructor(private messageService: MessageService) {}
  errorMessage: string | null = null;

  onSubmit() {
    this.errorMessage = null;

    if (this.form.valid) {
      //@ts-ignore
      this.authService.login(this.form.value).subscribe({
        next: res => {
          this.messageService.setMessage('Zalogowano pomyślnie!');
          this.router.navigate(['']);
        },
        error: err => {
          console.error('Błąd logowania:', err);
          this.errorMessage = 'Nieprawidłowy email lub hasło';
        }
      });
    } else {
      this.errorMessage = 'Wszystkie pola są wymagane';
    }
  }
}
