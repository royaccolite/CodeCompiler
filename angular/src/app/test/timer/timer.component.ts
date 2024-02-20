import { Component, Input, OnInit } from '@angular/core';
import { Subscription, interval } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-timer',
  templateUrl: './timer.component.html',
  styleUrls: ['./timer.component.scss']
})
export class TimerComponent implements OnInit {
  @Input() candidateId!: number;
  totalSecs = 2 * 60 * 60; // 2 hours in seconds
  timeInSeconds: number = this.totalSecs;
  private timerSubscription!: Subscription;
  startTime!: number;
  constructor(private router: Router) {}

  async ngOnInit(): Promise<void> {
    const storedStartTime = localStorage.getItem('timerStartTime');
    console.log(storedStartTime);
    if(storedStartTime == undefined){
      const response = await fetch('http://worldtimeapi.org/api/timezone/Asia/Kolkata');
      const data = await response.json();
      this.startTime = data.unixtime;
      localStorage.setItem('timerStartTime', ""+this.startTime);
    }else{
      this.startTime = parseInt(storedStartTime);
    }
    // this.startTime = storedStartTime ? parseInt(storedStartTime, 10) : new Date().getTime() - this.totalSecs * 1000;
  
    this.timerSubscription = interval(900).subscribe(async () => {
      const response = await fetch('http://worldtimeapi.org/api/timezone/Asia/Kolkata');
      const data = await response.json();
      this.checkAndUpdate(data.unixtime); // Convert unixtime to milliseconds
    });
  }

  checkAndUpdate(curtime: number): void {
    if (this.timeInSeconds > 0) {
      this.timeInSeconds = Math.max(0, this.totalSecs - (curtime - this.startTime));
    }else{
      localStorage.removeItem('timerStartTime');
      this.router.navigate(['/feedback', this.candidateId]);
    }
    // this.saveStartTime();
  }

  // saveStartTime(): void {
  //   localStorage.setItem('timerStartTime', this.startTime.toString());
  // }

  transform(value: number): string {
    const hours: number = Math.floor(value / 3600);
    const minutes: number = Math.floor((value % 3600) / 60);
    return ('00' + hours).slice(-2) + ':' + ('00' + minutes).slice(-2) + ':' + ('00' + Math.floor(value - minutes * 60)).slice(-2);
  }

}
