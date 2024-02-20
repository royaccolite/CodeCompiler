import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FileContentService {
  private apiUrl = 'http://localhost:8080/api/candidate/project-structure/explorer/file-content';  // Change this based on your actual API endpoint

  constructor(private http: HttpClient) { }

  // getFileContent(filePath: string): Observable<Map<string, string>> {
  //   const requestData = { filePath };
  //   console.log('the file path in file-content service is ',filePath);
    
  //   const headers = new HttpHeaders({
  //     'Content-Type': 'application/json',
  //     // Add other headers as needed
  //   });
  //   console.log('the http method resturns ',this.http.post<Map<string, string>>(this.apiUrl, requestData, { headers }));
    

  //   // return this.http.post<Map<string, string>>(this.apiUrl, requestData, { headers });
  //   return this.http.post<Map<string, string>>(this.apiUrl, requestData, { headers }).pipe(
  //     tap(response => console.log('Response from server:', response))
  //   );
  // }
  getFileContent(filePath: string): Observable<Map<string, string>> {
    const requestData = { filepath: filePath }; // Ensure that the parameter name matches what the server expects

    return this.http.post<Map<string, string>>(this.apiUrl, requestData);
  }
}