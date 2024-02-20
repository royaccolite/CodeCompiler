// file-explorer.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FileNode } from '../model/file-node.model';

@Injectable({
  providedIn: 'root',
})
export class FileExplorerService {
  private apiUrl = 'http://localhost:8080/api/candidate/project-structure';

  constructor(private http: HttpClient) {}

  getFileStructure(): Observable<FileNode[]> {
    return this.http.get<FileNode[]>(this.apiUrl);
  }
  loadChildren(node: FileNode): Observable<FileNode[]> {
    // Implement logic to fetch children of the given node
    // Example: return this.http.get<FileNodeDTO[]>(`${this.apiUrl}/${node.id}/children`);
    return this.http.get<FileNode[]>(`${this.apiUrl}/load-children`, { params: { parentId: node.relativePath.toString() } });
  }
  getFileContent(path: string): Observable<string> {
    // Define your headers
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      // Add other headers as needed
    });

    // Use HttpParams for URL parameters
    const params = new HttpParams().set('path', path);

    // Include headers and params in the HTTP request
    return this.http.get<string>(`${this.apiUrl}/get-file-content`, { headers, params });
  }
}