import { Component, inject } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-edit-book',
  standalone: false,
  templateUrl: './add-edit-book.component.html',
  styleUrl: './add-edit-book.component.css'
})
export class AddEditBookComponent {
  authService: AuthService = inject(AuthService);
  router = inject(Router);

  form = new FormGroup({
    title: new FormControl(null, Validators.required),
    author: new FormControl(null, Validators.required),
    genre: new FormControl(null, Validators.required),
    publisher: new FormControl(null, Validators.required),
    year_of_publication: new FormControl(null, Validators.required),
    pages: new FormControl(null, Validators.required),
    description: new FormControl(null, Validators.required)
  })

  userType: string | null = null;

  ngOnInit(): void {
    this.userType = localStorage.getItem('userType');

    if (this.userType !== 'ADMIN' && this.userType !== 'LIBRARIAN') {
      this.router.navigate(['/']);
      return;
    }
  }
}
