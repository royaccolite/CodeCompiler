// import { HttpClient, HttpParams } from '@angular/common/http';
// import { Injectable } from '@angular/core';
// import {  catchError, map } from 'rxjs/operators';
// import { Question } from '../test/model/question.model';
// import { Observable, of, throwError } from 'rxjs';


// @Injectable({
//   providedIn: 'root'
// })
// export class LoginService {
//   private apiUrl = 'http://localhost:8080/api/test/questions';
//   constructor(private http: HttpClient) {}

//   checkIfQuestionsExist(candidateId: number): Observable<boolean>{
//     const params = new HttpParams().set('candidateId', candidateId);

//     return this.http.get<Question[]>(this.apiUrl, { params }).pipe(
//       map((questions: string | any[]) => questions.length > 0),
//       catchError((error: any) => {
//         if (error.status === 404) {
//           return of(false);
//         }
//         else{
//         console.error('Error fetching questions:', error);
//         return throwError('Error checking questions existence.'); // re-throw the error to propagate it further
//         }
//       })
//     );
//   }
// }




import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {  catchError, map } from 'rxjs/operators';
import { Question } from '../test/model/question.model';
import { Observable, of, throwError } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class LoginService {
  private apiUrl = 'http://localhost:8080/api/fetch/candidateId';
  constructor(private http: HttpClient) {}

  getCandidateIdByEmail(email: string): Observable<number>{
    const params = new HttpParams().set('email', email);
    return this.http.get<number>(this.apiUrl, { params }).pipe(
      catchError((error: any) => {
        console.error('Error fetching candidateId:', error);
        return throwError('Error fetching candidateId.'); 
      })
    );
  }
}
