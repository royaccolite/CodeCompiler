import { Component, HostListener, Input, OnInit, ViewChild } from '@angular/core';
import { Question } from '../model/question.model';
import { MonacoEditorComponent } from '../monaco-editor/monaco-editor.component';
import { MonacoEditorService } from '../services/monaco-editor.service';
import { SharedService } from '../services/shared.service';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { WarningDialogComponent } from '../../warning-dialog/warning-dialog.component';

@Component({
  selector: 'app-question-list',
  templateUrl: './question-list.component.html',
  styleUrl: './question-list.component.scss'
})
export class QuestionListComponent implements OnInit {
  @ViewChild(MonacoEditorComponent)

  private monaco!: MonacoEditorComponent;
  @Input() candidateId!: number;

  results: string[] = [];
  runResults: string[] = [];
  consoleContent: string = '';

  questions: Question[] = [];
  selectedQuestion: Question | null = null;
  leftWidth = 200; // Initial width for the left div
  rightWidth = 1000;
  selectedQuestionId!: number;
  selectedLanguage: string = 'cpp'; // Default to C++
  leftMaxHeight: number = 400; // or any default value you want
  leftOverflow: string = 'auto'
  rightMaxHeight: number = 400;
  rightOverflow: string = 'auto';

  constructor(
    private sharedService: SharedService,
    private monacoService: MonacoEditorService,
    private dialog: MatDialog, private router: Router,
  ) { }

  @HostListener('window:scroll', ['$event'])
  onWindowScroll() {
    const fixedLeftContainer = document.getElementById('fixedLeftContainer');
    if (fixedLeftContainer) {
      fixedLeftContainer.style.top = `${window.scrollY}px`;
    }
  }

  ngOnInit(): void {
    this.sharedService.getData$().subscribe((data) => {
      this.questions = data;
      console.log(data);
      console.log(this.selectedQuestionId);

      this.onQuestionSelected(this.questions[0])
    });
    // this.startTimer();
    this.sharedService.userId$.subscribe((userId) => {
      this.candidateId != userId;
    });
  }
  onQuestionSelected(question: Question): void {
    this.selectedQuestion = question;
    this.selectedQuestionId = question.id;
    this.monaco.change(question);
    console.log(this.selectedQuestionId);
  }
  onLanguageChange(language: string) {
    this.selectedLanguage = language;

    this.sharedService.setSubmit(false);
  }
  // Handle the contentFromLocalStorage event
  onContentFromLocalStorage(content: string) {
    // Pass the content to ConsoleComponent
    this.consoleContent = content;
  }


  //header=======================
  // timeLeft: string = '02:00:00';
  // startTimer() {
  //   const endTime = new Date();
  //   endTime.setHours(endTime.getHours() + 2); // Set the end time to 2 hours from now

  //   const updateTimer = () => {
  //     const now = new Date();
  //     const difference = endTime.getTime() - now.getTime();

  //     if (difference > 0){
  //       const hours = Math.floor((difference % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  //       const minutes = Math.floor((difference % (1000 * 60 * 60)) / (1000 * 60));
  //       const seconds = Math.floor((difference % (1000 * 60)) / 1000);

  //       this.timeLeft = this.formatTime(hours) + ':' + this.formatTime(minutes) + ':' + this.formatTime(seconds);

  //       setTimeout(updateTimer, 1000); // Update every second
  //     }else {
  //       this.timeLeft = '00:00:00';
  //       alert('Times Up!')
  //       this.router.navigate(['/feedback', this.candidateId]);
  //     }
  //   };
  //   updateTimer();
  // }

  // formatTime(value: number): string {
  //   return value < 10 ? '0' + value : value.toString();
  // }

  openWarningDialog(): void {
    const dialogRef = this.dialog.open(WarningDialogComponent);

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        // User clicked "End Test"
        // Add your logic to handle the end test action here
        console.log('Ending test...');
      } else {
        // User clicked "Cancel" or closed the dialog
        console.log('Test cancellation canceled.');
      }
    });
  }

  onResultsChanged(results: string[]) {
    this.results = results;
  }

  onRunResultsChanged(runResults: string[]) {
    this.runResults = runResults;
  }

  onConsoleContentChanged(consoleContent: string) {
    this.consoleContent = consoleContent;
  }
}
