import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedService } from '../test/services/shared.service';
import { EndService } from '../services/end.service';
// import { EndService } from '../services/end.service';

@Component({
  selector: 'app-warning-dialog',
  templateUrl: './warning-dialog.component.html',
  styleUrl: './warning-dialog.component.scss'
})
export class WarningDialogComponent {
  candidateId: number | null | undefined
  constructor(public dialogRef: MatDialogRef<WarningDialogComponent>,
    private sharedService: SharedService, private route: ActivatedRoute, private endService: EndService,


    private router: Router) { }
  ngOnInit(): void {
    this.sharedService.userId$.subscribe((userId) => {
      this.candidateId = userId;
      console.log(this.candidateId);
    });
  }
  onCancelClick(): void {
    this.dialogRef.close(false);
  }
  onConfirmClick(): void {
    this.dialogRef.close(true);
    const candidateID = this.candidateId
    // Replace with the actual userId
    this.endService.sendUserIdToBackend(candidateID).subscribe(
      (response: any) => {
        console.log('Backend response:', response);
        localStorage.removeItem('timerStartTime');
      },
      (error: { status: any; statusText: any; }) => {
        console.error('Error sending userId to backend:', error);
      }
    );
    this.router.navigate(['/feedback', this.candidateId]);
  };

  // this.router.navigateByUrl('/feedback');
}
