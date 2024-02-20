import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FeedbackService {

 
  private apiUrl = 'http://localhost:8080/api/feedback';
  constructor(private http: HttpClient) {}
  sendFeedback(feedbackData: any): Observable<any> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');
    const options = { headers: headers };
    console.log(feedbackData);
    
    return this.http.post<{ result: string }>(
      `${this.apiUrl}/add`,
      feedbackData,
      options
    );
    
   
  }
 
}
