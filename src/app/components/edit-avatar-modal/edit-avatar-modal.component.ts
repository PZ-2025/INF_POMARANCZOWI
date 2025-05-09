import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-edit-avatar-modal',
  standalone: false,
  templateUrl: './edit-avatar-modal.component.html',
  styleUrl: './edit-avatar-modal.component.css'
})
export class EditAvatarModalComponent {
  @Output() cancel = new EventEmitter<void>();
  @Output() save = new EventEmitter<File>();
  @Output() delete = new EventEmitter<void>();

  selectedFile: File | null = null;
  previewUrl: string | null = null;

  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];

      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl = reader.result as string;
      };
      reader.readAsDataURL(this.selectedFile);
    }
  }

  onSave() {
    if (this.selectedFile) {
      this.save.emit(this.selectedFile);
    } else {
      this.save.emit();
    }
  }

  deleteAvatar(): void {
    this.delete.emit();
  }
}
