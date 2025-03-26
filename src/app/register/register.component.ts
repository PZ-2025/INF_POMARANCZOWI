<<<<<<< HEAD
import { Component } from '@angular/core';
=======
import { Component, inject } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
>>>>>>> frontend-develop

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
<<<<<<< HEAD

=======
  authService: AuthService = inject(AuthService);
  router = inject(Router);

  form = new FormGroup({
    name: new FormControl(null, Validators.required),
    surname: new FormControl(null, Validators.required),
    email: new FormControl(null, Validators.required),
    password: new FormControl(null, Validators.required),
    repeat_password: new FormControl(null, Validators.required),
  })
>>>>>>> frontend-develop
}
