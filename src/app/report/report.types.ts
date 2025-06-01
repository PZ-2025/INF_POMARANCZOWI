export interface Report {
  id: string;
  name: string;
  type: 'INVENTORY' | 'FILTERED_INVENTORY' | 'POPULARITY' | 'OVERDUE';
  createdAt: Date;
  generatedBy: string;
  parameters?: any;
}

export interface ReportRequest {
  type: 'INVENTORY' | 'FILTERED_INVENTORY' | 'POPULARITY' | 'OVERDUE';
  genre?: string;
  status?: string;
  publisher?: string;
  startDate?: string;
  endDate?: string;
  limit?: number;
}
