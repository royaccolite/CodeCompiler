import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-email-dialog',
  templateUrl: './email-dialog.component.html',
  styleUrl: './email-dialog.component.scss',
})
export class EmailDialogComponent {
  constructor(public dialogRef: MatDialogRef<EmailDialogComponent>) {}
  email: string = '';
  onNoClick(): void {
    this.dialogRef.close();
  }
}
