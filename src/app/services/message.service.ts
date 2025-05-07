import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class MessageService {
  private _messageSubject = new BehaviorSubject<string | null>(null);
  message$ = this._messageSubject.asObservable();

  setMessage(msg: string) {
    sessionStorage.setItem('global-message', msg);
    this._messageSubject.next(msg);
  }

  clearMessage() {
    sessionStorage.removeItem('global-message');
    this._messageSubject.next(null);
  }

  private getSessionMessage(): string | null {
    return sessionStorage.getItem('global-message');
  }

  getCurrentMessage(): string | null {
    return this._messageSubject.getValue();
  }
}
