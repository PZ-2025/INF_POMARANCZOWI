// src/app/report/report.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Report, ReportRequest } from './report.types';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private apiUrl = 'http://localhost:8080/api/v1/reports';

  constructor(private http: HttpClient) {}

  // Generowanie raportów - POPRAWIONA WERSJA
  generateReport(request: ReportRequest): Observable<Blob> {
    let params = new HttpParams();

    if (request.genre) params = params.set('genre', request.genre);
    if (request.status) params = params.set('status', request.status);
    if (request.publisher) params = params.set('publisher', request.publisher);
    if (request.startDate) params = params.set('startDate', request.startDate);
    if (request.endDate) params = params.set('endDate', request.endDate);
    if (request.limit) params = params.set('limit', request.limit.toString());

    const endpoint = this.getEndpoint(request.type);

    // WAŻNE: Dodaj poprawne headers dla PDF
    const headers = new HttpHeaders({
      'Accept': 'application/pdf',
      'Content-Type': 'application/json'
    });

    return this.http.get(`${this.apiUrl}/${endpoint}`, {
      params,
      headers,
      responseType: 'blob',
      observe: 'body'
    });
  }

  // Pobieranie pliku - POPRAWIONA WERSJA
  downloadFile(blob: Blob, filename: string): void {
    // Sprawdź czy blob nie jest pusty
    if (!blob || blob.size === 0) {
      console.error('Błąd: Pusty plik PDF');
      return;
    }

    // Sprawdź typ MIME
    console.log('Blob type:', blob.type);
    console.log('Blob size:', blob.size, 'bytes');

    // Utwórz URL dla blob
    const url = window.URL.createObjectURL(new Blob([blob], { type: 'application/pdf' }));

    // Utwórz link do pobrania
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.style.display = 'none';

    // Dodaj do DOM, kliknij i usuń
    document.body.appendChild(link);
    link.click();

    // Cleanup po 100ms
    setTimeout(() => {
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    }, 100);
  }

  // LocalStorage
  saveReport(report: Report): void {
    const reports = this.getReports();
    reports.unshift(report);
    localStorage.setItem('reports', JSON.stringify(reports.slice(0, 50)));
  }

  getReports(): Report[] {
    const stored = localStorage.getItem('reports');
    if (!stored) return [];

    try {
      const reports = JSON.parse(stored);
      return reports.map((r: any) => ({
        ...r,
        createdAt: new Date(r.createdAt) // Konwertuj string na Date
      }));
    } catch (error) {
      console.error('Error parsing reports from localStorage:', error);
      return [];
    }
  }

  deleteReport(id: string): void {
    const reports = this.getReports().filter(r => r.id !== id);
    localStorage.setItem('reports', JSON.stringify(reports));
  }

  clearReports(): void {
    localStorage.removeItem('reports');
  }

  // Utility
  private getEndpoint(type: string): string {
    switch (type) {
      case 'INVENTORY': return 'inventory';
      case 'FILTERED_INVENTORY': return 'filtered';
      case 'POPULARITY': return 'popularity';
      default: return 'inventory';
    }
  }

  generateFileName(type: string, parameters?: any): string {
    const timestamp = new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-');
    let filename = `${type.toLowerCase()}_report_${timestamp}`;

    if (parameters) {
      if (parameters.genre) filename += `_${parameters.genre}`;
      if (parameters.status) filename += `_${parameters.status}`;
      if (parameters.publisher) filename += `_${parameters.publisher}`;
    }

    return `${filename}.pdf`;
  }

  getTypeName(type: string): string {
    switch (type) {
      case 'INVENTORY': return 'Raport inwentaryzacyjny';
      case 'FILTERED_INVENTORY': return 'Raport filtrowany';
      case 'POPULARITY': return 'Raport popularności';
      default: return 'Raport';
    }
  }

  formatParameters(parameters: any): string {
    if (!parameters) return 'Brak parametrów';

    const parts: string[] = [];

    if (parameters.genre) parts.push(`Gatunek: ${parameters.genre}`);
    if (parameters.status) parts.push(`Status: ${parameters.status}`);
    if (parameters.publisher) parts.push(`Wydawca: ${parameters.publisher}`);
    if (parameters.startDate) parts.push(`Od: ${parameters.startDate}`);
    if (parameters.endDate) parts.push(`Do: ${parameters.endDate}`);
    if (parameters.limit) parts.push(`Limit: ${parameters.limit}`);

    return parts.length > 0 ? parts.join(', ') : 'Brak parametrów';
  }
}
