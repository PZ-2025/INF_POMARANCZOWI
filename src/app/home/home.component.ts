import { Component, OnInit } from '@angular/core';
import { ThemeService } from '../services/theme.service';
import { MessageService } from '../services/message.service';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  isDarkTheme = false;
  message: string | null = null;

  constructor(private themeService: ThemeService, private messageService: MessageService) {}

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
  }

  autoClearMessage() {
    setTimeout(() => {
      this.messageService.clearMessage();
    }, 5000);
  }

  toggleTheme() {
    this.themeService.toggleTheme();
  }
}
