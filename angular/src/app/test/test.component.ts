import { Component, HostListener, OnInit} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { QuestionService } from './services/question.service';
import { SharedService } from './services/shared.service';
import { Location } from '@angular/common';



@Component({
  selector: 'app-test',
  templateUrl: './test.component.html',
  styleUrl: './test.component.scss'
})
export class TestComponent implements OnInit{
  candidateId!: number;

  constructor(
    private route: ActivatedRoute,
    private questionService: QuestionService,
    private sharedService: SharedService,
    private location: Location
  ) {}

  ngOnInit(): void {
    this.location.forward();
    // Retrieve the candidateId from the route parameters
    document.documentElement.requestFullscreen();
    this.route.queryParams.subscribe(params => {
      this.candidateId = params['candidateId'];
      // Now you can use this.candidateId to fetch data
      
      this.fetchQuestions();


      this.sharedService.setUserId(this.candidateId);
      //console.log(this.candidateId);
    });
   
  }

  fetchQuestions() {
    this.questionService.getAllQuestionById(this.candidateId).subscribe(data => {
      // Handle the fetched data as needed
      this.sharedService.setData(data);
    });
  }
  @HostListener('document:fullscreenchange', ['$event'])
  fullscreenHandler(event: Event) {
    if (!document.fullscreenElement) {
      // If not in fullscreen, request fullscreen
      document.documentElement.requestFullscreen().catch(err => {
        console.error('Error attempting to enable fullscreen:', err);
      });
    } else {
      // If exiting fullscreen, check if a user gesture is present before re-entering fullscreen
      if (this.isUserGesturePresent()) {
        document.documentElement.requestFullscreen().catch(err => {
          console.error('Error attempting to enable fullscreen:', err);
        });
      } 
    }
  }
  private isUserGesturePresent(): boolean {
    // Replace this with your own logic to determine if a user gesture is present
    // For simplicity, this example always returns true
    return true;
  }
// toggleFullscreen() {
//   if (document.fullscreenElement) {
//     document.exitFullscreen();
//   } else {
//     document.documentElement.requestFullscreen().catch(err => {
//       console.error('Error attempting to enable fullscreen:', err);
//     });
//   }
// }
}