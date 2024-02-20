
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SubmitService {

  private apiUrl = 'http://localhost:8080/api/code/submission'; // Update with your actual API endpoint
  constructor(private http: HttpClient) { }
  
// temporary comments
  submitCode(code: any): Observable<string[]> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    console.log(code);

    return this.http.post<string[]>(
      this.apiUrl,
      code,
      { headers: headers }
    );
  }
}
