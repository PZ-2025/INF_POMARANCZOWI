import { Component } from '@angular/core';

@Component({
  selector: 'app-home',
<<<<<<< HEAD
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})


export class HomeComponent {
  books = [
    { title: 'Harry Potter i Kamień...', genre: 'Fantasy', author: 'J.K. Rowling' },
    { title: 'Harry Potter i Komnata...', genre: 'Fantasy', author: 'J.K. Rowling' },
    { title: 'Harry Potter i Więzień...', genre: 'Fantasy', author: 'J.K. Rowling' }
  ];
}
=======
  standalone: false,
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent { }
>>>>>>> frontend-develop
