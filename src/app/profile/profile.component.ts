import { Component } from '@angular/core';

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  activeTab: string = 'profile';

  userType: string | null = null;
  email: string | null = null;
  firstName: string | null = null;
  lastName: string | null = null;

  ngOnInit(): void {
    this.userType = localStorage.getItem('userType');
    this.email = localStorage.getItem('email');
    this.firstName = localStorage.getItem('firstName');
    this.lastName = localStorage.getItem('lastName');

    console.log('User type:', this.userType);
    console.log('Rmail:', this.email);
    console.log('First name:', this.firstName);
    console.log('Last name:', this.lastName);
  }
}
