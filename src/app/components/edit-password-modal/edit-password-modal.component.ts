import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-edit-password-modal',
  standalone: false,
  templateUrl: './edit-password-modal.component.html',
  styleUrl: './edit-password-modal.component.css'
})
export class EditPasswordModalComponent {
  oldPassword = '';
  newPassword = '';
  confirmPassword = '';

  @Output() cancel = new EventEmitter<void>();
  @Output() save = new EventEmitter<{ oldPassword: string, newPassword: string, confirmPassword: string }>();

  onSave() {
    this.save.emit({
      oldPassword: this.oldPassword,
      newPassword: this.newPassword,
      confirmPassword: this.confirmPassword
    });
  }
}
