import { Component, inject } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MessageService } from '../services/message.service';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  authService: AuthService = inject(AuthService);
  router = inject(Router);
  messageService = inject(MessageService);

  form = new FormGroup({
    firstName: new FormControl('', Validators.required),
    lastName: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', Validators.required),
    repeat_password: new FormControl('', Validators.required),
    terms: new FormControl(false, Validators.requiredTrue)
  })

  errorMessage: string | null = null;

  onSubmit() {
    this.errorMessage = null;

    const { firstName, lastName, email, password, repeat_password, terms } = this.form.value;

    if (!firstName || !lastName || !email || !password || !repeat_password) {
      this.errorMessage = 'Wszystkie pola są wymagane';
      return;
    }

    if (this.form.controls['email'].errors?.['email']) {
      this.errorMessage = 'Nieprawidłowy email';
      return;
    }

    if (password.length < 6 || password.length > 256) {
      this.errorMessage = 'Hasło musi mieć od 6 do 256 znaków';
      return;
    }

    if (repeat_password.length < 6 || repeat_password.length > 256) {
      this.errorMessage = 'Hasło musi mieć od 6 do 256 znaków';
      return;
    }

    if (password !== repeat_password) {
      this.errorMessage = 'Hasła nie są takie same';
      return;
    }

    if (!terms) {
      this.errorMessage = 'Regulamin niezaakceptowany';
      return;
    }

    this.authService.register({
      firstName: firstName.trim(),
      lastName: lastName.trim(),
      email: email.trim(),
      password: password
    }).subscribe({
      next: () => {
        this.messageService.setMessage('Zarejestrowano pomyślnie! Możesz się teraz zalogować.');
        this.router.navigate(['']);
      },
      error: err => {
        if (err.status === 409 || err.error?.message?.includes('already exists')) {
          this.errorMessage = 'Użytkownik z takim adresem email już istnieje';
        } else {
          this.errorMessage = 'Nie udało się zarejestrować, spróbuj ponownie później';
        }
      }
    });
  }
}
