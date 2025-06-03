import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import {Router} from '@angular/router';

@Component({
  selector: 'app-add-book',
  templateUrl: './add-book.component.html',
  standalone: false,
  styleUrls: ['./add-book.component.css']
})
export class AddBookComponent implements OnInit {
  form: FormGroup;
  selectedFile: File | null = null;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
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

  ngOnInit(): void {}

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

  // Metoda obsługi wyboru pliku (zakomentowana w template, ale może zostać)
  onFileSelected(event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      this.selectedFile = target.files[0];
    }
  }

  // Konwersja pliku do base64 (na przyszłość)
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

      // Struktura z przywróconą funkcjonalnością zdjęć
      const bookData = {
        title: this.form.value.title,
        authors: [
          {
            firstName: this.form.value.authorFirstName,
            lastName: this.form.value.authorLastName,
            biography: "Brak biografii"
          }
        ],
        publisher: {
          name: this.form.value.publisherName,
          description: "Brak opisu"
        },
        description: this.form.value.description,
        genre: this.form.value.genre,
        coverImage: this.selectedFile ? await this.fileToBase64(this.selectedFile) : null
        // ↑ PRZYWRÓCONE: jeśli wybrano plik, konwertuj do base64, inaczej null
      };

      console.log('📚 Wysyłam dane książki (z obsługą zdjęć):', bookData);

      this.http.post('http://localhost:8080/api/v1/book/create', bookData, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      }).subscribe({
        next: (response: any) => {
          console.log('✅ SUKCES! Książka dodana:', response);

          if (response.data && response.data.book) {
            const hasImage = this.selectedFile ? ' z okładką' : '';
            alert(`🎉 Książka "${response.data.book.title}"${hasImage} została pomyślnie dodana!`);
          } else {
            alert('🎉 Książka została pomyślnie dodana!');
          }

          this.form.reset();
          this.selectedFile = null;
          this.isSubmitting = false;
          this.router.navigate(['/me']);
        },
        error: (error) => {
          console.error('❌ BŁĄD podczas dodawania książki:', error);
          this.isSubmitting = false;

          if (error.status === 500) {
            if (error.message?.includes('Data too long')) {
              alert('❌ Zdjęcie za duże dla bazy danych. Spróbuj mniejszy plik.');
            } else {
              alert('❌ Błąd serwera: ' + (error.error?.message || 'Nieznany błąd'));
            }
          } else if (error.status === 403) {
            alert('❌ Brak uprawnień do dodawania książek.');
          } else if (error.status === 401) {
            alert('❌ Sesja wygasła - zaloguj się ponownie');
          } else if (error.status === 400) {
            alert('❌ Nieprawidłowe dane: ' + (error.error?.message || 'Sprawdź formularz'));
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

  resetForm(): void {
    this.form.reset();
    this.selectedFile = null;
    this.isSubmitting = false;
  }
}
