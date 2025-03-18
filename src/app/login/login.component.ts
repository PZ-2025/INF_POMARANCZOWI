import {Component, inject} from '@angular/core';
import {AuthService} from '../auth/auth.service';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';

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

  onSubmit() {
    console.log(this.form.value)
    if (this.form.valid) {
      //@ts-ignore
      this.authService.login(this.form.value)
        .subscribe(res => {
          this.router.navigate(['']);
          console.log(res)
        });
    }
  }


}
