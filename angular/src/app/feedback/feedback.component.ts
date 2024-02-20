import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FeedbackService } from '../services/feedback.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-feedback',
  templateUrl: './feedback.component.html',
  styleUrl: './feedback.component.scss'
})
export class FeedbackComponent {
  rating!: number ;
  stars: number[] = [1, 2, 3, 4, 5]; 
 
  description: string = '';
  candidateId!: string;

  constructor(private route: ActivatedRoute,private feedbackService: FeedbackService,private router: Router) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.candidateId = params['candidateId'];
    });
  }

  formatLabel(value: number | null) {
    if (!value) {
      return '';
    }
    return value.toString();
  }

  submitFeedback() {
    // You can implement logic to send feedback to the server or handle it as needed
    console.log(this.candidateId);
    console.log('Rating:', this.rating);
    console.log('Feedback Description:', this.description);
    const feedbackData = {candidateId:this.candidateId,description:this.description, rating:this.rating};
    this.feedbackService.sendFeedback(feedbackData).subscribe(
        
      (response: any) => {
        console.log('Feedback submitted successfully:', response);
        // You can handle success response here
      },
      (error: { status: any; statusText: any; }) => {
        console.error('Error submitting feedback:', error);
        // You can handle error response here
        if (error instanceof HttpErrorResponse) {
          console.error('Status  :', error.status);
          console.error('Status Text:', error.statusText);
        }
      }

      
    );
    this.router.navigate(['/thankyou']);
    // Add your logic to send feedback to the server or handle it locally
  }
  isButtonClicked = false;

  onButtonClick() {
    this.isButtonClicked = true;
    this.router.navigate(['/thankyou']);
  }
}
