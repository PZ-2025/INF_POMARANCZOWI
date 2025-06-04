import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-edit-book',
  templateUrl: './edit-book.component.html',
  standalone: false,
  styleUrls: ['./edit-book.component.css']
})
export class EditBookComponent implements OnInit {
  form: FormGroup;
  selectedFile: File | null = null;
  isSubmitting = false;
  isLoading = true;
  bookId: number = 0;
  currentBook: any = null;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.form = this.fb.group({
      title: ['', [Validators.required]],
      authorFirstName: ['', [Validators.required]],
      authorLastName: ['', [Validators.required]],
      publisherName: ['', [Validators.required]],
      genre: ['', [Validators.required]],
      description: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.bookId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadBookData();
  }

  // Ładowanie danych książki
  loadBookData(): void {
    const token = localStorage.getItem('token');

    if (!token) {
      alert('Nie jesteś zalogowany! Przejdź do strony logowania.');
      this.router.navigate(['/login']);
      return;
    }

    this.http.get(`http://localhost:8080/api/v1/book/${this.bookId}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }).subscribe({
      next: (response: any) => {
        console.log('📚 Załadowano dane książki:', response);
        this.currentBook = response.data.book;
        this.populateForm();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('❌ BŁĄD podczas ładowania książki:', error);
        this.isLoading = false;

        if (error.status === 404) {
          alert('❌ Książka nie została znaleziona.');
        } else if (error.status === 401) {
          alert('❌ Sesja wygasła - zaloguj się ponownie');
          this.router.navigate(['/login']);
        } else {
          alert('❌ Wystąpił błąd podczas ładowania książki.');
        }
        this.router.navigate(['/books']);
      }
    });
  }

  // Wypełnienie formularza danymi książki
  populateForm(): void {
    if (this.currentBook) {
      this.form.patchValue({
        title: this.currentBook.title,
        authorFirstName: this.currentBook.authors?.[0]?.firstName || '',
        authorLastName: this.currentBook.authors?.[0]?.lastName || '',
        publisherName: this.currentBook.publisher?.name || '',
        genre: this.currentBook.genre,
        description: this.currentBook.description
      });
    }
  }

  // Metoda sprawdzająca błędy (potrzebna dla template)
  hasError(fieldName: string): boolean {
    const field = this.form.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }

  // Metoda zwracająca komunikaty błędów (potrzebna dla template)
  getErrorMessage(fieldName: string): string {
    const field = this.form.get(fieldName);

    if (field && field.errors && field.touched) {
      if (field.errors['required']) {
        switch (fieldName) {
          case 'title': return 'Tytuł jest wymagany';
          case 'authorFirstName': return 'Imię autora jest wymagane';
          case 'authorLastName': return 'Nazwisko autora jest wymagane';
          case 'publisherName': return 'Nazwa wydawcy jest wymagana';
          case 'genre': return 'Gatunek jest wymagany';
          case 'description': return 'Opis jest wymagany';
          default: return 'To pole jest wymagane';
        }
      }
    }

    return '';
  }

  // Metoda obsługi wyboru pliku
  onFileSelected(event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      this.selectedFile = target.files[0];
    }
  }

  // Konwersja pliku do base64 (identyczna jak w add-book)
  private fileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => {
        const result = reader.result as string;
        resolve(result.split(',')[1]); // Remove data:image/...;base64, prefix
      };
      reader.onerror = error => reject(error);
    });
  }

  async onSubmit(): Promise<void> {
    if (this.form.valid && !this.isSubmitting) {
      this.isSubmitting = true;

      const token = localStorage.getItem('token');

      if (!token) {
        alert('Nie jesteś zalogowany! Przejdź do strony logowania.');
        this.isSubmitting = false;
        return;
      }

      // Struktura danych do aktualizacji (identyczna jak w add-book)
      const updateData = {
        title: this.form.value.title,
        authors: [
          {
            firstName: this.form.value.authorFirstName,
            lastName: this.form.value.authorLastName,
            biography: this.currentBook?.authors?.[0]?.biography || "Brak biografii"
          }
        ],
        publisher: {
          name: this.form.value.publisherName,
          description: this.currentBook?.publisher?.description || "Brak opisu"
        },
        description: this.form.value.description,
        genre: this.form.value.genre,
        coverImage: this.selectedFile ? await this.fileToBase64(this.selectedFile) : this.currentBook?.coverImage
        // ↑ IDENTYCZNE: jeśli wybrano nowy plik, konwertuj do base64, inaczej zachowaj obecny
      };

      console.log('📚 Wysyłam aktualizację książki (z obsługą zdjęć):', updateData);

      this.http.put(`http://localhost:8080/api/v1/book/${this.bookId}/update`, updateData, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      }).subscribe({
        next: (response: any) => {
          console.log('✅ SUKCES! Książka zaktualizowana:', response);

          if (response.data && response.data.book) {
            const hasNewImage = this.selectedFile ? ' z nową okładką' : '';
            alert(`🎉 Książka "${response.data.book.title}"${hasNewImage} została pomyślnie zaktualizowana!`);
          } else {
            alert('🎉 Książka została pomyślnie zaktualizowana!');
          }

          this.isSubmitting = false;

          // NOWA LINIJKA: Wyczyść cache wszystkich obrazków książek
          Object.keys(localStorage).forEach(key => {
            if (key.startsWith('book-image-')) {
              localStorage.removeItem(key);
            }
          });

          // Usuń też ogólne cache
          localStorage.removeItem('books');
          localStorage.removeItem('bookImages');

          this.router.navigate(['/books', this.bookId]);
        },
        error: (error) => {
          console.error('❌ BŁĄD podczas aktualizacji książki:', error);
          this.isSubmitting = false;

          if (error.status === 500) {
            if (error.message?.includes('Data too long')) {
              alert('❌ Zdjęcie za duże dla bazy danych. Spróbuj mniejszy plik.');
            } else {
              alert('❌ Błąd serwera: ' + (error.error?.message || 'Nieznany błąd'));
            }
          } else if (error.status === 403) {
            alert('❌ Brak uprawnień do edycji książek.');
          } else if (error.status === 401) {
            alert('❌ Sesja wygasła - zaloguj się ponownie');
          } else if (error.status === 400) {
            alert('❌ Nieprawidłowe dane: ' + (error.error?.message || 'Sprawdź formularz'));
          } else if (error.status === 404) {
            alert('❌ Książka nie została znaleziona.');
          } else {
            alert('❌ Wystąpił błąd: ' + (error.message || 'Nieznany błąd'));
          }
        }
      });
    } else {
      console.log('❌ Formularz nieprawidłowy:', this.form.errors);
      alert('❌ Wypełnij wszystkie wymagane pola!');
    }
  }

  // Anulowanie edycji
  cancelEdit(): void {
    this.router.navigate(['/books', this.bookId]);
  }

  // Pomocnicza metoda do formatowania URL obrazka (tak jak w innych komponentach)
  getBookImageUrl(coverImage: string): string {
    if (!coverImage) return '';

    // Sprawdź czy to już pełny URL
    if (coverImage.startsWith('http://') || coverImage.startsWith('https://')) {
      return coverImage;
    }

    // Sprawdź czy base64 już ma prefix
    if (coverImage.startsWith('data:image/')) {
      return coverImage;
    }

    // Dodaj prefix base64 jeśli go nie ma
    return `data:image/jpeg;base64,${coverImage}`;
  }

  // Alias dla kompatybilności
  getImageSrc(coverImage: string): string {
    return this.getBookImageUrl(coverImage);
  }

  // Obsługa błędu ładowania obrazka książki
  onImageBookError(event: any, bookId?: number): void {
    console.log('Błąd ładowania obrazka książki:', bookId, event);
    event.target.style.display = 'none';
  }

  // Alias dla kompatybilności
  onImageError(event: any): void {
    this.onImageBookError(event);
  }
}

