import { Component, Input, OnInit } from '@angular/core';
import { ReportService } from './report.service';
import { Report, ReportRequest } from './report.types';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  standalone: false,
})
export class ReportsComponent implements OnInit {
  @Input() userFirstName = '';
  @Input() userLastName = '';

  reports: Report[] = [];
  showModal = false;
  isGenerating = false;
  message = '';
  messageType = '';

  // Formularz
  reportType = 'INVENTORY';
  genre = '';
  status = '';
  publisher = '';
  startDate = '';
  endDate = '';
  limit = 10;

  // Paginacja
  currentPage = 1;
  reportsPerPage = 5;
  totalPages = 1;

  constructor(
    private reportService: ReportService
  ) {}

  ngOnInit() {
    this.loadReports();
  }

  loadReports() {
    this.reports = this.reportService.getReports();
    this.updatePagination();
  }

  updatePagination() {
    this.totalPages = Math.ceil(this.reports.length / this.reportsPerPage);
    if (this.currentPage > this.totalPages && this.totalPages > 0) {
      this.currentPage = 1;
    }
  }

  get paginatedReports(): Report[] {
    const start = (this.currentPage - 1) * this.reportsPerPage;
    const end = start + this.reportsPerPage;
    return this.reports.slice(start, end);
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  openModal() {
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
    this.resetForm();
  }

  generateReport() {
    this.isGenerating = true;
    this.showMessage('Generowanie raportu...', 'info');

    const request: ReportRequest = {
      type: this.reportType as any,
      genre: this.genre || undefined,
      status: this.status || undefined,
      publisher: this.publisher || undefined,
      startDate: this.startDate || undefined,
      endDate: this.endDate || undefined,
      limit: this.limit || undefined
    };

    console.log('Generowanie raportu:', request);

    this.reportService.generateReport(request).subscribe({
      next: (blob) => {
        console.log('Otrzymano blob:', blob);
        console.log('Rozmiar blob:', blob.size);
        console.log('Typ blob:', blob.type);

        if (blob.size === 0) {
          this.showMessage('Błąd: Otrzymano pusty plik PDF', 'error');
          this.isGenerating = false;
          return;
        }

        const filename = this.reportService.generateFileName(this.reportType, request);
        this.reportService.downloadFile(blob, filename);

        // Zapisz w historii
        const report: Report = {
          id: Date.now().toString(),
          name: filename,
          type: this.reportType as any,
          createdAt: new Date(),
          generatedBy: `${this.userFirstName} ${this.userLastName}`.trim() || 'Użytkownik',
          parameters: request
        };
        this.reportService.saveReport(report);

        this.loadReports();
        this.showMessage('Raport wygenerowany i pobrany pomyślnie!', 'success');
        this.closeModal();
      },
      error: (error) => {
        console.error('Report generation error:', error);
        let errorMsg = 'Nieznany błąd';

        if (error.error && error.error.message) {
          errorMsg = error.error.message;
        } else if (error.message) {
          errorMsg = error.message;
        } else if (error.status) {
          switch (error.status) {
            case 401: errorMsg = 'Brak uprawnień'; break;
            case 403: errorMsg = 'Dostęp zabroniony'; break;
            case 404: errorMsg = 'Nie znaleziono endpointa'; break;
            case 500: errorMsg = 'Błąd serwera'; break;
            default: errorMsg = `Błąd HTTP ${error.status}`;
          }
        }

        this.showMessage('Błąd: ' + errorMsg, 'error');
        this.isGenerating = false;
      },
      complete: () => {
        this.isGenerating = false;
      }
    });
  }

  deleteReport(id: string) {
    if (confirm('Usunąć raport z listy?')) {
      this.reportService.deleteReport(id);
      this.loadReports();
      this.showMessage('Raport usunięty z listy', 'success');
    }
  }

  clearAll() {
    if (confirm('Usunąć wszystkie raporty z listy? Ta operacja jest nieodwracalna.')) {
      this.reportService.clearReports();
      this.loadReports();
      this.showMessage('Wszystkie raporty zostały usunięte', 'success');
    }
  }

  private resetForm() {
    this.reportType = 'INVENTORY';
    this.genre = '';
    this.status = '';
    this.publisher = '';
    this.startDate = '';
    this.endDate = '';
    this.limit = 10;
  }

  private showMessage(text: string, type: string) {
    this.message = text;
    this.messageType = type;

    setTimeout(() => {
      this.message = '';
      this.messageType = '';
    }, 5000);
  }

  getTypeName(type: string): string {
    return this.reportService.getTypeName(type);
  }

  formatDate(date: Date): string {
    return new Date(date).toLocaleDateString('pl-PL', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatParameters(parameters: any): string {
    return this.reportService.formatParameters(parameters);
  }

  // Walidacja dat
  get isDateRangeValid(): boolean {
    if (!this.startDate || !this.endDate) return true;
    return new Date(this.startDate) <= new Date(this.endDate);
  }

  get canGenerate(): boolean {
    return !this.isGenerating && this.isDateRangeValid;
  }

  get mostCommonReportType(): string {
    if (this.reports.length === 0) return 'Brak danych';

    // Zlicz każdy typ raportu
    const typeCounts: { [key: string]: number } = {};

    this.reports.forEach(report => {
      typeCounts[report.type] = (typeCounts[report.type] || 0) + 1;
    });

    // Znajdź typ z największą liczbą
    let mostCommonType = '';
    let maxCount = 0;

    Object.entries(typeCounts).forEach(([type, count]) => {
      if (count > maxCount) {
        maxCount = count;
        mostCommonType = type;
      }
    });

    return this.getTypeName(mostCommonType);
  }

  get totalReportsCount(): number {
    return this.reports.length;
  }

  get lastReportDate(): string {
    if (this.reports.length === 0) return 'Brak';
    return this.formatDate(this.reports[0].createdAt);
  }

  // Metoda pomocnicza do sprawdzania czy raport potrzebuje określonych pól
  get needsGenreAndPublisher(): boolean {
    return this.reportType === 'FILTERED_INVENTORY' || this.reportType === 'OVERDUE';
  }

  get needsDateRange(): boolean {
    return this.reportType === 'POPULARITY' || this.reportType === 'OVERDUE';
  }

  get needsLimit(): boolean {
    return this.reportType === 'POPULARITY';
  }

  get needsStatus(): boolean {
    return this.reportType === 'FILTERED_INVENTORY' || this.reportType === 'POPULARITY';
  }
}
