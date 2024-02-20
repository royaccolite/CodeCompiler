import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EndService {
  private apiUrl = 'http://localhost:8080/api/test';

  constructor(private http: HttpClient) {}

  sendUserIdToBackend(candidateId: number | null | undefined): Observable<any> {
    // Handle the case when candidateId is null or undefined
    const params = candidateId !== null && candidateId !== undefined
      ? new HttpParams().set('candidateId', candidateId)
      : new HttpParams();

    console.log(candidateId);
    console.log('hello');
    
    // Make a POST request to the backend with userId
    return this.http.post<any>(`${this.apiUrl}/endTest`, null, { params: params });
    // The second parameter (null) is added to satisfy the HttpClient.post() signature,
    // even though the request body is empty in this case.
  }
}
