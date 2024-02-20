import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, ReplaySubject, Subject } from 'rxjs';
import { Question } from '../model/question.model';


@Injectable({
  providedIn: 'root'
})
export class SharedService {
  selectedQuestion!: Question;
  private allQuestions: ReplaySubject<Question[]> = new ReplaySubject<Question[]>(1);

  private resultsSubject = new BehaviorSubject<string[]>([]);
  private runResultsSubject = new BehaviorSubject<string[]>([]);
  private consoleContentSubject = new BehaviorSubject<string>('');
  private customInputSubject = new BehaviorSubject<string>('');
  private sqlRunResultsSubject = new BehaviorSubject<Object[]>([]);
  private sqlResultsSubject = new BehaviorSubject<String[]>([]);
  private selectedLanguageSubject = new BehaviorSubject<string>('');  // Add this line

customInput$ = this.customInputSubject.asObservable();
  results$ = this.resultsSubject.asObservable();
  sqlResults$ = this.sqlResultsSubject.asObservable();
  runResults$ = this.runResultsSubject.asObservable();
  consoleContent$ = this.consoleContentSubject.asObservable();
  sqlRunResults$ = this.sqlRunResultsSubject.asObservable();
  selectedLanguage$ = this.selectedLanguageSubject.asObservable();  // Add this line

  updateCustomInput(customInput: string) {
    this.customInputSubject.next(customInput);
  }
  
  updateResults(results: string[]) {
    this.resultsSubject.next(results);
  }
  updateSelectedLanguage(language: string) {  // Add this method
    this.selectedLanguageSubject.next(language);
  }

  updateRunResults(runResults: string[]) {
    this.runResultsSubject.next(runResults);
  }
  updateSqlResults(sqlResults: String[]) {
    this.sqlRunResultsSubject.next(sqlResults);
  }
  updateSqlRunResults(sqlRunResults: Object[]) {
    this.sqlRunResultsSubject.next(sqlRunResults);
  }
  setData(data: Question[]): void {
    this.allQuestions.next(data);
  }
  getData$(): Observable<Question[]> {
    return this.allQuestions.asObservable();
  }
  private userIdSource = new BehaviorSubject<number|null>(null);
  userId$ = this.userIdSource.asObservable();

  setUserId(userId: number): void {
    this.userIdSource.next(userId);
  }


  
  private submitSubject = new BehaviorSubject<boolean>(false);
  submit$ = this.submitSubject.asObservable();

  setSubmit(value: boolean): void {
    this.submitSubject.next(value);
  }
  private contentSource = new Subject<string>();
  content$ = this.contentSource.asObservable();

  updateContent(content: string) {
    this.contentSource.next(content);
    console.log('content in shared service is ',content);
  }
}
