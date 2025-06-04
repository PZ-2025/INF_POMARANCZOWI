import { Component, OnInit } from '@angular/core';
import { ThemeService } from '../services/theme.service';
import { MessageService } from '../services/message.service';
import { BookService, BookDto } from '../services/book.service';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  isDarkTheme = false;
  message: string | null = null;
  books: BookDto[] = [];
  selectedGenre: string | null = null;
  topGenres: string[] = [];
  genreLoadError = false;
  sectionTitle: string = 'Brak książek do wyświetlenia';
  searchQuery: string = '';

  constructor(private themeService: ThemeService, private messageService: MessageService, private bookService: BookService) {}

  ngOnInit(): void {
    this.isDarkTheme = localStorage.getItem('dark-theme-enabled') === 'true';

    this.themeService.themeChanged.subscribe((val) => {
      this.isDarkTheme = val;
    });

    this.messageService.message$.subscribe(msg => {
      this.message = msg;
      if (msg) {
        this.autoClearMessage();
      }
    });

    const currentMessage = this.messageService.getCurrentMessage();
    if (currentMessage) {
      this.message = currentMessage;
      this.autoClearMessage();
    }

    this.bookService.getTopGenres().subscribe({
      next: genres => {
        this.topGenres = genres;
        this.genreLoadError = false;
      },
      error: err => {
        console.error('Błąd przy ładowaniu kategorii', err);
        this.genreLoadError = true;
      }
    });

    this.bookService.getRandomBooks().subscribe({
      next: books => {
        this.books = books;
        this.sectionTitle = 'Polecane tytuły';
      },
      error: err => console.error('Błąd przy pobieraniu książek', err)
    });
  }

  autoClearMessage() {
    setTimeout(() => {
      this.messageService.clearMessage();
    }, 5000);
  }

  toggleTheme() {
    this.themeService.toggleTheme();
  }

  formatAuthors(authors: { firstName: string; lastName: string }[]): string {
    return authors.map(a => `${a.firstName} ${a.lastName}`).join(', ');
  }

  removeDot(text: string): string {
    return text.endsWith('.') ? text.slice(0, -1) : text;
  }

  loadBooksByGenre(genre: string) {
    this.selectedGenre = genre;
    this.bookService.getRandomBooksByGenre(genre).subscribe({
      next: books => {
        this.books = books;
        this.sectionTitle = `Polecane tytuły z kategorii "${this.genreTranslations[genre] || genre}"`;
      },
      error: err => {
        console.error('Błąd przy pobieraniu książek po kategorii', err);
        this.books = [];
        this.sectionTitle = `Brak książek do wyświetlenia w kategorii "${this.genreTranslations[genre] || genre}"`;
      }
    });
  }

  onImageError(event: Event) {
    const target = event.target as HTMLImageElement;

    if (target.dataset['fallbackUsed']) {
      target.src = '/assets/imgs/book.png';
      return;
    }

    const randomId = Math.floor(Math.random() * 1000);
    target.src = `https://picsum.photos/216?random=${randomId}`;
    target.dataset['fallbackUsed'] = 'true';
  }

  searchBooks(): void {
    const query = this.searchQuery.trim();

    if (!query) {
      this.bookService.getRandomBooks().subscribe({
        next: books => {
          this.books = books;
          this.sectionTitle = 'Polecane tytuły';
        },
        error: err => {
          console.error('Błąd przy ładowaniu książek', err);
          this.books = [];
          this.sectionTitle = 'Brak książek do wyświetlenia ';
        }
      });
      return;
    }

    this.bookService.searchBooks(query).subscribe({
      next: books => {
        this.books = books;
        this.sectionTitle = `Wyniki wyszukiwania dla "${query}"`;
      },
      error: err => {
        console.error('Błąd przy wyszukiwaniu książek', err);
        this.books = [];
        this.sectionTitle = `Brak wyników wyszukiwania dla "${query}"`;
      }
    });
  }

  genreTranslations: { [key: string]: string } = {
    'Fiction': 'Fikcja',
    'History': 'Historia',
    'Fantasy': 'Fantastyka',
    'Biography': 'Biografia',
    'Sci-Fi': 'Science Fiction',
  };

  getBookImageUrl(coverImage: string | null): string {
    if (!coverImage) return '/assets/default-book.png';

    // Zamień broken placeholder services
    if (coverImage.includes('placeimg.com') ||
      coverImage.includes('placekitten.com')) {
      return 'https://via.placeholder.com/300x400/f0f0f0/666666?text=Book';
    }

    // Jeśli URL - zwróć bezpośrednio
    if (coverImage.startsWith('http')) return coverImage;

    // Jeśli base64 - dodaj prefix
    return `data:image/jpeg;base64,${coverImage}`;
  }
}
