// monaco-editor-communication.service.ts
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class MonacoEditorCommunicationService {
  private codeSubject = new Subject<string>();

  setCode(code: string) {
    this.codeSubject.next(code);
  }

  getCodeObservable() {
    return this.codeSubject.asObservable();
  }
}