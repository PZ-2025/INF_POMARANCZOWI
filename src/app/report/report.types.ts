// src/app/report/report.types.ts

export interface Report {
  id: string;
  name: string;
  type: 'INVENTORY' | 'FILTERED_INVENTORY' | 'POPULARITY';
  createdAt: Date;
  generatedBy: string;
  parameters?: any;
}

export interface ReportRequest {
  type: 'INVENTORY' | 'FILTERED_INVENTORY' | 'POPULARITY';
  genre?: string;
  status?: string;
  publisher?: string;
  startDate?: string;
  endDate?: string;
  limit?: number;
}
