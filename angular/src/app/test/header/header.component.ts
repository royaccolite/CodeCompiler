import { Component } from '@angular/core';
import { WarningDialogComponent } from '../../warning-dialog/warning-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedService } from '../services/shared.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  timeLeft: string = '02:00:00';
  candidateId:number | null | undefined;
  constructor(private dialog: MatDialog,private router: Router,private sharedService: SharedService){}
  ngOnInit() {
    
    this.startTimer();
    this.sharedService.userId$.subscribe((userId) => {
      this.candidateId = userId;
      console.log(this.candidateId);
      console.log('Times Up');
    });
    
  }

  startTimer() {
    const endTime = new Date();
    endTime.setHours(endTime.getHours() + 2); // Set the end time to 2 hours from now

    const updateTimer = () => {
      const now = new Date();
      const difference = endTime.getTime() - now.getTime();

      if (difference > 0) {
        const hours = Math.floor((difference % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((difference % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((difference % (1000 * 60)) / 1000);

        this.timeLeft = this.formatTime(hours) + ':' + this.formatTime(minutes) + ':' + this.formatTime(seconds);

        setTimeout(updateTimer, 1000); // Update every second
      } else {
        this.timeLeft = '00:00:00';
        alert('Times Up!')
        this.router.navigate(['/feedback',this.candidateId]);
        // Perform any action you want when the timer reaches 0
      }
    };

    updateTimer();
  }

  formatTime(value: number): string {
    return value < 10 ? '0' + value : value.toString();
  }
  
  openWarningDialog(): void {
    const dialogRef = this.dialog.open(WarningDialogComponent);
      
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        // User clicked "End Test"
        // Add your logic to handle the end test action here
        console.log('Ending test...');
      } else {
        // User clicked "Cancel" or closed the dialog
        console.log('Test cancellation canceled.');
      }
    });
  }
}
