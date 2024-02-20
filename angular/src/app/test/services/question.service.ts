import { Injectable } from '@angular/core';
import { Question } from '../model/question.model';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class QuestionService {
  candidateId!:number;
  selectedQuestion!: Question;



  private apiUrl = 'http://localhost:8080/api/test/questions';

  constructor(private http: HttpClient) { }

  getAllQuestionById(candidateId: number): Observable<Question[]> {
    console.log("inside test :"+candidateId);
    const params = new HttpParams().set('candidateId', candidateId.toString());
    return this.http.get<Question[]>(this.apiUrl, { params });
    console.log(params);
    
  }
  onQuestionSelected(question: Question): void {
    this.selectedQuestion = question;
    // console.log(this.selectedQuestion);
  }
  
}
